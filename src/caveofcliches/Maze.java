/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caveofcliches;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author niall.crowley
 */
public class Maze {
    
    private final int tile_width;
    private final int tile_height;
    private final int start_x = 3;
    private final int start_y = 9;
    private final int tilesize;
    private final int width;
    private final int height;
    private final MazeSquare [][] maze;
    public boolean DEBUG;
    
    // static maze for testing
    public static final int mazeint [][] = {
    
        // 0 empty, 1 wall, 2, start, 3 finish, 4 magma
        {1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,1,1,1,0,0,0,0,1},
        {1,0,0,0,0,0,1,0,1,0,0,1},
        {1,0,0,0,1,0,1,0,0,0,0,1},
        {1,0,1,0,1,0,1,0,1,1,0,1},
        {1,0,1,0,1,0,1,0,1,0,0,1},
        {1,0,1,1,1,0,1,0,1,0,1,1},
        {1,0,1,0,0,0,1,0,1,0,1,1},
        {1,0,1,0,1,1,1,0,1,0,0,1},
        {1,1,0,2,0,0,0,0,1,0,0,1},
        {1,0,0,0,0,1,1,1,1,0,3,1},
        {1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // static maze for testing
    public static final int mazeint2 [][] = {
    
        // 0 empty, 1 wall, 2, start, 3 finish, 4 magma
        {1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,1,0,0,0,0,2,0,1},
        {1,0,0,0,1,0,0,0,0,0,0,1},
        {1,0,0,0,1,0,0,0,0,0,0,1},
        {1,0,0,0,1,0,0,0,0,0,1,1},
        {1,0,0,0,0,0,0,0,0,0,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public Maze(int tilesize, int[][] level) {
        
        this.tilesize = tilesize;
        this.tile_width = level.length;
        this.tile_height = level[0].length;
        this.width = tile_width * tilesize;
        this.height = tile_height * tilesize;
        
        // set debug
        DEBUG = false;
        
        // initialize maze
        maze = new MazeSquare [tile_width][tile_height];
                
        // populate the Maze array with new MazeSquare objects
        for (int x = 0; x < tile_width; x++)
            for (int y = 0; y < tile_height; y++)
                maze [x][y] = new MazeSquare (tilesize, level [x][y]);        
    }
    
    public MazeSquare getSquare (int x, int y) {
        return maze [x][y];
    }

    public MazeSquare getSquareAtPoint (int x, int y) {
        //System.out.println((int) x/tilesize + ", " + (int) y/tilesize);
        return maze [x/tilesize][y/tilesize];
    }
    
    public int getTileHeight () {
        return tile_height;
    }
    
    public int getTileWidth () {
        return tile_width;
    }

    public int getHeight () {
        return height;
    }
    
    public int getWidth () {
        return width;
    }
    
    public int getStartX() {
        return start_x;
    }
    
    public int getStartY() {
        return start_y;
    }
        
    public boolean isPassable (int x, int y) {
        return maze[y][x].isPassable();
    }

    public boolean isExit (int x, int y) {
        return maze[y][x].isExit();
    }

    public boolean isPassable (double x, double y) {
        //System.out.println((int) x/tilesize + ", " + (int) y/tilesize);
        return maze [(int) x/tilesize][(int) y/tilesize].isPassable();
    }

    public boolean isExit (double x, double y) {
        //System.out.println((int) x/tilesize + ", " + (int) y/tilesize);
        return maze [(int) x/tilesize][(int) y/tilesize].isExit();
    }
    
    public void render (GraphicsContext gc) {
        for (int y = 0; y < tile_width; y++) {
            for (int x = 0; x < tile_height; x++) {
                gc.setFill(getSquare(y,x).getColor());
                gc.fillRect(x * tilesize, y * tilesize, tilesize, tilesize);
                
                if (DEBUG) {
                    // setup font for text
                    Font theFont = Font.font("Helvetica", FontWeight.BOLD, 20);
                    gc.setFont(theFont);
                    gc.setFill(Color.WHITE);
                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(1);

                    gc.fillText("[" + x + "," + y + "]", x * tilesize + 5, y * tilesize + 30);
                    gc.strokeText("[" + x + "," + y + "]", x * tilesize + 5, y * tilesize + 30);
                }
            }
        }
    }
}