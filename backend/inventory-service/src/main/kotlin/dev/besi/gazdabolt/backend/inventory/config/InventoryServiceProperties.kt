package dev.besi.gazdabolt.backend.inventory.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(
	prefix = "gazdabolt.inventory-service"
)
@ConstructorBinding
class InventoryServiceProperties(
	var failOnInsufficientResources: Boolean = Defaults.FAIL_ON_INSUFFICIENT_RESOURCES
) {
	object Defaults {
		const val FAIL_ON_INSUFFICIENT_RESOURCES = true
	}
}