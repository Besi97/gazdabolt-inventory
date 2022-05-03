package dev.besi.gazdabolt.backend.inventory.persistence

import dev.besi.gazdabolt.backend.inventory.AbstractIT
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbCompositeProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbSimpleProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.SubProduct
import dev.besi.gazdabolt.backend.inventory.persistence.repositories.ProductRepository
import org.bson.Document
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.MongoTemplate
import kotlin.test.Test

open class ProductRepositoryIT : AbstractIT() {

	@Autowired
	private lateinit var productRepository: ProductRepository

	@Autowired
	private lateinit var mongoTemplate: MongoTemplate

	@AfterEach
	fun cleanDb() {
		mongoTemplate.db.getCollection(DbProduct.PRODUCT_COLLECTION_NAME).deleteMany(Document())
	}

	@Test
	fun `Ensure collection 'products' exists`() {
		val collections = mongoTemplate.db.listCollectionNames()
		assertThat(
			"Collections should include '${DbProduct.PRODUCT_COLLECTION_NAME}'",
			collections, hasItem(DbProduct.PRODUCT_COLLECTION_NAME)
		)
	}

	@Test
	fun `SimpleProduct instance saves successfully`() {
		val id = productRepository.save(
			DbSimpleProduct(
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
			DbSimpleProduct(
				name = "product1"
			)
		)
		val product2 = productRepository.save(
			DbSimpleProduct(
				name = "product2"
			)
		)

		val composite = productRepository.save(
			DbCompositeProduct(
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
			DbSimpleProduct(name = "product1", pluCode = 123)
		)
		assertThrows(DuplicateKeyException::class.java) {
			productRepository.save(
				DbSimpleProduct(name = "product2", pluCode = 123)
			)
		}

		assertThrows(DuplicateKeyException::class.java) {
			productRepository.save(
				DbCompositeProduct(name = "composite", pluCode = 123)
			)
		}

		assertThat(
			"Only 1 product should be in the database!",
			mongoTemplate.db.getCollection(DbProduct.PRODUCT_COLLECTION_NAME).countDocuments(),
			equalTo(1)
		)
	}

	@Test
	fun `Ensure unique index works for barcodes`() {
		productRepository.save(
			DbSimpleProduct(name = "product1", barCode = 123456789)
		)

		assertThrows(DuplicateKeyException::class.java) {
			productRepository.save(
				DbSimpleProduct(name = "product2", barCode = 123456789)
			)
		}

		assertThrows(DuplicateKeyException::class.java) {
			productRepository.save(
				DbCompositeProduct(name = "composite", barCode = 123456789)
			)
		}

		assertThat(
			"Only 1 product should be in the database!",
			mongoTemplate.db.getCollection(DbProduct.PRODUCT_COLLECTION_NAME).countDocuments(),
			equalTo(1)
		)
	}

	@Test
	fun `Test 'findProductByPluCode' works`() {
		var product = productRepository.findProductByPluCode(123)

		assertThat("Retrieved product should be null in empty database!", product, nullValue())

		mongoTemplate.insert(DbSimpleProduct(name = "product", pluCode = 123))
		mongoTemplate.insert(DbSimpleProduct(name = "wrong product", pluCode = 456))

		product = productRepository.findProductByPluCode(123)

		assertThat("Retrieved product should not be null!", product, not(nullValue()))
		product = product!!
		assertThat("Retrieved product's name should be 'product'", product.name, equalTo("product"))
	}

	@Test
	fun `Test 'findProductByBarCode' works`() {
		var product = productRepository.findProductByBarCode(123456789)

		assertThat("Retrieved product should be null in empty database!", product, nullValue())

		mongoTemplate.insert(DbSimpleProduct(name = "product", barCode = 123456789))
		mongoTemplate.insert(DbSimpleProduct(name = "wrong product", barCode = 456))

		product = productRepository.findProductByBarCode(123456789)

		assertThat("Retrieved product should not be null!", product, not(nullValue()))
		product = product!!
		assertThat("Retrieved product's name should be 'product'", product.name, equalTo("product"))
	}

}