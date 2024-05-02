package com.supernova.integrationtests.repository

import com.supernova.integrationtests.testcontainers.AbstractIntegrationTest
import com.supernova.model.Client
import com.supernova.repository.ClientRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientRepositoryTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var repository: ClientRepository
    private lateinit var client: Client

    @BeforeAll
    fun setup() {
        client = Client()
    }

    @Test
    @Order(1)
    fun testFindByName(){
        val pageable: Pageable = PageRequest.of(0, 12, Sort.Direction.ASC, "firstName")
        client = repository.findClientByFirstName("esc", pageable).content[0]
        assertNotNull(client.id)
        assertNotNull(client.firstName)
        assertNotNull(client.lastName)
        assertNotNull(client.gender)
        assertNotNull(client.address)
        assertEquals("Francesco", client.firstName)
        assertEquals("MacAllaster", client.lastName)
        assertEquals("Male", client.gender)
        assertEquals("5 Golf View Hill", client.address)
        assertEquals(true, client.enabled)
    }

    @Test
    @Order(2)
    fun testDisableClient(){
        repository.disabledClient(client.id)
        client = repository.findById(client.id).get()

        assertNotNull(client)
        assertNotNull(client.id)
        assertNotNull(client.firstName)
        assertNotNull(client.lastName)
        assertNotNull(client.address)
        assertNotNull(client.gender)
        assertEquals("Francesco", client.firstName)
        assertEquals("MacAllaster", client.lastName)
        assertEquals("Male", client.gender)
        assertEquals("5 Golf View Hill", client.address)
        assertEquals(false, client.enabled)
    }
}
