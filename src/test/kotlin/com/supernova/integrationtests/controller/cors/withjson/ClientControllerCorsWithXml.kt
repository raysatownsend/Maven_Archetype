package com.supernova.integrationtests.controller.cors.withjson

import com.supernova.integrationtests.TestsConfig
import com.supernova.integrationtests.testcontainers.AbstractIntegrationTest
import com.supernova.integrationtests.vo.AccountCredentialsVO
import com.supernova.integrationtests.vo.ClientVO
import com.supernova.integrationtests.vo.TokenVO
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientControllerCorsWithXml() : AbstractIntegrationTest() {

	private lateinit var specification: RequestSpecification
	private lateinit var objectMapper: ObjectMapper
	private lateinit var client: ClientVO
	private lateinit var token: String

	@BeforeAll
	fun setupTests(){
		objectMapper = ObjectMapper()
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		client = ClientVO()
		token = ""
	}

	@Test
	@Order(0)
	fun authorization() {
		val user = AccountCredentialsVO(
			username = "leandro",
			password = "admin123"
		)

		token = given()
				.basePath("/auth/signin")
				.port(TestsConfig.SERVER_PORT)
				.contentType(TestsConfig.CONTENT_TYPE_XML)
				.body(user)
			.`when`()
				.post()
					.then()
				.statusCode(200)
					.extract()
					.body()
			.`as`(TokenVO::class.java)
				.accessToken!!
	}

	@Test
	@Order(1)
	fun testCreate() {
		mockClient()

		specification = RequestSpecBuilder()
			.addHeader(
				TestsConfig.HEADER_PARAM_ORIGINS,
				TestsConfig.ORIGIN_ERUDIO
			)
			.addHeader(
				TestsConfig.HEADER_PARAM_AUTHORIZATION,
				"Bearer $token"
			)
				.setBasePath("/api/client/v1")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		val content = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_XML)
			.body(client)
				.`when`()
			.post()
			.then()
				.statusCode(200)
			.extract()
			.body()
				.asString()

		val createdClient = objectMapper.readValue(
			content,
			ClientVO::class.java
		)

		client = createdClient

		assertNotNull(createdClient.id)
		assertNotNull(createdClient.firstName)
		assertNotNull(createdClient.lastName)
		assertNotNull(createdClient.gender)
		assertNotNull(createdClient.address)

		assertTrue(createdClient.id > 0)

		assertEquals("Marina", createdClient.firstName)
		assertEquals("Silva", createdClient.lastName)
		assertEquals("Female", createdClient.gender)
		assertEquals("São Paulo - SP", createdClient.address)
	}

	@Test
	@Order(2)
	fun testCreateWithWrongEndpoint() {
		mockClient()

		specification = RequestSpecBuilder()
			.addHeader(
				TestsConfig.HEADER_PARAM_ORIGINS,
				TestsConfig.ORIGIN_SEMERU
			)
			.addHeader(
				TestsConfig.HEADER_PARAM_AUTHORIZATION,
				"Bearer $token"
			)
				.setBasePath("/api/client/v1")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		val content = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_XML)
			.body(client)
				.`when`()
			.post()
			.then()
				.statusCode(403)
			.extract()
			.body()
				.asString()

		assertEquals("Invalid CORS request", content)
	}

	@Test
	@Order(3)
	fun testFindById() {
		mockClient()

		specification = RequestSpecBuilder()
			.addHeader(
				TestsConfig.HEADER_PARAM_ORIGINS,
				TestsConfig.ORIGIN_LOCALHOST
			)
			.addHeader(
				TestsConfig.HEADER_PARAM_AUTHORIZATION,
				"Bearer $token"
			)
			.setBasePath("/api/client/v1/")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		val content = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_XML)
			.pathParam("id", client.id)
			.`when`() ["{id}"]
			.then()
			.statusCode(200)
			.extract()
			.body()
			.asString()

		val createdClient = objectMapper.readValue(
			content,
			ClientVO::class.java
		)

		assertNotNull(createdClient.id)
		assertNotNull(createdClient.firstName)
		assertNotNull(createdClient.lastName)
		assertNotNull(createdClient.gender)
		assertNotNull(createdClient.address)

		assertTrue(createdClient.id > 0)

		assertEquals("Marina", createdClient.firstName)
		assertEquals("Silva", createdClient.lastName)
		assertEquals("Female", createdClient.gender)
		assertEquals("São Paulo - SP", createdClient.address)
	}

	@Test
	@Order(4)
	fun testFindByIdWithWrongEndpoint() {
		mockClient()

		specification = RequestSpecBuilder()
			.addHeader(
				TestsConfig.HEADER_PARAM_ORIGINS,
				TestsConfig.ORIGIN_SEMERU
			)
			.addHeader(
				TestsConfig.HEADER_PARAM_AUTHORIZATION,
				"Bearer $token"
			)
			.setBasePath("/api/client/v1/")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		val content = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.accept(TestsConfig.CONTENT_TYPE_XML)
			.pathParam("id", client.id)
			.`when`() ["{id}"]
			.then()
			.statusCode(403)
			.extract()
			.body()
			.asString()

		assertEquals("Invalid CORS request", content)
	}

	private fun mockClient() {
		client.firstName = "Marina"
		client.lastName = "Silva"
		client.gender = "Female"
		client.address = "São Paulo - SP"
	}
}
