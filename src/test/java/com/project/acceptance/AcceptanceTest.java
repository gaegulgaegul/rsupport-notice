package com.project.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceTest {
	@LocalServerPort
	int port;

	@Autowired
	private CleanUpDatabase cleanUpDatabase;

	@BeforeEach
	public void acceptanceSetUp() {
		RestAssured.port = port;
		cleanUpDatabase.execute();
	}
}
