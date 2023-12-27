package cn.sdack.go.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

/**
 * @author sdack
 * @date 2023/12/26
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class DefaultWebSecurity {


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/open/**")
                                .permitAll()
                                .requestMatchers("/authority")
                                .access((authentication, context) -> new AuthorizationDecision((new IpAddressMatcher("127.0.0.1")).matches(context.getRequest())))
                                .anyRequest()
                                .authenticated()
                )
                .oauth2ResourceServer(
                        it -> it.jwt(Customizer.withDefaults())
                );
        return http.build();
    }

    /*@Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins();
        configuration.setAllowCredentials();
        configuration.setAllowedHeaders();
        configuration.setAllowedMethods();
        UrlBasedCorsConfigurationSource  source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/
}
