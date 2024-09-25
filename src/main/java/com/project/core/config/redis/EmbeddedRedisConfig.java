package com.project.core.config.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.project.core.exception.ApplicationException;

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
			boolean useRedisPort = isUseRedisPort();
			log.info("is alive redis: {}", useRedisPort);
			if (useRedisPort) {
				killRedisPort();
			}
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

	private void killRedisPort() {
		try {
			String command = "lsof -t -i:%d | xargs kill -9".formatted(redisPort);
			ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
			processBuilder.start().waitFor();
			log.info("Kill Redis port successfully");
		} catch (Exception e) {
			log.error("Kill Redis port failed");
		}
	}

	private boolean isUseRedisPort() throws IOException {
		return isRunning(executeGrepProcessCommand(redisPort));
	}

	private Process executeGrepProcessCommand(int redisPort) throws IOException {
		String command = String.format("netstat -nat | grep LISTEN|grep %d", redisPort);
		String[] shell = {"/bin/sh", "-c", command};

		return Runtime.getRuntime().exec(shell);
	}

	private boolean isRunning(Process process) {
		String line;
		StringBuilder pidInfo = new StringBuilder();

		try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			while ((line = input.readLine()) != null) {
				pidInfo.append(line);
			}
		} catch (Exception e) {
			throw new ApplicationException();
		}
		return StringUtils.hasText(pidInfo.toString());
	}
}
