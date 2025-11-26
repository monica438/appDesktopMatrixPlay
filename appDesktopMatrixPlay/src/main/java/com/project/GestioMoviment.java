package com.project;

import org.json.JSONObject;

import javafx.scene.input.KeyEvent;
public class GestioMoviment {

    private final UtilsWS wsClient;
    private String direccioActual = "none";

    public GestioMoviment(UtilsWS wsClient) {
        this.wsClient = wsClient;
    }

    public void enviarDireccio(String direccio){
        JSONObject json = new JSONObject();
        json.put("type", "move");
        json.put("direction", direccio);
        wsClient.safeSend(json.toString());
    }

    public void keyEvent(KeyEvent evt) {

        // Quan apretem una tecla
        if (evt.getEventType() == KeyEvent.KEY_PRESSED) {
            switch (evt.getCode()) {
                case UP:    direccioActual = "up"; break;
                case DOWN:  direccioActual = "down"; break;
            }
            enviarDireccio(direccioActual);
        }

        // Quan deixem anar la tecla
        if (evt.getEventType() == KeyEvent.KEY_RELEASED) {
            switch (evt.getCode()) {
                case UP:
                    if (direccioActual.equals("up")) direccioActual = "none";
                    break;
                case DOWN:
                    if (direccioActual.equals("down")) direccioActual = "none";
                    break;
            }
            enviarDireccio(direccioActual);
        }
    }
}
