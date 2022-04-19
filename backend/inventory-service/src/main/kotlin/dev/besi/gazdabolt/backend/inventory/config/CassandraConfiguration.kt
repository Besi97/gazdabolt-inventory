package dev.besi.gazdabolt.backend.inventory.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories


@Configuration
@EnableCassandraRepositories(basePackages = ["dev.besi.gazdabolt.backend.inventory.persistence.repositories"])
open class CassandraConfiguration : AbstractCassandraConfiguration() {
	companion object {
		val KEYSPACE_NAME = "inventory"
		val REPLICATION_FACTOR: Long = 1
	}

	override fun getKeyspaceName(): String = KEYSPACE_NAME

	override fun getEntityBasePackages(): Array<String> {
		return arrayOf("dev.besi.gazdabolt.backend.inventory.persistence.entities")
	}

	override fun getKeyspaceCreations(): MutableList<CreateKeyspaceSpecification> {
		return mutableListOf(
			CreateKeyspaceSpecification.createKeyspace(KEYSPACE_NAME)
				.with(KeyspaceOption.DURABLE_WRITES, true)
				.withSimpleReplication(REPLICATION_FACTOR)
		)
	}

	override fun getSchemaAction(): SchemaAction = SchemaAction.CREATE_IF_NOT_EXISTS
}
