package dev.besi.gazdabolt.backend.inventory.persistence.entities

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document(DbProduct.PRODUCT_COLLECTION_NAME)
@TypeAlias("SimpleProduct")
class DbSimpleProduct(
	id: String? = null,
	name: String? = null,
	pluCode: Int? = null,
	barCode: Long? = null,
	description: String? = null,
	stock: Int = 0,
	price: Double = 0.0
) : DbProduct(id, name, pluCode, barCode, description, stock) {
	override var price: Double = 0.0
		set(value) {
			require(value >= 0) { "Product price can not be set to negative: $price" }
			field = value
		}

	init {
		this.price = price
	}
}
