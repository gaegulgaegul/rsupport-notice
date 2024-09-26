package com.project.acceptance;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceTest {
	@LocalServerPort int port;

	@Autowired private CleanUpDatabase cleanUpDatabase;
	@Autowired private CleanUpFile cleanUpFile;

	@BeforeEach
	public void acceptanceSetUp() throws IOException {
		RestAssured.port = port;
		cleanUpDatabase.execute();
		cleanUpFile.execute();
	}
}
