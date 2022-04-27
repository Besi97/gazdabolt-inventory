package dev.besi.gazdabolt.backend.inventory.service

import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbSimpleProduct

data class SimpleProduct(
	override val id: String? = null,
	override val name: String? = null,
	override val pluCode: Int? = null,
	override val barCode: Long? = null,
	override val description: String? = null,
	override val stock: Int = 0,
	override val price: Double = 0.0
) : Product {
	init {
		require(stock >= 0) { "Product stock quantity can not be set to a negative number: $stock" }
		require(price >= 0) { "Product price can not be set to a negative number: $price" }
	}

	companion object {
		fun from(dbProduct: DbSimpleProduct): SimpleProduct =
			SimpleProduct(
				dbProduct.id,
				dbProduct.name,
				dbProduct.pluCode,
				dbProduct.barCode,
				dbProduct.description,
				dbProduct.stock,
				dbProduct.price
			)
	}

	override fun toDbProduct(): DbProduct =
		DbSimpleProduct(id, name, pluCode, barCode, description, stock, price)
}