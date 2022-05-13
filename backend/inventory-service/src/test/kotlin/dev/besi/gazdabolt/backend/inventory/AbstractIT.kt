package dev.besi.gazdabolt.backend.inventory

import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperAutoServiceRegistration
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.test.AfterTest

@SpringBootTest
@DirtiesContext
@Testcontainers
abstract class AbstractIT {

	@Autowired
	lateinit var zookeeperAutoServiceRegistration: ZookeeperAutoServiceRegistration

	companion object {
		@Container
		protected var mongo: MongoDBContainer = MongoDBContainer("mongo:5.0.7")

		@JvmStatic
		@Container
		protected var zookeeper: GenericContainer<*> =
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
			System.setProperty("spring.cloud.zookeeper.max-retries", "0")
		}
	}

	@AfterTest
	fun disconnectCurator() {
		zookeeperAutoServiceRegistration.destroy()
	}

}