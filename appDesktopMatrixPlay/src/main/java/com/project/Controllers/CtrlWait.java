package com.project.Controllers;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;

public class CtrlWait implements Initializable {

    @FXML
    public Label txtTitle;
    public ProgressIndicator loaderEspera;
    @FXML
    public Label txtPlayer0;
    public ImageView blackPersona;

    @FXML
    public Label txtPlayer1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}