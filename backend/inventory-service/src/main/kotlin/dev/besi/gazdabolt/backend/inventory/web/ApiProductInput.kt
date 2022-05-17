package dev.besi.gazdabolt.backend.inventory.web

class ApiProductInput (
	val name: String,
	val pluCode: Int? = null,
	val barCode: Long? = null,
	val description: String? = null,
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