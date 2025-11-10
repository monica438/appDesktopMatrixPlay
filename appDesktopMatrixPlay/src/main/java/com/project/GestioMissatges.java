package com.project;

import org.json.JSONObject;

import javafx.application.Platform;
import javafx.scene.control.Alert;




public class GestioMissatges {

        public static void crearJugador(String clientName, UtilsWS wsClient) {


            JSONObject nom = new JSONObject();
            nom.put("type", "setName");
            nom.put("value", clientName);
            wsClient.safeSend(nom.toString());
        }

         public static void processMessage(String response) {
        JSONObject msgObj;
        try {
            msgObj = new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        switch (msgObj.getString("type")) {
            case "broadcastHola":
                String missatgeOK = msgObj.optString("value", "desconegut");
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("INFORMACIÓ");
                    alert.setHeaderText(null);
                    alert.setContentText(missatgeOK);
                    alert.showAndWait();
                });                
                break;
            case "error" :
                String missatge = msgObj.optString("value", "Error desconegut");
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("ERROR");
                    alert.setHeaderText(null);
                    alert.setContentText(missatge);
                    alert.showAndWait();
                });
            


            

            default:
                System.out.println("Missatge desconegut: " + response);
                break;
        }
    }

}