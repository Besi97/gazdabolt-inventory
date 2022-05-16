package dev.besi.gazdabolt.backend.inventory.web

class ApiCompositeProduct(
	override val id: String? = null,
	override val name: String? = null,
	override val pluCode: Int? = null,
	override val barCode: Long? = null,
	override val description: String? = null,
	override val stock: Int = 0,
	override val price: Double = 0.0,
	val subProducts: Map<ApiProduct, Int>
) : ApiProduct