package dev.besi.gazdabolt.backend.inventory.service

import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct

sealed interface Product {
	val id: String?
	val name: String?
	val pluCode: Int?
	val barCode: Long?
	val description: String?
	val stock: Int
	val price: Double

	fun toDbProduct(): DbProduct
}