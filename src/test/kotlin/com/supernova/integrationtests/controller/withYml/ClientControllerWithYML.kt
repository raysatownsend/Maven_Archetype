package com.supernova.integrationtests.controller.withYml

import com.supernova.integrationtests.TestsConfig
import com.supernova.integrationtests.controller.withYml.mapper.YMLMapper
import com.supernova.integrationtests.testcontainers.AbstractIntegrationTest
import com.supernova.integrationtests.vo.AccountCredentialsVO
import com.supernova.integrationtests.vo.ClientVO
import com.supernova.integrationtests.vo.TokenVO
import com.supernova.integrationtests.vo.wrappers.WrapperClientsVO
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.EncoderConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientControllerWithYML() : AbstractIntegrationTest() {

	private lateinit var specification: RequestSpecification
	private lateinit var objectMapper: YMLMapper
	private lateinit var client: ClientVO

	@BeforeAll
	fun setupTests(){
		objectMapper = YMLMapper()
		client = ClientVO()
	}

	@Test
	@Order(0)
	fun testLogin() {
		val user = AccountCredentialsVO(
			username = "leandro",
			password = "admin123"
		)

		val token = RestAssured.given()
			.config(
				RestAssuredConfig
					.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
							.encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
				.basePath("/auth/signin")
				.port(TestsConfig.SERVER_PORT)
				.contentType(TestsConfig.CONTENT_TYPE_YML)
				.body(user, objectMapper)
			.`when`()
				.post()
					.then()
						.statusCode(200)
							.extract()
			.body()
				.`as`(TokenVO::class.java, objectMapper)
					.accessToken

		specification = RequestSpecBuilder()
			.addHeader(TestsConfig.HEADER_PARAM_AUTHORIZATION, "Bearer $token")
				.setBasePath("/api/client/v1")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()
	}

	@Test
	@Order(1)
	fun testCreate() {
		mockClient()

		val item = given()
			.config(
				RestAssured
					.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
							.encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_YML)
			.body(client, objectMapper)
				.`when`()
			.post()
			.then()
				.statusCode(200)
			.extract()
			.body()
				.`as`(ClientVO::class.java, objectMapper)

		client = item

		assertNotNull(item.id)
		assertTrue(item.id > 0)
		assertNotNull(item.firstName)
		assertNotNull(item.lastName)
		assertNotNull(item.gender)
		assertNotNull(item.address)
		assertEquals("Marisa", item.firstName)
		assertEquals("Orth", item.lastName)
		assertEquals("Female", item.gender)
		assertEquals("São Paulo - SP", item.address)
	}

	@Test
	@Order(2)
	fun testUpdate() {
		client.lastName = "Orthy"

		val item = given()
			.config(
				RestAssured
					.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
							.encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_YML)
			.body(client, objectMapper)
				.`when`()
			.put()
			.then()
				.statusCode(200)
			.extract()
			.body()
			.`as`(ClientVO::class.java, objectMapper)

		client = item

		assertNotNull(item.id)
		assertNotNull(item.firstName)
		assertNotNull(item.lastName)
		assertNotNull(item.gender)
		assertNotNull(item.address)
		assertEquals(client.id, item.id)
		assertEquals("Marisa", item.firstName)
		assertEquals("Orthy", item.lastName)
		assertEquals("Female", item.gender)
		assertEquals("São Paulo - SP", item.address)
		assertEquals(true, item.enabled)
	}

	@Test
	@Order(3)
	fun testFindById() {

		val item = given()
			.config(
				RestAssured
					.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
							.encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_YML)
			.pathParam("id", client.id)
			.`when`()
			.get("{id}")
			.then()
			.statusCode(200)
			.extract()
			.body()
			.`as`(ClientVO::class.java, objectMapper)

		client = item

		assertNotNull(item.id)
		assertNotNull(item.firstName)
		assertNotNull(item.lastName)
		assertNotNull(item.gender)
		assertNotNull(item.address)

		assertTrue(item.id > 0)

		assertEquals("Marisa", item.firstName)
		assertEquals("Orthy", item.lastName)
		assertEquals("Female", item.gender)
		assertEquals("São Paulo - SP", item.address)
		assertEquals(true, item.enabled)
	}

	@Test
	@Order(3)
	fun testDisablingClient() {

		val item = given()
			.config(
				RestAssured
					.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
							.encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_YML)
			.pathParam("id", client.id)
			.`when`()
			.patch("{id}")
			.then()
			.statusCode(200)
			.extract()
			.body()
			.`as`(ClientVO::class.java, objectMapper)

		client = item

		assertNotNull(item.id)
		assertNotNull(item.firstName)
		assertNotNull(item.lastName)
		assertNotNull(item.gender)
		assertNotNull(item.address)

		assertTrue(item.id > 0)

		assertEquals("Marisa", item.firstName)
		assertEquals("Orthy", item.lastName)
		assertEquals("Female", item.gender)
		assertEquals("São Paulo - SP", item.address)
		assertEquals(false, item.enabled)
	}

	@Test
	@Order(4)
	fun testDelete() {

		given()
			.config(
				RestAssured
					.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
							.encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
			.spec(specification)
			.pathParam("id", client.id)
			.`when`()
			.delete("{id}")
			.then()
			.statusCode(204)

	}

	@Test
	@Order(5)
	fun testFindAll() {
		val wrapper = given()
			.config(
				RestAssured
					.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
							.encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_YML)
			.queryParams("page", 1,
				"size", 12,
				"direction", "asc")
			.`when`()
			.get()
			.then()
			.statusCode(200)
			.extract()
			.body()
			.`as`(WrapperClientsVO::class.java, objectMapper)

		val clients = wrapper.embedded!!.clients

		val item1 = clients?.get(0)
		
		assertNotNull(item1!!.id)
		assertNotNull(item1.firstName)
		assertNotNull(item1.lastName)
		assertNotNull(item1.gender)
		assertNotNull(item1.address)
		assertEquals("Aileen", item1.firstName)
		assertEquals("Pidwell", item1.lastName)
		assertEquals("Female", item1.gender)
		assertEquals("6322 2nd Road", item1.address)
		assertEquals(false, item1.enabled)

		val item2 = clients[6]
		assertNotNull(item2.id)
		assertNotNull(item2.firstName)
		assertNotNull(item2.lastName)
		assertNotNull(item2.gender)
		assertNotNull(item2.address)
		assertEquals("Alberik", item2.firstName)
		assertEquals("Norcott", item2.lastName)
		assertEquals("Male", item2.gender)
		assertEquals("330 Hooker Road", item2.address)
		assertEquals(false, item2.enabled)
	}

	@Test
	@Order(6)
	fun testFindByName() {
		val wrapper = given()
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_JSON)
			.pathParam("firstName", "esc")
			.queryParams(
				"page", 0,
				"size", 12,
				"direction", "asc")
			.`when`()["findClientByName/{firstName}"]
			.then()
			.statusCode(200)
			.extract()
			.`as`(WrapperClientsVO::class.java, objectMapper)

		val clients = wrapper.embedded!!.clients

		val item1 = clients?.get(0)

		assertNotNull(item1!!.id)
		assertNotNull(item1.firstName)
		assertNotNull(item1.lastName)
		assertNotNull(item1.gender)
		assertNotNull(item1.address)
		assertEquals("Francesco", item1.firstName)
		assertEquals("MacAllaster", item1.lastName)
		assertEquals("Male", item1.gender)
		assertEquals("5 Golf View Hill", item1.address)
		assertEquals(true, item1.enabled)
	}

	@Test
	@Order(7)
	fun testFindAllClientsWithoutToken() {

		val specificationWithoutToken: RequestSpecification = RequestSpecBuilder()
			.setBasePath("/api/client/v1")
			.setPort(TestsConfig.SERVER_PORT)
			.addFilter(RequestLoggingFilter(LogDetail.ALL))
			.addFilter(ResponseLoggingFilter(LogDetail.ALL))
			.build()

		given()
			.config(
				RestAssured
					.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
							.encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
			.spec(specificationWithoutToken)
			.contentType(TestsConfig.CONTENT_TYPE_YML)
			.`when`()
			.get()
			.then()
			.statusCode(403)
			.extract()
			.body()
			.`as`(ClientVO::class.java, objectMapper)
	}

	@Test
	@Order(8)
	fun testHATEOAS() {
		val content = given()
			.config(
				RestAssured
					.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
							.encodeContentTypeAs(TestsConfig.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
			.spec(specification)
			.contentType(TestsConfig.CONTENT_TYPE_YML)
			.queryParams("page", 1,
				"size", 24,
				"direction", "asc")
			.`when`()
			.get()
			.then()
			.statusCode(200)
			.extract()
			.body()
			.asString()

		assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8036/api/client/v1/472"}}}"""))
		assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8036/api/client/v1/874"}}}"""))
		assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8036/api/client/v1/136"}}}"""))
		assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8036/api/client/v1/850"}}}"""))

		assertTrue(content.contains(""","self":{"href":"http://localhost:8036/api/client/v1?direction=asc&page=1&size=24&sort=firstName,asc"}"""))
		assertTrue(content.contains(""","next":{"href":"http://localhost:8036/api/client/v1?direction=asc&page=2&size=24&sort=firstName,asc"}"""))
		assertTrue(content.contains(""","last":{"href":"http://localhost:8036/api/client/v1?direction=asc&page=41&size=24&sort=firstName,asc"}}"""))
		assertTrue(content.contains(""","page":{"size":24,"totalElements":1008,"totalPages":42,"number":1}}"""))
	}

		private fun mockClient() {
		client.firstName = "Marisa"
		client.lastName = "Orth"
		client.gender = "Female"
		client.address = "São Paulo - SP"
		client.enabled = true
	}
}
