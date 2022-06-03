package dev.besi.gazdabolt.backend.inventory

import org.apache.curator.framework.api.PathAndBytesable

fun <T> PathAndBytesable<T>.forPath(path: String, value: String): T =
	this.forPath(path, value.toByteArray())
