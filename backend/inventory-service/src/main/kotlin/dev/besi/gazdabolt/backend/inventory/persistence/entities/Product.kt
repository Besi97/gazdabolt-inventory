package dev.besi.gazdabolt.backend.inventory.persistence.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(Product.PRODUCT_COLLECTION_NAME)
abstract class Product(
	@Id var id: String? = null,
	var name: String? = null,
	@Indexed(unique = true, sparse = true) var pluCode: Int? = null,
	@Indexed(unique = true, sparse = true) var barCode: Long? = null,
	var description: String? = null,
	stock: Int = 0
) {
	var stock: Int = 0
		set(value) {
			require(value >= 0) { "Product stock quantity can not be set to a negative number: $value" }
			field = value
		}

	init {
		this.stock = stock
	}

	abstract var price: Double

	companion object {
		const val PRODUCT_COLLECTION_NAME = "products"
	}
}
