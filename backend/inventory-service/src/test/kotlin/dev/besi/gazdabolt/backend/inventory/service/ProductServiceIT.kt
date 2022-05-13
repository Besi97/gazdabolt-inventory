package dev.besi.gazdabolt.backend.inventory.service

import dev.besi.gazdabolt.backend.inventory.AbstractIT
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbCompositeProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbSimpleProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.SubProduct
import dev.besi.gazdabolt.backend.inventory.persistence.repositories.ProductRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ProductServiceIT(
	@Autowired val repository: ProductRepository,
	@Autowired val service: ProductService
) : AbstractIT() {

	@BeforeTest
	fun initTestProducts() {
		val p1 = repository.save(DbSimpleProduct(id = "abc", name = "product1", pluCode = 123))
		val p2 = repository.save(DbSimpleProduct(name = "product2", pluCode = 456, barCode = 456789))
		val p3 = repository.save(DbSimpleProduct(name = "product3", barCode = 123456, pluCode = 789))
		val c1 = repository.save(
			DbCompositeProduct(
				name = "composite1",
				pluCode = 1,
				subProducts = listOf(SubProduct(p1, 2), SubProduct(p3, 3))
			)
		)
		repository.save(
			DbCompositeProduct(
				name = "composite2",
				barCode = 987654,
				subProducts = listOf(SubProduct(p2, 10))
			)
		)
		repository.save(
			DbCompositeProduct(
				name = "composite3",
				pluCode = 10,
				barCode = 123456789,
				subProducts = listOf(SubProduct(c1, 1), SubProduct(p2, 3))
			)
		)
	}

	@AfterTest
	fun cleanDb() {
		repository.deleteAll()
	}

	@Test
	fun `Ensure listing all products works`() {
		val products = service.listAllProducts()

		assertThat("Listing should have 6 products!", products, hasSize(6))
		assertThat(
			"Listing should contain 'product1'!",
			products, hasItem(hasProperty<String>("name", equalTo("product1")))
		)
		assertThat(
			"Listing should contain 'product2'!",
			products, hasItem(hasProperty<String>("name", equalTo("product2")))
		)
		assertThat(
			"Listing should contain 'product3'!",
			products, hasItem(hasProperty<String>("name", equalTo("product3")))
		)
		assertThat(
			"Listing should contain 'composite1'!",
			products, hasItem(hasProperty<String>("name", equalTo("composite1")))
		)
		assertThat(
			"Listing should contain 'composite2'!",
			products, hasItem(hasProperty<String>("name", equalTo("composite2")))
		)
		assertThat(
			"Listing should contain 'composite3'!",
			products, hasItem(hasProperty<String>("name", equalTo("composite3")))
		)
	}

	@Test
	fun `Ensure 'findProductById' works`() {
		val p1 = service.findProductById("abc")

		assertThat("Result should not be null!", p1, notNullValue())
		assertThat("Product name should be 'product1'", p1!!.name, equalTo("product1"))
		assertThat("Product PLU code should be 123!", p1.pluCode, equalTo(123))
		assertThat("Product bar code should be null!", p1.barCode, nullValue())

		val p2 = service.findProductById("asd")

		assertThat("Result should be null for unknown ID", p2, nullValue())
	}

	@Test
	fun `Ensure 'findProductByPluCode' works`() {
		val p1 = service.findProductByPluCode(456)

		assertThat("Result should not be null!", p1, notNullValue())
		assertThat("Product name should be 'product2'", p1!!.name, equalTo("product2"))
		assertThat("Product bar code should be null!", p1.barCode, equalTo(456789))

		val p2 = service.findProductByPluCode(0)

		assertThat("Result should be null for unknown PLU code!", p2, nullValue())
	}

	@Test
	fun `Ensure 'findProductByBarCode' works`() {
		val p1 = service.findProductByBarCode(123456789)

		assertThat("Result should not be null!", p1, notNullValue())
		assertThat("Product name should be 'composite3'", p1!!.name, equalTo("composite3"))
		assertThat("Product PLU code should be 123!", p1.pluCode, equalTo(10))

		val p2 = service.findProductByBarCode(0)

		assertThat("Result should be null for unknown ID", p2, nullValue())
	}

}