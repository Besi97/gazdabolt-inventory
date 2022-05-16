package dev.besi.gazdabolt.backend.inventory.service

import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbCompositeProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.SubProduct
import dev.besi.gazdabolt.backend.inventory.web.ApiCompositeProduct
import dev.besi.gazdabolt.backend.inventory.web.ApiProduct

data class CompositeProduct(
	override val id: String? = null,
	override val name: String? = null,
	override val pluCode: Int? = null,
	override val barCode: Long? = null,
	override val description: String? = null,
	override val stock: Int = 0,
	val subProducts: Map<Product, Int> = mapOf()
) : Product {
	override val price: Double
		get() = subProducts
			.map { (product, quantity) -> product.price * quantity }
			.sum()

	init {
		require(stock >= 0) { "Product stock quantity can not be set to a negative number: $stock" }
	}

	companion object {
		fun from(dbProduct: DbCompositeProduct): CompositeProduct =
			CompositeProduct(
				dbProduct.id,
				dbProduct.name,
				dbProduct.pluCode,
				dbProduct.barCode,
				dbProduct.description,
				dbProduct.stock,
				dbProduct.subProducts.associate { (dbProduct, quantity) -> dbProduct.toDomainPojo() to quantity }
			)
	}

	override fun toDbProduct(): DbProduct =
		DbCompositeProduct(
			id,
			name,
			pluCode,
			barCode,
			description,
			stock,
			subProducts.map { SubProduct(it.key.toDbProduct(), it.value) }
		)

	override fun toApiProduct(): ApiProduct =
		ApiCompositeProduct(
			id,
			name,
			pluCode,
			barCode,
			description,
			stock,
			price,
			subProducts.mapKeys { it.key.toApiProduct() }
		)
}