package dev.besi.gazdabolt.backend.inventory.web

import javax.validation.constraints.Size
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero

class ApiProductInput (
	@NotBlank
	@Size(max = 30)
	val name: String,
	@PositiveOrZero
	val pluCode: Int? = null,
	@PositiveOrZero
	val barCode: Long? = null,
	val description: String? = null,
	@PositiveOrZero
	val price: Float = 0f
) {
	fun toMap(): Map<String, String> =
		HashMap<String, String>().also {
			it["name"] = name
			pluCode?.let { plu -> it["pluCode"] = plu.toString() }
			barCode?.let { bar -> it["pluCode"] = bar.toString() }
			description?.let { description -> it["pluCode"] = description }
			it["price"] = price.toString()
		}
}