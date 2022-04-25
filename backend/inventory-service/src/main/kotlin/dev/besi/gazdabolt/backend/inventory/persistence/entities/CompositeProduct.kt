package dev.besi.gazdabolt.backend.inventory.persistence.entities

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document(Product.PRODUCT_COLLECTION_NAME)
@TypeAlias("CompositeProduct")
class CompositeProduct(
	id: String? = null,
	name: String? = null,
	pluCode: Int? = null,
	barCode: Long? = null,
	description: String? = null,
	stock: Int = 0,
	var subProducts: List<SubProduct> = listOf()
) : Product(id, name, pluCode, barCode, description, stock) {
	override var price: Double
		get() = subProducts
			.filterNot { it.subProduct == null || it.quantity == null }
			.sumOf { it.subProduct!!.price * it.quantity!! }
		set(value) {}
}

data class SubProduct(
	@DocumentReference var subProduct: Product? = null,
	var quantity: Int? = null
)
