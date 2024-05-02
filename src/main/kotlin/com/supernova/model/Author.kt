package com.supernova.model

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "books")
data class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "author", nullable = false, length = 180)
    var author: String = "",

    @Column(name = "launch_date", nullable = true)
    var launchDate: Date? = null,

    @Column(name = "price", nullable = false)
    var price: Double = 00.00,

    @Column(name = "title", nullable = false, length = 150)
    var title: String = ""
)
