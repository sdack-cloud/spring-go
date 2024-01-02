package cn.sdack.go.auth.config;

import cn.sdack.go.auth.dao.AccountDao;
import cn.sdack.go.auth.service.UserDetailsServiceImpl;
import cn.sdack.go.auth.utils.KeyStoreKeyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;

/**
 * @author sdack
 * @date 2023/12/23
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurity {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    AccountDao accountDao;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,RememberMeServices rememberMeServices,PasswordEncoder passwordEncoder) throws Exception {
        http.cors(Customizer.withDefaults());
        http.cors(corsCustomizer ->
                corsCustomizer.configurationSource(corsConfigurationSource()));

        CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider();
        userDetailsService.passwordEncoder = passwordEncoder;
        customAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        customAuthenticationProvider.setUserDetailsService(userDetailsService);
        customAuthenticationProvider.setUserDetailsPasswordService(userDetailsService);
        customAuthenticationProvider.setHideUserNotFoundExceptions(false);
        customAuthenticationProvider.stringRedisTemplate = stringRedisTemplate;

        http
                .authenticationProvider(customAuthenticationProvider)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/assets/**", "/webjars/**", "/login", "/logout",
                                        "/register", "/activate", "/confirm", "/resend", "/success","/sendSmsCode",
                                        "/open/**", "/authorization_code")
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin.loginPage("/login")
                                .defaultSuccessUrl("/index")
                )
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                )
                .rememberMe(it ->
                        it.rememberMeServices(rememberMeServices)
                )
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(Customizer.withDefaults())
                )
        ;
        return http.build();
    }

    @Bean
    RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("auth.sdack", userDetailsService,
                TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256);
        rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.MD5);
        rememberMe.setTokenValiditySeconds(30*24*3600); // token过期时间
        return rememberMe;
    }


    @Bean
    @Order(-200)
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        HashMap<String, PasswordEncoder> encoders = new HashMap<String, PasswordEncoder>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("testttst.jks"), "testdemoTTSTdemotest".toCharArray());
        return keyStoreKeyFactory.getKeyPair("testttst");
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(false); // 是否返回时生成凭证
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

