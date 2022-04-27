package dev.besi.gazdabolt.backend.inventory.persistence.entities

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document(DbProduct.PRODUCT_COLLECTION_NAME)
@TypeAlias("CompositeProduct")
class DbCompositeProduct(
	id: String? = null,
	name: String? = null,
	pluCode: Int? = null,
	barCode: Long? = null,
	description: String? = null,
	stock: Int = 0,
	var subProducts: List<SubProduct> = listOf()
) : DbProduct(id, name, pluCode, barCode, description, stock) {
	override var price: Double
		get() = subProducts.sumOf { it.subProduct.price * it.quantity }
		set(value) {}
}

data class SubProduct(
	@DocumentReference var subProduct: DbProduct,
	var quantity: Int
)
