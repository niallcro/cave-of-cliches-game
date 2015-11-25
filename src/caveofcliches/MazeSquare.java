/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caveofcliches;

import java.awt.Color;

/**
 *
 * @author niall.crowley
 */
public class MazeSquare {

    private int type = 0; // 0 open, 1 wall, 2 start, 3 finish, 4 magma
    // int item = 0; // 0 empty, 1 pickup_type1, 2 pickup_type2
    private Color color = Color.LIGHT_GRAY;
    private Color [] colors = { Color.LIGHT_GRAY, Color.BLACK, Color.GREEN, Color.RED, Color.ORANGE, Color.YELLOW };
    
    public MazeSquare(int t) {
        this.type = t;
        color = colors [t];
    }
    
    public Color getColor () {
        return color;
    }

    public int getType () {
        return type;
    }
    
    public boolean isPassable () {
        return type != 1;
    }

    public boolean isMagma () {
        return type == 4;
    }
    
    public void setMagma () {
        type = 4;
        color = colors [4];
    }

    public void setType (int x) {
        type = x;
        color = colors [x];
    }
    
    public boolean isExit () {
        return type == 3;
    }
    
    public boolean isOpen () {
        return type == 0;
    }
}
