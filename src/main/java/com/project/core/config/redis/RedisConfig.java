package com.project.core.config.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableRedisHttpSession
@EnableCaching
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}

	@Bean
	public ObjectMapper redisObjectMapper() {
		BasicPolymorphicTypeValidator polymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
			.allowIfBaseType(Object.class)
			.build();
		return new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.enable(JsonGenerator.Feature.IGNORE_UNKNOWN)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.activateDefaultTyping(polymorphicTypeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
	}

	@Bean
	public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper redisObjectMapper) {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper)))
			.entryTtl(Duration.ofMinutes(3L))
			.disableCachingNullValues();

		return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(redisCacheConfiguration)
			.build();
	}

}
