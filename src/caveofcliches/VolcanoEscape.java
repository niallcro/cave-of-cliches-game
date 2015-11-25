/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caveofcliches;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author niall.crowley
 */
public class VolcanoEscape extends JPanel {
    
    private Maze maze;
    private Player player;
    private final int squaresize = 30;
    private final int playersize = 20; // size to draw player
    
    public VolcanoEscape () {
        
        player = new Player();
        maze = new Maze();
        
        addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                
                if (e.getKeyChar() == 'a') {
                    //System.out.println(player.getX() + ", " + player.getY());
                    if (maze.isPassable(player.getX()-1, player.getY())) {
                        maze.moveMagma();
                        player.moveLeft();
                    }
                }
                if (e.getKeyChar() == 'd') {
                    if (maze.isPassable(player.getX()+1, player.getY())) {
                        maze.moveMagma();
                        player.moveRight();
                    }
                }
                if (e.getKeyChar() == 'w') {
                    if (maze.isPassable(player.getX(), player.getY()-1)) {
                        maze.moveMagma();
                        player.moveUp();
                    }
                }
                if (e.getKeyChar() == 's') {
                    if (maze.isPassable(player.getX(), player.getY()+1)) {
                        maze.moveMagma();
                        player.moveDown();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //player.keyReleased(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //player.keyPressed(e);
            }
        });
        setFocusable(true);
        requestFocus();
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        JFrame frame = new JFrame("Volcano Escape!");
        VolcanoEscape game = new VolcanoEscape();
        frame.add(game);
        frame.setSize(640, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while (!game.lost() && !game.won()) {
            game.repaint();
            Thread.sleep(10);
        } 
        
        game.repaint();
        if (game.lost()) System.out.println ("You got burned by the magma!");
        if (game.won()) System.out.println ("You escaped!");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        paintMaze(maze, g2d);
        paintPlayer(player, g2d);
    }
    
    private void paintPlayer (Player p, Graphics2D g2d) {
        g2d.setColor(Color.BLUE);
        g2d.fillRect(p.getX() * squaresize + 5, p.getY() * squaresize + 5, playersize, playersize);
    }
    
    private void paintMaze (Maze m, Graphics2D g2d) {
        for (int x = 0; x < m.getWidth(); x++) {
            for (int y = 0; y < m.getHeight(); y++) {
                g2d.setColor(m.getSquare(x,y).getColor());
                g2d.fillRect(y * squaresize, x * squaresize,
                        squaresize, squaresize);
            }
        }
    }
    
    private boolean won () {
        return maze.isExit(player.getX(), player.getY());
    }
    
    private boolean lost () {
        return maze.isMagma(player.getX(), player.getY());
    }
}
