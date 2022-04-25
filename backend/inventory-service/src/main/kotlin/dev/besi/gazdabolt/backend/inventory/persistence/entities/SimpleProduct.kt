package dev.besi.gazdabolt.backend.inventory.persistence.entities

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document(Product.PRODUCT_COLLECTION_NAME)
@TypeAlias("SimpleProduct")
class SimpleProduct(
	id: String? = null,
	name: String? = null,
	pluCode: Int? = null,
	barCode: Long? = null,
	description: String? = null,
	stock: Int = 0,
	override var price: Double = 0.0
) : Product(id, name, pluCode, barCode, description, stock) {
	init {
		require(price >= 0) { "Product price can not be set to negative: $price" }
	}
}
