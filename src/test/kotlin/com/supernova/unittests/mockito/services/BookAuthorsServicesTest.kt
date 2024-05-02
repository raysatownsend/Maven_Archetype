package com.supernova.unittests.mockito.services

import com.supernova.exceptions.RequiredObjectIsNullException
import com.supernova.repository.BookRepository
import com.supernova.services.BookAuthorsServices
import com.supernova.unittests.mocks.MockBook
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class BookAuthorsServicesTest {

    private lateinit var inputObject : MockBook

    @InjectMocks
    private lateinit var services : BookAuthorsServices

    @Mock
    private lateinit var repository : BookRepository

    @BeforeEach
    fun setUpMock() {
        inputObject = MockBook()
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun findById() {
        val book = inputObject.mockEntity(1)
        book.id = 1L
        `when`(repository.findById(1)).thenReturn(Optional.of(book))

        val result = services.findById(1)

        assertNotNull(result)
        assertNotNull(result.key)
        assertNotNull(result.links)
        assertTrue(result.links.toString().contains("/api/books/v1/1>;rel=\"self\""))
        assertEquals("Author Test1", result.author)
        assertEquals(25.00, result.price)
        assertEquals("Title Test1", result.title)
    }

    @Test
    fun create() {
        val entity = inputObject.mockEntity(1)

        val persisted = entity.copy()
        persisted.id = 1

        `when`(repository.save(entity)).thenReturn(persisted)

        val vo = inputObject.mockVO(1)
        val result = services.create(vo)

        assertNotNull(result)
        assertNotNull(result.key)
        assertNotNull(result.links)
        assertTrue(result.links.toString().contains("/api/books/v1/1>;rel=\"self\""))
        assertEquals("Author Test1", result.author)
        assertEquals(25.00, result.price)
        assertEquals("Title Test1", result.title)
    }

    @Test
    fun createWithNullBook() {
    val exception: Exception = assertThrows(
        RequiredObjectIsNullException::class.java
    ) {services.create(null)}
        val expectedMessage = "It's not allowed persist null objects"
        val actualMessage = exception.message
        assertTrue(actualMessage!!.contains(expectedMessage))
    }

    @Test
    fun update() {
        val entity = inputObject.mockEntity(1)

        val persisted = entity.copy()
        persisted.id = 1

        `when`(repository.findById(1)).thenReturn(Optional.of(entity))
        `when`(repository.save(entity)).thenReturn(persisted)

        val vo = inputObject.mockVO(1)
        val result = services.update(vo)

        assertNotNull(result)
        assertNotNull(result.key)
        assertNotNull(result.links)
        assertTrue(result.links.toString().contains("/api/books/v1/1>;rel=\"self\""))
        assertEquals("Author Test1", result.author)
        assertEquals(25.00, result.price)
        assertEquals("Title Test1", result.title)
    }

    @Test
    fun updateWithNullBook() {
        val exception: Exception = assertThrows(
            RequiredObjectIsNullException::class.java
        ) {services.update(null)}
        val expectedMessage = "It's not allowed persist null objects"
        val actualMessage = exception.message
        assertTrue(actualMessage!!.contains(expectedMessage))
    }

    @Test
    fun delete() {
        val entity = inputObject.mockEntity(1)
        `when`(repository.findById(1)).thenReturn(Optional.of(entity))
        services.delete(1)
    }

}
