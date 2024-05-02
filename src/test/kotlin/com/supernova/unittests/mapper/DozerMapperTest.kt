package com.supernova.unittests.mapper

import com.supernova.data.vo.v1.ClientVO
import com.supernova.mapper.DozerMapper
import com.supernova.model.Client
import com.supernova.unittests.mocks.MockClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DozerMapperTest {

    private var inputObject: MockClient? = null

    @BeforeEach
    fun setUp() {
        inputObject = MockClient()
    }

    @Test
    fun parseEntityToVOTest() {
        val output: ClientVO = DozerMapper.parseObject(inputObject!!.mockEntity(), ClientVO::class.java)
        assertEquals(0, output.key)
        assertEquals("First Name Test0", output.firstName)
        assertEquals("Last Name Test0", output.lastName)
        assertEquals("Address Test0", output.address)
        assertEquals("Male", output.gender)
    }

    @Test
    fun parseEntityListToVOListTest() {
        val outputList: ArrayList<ClientVO> =
            DozerMapper.parseListObjects(inputObject!!.mockEntityList(), ClientVO::class.java)

        val outputZero: ClientVO = outputList[0]
        assertEquals(0, outputZero.key)
        assertEquals("First Name Test0", outputZero.firstName)
        assertEquals("Last Name Test0", outputZero.lastName)
        assertEquals("Address Test0", outputZero.address)
        assertEquals("Male", outputZero.gender)

        val outputSeven: ClientVO = outputList[7]
        assertEquals(7.toLong(), outputSeven.key)
        assertEquals("First Name Test7", outputSeven.firstName)
        assertEquals("Last Name Test7", outputSeven.lastName)
        assertEquals("Address Test7", outputSeven.address)
        assertEquals("Female", outputSeven.gender)

        val outputTwelve: ClientVO = outputList[12]
        assertEquals(12.toLong(), outputTwelve.key)
        assertEquals("First Name Test12", outputTwelve.firstName)
        assertEquals("Last Name Test12", outputTwelve.lastName)
        assertEquals("Address Test12", outputTwelve.address)
        assertEquals("Male", outputTwelve.gender)
    }

    @Test
    fun parseVOToEntityTest() {

        val output: Client = DozerMapper.parseObject(inputObject!!.mockVO(), Client::class.java)

        assertEquals(0, output.id)
        assertEquals("First Name Test0", output.firstName)
        assertEquals("Last Name Test0", output.lastName)
        assertEquals("Address Test0", output.address)
        assertEquals("Male", output.gender)
    }

    @Test
    fun parserVOListToEntityListTest() {

        val outputList: ArrayList<Client> = DozerMapper.parseListObjects(inputObject!!.mockVOList(), Client::class.java)

        val outputZero: Client = outputList[0]
        assertEquals(0, outputZero.id)
        assertEquals("First Name Test0", outputZero.firstName)
        assertEquals("Last Name Test0", outputZero.lastName)
        assertEquals("Address Test0", outputZero.address)
        assertEquals("Male", outputZero.gender)

        val outputSeven: Client = outputList[7]
        assertEquals(7, outputSeven.id)
        assertEquals("First Name Test7", outputSeven.firstName)
        assertEquals("Last Name Test7", outputSeven.lastName)
        assertEquals("Address Test7", outputSeven.address)
        assertEquals("Female", outputSeven.gender)

        val outputTwelve: Client = outputList[12]
        assertEquals(12, outputTwelve.id)
        assertEquals("First Name Test12", outputTwelve.firstName)
        assertEquals("Last Name Test12", outputTwelve.lastName)
        assertEquals("Address Test12", outputTwelve.address)
        assertEquals("Male", outputTwelve.gender)
    }
}
