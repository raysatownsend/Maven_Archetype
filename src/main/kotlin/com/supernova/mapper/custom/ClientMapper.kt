package com.supernova.mapper.custom

import com.supernova.data.vo.v2.ClientVO
import com.supernova.model.Client
import org.springframework.stereotype.Service
import java.util.*

@Service
class ClientMapper {

    fun mapEntityToVO(client : Client): ClientVO {
        val vo = ClientVO()

        vo.id = client.id
        vo.firstName = client.firstName
        vo.lastName = client.lastName
        vo.address = client.address
        vo.gender = client.gender
        vo.birthDay = Date()

        return vo
    }
    fun mapVOToEntity(client : ClientVO): Client {
        val entity = Client()

        entity.id = client.id
        entity.firstName = client.firstName
        entity.lastName = client.lastName
        entity.address = client.address
        entity.gender = client.gender
        //entity.birthDay = Date()

        return entity
    }

}
