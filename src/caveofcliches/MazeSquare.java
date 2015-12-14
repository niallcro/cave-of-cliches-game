/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caveofcliches;

import java.util.HashMap;
import javafx.scene.paint.Color;

/**
 *
 * @author niall.crowley
 */
public class MazeSquare {

    private final int size;
    private int type = 0; // 0 open, 1 wall, 2 start, 3 finish
    private Color color;
    private final Color [] colors = { Color.ALICEBLUE, Color.BLUE, Color.GREEN, Color.RED };
    private HashMap color_map;

    public MazeSquare(int s, int t) {

        // Create a hash map
        color_map = new HashMap();

        // Put elements to the map
        color_map.put(Color.ALICEBLUE, "OPEN");
        color_map.put(Color.BLUE, "WALL");
        color_map.put(Color.GREEN, "START");
        color_map.put(Color.RED, "FINISH");
        color_map.put(Color.ORANGE, "HAZARD");
        
        this.size = s;
        this.type = t;
        color = colors [t];
    }
    
    public Color getColor () {
        return color;
    }

    public String getName () {
        return color_map.get(color).toString();
    }
    
    public int getSize () {
        return size;
    }
    
    public int getType () {
        return type;
    }
    
    public boolean isPassable () {
        return type != 1;
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
