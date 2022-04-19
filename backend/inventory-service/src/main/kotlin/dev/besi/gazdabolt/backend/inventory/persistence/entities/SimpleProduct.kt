package dev.besi.gazdabolt.backend.inventory.persistence.entities

import org.springframework.data.cassandra.core.mapping.Table
import java.util.*

@Table
class SimpleProduct(
	id: UUID = UUID.randomUUID(),
	name: String? = null,
	pluCode: Int? = null,
	barCode: Long? = null,
	description: String? = null,
	stock: Int = 0,
	price: Int = 0
) : Product(id, name, pluCode, barCode, description, stock) {
	override var price: Int = price
		set(value) {
			if (value < 0) {
				throw IllegalArgumentException()
			} else {
				field = value
			}
		}
}
