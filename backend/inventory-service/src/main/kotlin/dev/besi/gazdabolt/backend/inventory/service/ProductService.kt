package dev.besi.gazdabolt.backend.inventory.service

import dev.besi.gazdabolt.backend.inventory.config.InventoryServiceProperties
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import dev.besi.gazdabolt.backend.inventory.persistence.repositories.ProductRepository
import dev.besi.gazdabolt.backend.inventory.web.ApiProductInput
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
		val product = repository.findByIdRequireNotNull(id)

		product.stock += amount
		repository.save(product)

		return product.toDomainPojo()
	}

	@Throws(IllegalArgumentException::class)
	fun decrementProduct(id: String, amount: Int): Product {
		val product = repository.findByIdRequireNotNull(id)

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

	@Throws(IllegalArgumentException::class)
	fun updateProduct(id: String, updated: ApiProductInput): Product {
		val product = repository.findByIdRequireNotNull(id)

		product.name = updated.name
		product.pluCode = updated.pluCode
		product.barCode = updated.barCode
		product.description = updated.description
		product.price = updated.price.toDouble()
		repository.save(product)

		return product.toDomainPojo()
	}

	@Throws(IllegalArgumentException::class)
	private fun ProductRepository.findByIdRequireNotNull(id: String): DbProduct =
		findByIdOrNull(id) ?: throw IllegalArgumentException("Could not find product by ID: $id")

}