package dev.besi.gazdabolt.backend.inventory.service

import dev.besi.gazdabolt.backend.inventory.config.InventoryServiceProperties
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import dev.besi.gazdabolt.backend.inventory.persistence.repositories.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(
	@Autowired val repository: ProductRepository,
	@Autowired val config: InventoryServiceProperties
) {

	fun listAllProducts(): Collection<Product> = repository.findAll().map(DbProduct::toDomainPojo)

	fun findProductById(id: String): Product? = repository.findByIdOrNull(id)?.toDomainPojo()

	fun findProductByPluCode(pluCode: Int): Product? = repository.findProductByPluCode(pluCode)?.toDomainPojo()

	fun findProductByBarCode(barCode: Long): Product? = repository.findProductByBarCode(barCode)?.toDomainPojo()

	fun createProduct(product: Product): Product = repository.save(product.toDbProduct()).toDomainPojo()

	@Throws(IllegalArgumentException::class)
	fun incrementProduct(id: String, amount: Int): Product {
		val product = repository.findByIdOrNull(id)
		requireNotNull(product) { "Could not find product by ID: $id" }

		product.stock += amount
		repository.save(product)

		return product.toDomainPojo()
	}

	@Throws(IllegalArgumentException::class)
	fun decrementProduct(id: String, amount: Int): Product {
		val product = repository.findByIdOrNull(id)
		requireNotNull(product) { "Could not find product by ID: $id" }

		try {
			product.stock -= amount
		} catch (e: IllegalArgumentException) {
			if (config.failOnInsufficientResources) {
				throw e
			} else {
				product.stock = 0
			}
		}
		repository.save(product)

		return product.toDomainPojo()
	}

}