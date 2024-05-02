package com.supernova.unittests.mocks

import com.supernova.data.vo.v1.ClientVO
import com.supernova.model.Client

class MockClient {
    fun mockEntity(): Client {
        return mockEntity(0)
    }

    fun mockVO(): ClientVO {
        return mockVO(0)
    }

    fun mockEntityList(): ArrayList<Client> {
        val clients: ArrayList<Client> = ArrayList()
        for (i in 0..13) {
            clients.add(mockEntity(i))
        }
        return clients
    }

    fun mockVOList(): ArrayList<ClientVO> {
        val clients: ArrayList<ClientVO> = ArrayList()
        for (i in 0..13) {
            clients.add(mockVO(i))
        }
        return clients
    }

    fun mockEntity(number: Int): Client {
        val client = Client()
        client.address = "Address Test$number"
        client.firstName = "First Name Test$number"
        client.gender = if (number % 2 == 0) "Male" else "Female"
        client.id = number.toLong()
        client.lastName = "Last Name Test$number"
        return client
    }

    fun mockVO(number: Int): ClientVO {
        val client = ClientVO()
        client.address = "Address Test$number"
        client.firstName = "First Name Test$number"
        client.gender = if (number % 2 == 0) "Male" else "Female"
        client.key = number.toLong()
        client.lastName = "Last Name Test$number"
        return client
    }
}
