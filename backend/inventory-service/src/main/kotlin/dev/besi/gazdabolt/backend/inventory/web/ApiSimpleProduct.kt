package dev.besi.gazdabolt.backend.inventory.web

class ApiSimpleProduct(
	override val id: String? = null,
	override val name: String? = null,
	override val pluCode: Int? = null,
	override val barCode: Long? = null,
	override val description: String? = null,
	override val stock: Int = 0,
	override val price: Double = 0.0
) : ApiProduct