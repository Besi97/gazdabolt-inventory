package dev.besi.gazdabolt.backend.inventory.persistence

import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbCompositeProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbSimpleProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.SubProduct
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test

class DbProductTest {

	@Test
	fun `Ensure product stock value can not get negative`() {
		assertThrows(IllegalArgumentException::class.java) {
			DbSimpleProduct(name = "product", stock = -3)
		}

		val product = DbSimpleProduct(name = "product", stock = 5)
		assertThrows(IllegalArgumentException::class.java) {
			product.stock = -1
		}
		assertThat("Product stock changed unexpectedly!", product.stock, equalTo(5))
	}

	@Test
	fun `Ensure SimpleProduct price works properly`() {
		assertThrows(IllegalArgumentException::class.java) {
			DbSimpleProduct(price = -105.6)
		}

		val product: DbProduct = DbSimpleProduct(name = "product", price = 15.7)
		assertThat("Product price is not as expected!", product.price, equalTo(15.7))
		assertThrows(IllegalArgumentException::class.java) {
			product.price = -30.0
		}
		assertThat("Product price changed unexpectedly!", product.price, equalTo(15.7))
	}

	@Test
	fun `Ensure CompositeProduct price work properly`() {
		val simple1 = DbSimpleProduct(price = 26.7)
		val simple2 = DbSimpleProduct(price = 98.3)
		val composite: DbProduct = DbCompositeProduct(
			subProducts = listOf(
				SubProduct(simple1, 7),
				SubProduct(simple2, 3)
			)
		)

		assertThat(
			"Composite product price is not as expected",
			composite.price, closeTo(7 * 26.7 + 3 * 98.3, 1e-10)
		)
	}

}