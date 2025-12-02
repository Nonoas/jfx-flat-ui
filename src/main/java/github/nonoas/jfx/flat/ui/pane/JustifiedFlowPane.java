package github.nonoas.jfx.flat.ui.pane;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.List;

public class JustifiedFlowPane extends Pane {

    private final double hgap;
    private final double vgap;
    private final double itemMinWidth;

    /**
     * @param hgap         组件间的水平间隔
     * @param vgap         行间的垂直间隔
     * @param itemMinWidth 组件的最小宽度（布局计算的基准）
     */
    public JustifiedFlowPane(double hgap, double vgap, double itemMinWidth) {
        this.hgap = hgap;
        this.vgap = vgap;
        this.itemMinWidth = Math.max(1.0, itemMinWidth);

        this.setMinWidth(itemMinWidth);
        this.setPrefWidth(Region.USE_COMPUTED_SIZE);
    }

    @Override
    protected void layoutChildren() {
        final List<Node> children = getManagedChildren();
        if (children.isEmpty()) return;
        
        final double width = getWidth();
        final Insets insets = getInsets();
        final double leftBoundary = insets.getLeft();
        final double rightBoundary = width - insets.getRight();
        // 可用宽度（扣除 insets）
        double usableWidth = rightBoundary - leftBoundary;

        int rowCount = Math.max(1, (int) ((usableWidth + hgap) / (itemMinWidth + hgap)));

        double itemWidth = (usableWidth - hgap * (rowCount - 1)) / rowCount;

        double x = leftBoundary;
        double y = insets.getTop();

        int countInRow = 0;
        double rowHeight = 0;

        for (Node child : children) {

            double childPrefHeight = child.prefHeight(itemWidth);

            // 布局节点
            child.resizeRelocate(x, y, itemWidth, childPrefHeight);

            // 行高更新
            rowHeight = Math.max(rowHeight, childPrefHeight);

            countInRow++;
            if (countInRow == rowCount) {
                // 换行
                x = leftBoundary;
                y += rowHeight + vgap;
                rowHeight = 0;
                countInRow = 0;
            } else {
                // 下一个位置
                x += itemWidth + hgap;
            }
        }
    }
}