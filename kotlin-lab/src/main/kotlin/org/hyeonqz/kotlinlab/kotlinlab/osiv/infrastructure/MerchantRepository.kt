package org.hyeonqz.kotlinlab.kotlinlab.osiv.infrastructure

import org.hyeonqz.kotlinlab.kotlinlab.osiv.domain.Merchant
import org.springframework.data.jpa.repository.JpaRepository

interface MerchantRepository: JpaRepository<Merchant, Long> {
}