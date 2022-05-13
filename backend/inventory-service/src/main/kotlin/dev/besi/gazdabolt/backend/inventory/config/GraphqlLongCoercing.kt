package dev.besi.gazdabolt.backend.inventory.config

import graphql.language.IntValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingSerializeException
import java.math.BigDecimal
import java.math.BigInteger

class GraphqlLongCoercing : Coercing<Long, Long> {

	private fun convertImpl(input: Any): Long? {
		return when (input) {
			is Long -> {
				input
			}
			is Number, is String -> {
				val value: BigDecimal =
					try {
						BigDecimal(input.toString())
					} catch (e: NumberFormatException) {
						return null
					}
				try {
					value.longValueExact()
				} catch (e: ArithmeticException) {
					null
				}
			}
			else -> {
				null
			}
		}
	}

	override fun serialize(dataFetcherResult: Any): Long = convertImpl(dataFetcherResult)
			?: throw CoercingSerializeException(
				"Expected type 'Long' but was '${dataFetcherResult.javaClass.simpleName}'."
			)

	override fun parseValue(input: Any): Long = serialize(input)

	override fun parseLiteral(input: Any): Long {
		if (input !is IntValue) {
			throw CoercingParseLiteralException(
				"Expected AST type 'IntValue' but was '${input.javaClass.simpleName}'."
			)
		}

		val value: BigInteger = input.value

		if (value < BigInteger.valueOf(Long.MIN_VALUE) || value > BigInteger.valueOf(Long.MAX_VALUE)) {
			throw CoercingParseLiteralException(
				"Expected value to be in the Integer range but it was '$value'"
			)
		}

		return value.toLong()
	}

}
