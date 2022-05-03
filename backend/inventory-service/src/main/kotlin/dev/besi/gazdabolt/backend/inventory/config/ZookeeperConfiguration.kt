package dev.besi.gazdabolt.backend.inventory.config

import org.apache.curator.framework.CuratorFramework
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.Watcher.Event.EventType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ZookeeperConfiguration {

	companion object {
		private var curatorWatchersInitialized = false
	}

	@Autowired
	private lateinit var properties: InventoryServiceProperties

	@Autowired
	private lateinit var curator: CuratorFramework

	@EventListener
	fun addPropertyWatchers(refreshedEvent: ContextRefreshedEvent) {
		if (curatorWatchersInitialized) {
			return
		}

		curator.watchers().add()
			.usingWatcher(FailOnInsufficientResourcesNodeWatcher(curator, properties))
			.forPath(
				"/gazdabolt/inventory-service/gazdabolt.inventory-service.failOnInsufficientResources"
			)

		curatorWatchersInitialized = true
	}

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

}