package com.bohan.config;


import com.bohan.serializer.MyStringRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        /**
         *
         * 默认 springboot会帮我们配置好redis 只需要导入包 + 在配置文件中配置
         * 这里是设置template的序列化方式 为了在用redis图形软件打开的时候可以读取，因为key
         * 一般都是string 所以就用默认的string 但是object需要使用自己的序列化方式
         *
         */
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        MyStringRedisSerializer myStringRedisSerializer = new MyStringRedisSerializer();

        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(myStringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(myStringRedisSerializer);
        return redisTemplate;
    }
}
