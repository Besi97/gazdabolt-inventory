package dev.besi.gazdabolt.backend.inventory.config

import dev.besi.gazdabolt.backend.inventory.AbstractIT
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.api.PathAndBytesable
import org.apache.curator.retry.RetryOneTime
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance
import kotlin.test.Test

class ZookeeperIT : AbstractIT() {

	companion object {
		const val CONFIG_PATH = "/gazdabolt/config/inventory-service/gazdabolt.inventory-service."

		lateinit var curator: CuratorFramework

		@JvmStatic
		@BeforeAll
		fun setTestData() {
			curator = CuratorFrameworkFactory.newClient(
				"localhost:${zookeeper.getMappedPort(2181)}",
				RetryOneTime(150)
			)
			curator.start()
			curator.blockUntilConnected()

			curator.create().forPath("/gazdabolt")
			curator.create().forPath("/gazdabolt/config")
			curator.create().forPath("/gazdabolt/config/inventory-service")
			curator.create().forPath("${CONFIG_PATH}failOnInsufficientResources", "false")

			curator.create().forPath("/gazdabolt/services")
		}
	}

	@Autowired
	private lateinit var properties: InventoryServiceProperties

	@Test
	fun `Ensure configuration data is loaded from Zookeeper`() {
		assertThat("Config data should be loaded from Zookeeper", properties, notNullValue())
		assertThat(
			"failOnInsufficientResources should be set to 'false'",
			properties.failOnInsufficientResources,
			equalTo(false)
		)
	}

	@Test
	fun `Ensure that configuration bean mutates with data changes in Zookeeper`() {
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
		Thread.sleep(100)

		assertThat(
			"failOnInsufficientResources should mutate with changes in Zookeeper",
			properties.failOnInsufficientResources,
			equalTo(expected)
		)
	}

	@Test
	fun `Ensure service is registered in Zookeeper`() {
		val serviceDiscovery = ServiceDiscoveryBuilder.builder(ZookeeperInstance::class.java)
			.client(curator)
			.basePath("/gazdabolt/services")
			.build()
		val services = serviceDiscovery.queryForNames()
			.map {
				serviceDiscovery.queryForInstances(it)
			}
			.flatten()

		assertThat("There should be a total of 1 service registered!", services, hasSize(1))
		assertThat(
			"Name of the registered service should be 'inventory-service'!",
			services,
			hasItem<ServiceInstance<ZookeeperInstance>>(
				hasProperty("name", equalTo("inventory-service"))
			)
		)
	}

}

private fun <T> PathAndBytesable<T>.forPath(path: String, value: String): T =
	this.forPath(path, value.toByteArray())
