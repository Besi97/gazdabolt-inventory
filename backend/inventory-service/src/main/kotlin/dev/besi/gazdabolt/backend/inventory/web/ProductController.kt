package dev.besi.gazdabolt.backend.inventory.web

import dev.besi.gazdabolt.backend.inventory.service.Product
import dev.besi.gazdabolt.backend.inventory.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class ProductController(
	@Autowired val productService: ProductService
) {

	@QueryMapping
	fun products(): Collection<Product> = productService.listAllProducts()

	@QueryMapping
	fun productById(@Argument id: String): Product? = productService.findProductById(id)

	@QueryMapping
	fun productByPluCode(@Argument pluCode: Int): Product? = productService.findProductByPluCode(pluCode)

	@QueryMapping
	fun productByBarCode(@Argument barCode: Long): Product? = productService.findProductByBarCode(barCode)

}