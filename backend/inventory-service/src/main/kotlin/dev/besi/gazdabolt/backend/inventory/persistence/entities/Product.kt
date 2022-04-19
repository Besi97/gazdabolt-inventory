package dev.besi.gazdabolt.backend.inventory.persistence.entities

import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import java.util.UUID

abstract class Product(
	@PrimaryKey var id: UUID = UUID.randomUUID(),
	var name: String? = null,
	@Indexed var pluCode: Int? = null,
	@Indexed var barCode: Long? = null,
	var description: String? = null,
	stock: Int = 0
) {
	var stock: Int = stock
		set(value) {
			if (value < 0) {
				throw IllegalArgumentException()
			} else {
				field = value
			}
		}

	abstract var price: Int
}
