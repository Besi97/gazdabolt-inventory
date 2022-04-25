package dev.besi.gazdabolt.backend.inventory.persistence

import dev.besi.gazdabolt.backend.inventory.persistence.entities.CompositeProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.Product
import dev.besi.gazdabolt.backend.inventory.persistence.entities.SimpleProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.SubProduct
import dev.besi.gazdabolt.backend.inventory.persistence.repositories.ProductRepository
import org.bson.Document
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException
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

	@AfterEach
	fun cleanDb() {
		mongoTemplate.db.getCollection(Product.PRODUCT_COLLECTION_NAME).deleteMany(Document())
	}

	@Test
	fun `Ensure collection 'products' exists`() {
		val collections = mongoTemplate.db.listCollectionNames()
		assertThat(
			"Collections should include '${Product.PRODUCT_COLLECTION_NAME}'",
			collections, hasItem(Product.PRODUCT_COLLECTION_NAME)
		)
	}

	@Test
	fun `SimpleProduct instance saves successfully`() {
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
	fun `CompositeProduct instance saves successfully`() {
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
					SubProduct(product2, 3)
				)
			)
		)

		assertThat("Id of persisted object should not be null!", composite.id, not(blankOrNullString()))
	}

	@Test
	fun `Ensure unique index works for PLU codes`() {
		productRepository.save(
			SimpleProduct(name = "product1", pluCode = 123)
		)
		assertThrows(DuplicateKeyException::class.java) {
			productRepository.save(
				SimpleProduct(name = "product2", pluCode = 123)
			)
		}

		assertThrows(DuplicateKeyException::class.java) {
			productRepository.save(
				CompositeProduct(name = "composite", pluCode = 123)
			)
		}

		assertThat(
			"Only 1 product should be in the database!",
			mongoTemplate.db.getCollection(Product.PRODUCT_COLLECTION_NAME).countDocuments(),
			equalTo(1)
		)
	}

	@Test
	fun `Ensure unique index works for barcodes`() {
		productRepository.save(
			SimpleProduct(name = "product1", barCode = 123456789)
		)

		assertThrows(DuplicateKeyException::class.java) {
			productRepository.save(
				SimpleProduct(name = "product2", barCode = 123456789)
			)
		}

		assertThrows(DuplicateKeyException::class.java) {
			productRepository.save(
				CompositeProduct(name = "composite", barCode = 123456789)
			)
		}

		assertThat(
			"Only 1 product should be in the database!",
			mongoTemplate.db.getCollection(Product.PRODUCT_COLLECTION_NAME).countDocuments(),
			equalTo(1)
		)
	}

}