package github.nonoas.jfx.flat.ui.control;

import javafx.beans.property.Property;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;


/**
 * 可以设置对其方式的表格列
 *
 * @author huangshengsheng
 * @date 2025/8/30 10:06
 */

public class AlignedTableColumn<S, T> extends TableColumn<S, T> {

    public enum Alignment {
        LEFT(Pos.CENTER_LEFT, "CENTER-LEFT"),
        CENTER(Pos.CENTER, "CENTER"),
        RIGHT(Pos.CENTER_RIGHT, "CENTER-RIGHT");

        private final Pos headerPos;
        private final String cellCss;

        Alignment(Pos headerPos, String cellCss) {
            this.headerPos = headerPos;
            this.cellCss = cellCss;
        }

        public Pos getHeaderPos() {
            return headerPos;
        }

        public String getCellCss() {
            return cellCss;
        }
    }

    public AlignedTableColumn(String title, Alignment headerAlign, Alignment cellAlign) {
        super(title);

        // 设置表头对齐
        Label headerLabel = new Label(title);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(headerAlign.getHeaderPos());
        setText(null); // 清空默认表头文本
        setGraphic(headerLabel);

        // 设置单元格对齐
        setCellFactory(new Callback<>() {
            @Override
            public TableCell<S, T> call(TableColumn<S, T> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // 确保无论是 Number 还是 String 都能显示
                            setText(item.toString());
                        }
                        // ⚠️ 注意：即使为空也要设置对齐，否则会丢失
                        setStyle("-fx-alignment: " + cellAlign.getCellCss() + ";");
                    }
                };
            }
        });
    }

    // 便捷构造：表头和单元格对齐一致
    public AlignedTableColumn(String title, Alignment alignment) {
        this(title, alignment, alignment);
    }
}
