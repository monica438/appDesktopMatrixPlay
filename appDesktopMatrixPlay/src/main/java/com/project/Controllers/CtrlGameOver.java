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
        // Inicialización si es necesaria
    }

    /** Desconecta al cliente y cierra la aplicación */
    public void Sortir() {
        // 1. Crear el mensaje de desconexión
        JSONObject msg = new JSONObject();
        msg.put("type", "desconecta");

        // 2. Obtener instancia de WebSocket
        UtilsWS ws = UtilsWS.getSharedInstance("wss://" + Main.ctrlConfig.txtHost.getText() + "443");

        // 3. Enviar al servidor si está abierto
        if (ws.isOpen()) {
            ws.safeSend(msg.toString());
        }

        // 4. Evitar reconexión automática y cerrar WebSocket
        ws.forceExit();

        // 5. Cerrar la aplicación JavaFX
        Platform.exit();
        System.exit(0);
    }

    /** Desconecta al cliente y vuelve a la vista de configuración */
    public void Enrere() {
        Platform.runLater(() -> {
            if (UtilsViews.getActiveView().equals("ViewGameOver")) {
                // Cambiar vista a configuración
                UtilsViews.setViewAnimating("ViewConfig");

                // 1. Crear mensaje de desconexión
                JSONObject msg = new JSONObject();
                msg.put("type", "desconecta");

                // 2. Obtener instancia de WebSocket
                UtilsWS ws = UtilsWS.getSharedInstance("wss://" + Main.ctrlConfig.txtHost.getText() + "443");

                // 3. Enviar mensaje y evitar reconexión automática
                if (ws.isOpen()) {
                    ws.safeSend(msg.toString());
                }
                ws.forceExit();

                // Nota: no se cierra la app, solo se vuelve a la configuración
            }
        });
    }
}
