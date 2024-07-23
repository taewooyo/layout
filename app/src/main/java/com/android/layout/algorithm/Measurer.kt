package com.android.layout.algorithm

interface Measurer {
    fun measureNodes(values: List<Double>, width: Int, height: Int): List<TreemapNode>
}