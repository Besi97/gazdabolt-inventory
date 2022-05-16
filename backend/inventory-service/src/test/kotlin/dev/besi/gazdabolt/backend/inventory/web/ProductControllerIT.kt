package dev.besi.gazdabolt.backend.inventory.web

import dev.besi.gazdabolt.backend.inventory.AbstractIT
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbCompositeProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbSimpleProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.SubProduct
import dev.besi.gazdabolt.backend.inventory.persistence.repositories.ProductRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester
import org.springframework.graphql.test.tester.GraphQlTester
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@AutoConfigureGraphQlTester
class ProductControllerIT(
	@Autowired val graphQlTester: GraphQlTester,
	@Autowired val repository: ProductRepository
) : AbstractIT() {

	lateinit var persistedProducts: List<DbProduct>

	@BeforeTest
	fun initTestData() {
		val p1 = repository.save(
			DbSimpleProduct(
				name = "product 1",
				pluCode = 123,
				barCode = 123456789,
				description = "Sample test product",
				price = 1500.0
			)
		)
		val p2 = repository.save(
			DbSimpleProduct(
				name = "product 2",
				pluCode = 456,
				description = "Second sample test product with a bit longer description",
				price = 6920.0
			)
		)
		val p3 = repository.save(
			DbSimpleProduct(
				name = "product 3",
				barCode = 987654321,
				price = 9990.0,
				stock = 3
			)
		)
		val c1 = repository.save(
			DbCompositeProduct(
				name = "composite 1",
				pluCode = 1,
				stock = 2,
				subProducts = listOf(SubProduct(p1, 2), SubProduct(p3, 1))
			)
		)
		val c2 = repository.save(
			DbCompositeProduct(
				name = "composite 2",
				pluCode = 900,
				subProducts = listOf(SubProduct(p2, 12))
			)
		)
		persistedProducts = listOf(p1, p2, p3, c1, c2)
	}

	@AfterTest
	fun cleanDb() {
		repository.deleteAll()
	}

	@Test
	fun `Ensure 'products' query returns all items as expected`() {
		val request = """{
			|   products {
			|       name
			|       price
			|       stock
			|   }
			|}
		""".trimMargin()

		graphQlTester.document(request)
			.executeAndVerifyWithPath("products")
			.entity(Collection::class.java)
			.satisfies {
				run {
					val products = it as List<Map<String, *>>

					assertThat("Endpoint should return 5 products!", products, hasSize(5))
					assertThat("Products should have 3 properties!", products, everyItem(aMapWithSize(3)))

					assertThat(
						"One of the products should be 'product 1'!",
						products, hasItem(hasEntry("name", "product 1"))
					)
					assertThat(
						"'product 1' stock should be 0!",
						products.find { product -> product["name"] == "product 1" }, hasEntry("stock", 0)
					)
					assertThat(
						"'product 1' price should be 1500!",
						products.find { product -> product["name"] == "product 1" }, hasEntry("price", 1500.0)
					)

					assertThat(
						"One of the products should be 'product 2'!",
						products, hasItem(hasEntry("name", "product 2"))
					)

					assertThat(
						"One of the products should be 'product 3'!",
						products, hasItem(hasEntry("name", "product 3"))
					)
					assertThat(
						"'product 3' stock should be 3!",
						products.find { product -> product["name"] == "product 3" }, hasEntry("stock", 3)
					)

					assertThat(
						"One of the products should be 'composite 1'!",
						products, hasItem(hasEntry("name", "composite 1"))
					)
					assertThat(
						"'composite 1' stock should be 2!",
						products.find { product -> product["name"] == "composite 1" }, hasEntry("stock", 2)
					)
					assertThat(
						"'composite 1' price should be 12990!",
						products.find { product -> product["name"] == "composite 1" }, hasEntry("price", 12990.0)
					)

					assertThat(
						"One of the products should be 'composite 2'!",
						products, hasItem(hasEntry("name", "composite 2"))
					)
					assertThat(
						"'composite 2' stock should be 0!",
						products.find { product -> product["name"] == "composite 2" }, hasEntry("stock", 0)
					)
					assertThat(
						"'composite 2' price should be 83040!",
						products.find { product -> product["name"] == "composite 2" }, hasEntry("price", 83040.0)
					)
				}
			}
	}

	@Test
	fun `Ensure 'products' query returns empty list on empty DB`() {
		cleanDb()

		val request = """{
			|   products {
			|       name
			|       price
			|       stock
			|   }
			|}
		""".trimMargin()

		graphQlTester.document(request)
			.executeAndVerifyWithPath("products")
			.entity(Collection::class.java)
			.satisfies {
				run {
					assertThat("Result should be an empty collection!", it, emptyIterable())
				}
			}
	}

	@Test
	fun `Ensure 'productById' query returns product as expected`() {
		val id = persistedProducts.find { it.name == "product 1" }!!.id
		val request = """{
			|   product: productById(id: "$id") {
			|       name
			|       pluCode
			|       barCode
			|       price
			|   }
			|}
		""".trimMargin()

		graphQlTester.document(request)
			.executeAndVerifyWithPath("product")
			.entity(Map::class.java)
			.satisfies {
				run {
					assertThat("Returned product should have 4 properties!", it, aMapWithSize(4))
					assertThat("Product name should be 'product 1'!", it, hasEntry("name", "product 1"))
					assertThat("Product pluCode should be 123!", it, hasEntry("pluCode", 123))
					assertThat("Product barCode should be 123456789!", it, hasEntry("barCode", 123456789))
					assertThat("Product price should be 1500!", it, hasEntry("price", 1500.0))
				}
			}
	}

	@Test
	fun `Ensure 'productById' handles missing ID safely`() {
		val request = """{
			|   product: productById(id: "65351351") {
			|       name
			|       pluCode
			|       barCode
			|       price
			|   }
			|}
		""".trimMargin()

		graphQlTester.document(request)
			.executeAndVerifyWithPath("product")
			.valueIsNull()
	}

	@Test
	fun `Ensure 'productByPluCode' query returns product as expected`() {
		val plu = persistedProducts.find { it.name == "product 1" }!!.pluCode
		val request = """{
			|   product: productByPluCode(pluCode: $plu) {
			|       name
			|       price
			|   }
			|}
		""".trimMargin()

		graphQlTester.document(request)
			.executeAndVerifyWithPath("product")
			.entity(Map::class.java)
			.satisfies {
				run {
					assertThat("Returned product should have 2 properties!", it, aMapWithSize(2))
					assertThat("Returned product name should be 'product 1'!", it, hasEntry("name", "product 1"))
					assertThat("Returned product price should be 1500!", it, hasEntry("price", 1500.0))
				}
			}
	}

	@Test
	fun `Ensure 'productByPluCode' handles missing PLU safely`() {
		val request = """{
			|   product: productByPluCode(pluCode: 64671) {
			|       name
			|       price
			|   }
			|}
		""".trimMargin()

		graphQlTester.document(request)
			.executeAndVerifyWithPath("product")
			.valueIsNull()
	}

	@Test
	fun `Ensure 'productByBarCode' query returns product as expected`() {
		val barCode = persistedProducts.find { it.name == "product 1" }!!.barCode
		val request = """{
			|   product: productByBarCode(barCode: $barCode) {
			|       name
			|       price
			|   }
			|}
		""".trimMargin()

		graphQlTester.document(request)
			.executeAndVerifyWithPath("product")
			.entity(Map::class.java)
			.satisfies {
				run {
					assertThat("Product should have 2 properties!", it, aMapWithSize(2))
					assertThat("Product name should be 'product 1'!", it, hasEntry("name", "product 1"))
					assertThat("Product price should be 1500!", it, hasEntry("price", 1500.0))
				}
			}
	}

	@Test
	fun `Ensure 'productByBarCode' handles missing barcode safely`() {
		val request = """{
			|   product: productByBarCode(barCode: 65461611761751786) {
			|       name
			|       price
			|   }
			|}
		""".trimMargin()

		graphQlTester.document(request)
			.executeAndVerifyWithPath("product")
			.valueIsNull()
	}

	@Test
	fun `Ensure 'createProduct' mutation works`() {
		val request = """mutation CreateProduct(${'$'}product: ApiProductInput!) {
			|   product: createProduct(product: ${'$'}product) {
			|       id
			|       name
			|       price
			|   }
			|}
		""".trimMargin()

		graphQlTester.document(request)
			.variable("product", ApiProductInput(name = "test input product", price = 15.6f).toMap())
			.executeAndVerifyWithPath("product")
			.entity(Map::class.java)
			.satisfies {
				run {
					assertThat("Returned product should have 3 fields!", it, aMapWithSize(3))
					assertThat("Product ID should not be blank!", it["id"] as String, `is`(not(blankOrNullString())))
					assertThat("Product name should match input!", it["name"], equalTo("test input product"))
					assertThat("Product price should match input!", it["price"] as Double, closeTo(15.6, 1e-6))
				}
			}
	}

	@Test
	fun `Ensure validation for 'createProduct' works`() {
		TODO("Not implemented yet")
	}

}

fun GraphQlTester.Request<*>.executeAndVerifyWithPath(path: String) =
	execute().errors().verify().path(path)
