package dev.besi.gazdabolt.backend.inventory.web

sealed interface ApiProduct {
	val id: String?
	val name: String?
	val pluCode: Int?
	val barCode: Long?
	val description: String?
	val stock: Int
	val price: Double
}