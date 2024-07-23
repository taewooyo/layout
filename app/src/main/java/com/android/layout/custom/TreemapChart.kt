package com.android.layout.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.android.layout.algorithm.Measurer
import com.android.layout.algorithm.SquarifiedMeasurer
import com.android.layout.model.Element
import com.android.layout.model.Tree

internal val LocalVolcanoMeasurer = compositionLocalOf<Measurer> { SquarifiedMeasurer() }

@Composable
internal fun TreemapChart(
    tree: Tree<Element>,
    selectedItem: Element?,
    selectedBorderColor: Color,
    modifier: Modifier = Modifier,
    onClickItem: (Element) -> Unit,
    borderColor: Color,
) {
    TreemapChart(
        data = tree,
        evaluateItem = Element::value,
        modifier = modifier,
        borderColor = borderColor,
    ) { node, _ ->
        if (node.elements.isNotEmpty()) {
            Element(
                item = node.data,
                selectedItem = selectedItem as? Element,
                selectedBorderColor = selectedBorderColor,
                onClick = onClickItem,
                borderColor = borderColor,
            )
        }
    }
}

@Composable
private fun Element(
    item: Element,
    selectedItem: Element?,
    selectedBorderColor: Color,
    modifier: Modifier = Modifier,
    onClick: (Element) -> Unit,
    borderColor: Color,
) {
    Box(
        modifier = modifier
            .clickable { onClick(item) }
            .border(
                (0.5).dp,
                if (item.name == selectedItem?.name) selectedBorderColor else borderColor,
            )
            .background(Color(item.color)),
        contentAlignment = Alignment.Center,
    ) {}
}

@Composable
internal fun <T> TreemapChart(
    data: Tree<T>,
    evaluateItem: (T) -> Double,
    modifier: Modifier = Modifier,
    borderColor: Color,
    nodeContent: @Composable (
        data: Tree.Node<T>,
        groupContent: @Composable (Tree.Node<T>) -> Unit,
    ) -> Unit,
) {
    Box(modifier) {
        TreemapChartNode(
            data = data.root,
            evaluateItem = evaluateItem,
            nodeContent = nodeContent,
            borderColor = borderColor,
        )
    }
}

@Composable
internal fun <T> TreemapChartNode(
    data: Tree.Node<T>,
    evaluateItem: (T) -> Double,
    borderColor: Color,
    nodeContent: @Composable (
        data: Tree.Node<T>,
        groupContent: @Composable (Tree.Node<T>) -> Unit,
    ) -> Unit,
) {
    nodeContent(data) { node ->
        TreemapChartLayout(
            data = node,
            evaluateItem = evaluateItem,
        ) { elementNode ->
            TreemapChartNode(
                data = elementNode,
                evaluateItem = evaluateItem,
                nodeContent = nodeContent,
                borderColor = borderColor,
            )
        }
    }
}

@Composable
internal fun <T> TreemapChartLayout(
    data: Tree.Node<T>,
    evaluateItem: (T) -> Double,
    modifier: Modifier = Modifier,
    itemContent: @Composable (Tree.Node<T>) -> Unit,
) {
    val treemapChartMeasurer = LocalVolcanoMeasurer.current
    Layout(
        content = {
            data.elements.forEach { node ->
                itemContent(node)
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val nodes = treemapChartMeasurer.measureNodes(
            data.elements.map { evaluateItem(it.data) },
            constraints.maxWidth,
            constraints.maxHeight,
        )
        val placeables = measurables.mapIndexed { index, measurable ->
            measurable.measure(Constraints.fixed(nodes[index].width, nodes[index].height))
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x = nodes[index].offsetX, y = nodes[index].offsetY)
            }
        }
    }
}