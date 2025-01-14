package com.android.layout.algorithm

internal enum class LayoutOrientation {
    VERTICAL,
    HORIZONTAL,
    ;

    fun toggle(layoutOrientation: LayoutOrientation): LayoutOrientation =
        if (layoutOrientation == HORIZONTAL) VERTICAL else HORIZONTAL
}