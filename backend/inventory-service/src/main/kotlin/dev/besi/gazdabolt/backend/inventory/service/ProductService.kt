package dev.besi.gazdabolt.backend.inventory.service

import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import dev.besi.gazdabolt.backend.inventory.persistence.repositories.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(
	@Autowired val repository: ProductRepository
) {

	fun listAllProducts(): Collection<Product> = repository.findAll().map(DbProduct::toDomainPojo)

	fun findProductById(id: String): Product? = repository.findByIdOrNull(id)?.toDomainPojo()

	fun findProductByPluCode(pluCode: Int): Product? = repository.findProductByPluCode(pluCode)?.toDomainPojo()

	fun findProductByBarCode(barCode: Long): Product? = repository.findProductByBarCode(barCode)?.toDomainPojo()

	fun createProduct(product: Product): Product = repository.save(product.toDbProduct()).toDomainPojo()

}