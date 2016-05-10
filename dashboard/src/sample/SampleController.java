package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class SampleController {

    @FXML
    private void actionNew(final ActionEvent event)
    {
        System.out.println("Menu Item : NEW");
    }

}
