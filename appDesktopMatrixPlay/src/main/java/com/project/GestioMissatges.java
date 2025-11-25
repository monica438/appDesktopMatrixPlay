package com.project;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.json.Json;
import jakarta.json.JsonObject;
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
            case "RegistreOK":
                System.out.println("a");
                break;
            case "gameOver":
                handleGameOver(msgObj);

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

    private static void handleGameOver(JSONObject msgObj) {
        String winner = msgObj.optString("winner", "desconegut");
        Platform.runLater(() -> {
            if (UtilsViews.getActiveView().equals("ViewPlay")) {
                UtilsViews.setViewAnimating("ViewGameOver");
                if (winner.equals(Main.clientName)){
                    Main.ctrlGameOver.txtTitle.setText("VICTORIA");

                }else{
                    Main.ctrlGameOver.txtTitle.setText("DERROTA");

                }
            }
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


        int ampladaCanvas = (int) Main.ctrlPlay.canvas.getWidth();
        int alcadaCanvas = (int) Main.ctrlPlay.canvas.getHeight();

        JSONArray jsonObjects = msgObj.optJSONArray("objectsList");
        Main.objects = new ArrayList<>();
        System.out.println("WIDTH" + ampladaCanvas);
        System.out.println("ALTURA" + alcadaCanvas);
        if (jsonObjects != null) {
            for (int i = 0; i < jsonObjects.length(); i++) {
                JSONObject o = jsonObjects.getJSONObject(i);
                Main.objects.add(GameObject.fromJSONScaledToGameArea(o, ampladaCanvas, alcadaCanvas, 0, 600, 500));
            }

        }

        Platform.runLater(() -> {
            if (UtilsViews.getActiveView().equals("ViewConfig")) {
                UtilsViews.setViewAnimating("ViewWait");
            }

            if (Main.ctrlWait != null) {
                if (Main.clients.size() > 0) {
                    Main.ctrlWait.txtPlayer0.setText(Main.clients.get(0).name);
                    Main.ctrlWait.loaderEspera.setVisible(true);



                }
                if (Main.clients.size() > 1){
                    Main.ctrlWait.txtPlayer1.setText(Main.clients.get(1).name);
                    Main.ctrlWait.blackPersona.setImage(new Image("assets/icon_negro.png"));
                    Main.ctrlWait.loaderEspera.setVisible(false);

                } 
            }

            if (Main.ctrlPlay != null && Main.clients.size() > 1) {
                Main.ctrlPlay.title.setText(" vs ");
                Main.ctrlPlay.j1Nom.setText(Main.clients.get(0).name);
                Main.ctrlPlay.j2Nom.setText(Main.clients.get(1).name);


            }
                Main.ctrlPlay.j1Punts.setText("Punts: " + String.valueOf(msgObj.optInt("J1Punts", 0)));
                Main.ctrlPlay.j2Punts.setText("Punts: " + String.valueOf(msgObj.optInt("J2Punts", 0)));

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


            if (Main.ctrlCountdown != null && Main.ctrlCountdown.countdownLabel != null) {
                Main.ctrlCountdown.actualitzarCountdown(txtFinal);
            }

            if (UtilsViews.getActiveView().equals("ViewWait") && value > 0){
                    UtilsViews.setViewAnimating("ViewCountdown");

            }
            if (value == 0 && UtilsViews.getActiveView().equals("ViewCountdown")) {
                UtilsViews.setViewAnimating("ViewPlay");

            }
        });
    }
    
    public static void crearEspectador(String nom, UtilsWS ws) {
        JsonObject json = Json.createObjectBuilder()
            .add("type", "raspberryEspectador")
            .add("name", nom)
            .build();

            ws.safeSend(json.toString());
    }

    }
