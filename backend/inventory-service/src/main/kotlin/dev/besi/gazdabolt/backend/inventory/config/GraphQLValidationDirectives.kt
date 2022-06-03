package dev.besi.gazdabolt.backend.inventory.config

import graphql.language.DirectiveDefinition
import graphql.schema.DirectiveInputValueTransformer
import graphql.schema.GraphQLArgument
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

		environment.codeRegistry.inputDirectiveTransformer(DIRECTIVE_NAME) { directive, inputField, value ->
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
	/*
	The transformers registered in onInputObjectField and onArgument will override each other, one of the returned
	transformer objects will get lost. However, those transformers are the same anyway, so no information lost all in
	all.
	 */

	companion object {
		const val DIRECTIVE_NAME = "NonNegative"
	}

	override fun onInputObjectField(
		environment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectField>?
	): GraphQLInputObjectField {
		val field = environment!!.element

		environment.codeRegistry.inputDirectiveTransformer(
			DIRECTIVE_NAME,
			getTransformer(environment.directive.definition!!)
		)

		return field
	}

	override fun onArgument(environment: SchemaDirectiveWiringEnvironment<GraphQLArgument>?): GraphQLArgument {
		val field = environment!!.element

		environment.codeRegistry.inputDirectiveTransformer(
			DIRECTIVE_NAME,
			getTransformer(environment.directive.definition!!)
		)

		return field
	}

	private fun getTransformer(directiveDefinition: DirectiveDefinition) =
		DirectiveInputValueTransformer { _, input, value ->
			when (value) {
				is Int -> {
					if (value < 0) {
						throw exception(input.name, value)
					}
					value
				}
				is Long -> {
					if (value < 0) {
						throw exception(input.name, value)
					}
					value
				}
				is Float -> {
					if (value < 0) {
						throw exception(input.name, value)
					}
					value
				}
				is Double -> {
					if (value < 0) {
						throw exception(input.name, value)
					}
					value
				}
				else -> throw DirectiveIllegalLocationError(directiveDefinition, input.name)
			}
		}

	private fun exception(fieldName: String, value: Number): Throwable =
		ValidationException("Non-negative requirement not fulfilled for field $fieldName: $value")
}
