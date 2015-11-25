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
public class Player {

    private int x = 1;
    private int y = 1;
    
    public Player () {
        
    }
        
    public int getX () {
        return x;
    }

    public int getY () {
        return y;
    }

    public void setX (int x) {
        this.x = x;
    }

    public void setY (int y) {
        this.y = y;
    }
    
    public void moveLeft () {
        x-=1;
    }
    
    public void moveRight () {
        x+=1;
    }
    
    public void moveUp () {
        y-=1;
    }
    
    public void moveDown () {
        y+=1;
    }

}