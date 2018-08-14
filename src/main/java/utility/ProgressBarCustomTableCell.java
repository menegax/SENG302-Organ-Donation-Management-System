package utility;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import model.PatientOrgan;

public class ProgressBarCustomTableCell {

    public static TableCell<PatientOrgan, ProgressTask> getCell(TableColumn<PatientOrgan, ProgressTask> column){
        ProgressBar progressBar = new ProgressBar(1.0F);
        TableCell<PatientOrgan, ProgressTask> tableCell = new TableCell<PatientOrgan, ProgressTask>() {
            @Override
            protected void updateItem(ProgressTask item, boolean empty){
                super.updateItem(item, empty);
                if (item != null) {
                    progressBar.progressProperty().bind(item.progressProperty());
                    progressBar.minWidthProperty().bind(column.widthProperty().subtract(10));
                    item.setProgressBar(progressBar);
                    CachedThreadPool.getCachedThreadPool().getThreadService().submit(item);
                }
            }
        };
        tableCell.graphicProperty().bind(Bindings.when(tableCell.emptyProperty()).then((Node) null).otherwise(progressBar));
        return  tableCell;
    }
}
