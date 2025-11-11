package com.project.Controllers;

import java.net.URL;
import java.util.ResourceBundle;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.project.*;

public class CtrlPlay implements Initializable {

    @FXML
    public javafx.scene.control.Label title;

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
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawPongObjects();
        dibuixarPuntuacio();
    }

    private void drawPongObjects() {
        if (Main.objects == null) return;

        // Escalado base para el tablero 8x8
        double scaleX = canvas.getWidth() / 8.0;
        double scaleY = canvas.getHeight() / 8.0;

        for (GameObject obj : Main.objects) {
            switch (obj.id) {
                case "P1": // Pala jugador 1
                    gc.setFill(Color.web(obj.color)); 
                    drawRect(obj, scaleX, scaleY, 0.2, 0.9);
                    break;
                case "P2": // Pala jugador 2
                    gc.setFill(Color.web(obj.color)); 
                    drawRect(obj, scaleX, scaleY, 0.2, 0.9); 
                    break;
                case "B0": // Pelota
                    gc.setFill(Color.web(obj.color)); 
                    drawCircle(obj, scaleX, scaleY, 0.2);
                    break;
                default:
                    gc.setFill(Color.web(obj.color)); 
                    drawRect(obj, scaleX, scaleY, 1, 1);
            }
        }
    }
    private void drawRect(GameObject obj, double scaleX, double scaleY, double widthFactor, double heightFactor) {
        double x = obj.x * scaleX + (scaleX * (1 - widthFactor) / 2);
        double y = obj.y * scaleY + (scaleY * (1 - heightFactor) / 2);
        double w = obj.col * scaleX * widthFactor;
        double h = obj.row * scaleY * heightFactor;
        gc.fillRect(x, y, w, h);
    }

    private void drawCircle(GameObject obj, double scaleX, double scaleY, double sizeFactor) {
        double diameterX = obj.col * scaleX * sizeFactor;
        double diameterY = obj.row * scaleY * sizeFactor;
        double x = obj.x * scaleX + (obj.col * scaleX - diameterX) / 2;
        double y = obj.y * scaleY + (obj.row * scaleY - diameterY) / 2;
        gc.fillOval(x, y, diameterX, diameterY);
    }

    private void dibuixarPuntuacio() {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        String scoreText = Main.j1Points + " - " + Main.j2Points;
        gc.fillText(scoreText, canvas.getWidth() / 2 - 30, 50);
    }


}
