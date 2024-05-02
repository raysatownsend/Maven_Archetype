package com.supernova.integrationtests.controller.withJson

import com.supernova.integrationtests.TestsConfig
import com.supernova.integrationtests.testcontainers.AbstractIntegrationTest
import com.supernova.integrationtests.vo.AccountCredentialsVO
import com.supernova.integrationtests.vo.BookVO
import com.supernova.integrationtests.vo.TokenVO
import com.supernova.integrationtests.vo.wrappers.WrapperBooksVO
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
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
class BookControllerWithJson : AbstractIntegrationTest() {

	private lateinit var specification: RequestSpecification
	private lateinit var objectMapper: ObjectMapper
	private lateinit var book: BookVO

	@BeforeAll
	fun setupTests(){
		objectMapper = ObjectMapper()
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		book = BookVO()
	}

	@Test
	@Order(0)
	fun testLogin() {
		val user = AccountCredentialsVO(
			username = "leandro",
			password = "admin123"
		)

		val token = RestAssured.given()
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
				.accessToken

		specification = RequestSpecBuilder()
			.addHeader(TestsConfig.HEADER_PARAM_AUTHORIZATION, "Bearer $token")
				.setBasePath("/api/books/v1")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()
	}

	@Test
	@Order(1)
	@Throws(JsonProcessingException::class, JsonProcessingException::class)
	fun testCreate() {
		mockBook()

		val content: String = given()
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

		book = objectMapper.readValue(
			content,
			BookVO::class.java
		)

		assertNotNull(book.id)
		assertNotNull(book.author)
		assertNotNull(book.title)
		assertNotNull(book.price)
		assertTrue(book.id > 0)
		assertEquals("Clarice Lispector", book.author)
		assertEquals("A hora da Estrela", book.title)
		assertEquals(25.00, book.price)
	}

	@Test
	@Order(2)
	@Throws(JsonMappingException::class, JsonProcessingException::class)
	fun testUpdate() {
		book.author = "Clarice Lispector -"

		val content: String = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.body(book)
				.`when`()
			.put()
			.then()
				.statusCode(200)
			.extract()
			.body()
				.asString()

		val updatedBook: BookVO = objectMapper.readValue(content, BookVO::class.java)

		assertNotNull(updatedBook.id)
		assertNotNull(updatedBook.author)
		assertNotNull(updatedBook.title)
		assertNotNull(updatedBook.price)
		assertEquals(updatedBook.id, book.id)
		assertEquals("Clarice Lispector -", updatedBook.author)
		assertEquals("A hora da Estrela", updatedBook.title)
		assertEquals(25.00, updatedBook.price)
	}

	@Test
	@Order(3)
	@Throws(JsonMappingException::class, JsonProcessingException::class)
	fun testFindById() {
		val content: String = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.pathParam("id", book.id)
			.`when`()
			.get("{id}")
			.then()
			.statusCode(200)
			.extract()
			.body()
			.asString()

		val foundBook: BookVO = objectMapper.readValue(
			content,
			BookVO::class.java
		)

		assertNotNull(foundBook.id)
		assertNotNull(foundBook.author)
		assertNotNull(foundBook.title)
		assertNotNull(foundBook.price)
		assertEquals(foundBook.id, book.id)
		assertEquals("Clarice Lispector -", foundBook.author)
		assertEquals("A hora da Estrela", foundBook.title)
		assertEquals(25.00, foundBook.price)
	}

	@Test
	@Order(4)
	fun testDelete() {
		given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.pathParam("id", book.id)
			.`when`()
			.delete("{id}")
			.then()
			.statusCode(204)
	}

	@Test
	@Order(5)
	@Throws(JsonMappingException::class, JsonProcessingException::class)
	fun testFindAll() {
		val strContent = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.queryParams("page", 0,
				"size", 12,
				"direction", "asc")
			.`when`()
			.get()
			.then()
			.statusCode(200)
			.extract()
			.body()
			.asString()

		val wrapper = objectMapper.readValue(strContent, WrapperBooksVO::class.java)
		val books = wrapper.embedded!!.books

		val book1 = books?.get(0)

		assertNotNull(book1!!.id)
		assertNotNull(book1.author)
		assertNotNull(book1.title)
		assertNotNull(book1.price)
		assertTrue(book1.id > 0)
		assertEquals("MViktor Mayer-Schonberger e Kenneth Kukier", book1.author)
		assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", book1.title)
		assertEquals(54.00, book1.price)

		val book2 = books[4]
		assertNotNull(book2.id)
		assertNotNull(book2.author)
		assertNotNull(book2.title)
		assertNotNull(book2.price)
		assertTrue(book1.id > 0)
		assertEquals("Eric Evans", book2.author)
		assertEquals("Domain Driven Design", book2.title)
		assertEquals(92.00, book2.price)
	}

	@Test
	@Order(6)
	fun testFindAllBooksWithoutToken() {

		val specificationWithoutToken: RequestSpecification = RequestSpecBuilder()
			.setBasePath("/api/books/v1")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		given()
			.spec(specificationWithoutToken)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.`when`()
			.get()
			.then()
			.statusCode(403)
			.extract()
			.body()
			.asString()
	}

	@Test
	@Order(9)
	fun testHATEOAS() {
		val content = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.queryParams("page", 0,
				"size", 12,
				"direction", "asc")
			.`when`()
			.get()
			.then()
			.statusCode(200)
			.extract()
			.body()
			.asString()

		assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8036/api/books/v1/12"}}}"""))
		assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8036/api/books/v1/3"}}}"""))
		assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8036/api/books/v1/5"}}}"""))

		assertTrue(content.contains(""""self":{"href":"http://localhost:8036/api/books/v1?direction=asc&page=0&size=12&sort=title,asc"}"""))
		assertTrue(content.contains(""","next":{"href":"http://localhost:8036/api/books/v1?direction=asc&page=1&size=12&sort=title,asc"}"""))
		assertTrue(content.contains(""""page":{"size":12,"totalElements":15,"totalPages":2,"number":0}}"""))

	}

		private fun mockBook() {
			book.author = "Clarice Lispector"
			book.title = "A hora da Estrela"
			book.price = 25.00
			book.launchDate = Date()
	}
}
