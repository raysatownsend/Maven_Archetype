package com.supernova.unittests.mapper

import com.supernova.data.vo.v1.AuthorVO
import com.supernova.mapper.DozerMapper
import com.supernova.model.Author
import com.supernova.unittests.mocks.MockBook
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DozerMapperBookTest {

    private var inputObject: MockBook? = null

    @BeforeEach
    fun setUp() {
        inputObject = MockBook()
    }

    @Test
    fun parseEntityToVOTest() {
        val output: AuthorVO = DozerMapper.parseObject(inputObject!!.mockEntity(), AuthorVO::class.java)
        assertEquals(0, output.key)
        assertEquals("Author Test0", output.author)
        assertEquals(25.00, output.price)
        assertEquals("Title Test0", output.title)
    }

    @Test
    fun parseEntityListToVOListTest() {
        val outputList: ArrayList<AuthorVO> =
            DozerMapper.parseListObjects(inputObject!!.mockEntityList(), AuthorVO::class.java)

        val outputZero: AuthorVO = outputList[0]
        assertEquals(0, outputZero.key)
        assertEquals("Author Test0", outputZero.author)
        assertEquals(25.00, outputZero.price)
        assertEquals("Title Test0", outputZero.title)

        val outputSeven: AuthorVO = outputList[7]
        assertEquals(7.toLong(), outputSeven.key)
        assertEquals("Author Test7", outputSeven.author)
        assertEquals(25.00, outputSeven.price)
        assertEquals("Title Test7", outputSeven.title)

        val outputTwelve: AuthorVO = outputList[12]
        assertEquals(12.toLong(), outputTwelve.key)
        assertEquals("Author Test12", outputTwelve.author)
        assertEquals(25.00, outputTwelve.price)
        assertEquals("Title Test12", outputTwelve.title)
    }

    @Test
    fun parseVOToEntityTest() {

        val output: Author = DozerMapper.parseObject(inputObject!!.mockVO(), Author::class.java)

        assertEquals(0, output.id)
        assertEquals("Author Test0", output.author)
        assertEquals(25.00, output.price)
        assertEquals("Title Test0", output.title)
    }

    @Test
    fun parserVOListToEntityListTest() {

        val outputList: ArrayList<Author> = DozerMapper.parseListObjects(inputObject!!.mockVOList(), Author::class.java)

        val outputZero: Author = outputList[0]
        assertEquals(0, outputZero.id)
        assertEquals("Author Test0", outputZero.author)
        assertEquals(25.00, outputZero.price)
        assertEquals("Title Test0", outputZero.title)

        val outputSeven: Author = outputList[7]
        assertEquals(7, outputSeven.id)
        assertEquals("Author Test7", outputSeven.author)
        assertEquals(25.00, outputSeven.price)
        assertEquals("Title Test7", outputSeven.title)

        val outputTwelve: Author = outputList[12]
        assertEquals(12, outputTwelve.id)
        assertEquals("Author Test12", outputTwelve.author)
        assertEquals(25.00, outputTwelve.price)
        assertEquals("Title Test12", outputTwelve.title)
    }
}
