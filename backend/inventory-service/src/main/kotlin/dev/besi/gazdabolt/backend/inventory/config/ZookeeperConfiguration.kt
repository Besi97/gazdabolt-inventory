package dev.besi.gazdabolt.backend.inventory.config

import org.apache.curator.framework.CuratorFramework
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.Watcher.Event.EventType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperAutoServiceRegistration
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ZookeeperConfiguration(
	@Autowired val properties: InventoryServiceProperties,
	@Autowired val curator: CuratorFramework,
	@Autowired val serviceRegistration: ZookeeperAutoServiceRegistration
) {

	private class FailOnInsufficientResourcesNodeWatcher(
		val curator: CuratorFramework,
		val properties: InventoryServiceProperties
	) : Watcher {
		override fun process(event: WatchedEvent?) {
			event ?: return

			val updated: Boolean = when (event.type) {
				EventType.NodeCreated, EventType.NodeDataChanged -> {
					String(curator.data.forPath(event.path)).toBoolean()
				}
				EventType.NodeDeleted -> {
					InventoryServiceProperties.Defaults.FAIL_ON_INSUFFICIENT_RESOURCES
				}
				else -> return
			}

			properties.failOnInsufficientResources = updated
		}
	}

	@EventListener(ApplicationStartedEvent::class)
	fun startServiceRegistration(event: ApplicationStartedEvent) {
		serviceRegistration.start()
		ensurePropertiesExistInZookeeper()
		registerWatchers()
	}

	private fun ensurePropertiesExistInZookeeper() {
		val failOnInsufficientResourcesConfigPath =
			getServicePropertyConfigPath(InventoryServiceProperties.FAIL_ON_INSUFFICIENT_RESOURCES_KEY)

		val result = curator.checkExists().forPath(failOnInsufficientResourcesConfigPath)
		if (result == null) {
			curator.create()
				.creatingParentsIfNeeded()
				.forPath(
					failOnInsufficientResourcesConfigPath,
					InventoryServiceProperties.Defaults.FAIL_ON_INSUFFICIENT_RESOURCES.toString().encodeToByteArray()
				)
		}
	}

	private fun registerWatchers() {
		curator.watchers().add()
			.usingWatcher(FailOnInsufficientResourcesNodeWatcher(curator, properties))
			.forPath(getServicePropertyConfigPath(InventoryServiceProperties.FAIL_ON_INSUFFICIENT_RESOURCES_KEY))
	}

	private fun getServicePropertyConfigPath(propertyKey: String): String =
		"/gazdabolt/config/inventory-service/${InventoryServiceProperties.PROPERTIES_PREFIX}.$propertyKey"

}