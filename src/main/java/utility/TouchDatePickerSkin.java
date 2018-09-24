package utility;

import com.sun.javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class TouchDatePickerSkin extends DatePickerSkin {

    public TouchDatePickerSkin(DatePicker datePicker, Pane pane) {
        super(datePicker);
        StackPane arrowButtonAlias = (StackPane) datePicker.lookup(".arrow-button");
        arrowButtonAlias.setStyle("-fx-padding: 0 15 0 15;");
        getPopupContent().focusedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(datePicker.getLayoutX());
            System.out.println(datePicker.getLayoutY());
            System.out.println(datePicker.getTranslateX());
            System.out.println(datePicker.getTranslateY());
            System.out.println(datePicker.getScaleX());
            System.out.println(datePicker.getScaleY());

            System.out.println(pane.getLayoutX());
            System.out.println(pane.getLayoutY());
            System.out.println(pane.getTranslateX());
            System.out.println(pane.getTranslateY());
            System.out.println(pane.getScaleX());
            System.out.println(pane.getScaleY());

            System.out.println(getPopupContent().getLayoutX());
            System.out.println(getPopupContent().getLayoutY());
            System.out.println(getPopupContent().getTranslateX());
            System.out.println(getPopupContent().getTranslateY());
            System.out.println(getPopupContent().getScaleX());
            System.out.println(getPopupContent().getScaleY());

            getPopupContent().setRotate(pane.getRotate());
            getPopupContent().setTranslateX((223 * pane.getScaleX()) - datePicker.getLayoutX());
            getPopupContent().setTranslateX((389 * pane.getScaleY()) - datePicker.getLayoutY());
        });
        getPopup().setAutoHide(false);
        pane.setOnTouchPressed(event -> {
            hide();
        });
        pane.setOnMouseClicked(event -> {
            hide();
        });
        datePicker.setOnTouchPressed(event -> {
            show();
        });
        datePicker.setOnMouseClicked(event -> {
            show();
        });
    }
}
