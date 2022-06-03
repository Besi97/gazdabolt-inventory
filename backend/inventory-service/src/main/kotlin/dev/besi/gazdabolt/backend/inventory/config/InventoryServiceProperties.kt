package dev.besi.gazdabolt.backend.inventory.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(
	prefix = InventoryServiceProperties.PROPERTIES_PREFIX
)
@ConstructorBinding
class InventoryServiceProperties(
	var failOnInsufficientResources: Boolean = Defaults.FAIL_ON_INSUFFICIENT_RESOURCES
) {
	companion object {
		const val PROPERTIES_PREFIX = "gazdabolt.inventory-service"
		const val FAIL_ON_INSUFFICIENT_RESOURCES_KEY = "failOnInsufficientResources"
	}

	object Defaults {
		const val FAIL_ON_INSUFFICIENT_RESOURCES = true
	}
}