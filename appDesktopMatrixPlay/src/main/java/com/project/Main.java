package com.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.project.Controllers.CtrlConfig;
import com.project.Controllers.CtrlCountdown;
import com.project.Controllers.CtrlGameOver;
import com.project.Controllers.CtrlPlay;
import com.project.Controllers.CtrlWait;

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
    public static List<ClientData> clients = new ArrayList<>();
    public static List<GameObject> objects = new ArrayList<>();
    public static int j1Points, j2Points;
    public static CtrlConfig ctrlConfig;
    public static CtrlWait ctrlWait;
    public static CtrlPlay ctrlPlay;
    public static CtrlCountdown ctrlCountdown;
    public static CtrlGameOver ctrlGameOver;
    public static final String filePath = "dades/dades.json";
    public static boolean espectador = false;
    public static GestioMoviment gestioMoviment;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        initViews(); 
        Scene scene = new Scene(UtilsViews.parentContainer);
        stage.setScene(scene);
        configureStage(stage);

        // Controladores
        ctrlConfig = (CtrlConfig) UtilsViews.getController("ViewConfig");
        ctrlWait = (CtrlWait) UtilsViews.getController("ViewWait");
        ctrlPlay = (CtrlPlay) UtilsViews.getController("ViewPlay");
        ctrlCountdown = (CtrlCountdown) UtilsViews.getController("ViewCountdown");
        ctrlGameOver = (CtrlGameOver) UtilsViews.getController("ViewGameOver");

        carregarDades();
    }

    private void initViews() {
        try {
            UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
            Carregar();
            UtilsViews.addView(getClass(), "ViewConfig", "/assets/viewConfig.fxml");
            UtilsViews.addView(getClass(), "ViewCountdown", "/assets/viewCountdown.fxml");
            UtilsViews.addView(getClass(), "ViewWait", "/assets/viewWait.fxml");
            UtilsViews.addView(getClass(), "ViewPlay", "/assets/viewPlay.fxml");
            UtilsViews.addView(getClass(), "ViewGameOver", "/assets/viewGameOver.fxml");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Carregar() {
        try {
            UtilsViews.addView(getClass(), "ViewCarregar", "/assets/viewCarregar.fxml");

            PauseTransition pause = new PauseTransition(Duration.seconds(2)); // pausa de 2 segundos
            pause.setOnFinished(event -> UtilsViews.setViewAnimating("ViewConfig"));
            pause.play();

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

    private void carregarDades() {
        File jsonFile = new File(filePath);
        if (!jsonFile.exists()) return;

        try (JsonReader reader = Json.createReader(new FileReader(jsonFile))) {
            JsonObject jsonObject = reader.readObject();
            clientName = jsonObject.getString("nom", "");
            String host = jsonObject.getString("link", "");

            if (ctrlConfig != null) {
                ctrlConfig.usernameText.setText(clientName);
                ctrlConfig.txtHost.setText(host);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (wsClient != null) wsClient.forceExit();
        System.exit(0);
    }

    public static void pauseDuring(long millis, Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.millis(millis));
        pause.setOnFinished(e -> Platform.runLater(action));
        pause.play();
    }

    public static void connectToServer() {
        if (ctrlConfig == null) return;

        ctrlConfig.txtMessage.setTextFill(Color.BLACK);
        ctrlConfig.txtMessage.setText("Connecting ...");

        clientName = ctrlConfig.usernameText.getText();

        pauseDuring(1500, () -> {
            String url = "wss://" + ctrlConfig.txtHost.getText() + ":443";
            wsClient = UtilsWS.getSharedInstance(url);

            wsClient.onMessage(response -> Platform.runLater(() -> GestioMissatges.processMessage(response)));
            wsClient.onError(response -> Platform.runLater(() -> handleError(response)));

            wsClient.onOpen(response -> Platform.runLater(() -> {
                if (!espectador) {
                    GestioMissatges.crearJugador(clientName, wsClient);
                } else {
                    GestioMissatges.crearEspectador(clientName, wsClient);
                }

                gestioMoviment = new GestioMoviment(wsClient);

                Scene scene = UtilsViews.parentContainer.getScene();
                if (scene != null) {
                    scene.setOnKeyPressed(evt -> gestioMoviment.keyEvent(evt));
                    scene.setOnKeyReleased(evt -> gestioMoviment.keyEvent(evt));
                }
            }));
        });

        guardarDades();
    }

    private static void guardarDades() {
        File carpeta = new File("dades");
        if (!carpeta.exists()) carpeta.mkdirs();

        if (ctrlConfig == null) return;

        JsonObject jsonObject = Json.createObjectBuilder()
                .add("nom", clientName)
                .add("link", ctrlConfig.txtHost.getText())
                .build();
        try (JsonWriter writer = Json.createWriter(new FileWriter(filePath))) {
            writer.writeObject(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleError(String response) {
        if (ctrlConfig == null) return;

        if (response.contains("Connection refused")) {
            ctrlConfig.txtMessage.setTextFill(Color.RED);
            ctrlConfig.txtMessage.setText("Connection refused");
            pauseDuring(1500, () -> ctrlConfig.txtMessage.setText(""));
        }
    }
}
