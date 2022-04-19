package dev.besi.gazdabolt.backend.inventory.persistence.entities

import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID

@Table
class CompositeProduct(
	id: UUID = UUID.randomUUID(),
	name: String?,
	pluCode: Int?,
	barCode: Long?,
	description: String?,
	stock: Int = 0,
	var subProducts: Map<Product, Int> = mapOf()
) : Product(id, name, pluCode, barCode, description, stock) {
	override var price: Int
		get() = subProducts.entries.sumOf { it.value * it.key.price }
		set(value) {}
}