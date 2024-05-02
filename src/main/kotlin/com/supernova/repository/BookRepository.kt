package com.supernova.repository

import com.supernova.model.Author
import org.springframework.data.jpa.repository.JpaRepository


interface BookRepository: JpaRepository<Author, Long?>
