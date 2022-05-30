package dev.besi.gazdabolt.backend.inventory.config

import graphql.schema.GraphQLInputObjectField
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.errors.DirectiveIllegalLocationError
import javax.validation.ValidationException

class SizeDirectiveWiring : SchemaDirectiveWiring {
	companion object {
		const val DIRECTIVE_NAME = "Size"
	}

	override fun onInputObjectField(
		environment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectField>?
	): GraphQLInputObjectField {
		val field = environment!!.element

		environment.codeRegistry.inputFieldTransformer(DIRECTIVE_NAME) { directive, inputField, value ->
			if (value is String) {
				val min = directive.getArgument("min").getValue<Int>()
				val max = directive.getArgument("max").getValue<Int>()

				if (value.length < min) {
					throw ValidationException("Minimum length requirement of $min characters is not fulfilled for field ${inputField.name}.")
				}
				if (value.length > max) {
					throw ValidationException("Maximum length requirement of $max characters is not fulfilled for field ${inputField.name}")
				}

				value
			} else {
				throw DirectiveIllegalLocationError(environment.directive.definition, inputField.name)
			}
		}

		return field
	}
}

class NonNegativeDirectiveWiring : SchemaDirectiveWiring {
	companion object {
		const val DIRECTIVE_NAME = "NonNegative"
	}

	override fun onInputObjectField(
		environment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectField>?
	): GraphQLInputObjectField {
		val field = environment!!.element

		environment.codeRegistry.inputFieldTransformer(DIRECTIVE_NAME) { _, inputField, value ->
			when (value) {
				is Int -> {
					if (value < 0) {
						throw exception(inputField.name, value)
					}
					value
				}
				is Long -> {
					if (value < 0) {
						throw exception(inputField.name, value)
					}
					value
				}
				is Float -> {
					if (value < 0) {
						throw exception(inputField.name, value)
					}
					value
				}
				is Double -> {
					if (value < 0) {
						throw exception(inputField.name, value)
					}
					value
				}
				else -> throw DirectiveIllegalLocationError(environment.directive.definition, inputField.name)
			}
		}

		return field
	}

	private fun exception(fieldName: String, value: Number): Throwable =
		ValidationException("Non-negative requirement not fulfilled for field $fieldName: $value")
}
