package dev.besi.gazdabolt.backend.inventory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class InventoryService

fun main(args: Array<String>) {
	runApplication<InventoryService>(*args)
}