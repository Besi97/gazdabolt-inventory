package dev.besi.gazdabolt.backend.inventory.config

import dev.besi.gazdabolt.backend.inventory.AbstractIT
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.api.PathAndBytesable
import org.apache.curator.retry.RetryNTimes
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ZookeeperIT : AbstractIT() {

	@Autowired
	private lateinit var curator: CuratorFramework

	companion object {
		const val CONFIG_PATH = "/gazdabolt/inventory-service/gazdabolt.inventory-service."

		@JvmStatic
		@BeforeAll
		fun setTestData() {
			val curator = CuratorFrameworkFactory.newClient(
				"localhost:${zookeeper.getMappedPort(2181)}",
				RetryNTimes(3, 400)
			)
			curator.start()
			curator.blockUntilConnected()

			curator.create().forPath("/gazdabolt")
			curator.create().forPath("/gazdabolt/inventory-service")
			curator.create().forPath("${CONFIG_PATH}failOnInsufficientResources", "false")
		}
	}

	@Autowired
	private lateinit var properties: InventoryServiceProperties

	@Test
	@Order(1)
	fun testConfigurationDataIsLoaded() {
		assertThat("Config data should be loaded from Zookeeper", properties, notNullValue())
		assertThat(
			"failOnInsufficientResources should be set to 'false'",
			properties.failOnInsufficientResources,
			equalTo(false)
		)
	}

	@Test
	@Order(2)
	fun testConfigurationMutatesWithZookeeperChanges() {
		curator.setData().forPath("${CONFIG_PATH}failOnInsufficientResources", "true")
		assertFailOnInsufficientResourcesCurrent(true)

		curator.setData().forPath("${CONFIG_PATH}failOnInsufficientResources", "false")
		assertFailOnInsufficientResourcesCurrent(false)

		curator.delete().forPath("${CONFIG_PATH}failOnInsufficientResources")
		assertFailOnInsufficientResourcesCurrent(InventoryServiceProperties.Defaults.FAIL_ON_INSUFFICIENT_RESOURCES)

		curator.create().forPath("${CONFIG_PATH}failOnInsufficientResources", "false")
	}

	private fun assertFailOnInsufficientResourcesCurrent(expected: Boolean) {
		// wait for changes to propagate
		Thread.sleep(10)

		assertThat(
			"failOnInsufficientResources should mutate with changes in Zookeeper",
			properties.failOnInsufficientResources,
			equalTo(expected)
		)
	}

}

private fun <T> PathAndBytesable<T>.forPath(path: String, value: String): T =
	this.forPath(path, value.toByteArray())
