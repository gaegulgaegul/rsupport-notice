package com.project.core.config.redis;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Slf4j
@Configuration
public class EmbeddedRedisConfig {

	@Value("${spring.data.redis.port}")
	private int redisPort;

	private RedisServer redisServer;

	@PostConstruct
	public void redisServer() throws IOException {
		redisServer = RedisServer.newRedisServer()
			.port(redisPort)
			.build();
		try {
			redisServer.start();
			log.info("Redis server started on port {}", redisPort);
		} catch (Exception e) {
			log.error("Redis server start failed");
		}
	}

	@PreDestroy
	public void stopRedis() throws IOException {
		if (redisServer != null) {
			redisServer.stop();
		}
	}
}
