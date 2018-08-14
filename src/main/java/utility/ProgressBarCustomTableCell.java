package utility;

import controller.GUIAvailibleOrgans;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class ProgressBarCustomTableCell {

    public static TableCell<GUIAvailibleOrgans.PatientOrgan, ProgressTask> getCell(TableColumn<GUIAvailibleOrgans.PatientOrgan, ProgressTask> column){
        ProgressBar progressBar = new ProgressBar(1.0F);
        progressBar.setStyle("-fx-accent: green");
        TableCell<GUIAvailibleOrgans.PatientOrgan, ProgressTask> tableCell = new TableCell<GUIAvailibleOrgans.PatientOrgan, ProgressTask>() {
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
