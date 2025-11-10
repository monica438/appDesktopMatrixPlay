package com.project.Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.project.Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CtrlConfig implements Initializable {


    @FXML
    public TextField txtHost;

    @FXML
    public TextField usernameText;

    @FXML
    public Label txtMessage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void connectToServer() {
        Main.clientName = usernameText.getText().trim();
        Main.connectToServer();
    }

    public String getUserText(){
        return usernameText.getText();
    }
}