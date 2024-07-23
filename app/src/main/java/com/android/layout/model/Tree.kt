package com.android.layout.model

@JvmInline
value class Tree<T> internal constructor(val root: Node<T>) {
    class Node<T>(val data: T, elements: List<Node<T>> = listOf()) {

        private val _elements = elements.toMutableList()
        val elements: List<Node<T>>
            get() = _elements

        fun add(node: Node<T>) = run { _elements += node }

        fun remove(node: Node<T>) = run { _elements -= node }
    }
}

data class Element internal constructor(
    val name: String?,
    val value: Double,
    val percentage: Double,
    val color: Long,
)