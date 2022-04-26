package dev.besi.gazdabolt.backend.inventory.persistence

import dev.besi.gazdabolt.backend.inventory.persistence.entities.SimpleProduct
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test

class ProductTest {

	@Test
	fun `Ensure product stock value can not get negative`() {
		assertThrows(IllegalArgumentException::class.java) {
			SimpleProduct(name = "product", stock = -3)
		}

		val product = SimpleProduct(name = "product", stock = 5)
		assertThrows(IllegalArgumentException::class.java) {
			product.stock = -1
		}
	}

	// TODO test composite and simple product price

}