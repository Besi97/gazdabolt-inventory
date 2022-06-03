package dev.besi.gazdabolt.backend.inventory.config

import dev.besi.gazdabolt.backend.inventory.error.IllegalArgumentError
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.DataFetcherExceptionResolver
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
open class GraphQLConfig {

	@Bean
	open fun getValidationExceptionHandler(): DataFetcherExceptionResolver =
		DataFetcherExceptionResolverAdapter.from { exception, environment ->
			if (exception is CoercingParseValueException) {
				exception
			} else {
				null
			}
		}

	@Bean
	open fun getIllegalArgumentErrorHandler(): DataFetcherExceptionResolver =
		DataFetcherExceptionResolverAdapter.from { exception, _ ->
			if (exception is IllegalArgumentError) {
				exception
			} else {
				null
			}
		}

	@Bean
	open fun runtimeWiringConfigurer(): RuntimeWiringConfigurer {
		val longScalar = GraphQLScalarType.newScalar()
			.name("Long")
			.coercing(GraphqlLongCoercing())
			.build()

		return RuntimeWiringConfigurer {
			it
				.scalar(longScalar)
				.directive(TrimDirectiveWiring.DIRECTIVE_NAME, TrimDirectiveWiring())
				.directive(SizeDirectiveWiring.DIRECTIVE_NAME, SizeDirectiveWiring())
				.directive(NonNegativeDirectiveWiring.DIRECTIVE_NAME, NonNegativeDirectiveWiring())
		}
	}

}