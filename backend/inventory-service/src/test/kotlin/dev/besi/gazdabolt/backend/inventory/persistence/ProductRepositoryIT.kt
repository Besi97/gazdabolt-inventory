package dev.besi.gazdabolt.backend.inventory.persistence

import dev.besi.gazdabolt.backend.inventory.persistence.entities.CompositeProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.Product
import dev.besi.gazdabolt.backend.inventory.persistence.entities.SimpleProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.SubProduct
import dev.besi.gazdabolt.backend.inventory.persistence.repositories.ProductRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.Test

@SpringBootTest
@Testcontainers
open class ProductRepositoryIT {

	companion object {
		@Container
		private val mongo = MongoDBContainer("mongo:5.0.7")

		@JvmStatic
		@DynamicPropertySource
		fun setupMongoSpringConfig(registry: DynamicPropertyRegistry) {
			registry.add("spring.data.mongodb.database") { "test_db" }
			registry.add("spring.data.mongodb.host", mongo::getHost)
			registry.add("spring.data.mongodb.port") { mongo.getMappedPort(27017).toString() }
		}
	}

	@Autowired
	private lateinit var productRepository: ProductRepository

	@Autowired
	private lateinit var mongoTemplate: MongoTemplate

	@Test
	fun testProductsCollectionExists() {
		val collections = mongoTemplate.db.listCollectionNames()
		assertThat(
			"Collections should include '${Product.PRODUCT_COLLECTION_NAME}'",
			collections, hasItem(Product.PRODUCT_COLLECTION_NAME)
		)
	}

	@Test
	fun testSimpleProductSaveSuccessful() {
		val id = productRepository.save(
			SimpleProduct(
				name = "test product",
				pluCode = 123,
				description = "test product description"
			)
		).id!!

		assertThat("Id of persisted object should not be null!", id, not(blankOrNullString()))
	}

	@Test
	fun testCompositeProductSaveSuccessful() {
		val product1 = productRepository.save(
			SimpleProduct(
				name = "product1"
			)
		)
		val product2 = productRepository.save(
			SimpleProduct(
				name = "product2"
			)
		)

		val composite = productRepository.save(
			CompositeProduct(
				name = "composite",
				subProducts = listOf(
					SubProduct(product1, 2),
					SubProduct(product2, 3),
					SubProduct(SimpleProduct(name = "asd"), 1)
				)
			)
		)

		assertThat("Id of persisted object should not be null!", composite.id, not(blankOrNullString()))
	}

}