package dev.besi.gazdabolt.backend.inventory.config

import graphql.schema.GraphQLInputObjectField
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.errors.DirectiveIllegalLocationError

class TrimDirectiveWiring : SchemaDirectiveWiring {
	companion object {
		const val DIRECTIVE_NAME = "Trim"
	}

	override fun onInputObjectField(
		environment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectField>?
	): GraphQLInputObjectField {
		val field = environment!!.element

		environment.codeRegistry.inputFieldTransformer(DIRECTIVE_NAME) { _, inputField, value ->
			when (value) {
				is String -> {
					value.trim()
				}
				else -> {
					throw DirectiveIllegalLocationError(environment.directive.definition, inputField.name)
				}
			}
		}

		return field
	}
}
