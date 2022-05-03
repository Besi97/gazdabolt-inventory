package dev.besi.gazdabolt.backend.inventory

import dev.besi.gazdabolt.backend.inventory.config.InventoryServiceProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(InventoryServiceProperties::class)
open class InventoryService

fun main(args: Array<String>) {
	runApplication<InventoryService>(*args)
}