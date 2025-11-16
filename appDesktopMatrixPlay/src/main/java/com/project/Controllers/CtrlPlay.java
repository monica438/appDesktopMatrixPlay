package com.project.Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.project.GameObject;
import com.project.Main;
import com.project.UtilsViews;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CtrlPlay implements Initializable {

    @FXML
    public javafx.scene.control.Label title;
    public javafx.scene.control.Label j1Nom;
    public javafx.scene.control.Label j2Nom;
    public javafx.scene.control.Label j1Punts;
    public javafx.scene.control.Label j2Punts;

    @FXML
    private Canvas canvas;
    private GraphicsContext gc;

    private PlayTimer animationTimer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.gc = canvas.getGraphicsContext2D();

        UtilsViews.parentContainer.heightProperty().addListener((obs, oldV, newV) -> resizeCanvas());
        UtilsViews.parentContainer.widthProperty().addListener((obs, oldV, newV) -> resizeCanvas());

        animationTimer = new PlayTimer(this::run, this::draw, 60); // 60 FPS
        animationTimer.start();
    }

    private void resizeCanvas() {
        canvas.setWidth(UtilsViews.parentContainer.getWidth());
        canvas.setHeight(UtilsViews.parentContainer.getHeight());
    }

    private void run(double fps) {
        // TODO
    }

    private void draw() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Limpiar canvas
        gc.clearRect(0, 0, width, height);

        // Dibujar borde
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeRect(0, 0, width, height);

        // Dibujar línea vertical en mitad
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeLine(width / 2, 0, width / 2, height);

        // Dibujar los objetos del juego
        drawPongObjects();
    }


    private void drawPongObjects() {
    if (Main.objects == null) return;

    for (GameObject obj : Main.objects) {
        switch (obj.id) {
            case "P1": // Pala jugador 1
            case "P2": // Pala jugador 2
                drawRect(obj);
                break;
            case "B0": // Pelota
                drawCircle(obj);
                break;
            default:
                drawRect(obj);
        }
    }
}
    private void drawRect(GameObject obj) {
    gc.setFill(Color.web(obj.color));
    gc.fillRect(obj.x, obj.y, obj.ancho, obj.alto);
    }

    private void drawCircle(GameObject obj) {
        gc.setFill(Color.web(obj.color));
        gc.fillOval(obj.x, obj.y, obj.ancho, obj.alto);
    }


}
