package cn.sdack.go.auth.config;

import cn.sdack.go.auth.entity.AccountEntity;
import cn.sdack.go.auth.jackson2.AccountMixin;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.List;

/**
 * @date 2023/12/23
 * @author sdack
 */
@EnableRedisHttpSession
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Module> modules = SecurityJackson2Modules.getModules(getClass().getClassLoader());
        objectMapper.registerModules(modules);
        objectMapper.addMixIn(AccountEntity.class, AccountMixin.class);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
