package dev.besi.gazdabolt.backend.inventory.config

import graphql.schema.GraphQLScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
open class GraphQLConfig {

	@Bean
	open fun runtimeWiringConfigurer(): RuntimeWiringConfigurer {
		val longScalar = GraphQLScalarType.newScalar()
			.name("Long")
			.coercing(GraphqlLongCoercing())
			.build()

		return RuntimeWiringConfigurer { it.scalar(longScalar) }
	}

}