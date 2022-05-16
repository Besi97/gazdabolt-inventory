package dev.besi.gazdabolt.backend.inventory.service

import dev.besi.gazdabolt.backend.inventory.persistence.entities.DbProduct
import dev.besi.gazdabolt.backend.inventory.web.ApiProduct

sealed interface Product {
	val id: String?
	val name: String?
	val pluCode: Int?
	val barCode: Long?
	val description: String?
	val stock: Int
	val price: Double

	fun toDbProduct(): DbProduct
	fun toApiProduct(): ApiProduct
}