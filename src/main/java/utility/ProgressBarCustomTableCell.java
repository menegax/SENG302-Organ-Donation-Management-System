package utility;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import model.PatientOrgan;

public class ProgressBarCustomTableCell {

    /**
     * Creates a new table cell with a progress bar bound to it and starts the service
     * @param column - column to add the cells to
     * @return - the new table cell with the progress bar added
     */
    public static TableCell<PatientOrgan, ProgressTask> getCell(TableColumn<PatientOrgan, ProgressTask> column){
        ProgressBar progressBar = new ProgressBar(1.0F);
        TableCell<PatientOrgan, ProgressTask> tableCell = new TableCell<PatientOrgan, ProgressTask>() {
            @Override
            protected void updateItem(ProgressTask item, boolean empty){
                super.updateItem(item, empty);
                if (item != null) {
                    item.setProgressBar(progressBar);
                    progressBar.setMinHeight(15.0);
                    item.setColor(item.getElapsedTime() / item.getOrgan().getOrganUpperBoundSeconds());
                    progressBar.progressProperty().bind(item.progressProperty());
                    progressBar.minWidthProperty().bind(column.widthProperty().subtract(10));
                    CachedThreadPool.getCachedThreadPool().getThreadService().submit(item);
                }
            }
        };
        tableCell.graphicProperty().bind(Bindings.when(tableCell.emptyProperty()).then((Node) null).otherwise(progressBar));
        return  tableCell;
    }
}
