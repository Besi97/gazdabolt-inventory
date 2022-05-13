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
class ZookeeperConfiguration {

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

	@Autowired
	private lateinit var properties: InventoryServiceProperties

	@Autowired
	private lateinit var curator: CuratorFramework

	@Autowired
	private lateinit var serviceRegistration: ZookeeperAutoServiceRegistration

	@EventListener(ApplicationStartedEvent::class)
	fun startServiceRegistration(event: ApplicationStartedEvent) {
		serviceRegistration.start()

		curator.watchers().add()
			.usingWatcher(FailOnInsufficientResourcesNodeWatcher(curator, properties))
			.forPath(
				"/gazdabolt/config/inventory-service/gazdabolt.inventory-service.failOnInsufficientResources"
			)
	}

}