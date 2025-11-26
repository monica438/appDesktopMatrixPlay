package com.project.Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import com.project.Main;
import com.project.UtilsViews;
import com.project.UtilsWS;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CtrlGameOver implements Initializable {

    @FXML
    public Label txtTitle;
    @FXML
    public Button btnSortir; 
    @FXML
    public Button btnEnrere; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void Sortir() {
        JSONObject msg = new JSONObject();
        msg.put("type", "desconecta");
        UtilsWS ws = UtilsWS.getSharedInstance("wss://" + Main.ctrlConfig.txtHost.getText() + "443");
        if (ws.isOpen()) {
            ws.safeSend(msg.toString());
        }
        Main.ctrlWait.reset();
        ws.forceExit();
        Platform.exit();
        System.exit(0);
    }

    public void Enrere() {
        Platform.runLater(() -> {
            if ("ViewGameOver".equals(UtilsViews.getActiveView())) {
                JSONObject msg = new JSONObject();
                msg.put("type", "desconecta");
                UtilsWS ws = Main.wsClient; 
                Main.ctrlWait.reset();
                if (ws != null && ws.isOpen()) {
                    ws.safeSend(msg.toString());
                }
                if (ws != null) {
                    ws.forceExit(); 
                }
                UtilsViews.setViewAnimating("ViewConfig");
            }
        });
    }

}
