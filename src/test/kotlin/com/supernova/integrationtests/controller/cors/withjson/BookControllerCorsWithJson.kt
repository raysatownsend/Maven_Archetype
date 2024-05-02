package com.supernova.integrationtests.controller.cors.withjson

import com.supernova.integrationtests.TestsConfig
import com.supernova.integrationtests.testcontainers.AbstractIntegrationTest
import com.supernova.integrationtests.vo.AccountCredentialsVO
import com.supernova.integrationtests.vo.BookVO
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
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerCorsWithJson() : AbstractIntegrationTest() {

	private lateinit var specification: RequestSpecification
	private lateinit var objectMapper: ObjectMapper
	private lateinit var book: BookVO
	private lateinit var token: String

	@BeforeAll
	fun setupTests(){
		objectMapper = ObjectMapper()
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		book = BookVO()
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
				.contentType(TestsConfig.CONTENT_TYPE_JSON)
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
		mockBook()

		specification = RequestSpecBuilder()
			.addHeader(
				TestsConfig.HEADER_PARAM_ORIGINS,
				TestsConfig.ORIGIN_ERUDIO
			)
			.addHeader(
				TestsConfig.HEADER_PARAM_AUTHORIZATION,
				"Bearer $token"
			)
				.setBasePath("/api/books/v1")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		val content = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.body(book)
				.`when`()
			.post()
			.then()
				.statusCode(200)
			.extract()
			.body()
				.asString()

		val createdBook = objectMapper.readValue(
			content,
			BookVO::class.java
		)

		book = createdBook

		assertNotNull(createdBook.id)
		assertNotNull(createdBook.author)
		assertNotNull(createdBook.title)
		assertNotNull(createdBook.price)
		assertNotNull(createdBook.launchDate)

		assertTrue(createdBook.id > 0)

		assertEquals("Clarice Lispector", createdBook.author)
		assertEquals("A hora da Estrela", createdBook.title)
		assertEquals(25.00, createdBook.price)
	}

	@Test
	@Order(2)
	fun testCreateWithWrongEndpoint() {
		mockBook()

		specification = RequestSpecBuilder()
			.addHeader(
				TestsConfig.HEADER_PARAM_ORIGINS,
				TestsConfig.ORIGIN_SEMERU
			)
			.addHeader(
				TestsConfig.HEADER_PARAM_AUTHORIZATION,
				"Bearer $token"
			)
				.setBasePath("/api/books/v1")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		val content = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.body(book)
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
		mockBook()

		specification = RequestSpecBuilder()
			.addHeader(
				TestsConfig.HEADER_PARAM_ORIGINS,
				TestsConfig.ORIGIN_LOCALHOST
			)
			.addHeader(
				TestsConfig.HEADER_PARAM_AUTHORIZATION,
				"Bearer $token"
			)
			.setBasePath("/api/books/v1/")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		val content = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.pathParam("id", book.id)
			.`when`() ["{id}"]
			.then()
			.statusCode(200)
			.extract()
			.body()
			.asString()

		val createdBook = objectMapper.readValue(
			content,
			BookVO::class.java
		)

		assertNotNull(createdBook.id)
		assertNotNull(createdBook.author)
		assertNotNull(createdBook.title)
		assertNotNull(createdBook.price)
		assertNotNull(createdBook.launchDate)

		assertTrue(createdBook.id > 0)

		assertEquals("Clarice Lispector", createdBook.author)
		assertEquals("A hora da Estrela", createdBook.title)
		assertEquals(25.00, createdBook.price)
	}

	@Test
	@Order(4)
	fun testFindByIdWithWrongEndpoint() {
		mockBook()

		specification = RequestSpecBuilder()
			.addHeader(
				TestsConfig.HEADER_PARAM_ORIGINS,
				TestsConfig.ORIGIN_SEMERU
			)
			.addHeader(
				TestsConfig.HEADER_PARAM_AUTHORIZATION,
				"Bearer $token"
			)
			.setBasePath("/api/books/v1/")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		val content = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.pathParam("id", book.id)
			.`when`() ["{id}"]
			.then()
			.statusCode(403)
			.extract()
			.body()
			.asString()

		assertEquals("Invalid CORS request", content)
	}

	@Test
	@Order(5)
	fun testDelete() {
		mockBook()

		specification = RequestSpecBuilder()
			.addHeader(
				TestsConfig.HEADER_PARAM_ORIGINS,
				TestsConfig.ORIGIN_LOCALHOST
			)
			.addHeader(
				TestsConfig.HEADER_PARAM_AUTHORIZATION,
				"Bearer $token"
			)
			.setBasePath("/api/books/v1/")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.pathParam("id", book.id)
			.`when`()
			.delete("{id}")
			.then()
			.statusCode(204)

	}

	private fun mockBook() {
		book.author = "Clarice Lispector"
		book.title = "A hora da Estrela"
		book.price = 25.00
		book.launchDate = Date()
	}
}
