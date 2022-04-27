package dev.besi.gazdabolt.backend.inventory.persistence.repositories

import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductRepository : MongoRepository<DbProduct, String> {
	fun findProductByPluCode(pluCode: Int): DbProduct?
	fun findProductByBarCode(barCode: Long): DbProduct?
}