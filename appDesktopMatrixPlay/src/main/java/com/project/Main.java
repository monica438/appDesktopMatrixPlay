package com.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.project.Controllers.CtrlConfig;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    public static UtilsWS wsClient;
    public static String clientName = "";
    public static String rivalName = "";

    public static CtrlConfig ctrlConfig;

    public static final String filePath = "Desktop/dades/dades.json";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        initViews();
        Scene scene = new Scene(UtilsViews.parentContainer);
        stage.setScene(scene);
        configureStage(stage);
    }

    private void initViews() {
        try {
            UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
            UtilsViews.addView(getClass(), "ViewConfig", "/assets/viewConfig.fxml");

            // Obtener controlador primero
            ctrlConfig = (CtrlConfig) UtilsViews.getController("ViewConfig");

            // Cargar datos desde JSON si existe
            File jsonFile = new File(filePath);
            if (jsonFile.exists()) {
                try (JsonReader reader = Json.createReader(new FileReader(jsonFile))) {
                    JsonObject jsonObject = reader.readObject();
                    clientName = jsonObject.getString("nom", "");
                    String host = jsonObject.getString("link", "");

                    ctrlConfig.usernameText.setText(clientName);
                    ctrlConfig.txtHost.setText(host);
                    System.out.println("Datos cargados desde JSON.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureStage(Stage stage) {
        final int width = 600, height = 500;
        stage.setTitle("JavaFX");
        stage.setMinWidth(width);
        stage.setMinHeight(height);
        stage.show();

        if (!System.getProperty("os.name").contains("Mac")) {
            stage.getIcons().add(new Image("file:/icons/icon.png"));
        }
    }

    @Override
    public void stop() {
        if (wsClient != null) wsClient.forceExit();
        System.exit(1);
    }

    public static void pauseDuring(long millis, Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.millis(millis));
        pause.setOnFinished(e -> Platform.runLater(action));
        pause.play();
    }

    public static <T> List<T> jsonArrayToList(JSONArray array, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) list.add(clazz.cast(array.get(i)));
        return list;
    }

    public static void connectToServer() {
        ctrlConfig.txtMessage.setTextFill(Color.BLACK);
        ctrlConfig.txtMessage.setText("Connecting ...");

        // Actualizar clientName con el valor de la UI
        clientName = ctrlConfig.usernameText.getText();

        pauseDuring(1500, () -> {
            String url = "wss://" + ctrlConfig.txtHost.getText() + ":443";
            wsClient = UtilsWS.getSharedInstance(url);

            wsClient.onMessage(response -> Platform.runLater(() -> GestioMissatges.processMessage(response)));
            wsClient.onError(response -> Platform.runLater(() -> handleError(response)));
            wsClient.onOpen(response -> Platform.runLater(() -> GestioMissatges.crearJugador(clientName, wsClient)));
        });

        // Crear carpeta si no existe
        File carpeta = new File("Desktop/dades");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        // Guardar datos en JSON
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("nom", clientName)
                .add("link", ctrlConfig.txtHost.getText())
                .build();
        try (JsonWriter writer = Json.createWriter(new FileWriter(filePath))) {
            writer.writeObject(jsonObject);
            System.out.println("Datos guardados correctamente en JSON: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleError(String response) {
        if (response.contains("Connection refused")) {
            ctrlConfig.txtMessage.setTextFill(Color.RED);
            ctrlConfig.txtMessage.setText("Connection refused");
            pauseDuring(1500, () -> ctrlConfig.txtMessage.setText(""));
        }
    }
}
