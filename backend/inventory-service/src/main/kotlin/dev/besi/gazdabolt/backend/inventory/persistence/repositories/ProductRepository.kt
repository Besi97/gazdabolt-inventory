package dev.besi.gazdabolt.backend.inventory.persistence.repositories

import dev.besi.gazdabolt.backend.inventory.persistence.entities.Product
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductRepository : MongoRepository<Product, String> {
	fun findProductByPluCode(pluCode: Int): Product?
	fun findProductByBarCode(barCode: Long): Product?
}