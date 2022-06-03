package dev.besi.gazdabolt.backend.inventory.web

import dev.besi.gazdabolt.backend.inventory.error.IllegalArgumentError
import dev.besi.gazdabolt.backend.inventory.service.Product
import dev.besi.gazdabolt.backend.inventory.service.ProductService
import dev.besi.gazdabolt.backend.inventory.service.SimpleProduct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class ProductController(
	@Autowired val productService: ProductService
) {

	@QueryMapping
	fun products(): Collection<ApiProduct> =
		productService.listAllProducts().map(Product::toApiProduct)

	@QueryMapping
	fun productById(@Argument id: String): ApiProduct? =
		productService.findProductById(id)?.toApiProduct()

	@QueryMapping
	fun productByPluCode(@Argument pluCode: Int): ApiProduct? =
		productService.findProductByPluCode(pluCode)?.toApiProduct()

	@QueryMapping
	fun productByBarCode(@Argument barCode: Long): ApiProduct? =
		productService.findProductByBarCode(barCode)?.toApiProduct()

	@MutationMapping
	fun createProduct(@Argument product: ApiProductInput): ApiProduct = productService.createProduct(
		SimpleProduct(
			name = product.name,
			pluCode = product.pluCode,
			barCode = product.barCode,
			description = product.description,
			price = product.price.toDouble()
		)
	).toApiProduct()

	@MutationMapping
	fun incrementStock(@Argument id: String, @Argument amount: Int): ApiProduct =
		try {
			productService.incrementProduct(id, amount).toApiProduct()
		} catch (e: IllegalArgumentException) {
			throw IllegalArgumentError(e)
		}

	@MutationMapping
	fun decrementStock(@Argument id: String, @Argument amount: Int): ApiProduct =
		try {
			productService.decrementProduct(id, amount).toApiProduct()
		} catch (e: IllegalArgumentException) {
			throw IllegalArgumentError(e)
		}

}