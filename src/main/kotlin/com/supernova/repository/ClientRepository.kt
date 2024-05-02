package com.supernova.repository

import com.supernova.model.Client
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface ClientRepository: JpaRepository<Client, Long?> {

    @Modifying
    @Query("UPDATE Client c SET c.enabled = false WHERE c.id=:id")
    fun disabledClient(@Param("id") id: Long?)

    @Query("SELECT c FROM Client c WHERE c.firstName LIKE LOWER(CONCAT('%', :firstName, '%'))")
    fun findClientByFirstName(@Param("firstName") firstName: String, pageable : Pageable) : Page<Client>
}
