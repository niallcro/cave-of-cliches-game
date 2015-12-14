/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caveofcliches;

/**
 *
 * @author niall.crowley
 */

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Dialog {
    
    int width;
    int height;
    int positionX;
    int positionY;
    String message;
    boolean visible;
    
    public Dialog(int px, int py, int w, int h, String s, boolean v) {
        positionX = px;
        positionY = py;
        width = w;
        height = h;
        message = s;
        visible = v;
    }

    public void show() {
        visible = true;
    }
    
    public void hide() {
        visible = false;
    }
    
    public void render (GraphicsContext gc) { 
    
        if(visible) {
            
            int spacing = 30;
            int row = 1;

            // draw dialog box
            gc.setFill(Color.BROWN);
            gc.fillRect(positionX, positionY, width, height);
            gc.strokeRect(positionX, positionY, width, height);
            
            // setup font for dialog
            //setFont(gc, 24);

            // write message
            gc.fillText(message, positionX + spacing, positionY + spacing);
            gc.strokeText(message, positionX + spacing, positionY + spacing);
        }
    }

    public void setFont(GraphicsContext gc, int size) {

        Font theFont = Font.font("Helvetica", FontWeight.BOLD, size);
        gc.setFont(theFont);
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
    }
    
}