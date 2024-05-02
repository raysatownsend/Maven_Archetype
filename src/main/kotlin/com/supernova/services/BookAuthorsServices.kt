package com.supernova.services

import com.supernova.controller.AuthorController
import com.supernova.data.vo.v1.AuthorVO
import com.supernova.exceptions.RequiredObjectIsNullException
import com.supernova.exceptions.ResourceNotFoundException
import com.supernova.mapper.DozerMapper
import com.supernova.mapper.custom.AuthorMapper
import com.supernova.model.Author
import com.supernova.repository.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class BookAuthorsServices {

    @Autowired
    private lateinit var repository : BookRepository

    @Autowired
    private lateinit var assembler: PagedResourcesAssembler<AuthorVO>

    private val logger = Logger.getLogger(BookAuthorsServices::class.java.name)

    fun findAll(pageable : Pageable): PagedModel<EntityModel<AuthorVO>> {
        logger.info("finding all books for you...")
        val page = repository.findAll(pageable)
        val authorVOS = page.map { p -> DozerMapper.parseObject(p, AuthorVO::class.java)}
        authorVOS.map { p -> p.add(linkTo(AuthorController::class.java).slash(p.key).withSelfRel()) }
        return assembler.toModel(authorVOS)
    }

    fun findById(id:Long): AuthorVO {
        logger.info("finding a book with de id $id for you...")
        val author = repository.findById(id)
            .orElseThrow {ResourceNotFoundException("ID not founded!")}
        val authorVO: AuthorVO = DozerMapper.parseObject(author, AuthorVO::class.java)
        val withSelfRel = linkTo(AuthorController::class.java).slash(authorVO.key).withSelfRel()
        authorVO.add(withSelfRel)
        return authorVO
    }

    fun create(author : AuthorVO?): AuthorVO {
        if (author == null) throw RequiredObjectIsNullException()
      logger.info("creating a book named ${author.title}...")
      var entity: Author = DozerMapper.parseObject(author, Author::class.java)
        val authorVO: AuthorVO = DozerMapper.parseObject(repository.save(entity), AuthorVO::class.java)
        val withSelfRel = linkTo(AuthorController::class.java).slash(authorVO.key).withSelfRel()
        authorVO.add(withSelfRel)
        return authorVO
    }

    fun update(author : AuthorVO?): AuthorVO {
        if (author == null) throw RequiredObjectIsNullException()
        logger.info("updating the information of the book named: id ${author.title} wrote by ${author.author}...")
        val entity = repository.findById(author.key)
            .orElseThrow {ResourceNotFoundException("ID not founded!")}

        entity.author = author.author
        entity.launchDate = author.launchDate
        entity.price = author.price
        entity.title = author.title

        val authorVO: AuthorVO = DozerMapper.parseObject(repository.save(entity), AuthorVO::class.java)
        val withSelfRel = linkTo(AuthorController::class.java).slash(authorVO.key).withSelfRel()
        authorVO.add(withSelfRel)
        return authorVO
    }

    fun delete(id: Long) {
        logger.info("deleting the book by ID $id for you...")
        val entity = repository.findById(id)
            .orElseThrow {ResourceNotFoundException("ID not founded!")}
        repository.delete(entity)
    }
}
