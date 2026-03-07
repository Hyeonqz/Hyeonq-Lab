package org.hyeonqz.kotlinlab.kotlinlab.osiv.domain

import jakarta.persistence.*

@Entity
@Table(name = "orders")
class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val amount: Long,

    val status: String = "PENDING",

    // Lazy 연관 — OSIV 테스트의 핵심 포인트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    val merchant: Merchant,

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val items: MutableList<OrderItem> = mutableListOf()
)