package org.hyeonqz.kotlinlab.kotlinlab.osiv.domain

import jakarta.persistence.*

@Entity
@Table(name = "merchant")
class Merchant(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    val code: String
)