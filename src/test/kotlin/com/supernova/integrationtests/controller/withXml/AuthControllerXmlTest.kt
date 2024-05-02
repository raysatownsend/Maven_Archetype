package com.supernova.integrationtests.controller.withXml

import com.supernova.integrationtests.TestsConfig
import com.supernova.integrationtests.testcontainers.AbstractIntegrationTest
import com.supernova.integrationtests.vo.AccountCredentialsVO
import com.supernova.integrationtests.vo.TokenVO
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import jakarta.xml.bind.annotation.XmlRootElement
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@XmlRootElement
class AuthControllerXmlTest : AbstractIntegrationTest() {
    private lateinit var tokenVO: TokenVO

    @BeforeAll
    fun setupTests() {
        tokenVO = TokenVO()
    }

    @Test
    @Order(0)
    fun testLogin() {
        val user = AccountCredentialsVO(
            username = "leandro",
            password = "admin123"
        )

        tokenVO = RestAssured.given()
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

        assertNotNull(tokenVO.accessToken)
        assertNotNull(tokenVO.refreshToken)
    }

    @Test
    @Order(1)
    fun testRefreshToken() {
        tokenVO = given()
            .basePath("/auth/refresh")
                .port(TestsConfig.SERVER_PORT)
                .contentType(TestsConfig.CONTENT_TYPE_XML)
                .pathParam("username", tokenVO.username)
            .header(
                TestsConfig.HEADER_PARAM_AUTHORIZATION,
                "Bearer ${tokenVO.refreshToken}")
            .`when`()
                .put("{username}")
                    .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .`as`(TokenVO::class.java)
        assertNotNull(tokenVO.accessToken)
        assertNotNull(tokenVO.refreshToken)
    }
}
