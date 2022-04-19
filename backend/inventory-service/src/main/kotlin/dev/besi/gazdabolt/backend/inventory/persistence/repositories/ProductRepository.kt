package dev.besi.gazdabolt.backend.inventory.persistence.repositories

import dev.besi.gazdabolt.backend.inventory.persistence.entities.Product
import org.springframework.data.cassandra.repository.CassandraRepository
import java.util.UUID

interface ProductRepository : CassandraRepository<Product, UUID> {
	fun findByPluCode(pluCode: Int): Product?
	fun findByBarCode(barCode: Long): Product?
}