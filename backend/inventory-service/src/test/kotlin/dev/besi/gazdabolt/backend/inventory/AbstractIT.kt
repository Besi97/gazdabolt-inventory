package dev.besi.gazdabolt.backend.inventory

import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@Testcontainers
abstract class AbstractIT {

	companion object {
		@Container
		protected val mongo = MongoDBContainer("mongo:5.0.7")

		@JvmStatic
		@Container
		protected val zookeeper: GenericContainer<*> =
			GenericContainer(DockerImageName.parse("zookeeper:3.8.0"))
				.withExposedPorts(2181)

		@JvmStatic
		@DynamicPropertySource
		fun setupMongoSpringConfig(registry: DynamicPropertyRegistry) {
			registry.add("spring.data.mongodb.database") { "test_db" }
			registry.add("spring.data.mongodb.host", mongo::getHost)
			registry.add("spring.data.mongodb.port") { mongo.getMappedPort(27017).toString() }

			registry.add("spring.cloud.zookeeper.connect-string") {
				"localhost:${zookeeper.getMappedPort(2181)}"
			}
		}

		@JvmStatic
		@BeforeAll
		fun setZookeeperConnectString() {
			System.setProperty("spring.cloud.zookeeper.connect-string", "localhost:${zookeeper.getMappedPort(2181)}")
		}
	}

}