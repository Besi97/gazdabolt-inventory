package dev.besi.gazdabolt.backend.inventory.config

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["dev.besi.gazdabolt.backend.inventory.persistence"])
open class MongoDbConfiguration : AbstractMongoClientConfiguration() {

	@Autowired
	lateinit var mongoProperties: MongoProperties

	override fun getDatabaseName(): String = mongoProperties.database

	override fun configureClientSettings(builder: MongoClientSettings.Builder) {
		builder.applyToClusterSettings {
			it.hosts(listOf(ServerAddress(mongoProperties.host, mongoProperties.port)))
		}

		mongoProperties.username?.let {
			builder.credential(
				MongoCredential.createCredential(
					it, mongoProperties.database, mongoProperties.password
				)
			)
		}
	}

}