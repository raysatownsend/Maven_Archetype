package com.supernova.services

import com.supernova.controller.ClientController
import com.supernova.data.vo.v1.ClientVO
import com.supernova.exceptions.RequiredObjectIsNullException
import com.supernova.exceptions.ResourceNotFoundException
import com.supernova.mapper.DozerMapper
import com.supernova.model.Client
import com.supernova.repository.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.logging.Logger

@Service
class ClientServices {

    @Autowired
    private lateinit var repository : ClientRepository

    @Autowired
    private lateinit var assembler : PagedResourcesAssembler<ClientVO>

    private val logger = Logger.getLogger(ClientServices::class.java.name)

    fun findAll(pageable : Pageable): PagedModel<EntityModel<ClientVO>> {

        logger.info("finding all clients...")

        val clients = repository.findAll(pageable)
        val clientVOS = clients.map { c -> DozerMapper.parseObject(c, ClientVO::class.java) }
        clientVOS.map { c -> c.add(linkTo(ClientController::class.java).slash(c.key).withSelfRel())}
        return assembler.toModel(clientVOS)
    }

    fun findClientByFirstName(firstName: String, pageable : Pageable): PagedModel<EntityModel<ClientVO>> {

        logger.info("finding a client by First Name...")

        val clients = repository.findClientByFirstName(firstName, pageable)
        val clientVOS = clients.map { c -> DozerMapper.parseObject(c, ClientVO::class.java) }
        clientVOS.map { c -> c.add(linkTo(ClientController::class.java).slash(c.key).withSelfRel())}
        return assembler.toModel(clientVOS)
    }

    fun findById(id:Long): ClientVO {
        logger.info("finding a client with de id $id for you...")
        val client = repository.findById(id)
            .orElseThrow {ResourceNotFoundException("ID not founded!")}
        val clientVO: ClientVO = DozerMapper.parseObject(client, ClientVO::class.java)
        val withSelfRel = linkTo(ClientController::class.java).slash(clientVO.key).withSelfRel()
        clientVO.add(withSelfRel)
        return clientVO
    }

    fun create(client : ClientVO?): ClientVO {
        if (client == null) throw RequiredObjectIsNullException()
      logger.info("creating a client named ${client.firstName}...")
        var entity: Client = DozerMapper.parseObject(client, Client::class.java)
        val clientVO: ClientVO = DozerMapper.parseObject(repository.save(entity), ClientVO::class.java)
        val withSelfRel = linkTo(ClientController::class.java).slash(clientVO.key).withSelfRel()
        clientVO.add(withSelfRel)
        return clientVO
    }

    fun update(client : ClientVO?): ClientVO {
        if (client == null) throw RequiredObjectIsNullException()
        logger.info("updating the information of a client: id ${client.key} name ${client.firstName}...")
        val entity = repository.findById(client.key)
            .orElseThrow {ResourceNotFoundException("ID not founded!")}

        entity.firstName = client.firstName
        entity.lastName = client.lastName
        entity.address = client.address
        entity.gender = client.gender

        val clientVO: ClientVO = DozerMapper.parseObject(repository.save(entity), ClientVO::class.java)
        val withSelfRel = linkTo(ClientController::class.java).slash(clientVO.key).withSelfRel()
        clientVO.add(withSelfRel)
        return clientVO
    }

    @Transactional
    fun disabledClient(id:Long): ClientVO {
        logger.info("Disabling a client with de id $id for you...")
        repository.disabledClient(id)
        val client = repository.findById(id)
            .orElseThrow {ResourceNotFoundException("ID not founded!")}
        val clientVO: ClientVO = DozerMapper.parseObject(client, ClientVO::class.java)
        val withSelfRel = linkTo(ClientController::class.java).slash(clientVO.key).withSelfRel()
        clientVO.add(withSelfRel)
        return clientVO
    }

    fun delete(id: Long) {
        logger.info("deleting a client by ID $id for you...")
        val entity = repository.findById(id)
            .orElseThrow {ResourceNotFoundException("ID not founded!")}
        repository.delete(entity)
    }
}
