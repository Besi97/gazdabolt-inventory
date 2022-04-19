package dev.besi.gazdabolt.backend.inventory.persistence

import dev.besi.gazdabolt.backend.inventory.persistence.entities.SimpleProduct
import dev.besi.gazdabolt.backend.inventory.persistence.repositories.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.CassandraContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.Test

@SpringBootTest
@Testcontainers
class ProductRepositoryIT {

	@Container
	private val cassandra = CassandraContainer("cassandra:4.0.3").withExposedPorts(9042)

	@Autowired
	private lateinit var productRepository: ProductRepository

	@Test
	fun test() {
		productRepository.save(SimpleProduct(
			name = "test product",
			pluCode = 123
		))
	}

}