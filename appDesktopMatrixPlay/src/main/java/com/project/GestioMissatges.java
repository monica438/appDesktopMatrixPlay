package com.project;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.project.Controllers.CtrlPlay;
import com.project.Controllers.CtrlWait;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

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

        String type = msgObj.optString("type", "");
        switch (type) {
            case "broadcastHola":
                handleBroadcastHola(msgObj);
                break;

            case "jocData":
                handleJocData(msgObj);
                break;

            case "error":
                handleError(msgObj);
                break;

            case "countdown":
                handleCountdown(msgObj);
                break;

            default:
                System.out.println("Missatge desconegut: " + response);
                break;
        }
    }

    private static void handleBroadcastHola(JSONObject msgObj) {
        String missatgeOK = msgObj.optString("value", "desconegut");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("INFORMACIÓ");
            alert.setHeaderText(null);
            alert.setContentText(missatgeOK);
            alert.showAndWait();
        });
    }

    private static void handleJocData(JSONObject msgObj) {
        Main.clientName = msgObj.optString("clientName", Main.clientName);

        // Actualizar lista de jugadores
        JSONArray jsonClients = msgObj.optJSONArray("Jugadors");
        Main.clients = new ArrayList<>();
        if (jsonClients != null) {
            for (int i = 0; i < jsonClients.length(); i++) {
                String name = jsonClients.getString(i);
                Main.clients.add(new ClientData(name, "gray"));
            }
        }

        JSONArray jsonObjects = msgObj.optJSONArray("objectsList");
        Main.objects = new ArrayList<>();
        if (jsonObjects != null) {
            for (int i = 0; i < jsonObjects.length(); i++) {
                JSONObject o = jsonObjects.getJSONObject(i);
                Main.objects.add(new GameObject(
                        o.getString("id"),
                        o.getInt("x"),
                        o.getInt("y"),
                        o.getInt("cols"),
                        o.getInt("rows"),
                        o.getString("color")
                ));
            }
        }

        Platform.runLater(() -> {
            if (UtilsViews.getActiveView().equals("ViewConfig")) {
                UtilsViews.setViewAnimating("ViewWait");
            }

            if (Main.ctrlWait != null) {
                if (Main.clients.size() > 0) Main.ctrlWait.txtPlayer0.setText(Main.clients.get(0).name);
                if (Main.clients.size() > 1){
                    Main.ctrlWait.txtPlayer1.setText(Main.clients.get(1).name);
                    Main.ctrlWait.blackPersona.setImage(new Image("assets/icon_negro.png"));
                } 
            }

            if (Main.ctrlPlay != null && Main.clients.size() > 1) {
                Main.ctrlPlay.title.setText(Main.clients.get(0).name + " vs " + Main.clients.get(1).name);
                
            }

            Main.j1Points = msgObj.optInt("J1Punts", 0);
            Main.j2Points = msgObj.optInt("J2Punts", 0);
        });
    }

    private static void handleError(JSONObject msgObj) {
        String missatge = msgObj.optString("value", "Error desconegut");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText(missatge);
            alert.showAndWait();
        });
    }

private static void handleCountdown(JSONObject msgObj) {
    int value = msgObj.optInt("value", 0);

    Platform.runLater(() -> {
        final String txtFinal = (value == 0) ? "GO" : String.valueOf(value);

        if (Main.ctrlWait != null && Main.ctrlWait.txtTitle != null) {
            Main.ctrlWait.txtTitle.setText(txtFinal);
        }

        if (value == 0 && UtilsViews.getActiveView().equals("ViewWait")) {
            UtilsViews.setViewAnimating("ViewPlay");
        }
    });
}

}
