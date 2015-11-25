/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caveofcliches;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author niall.crowley
 */
public class Maze {
    
    private int width = 12;
    private int height = 12;
    private MazeSquare [][] maze = new MazeSquare [width][height];
    
    /* static maze for testing */
    private int mazeint [][] = {
    
        // 0 empty, 1 wall, 2, start, 3 finish, 4 magma
        {1,1,1,1,1,1,1,1,1,1,1,1},
        {1,2,0,0,1,1,1,0,0,0,0,1},
        {1,0,0,0,0,0,1,0,1,0,0,1},
        {1,0,0,0,1,0,1,0,0,0,0,1},
        {1,0,1,0,1,0,1,0,1,1,0,1},
        {1,0,1,0,1,0,1,0,1,0,0,1},
        {1,0,1,1,1,0,1,0,1,0,1,1},
        {1,4,1,0,0,0,1,0,1,0,1,1},
        {1,0,1,0,1,1,1,0,1,0,0,1},
        {1,1,0,0,0,0,0,0,1,0,0,1},
        {1,0,0,0,0,1,1,1,1,0,3,1},
        {1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    public Maze() {
        // populate the Maze array with new MazeSquare objects
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                maze [x][y] = new MazeSquare (mazeint [x][y]);        
    }
    
    public MazeSquare getSquare (int x, int y) {
        return maze [x][y];
    }

    public int getHeight () {
        return height;
    }
    
    public int getWidth () {
        return width;
    }
    
    public boolean isPassable (int x, int y) {
        return maze[y][x].isPassable();
    }

    public boolean isMagma (int x, int y) {
        return maze[y][x].isMagma();
    }
    
    public boolean isExit (int x, int y) {
        return maze[y][x].isExit();
    }

    public void moveMagma () {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (maze [y][x].isMagma()) {
                    if (inMaze(x-1,y) && maze [y][x-1].isOpen()) maze [y][x-1].setType(5);
                    if (inMaze(x+1,y) && maze [y][x+1].isOpen()) maze [y][x+1].setType(5);
                    if (inMaze(x,y-1) && maze [y-1][x].isOpen()) maze [y-1][x].setType(5);
                    if (inMaze(x,y+1) && maze [y+1][x].isOpen()) maze [y+1][x].setType(5);
                }
            }  
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                    if (maze [y][x].getType()==5) maze [y][x].setMagma();
            }  
        }
    }
    
    public boolean inMaze (int x, int y) {
        return (x > 0 && x < width) && (y > 0 && y < height);
    }
}
