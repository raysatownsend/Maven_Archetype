package com.supernova.repository

import com.supernova.model.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface UsersRepository: JpaRepository<Users?, Long?> {

    @Query("SELECT u FROM Users u WHERE u.userName = :userName")
    fun findByUsername(@Param("userName") userName: String?): Users?
}
