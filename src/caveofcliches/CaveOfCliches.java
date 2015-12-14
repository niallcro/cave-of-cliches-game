package caveofcliches;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


/* Based on code from:
http://gamedevelopment.tutsplus.com/tutorials/introduction-to-javafx-for-game-development--cms-23835
*/

public class CaveOfCliches extends Application 
{
    
    // screen size
    int stage_width = 1024;
    int stage_height = 768;

    // map tile size
    int tilesize = 64;
    
    // player
    int player_v = 200;
    int playersize = playersize = tilesize/2;
    private Sprite player;
    int topleft_x, topleft_y, topright_x, topright_y, bottomleft_x, bottomleft_y, bottomright_x, bottomright_y;
    String topleft, topright, bottomleft, bottomright;

    // coins
    ArrayList<Sprite> coins;
    
    private Maze maze;
    private Clip audioclip;
    private final String image_path = "caveofcliches/";
    private final String audio_path = "src/caveofcliches/";
    private final boolean DEBUG = false;
    private boolean clipping = true;
    private IntValue score;
    Dialog dialog;
    
    public static void main(String[] args) {
        launch(args);
    }

    // reset game
    public void setupGame(int[][] level) {

        // game score
        score = new IntValue(0);
        
        // create maze
        maze = new Maze(tilesize, level);
        
        // create player
        player = new Sprite();
        
        Image player_img = new Image(image_path + "player.png", 
                playersize, playersize, true, true);
        player.setImage(player_img);
        player.setPosition(maze.getStartX() * tilesize, 
                maze.getStartY() * tilesize);

        // get player starting location
        getPlayerLocation();

        // place coins
        coins = new ArrayList<Sprite>();

        for (int i = 0; i < 9; i++) {
            Sprite coin = new Sprite();
            coin.setImage(image_path + "coin.png");
            double px = stage_width * Math.random();
            double py = stage_height * Math.random();
            coin.setPosition(px,py);
            if(!player.intersects(coin)) {
                coins.add(coin);
            };

        }
        
        // debugging
        maze.DEBUG = DEBUG;
        player.DEBUG = DEBUG;
    }
    
    @Override
    public void start(Stage theStage) {
        
        // setup stage
        theStage.setTitle("Cave of Cliches!");
        Group root = new Group();
        Scene theScene = new Scene(root);
        theStage.setScene(theScene);
        Canvas canvas = new Canvas(stage_width, stage_height);
        root.getChildren().add(canvas);

        // create map and player
        setupGame(Maze.mazeint);

        // graphics
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // setup font for text
        setFont(gc, 24);

        // game time
        LongValue lastNanoTime = new LongValue(System.nanoTime());
        
        // background audio
        //playAudio(audio_path + "AMBIENT.aif", true);
        
        // keyboard input
        ArrayList<String> input = new ArrayList<>();
        
        theScene.setOnKeyPressed(
            new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    if (!input.contains(code))
                        input.add(code);
                }
            });

        theScene.setOnKeyReleased(
            new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    input.remove(code);
                }
            });
        
        // main game loop
        new AnimationTimer()
        {
            String game_state = "NEW";
            
            @Override
            public void handle(long currentNanoTime)
            {
                // calculate time since last update.
                double elapsedTime = (currentNanoTime - lastNanoTime.value) / 1000000000.0;
                lastNanoTime.value = currentNanoTime;

                player.stop();
                
                // handle keyboard input
                
                // ESCAPE - close game
                if(input.contains("ESCAPE")) {
                    System.exit(0);
                }

                // R - reset game
                if(input.contains("R")) {
                    setupGame(Maze.mazeint);
                    input.remove("R");
                }

                // C - toggle clipping
                if(input.contains("C")) {
                    clipping = !clipping;
                    input.remove("C");
                }
                
                // SPACE - change game state
                
                if (input.contains("SPACE")) {
                    // PLAYING - pause game
                    if ("PLAYING".equals(game_state)) {
                        game_state = "PAUSED";
                        input.remove("SPACE");
                    }
                    // PAUSED - unpause game
                    else {
                        game_state = "PLAYING";
                        input.remove("SPACE");
                    }                    
                    // NEW - start game
                    if ("NEW".equals(game_state)) {
                        setupGame(Maze.mazeint);
                        game_state = "PLAYING";
                        input.remove("SPACE");
                    }
                    // WON OR LOST - reset and start new game
                    if ("WON".equals(game_state) || "LOST".equals(game_state)) {
                        score.setValue(0);
                        setupGame(Maze.mazeint);
                        game_state = "PLAYING";
                        input.remove("SPACE");
                    }
                }
                
                // dialog
               if (input.contains("E")) {
                   
                    int i = stage_width / 4;
                   
                    dialog = new Dialog(i, i, i, i, "Hi there", true);
                   
                    // PLAYING - show dialog
                    if ("PLAYING".equals(game_state)) {
                        dialog.show();
                        game_state = "DIALOG";
                        input.remove("E");
                    }
                    // DIALOG - close dialog
                    else {
                        dialog.hide();
                        game_state = "PLAYING";
                        input.remove("E");
                    }                  
               }
                
                // player movement
                
                if (!"PAUSED".equals(game_state)) {

                    if ("DIALOG".equals(game_state)) {
                        if(dialog != null) dialog.render(gc);
                    }
                    
                    if ((input.contains("UP") && input.contains("DOWN"))
                            || (input.contains("LEFT") && input.contains("RIGHT"))) {
                        player.stop();
                    }
                    else {
                        
                        getPlayerLocation();
                        int buffer = 5;
                        
                        if (input.contains("LEFT")) {

                            if(maze.getSquareAtPoint(topleft_x-buffer, topleft_y).isPassable() && maze.getSquareAtPoint(bottomleft_x-buffer, bottomleft_y).isPassable()) player.addVelocity(-player_v,0);
                            if (clipping == false) player.addVelocity(-player_v,0);
                        }
                        if (input.contains("RIGHT")) {
                            if(maze.getSquareAtPoint(topright_x+buffer, topright_y).isPassable() && maze.getSquareAtPoint(bottomright_x+buffer, bottomright_y).isPassable()) player.addVelocity(player_v,0);
                            if (clipping == false) player.addVelocity(player_v,0);
                        }
                        if (input.contains("UP")) {
                            if(maze.getSquareAtPoint(topleft_x, topleft_y-buffer).isPassable() && maze.getSquareAtPoint(topright_x, topright_y-buffer).isPassable()) player.addVelocity(0,-player_v);
                            if (clipping == false) player.addVelocity(0,-player_v);
                        }
                        if (input.contains("DOWN")) {
                            if(maze.getSquareAtPoint(bottomleft_x, bottomleft_y+buffer).isPassable() && maze.getSquareAtPoint(bottomright_x, bottomright_y+buffer).isPassable()) player.addVelocity(0,player_v);
                            if (clipping == false) player.addVelocity(0,player_v);
                        }
                    }
                }

                // collision detection
                Iterator<Sprite> coinIter = coins.iterator();
                
                while (coinIter.hasNext())
                {
                    Sprite coin = coinIter.next();
                    if (player.intersects(coin))
                    {
                        playAudio(audio_path + "HIT.wav", false);
                        coinIter.remove();
                        score.value++;
                    }
                }
                
                // render
                
                // clear screen
                if (!dialog.visible) gc.clearRect(0, 0, stage_width, stage_height);
                
                // apply game logic
                
                // WON
                if("PLAYING".equals(game_state) && score.value >= 5) {
                    if (!"WON".equals(game_state)) playAudio(audio_path + "WON.wav", false);
                    game_state = "WON";
                }
                
                // PLAYING
                
                if("PLAYING".equals(game_state)) {
                    
                    // render maze
                    maze.render(gc);

                    // update player
                    player.update(elapsedTime);
                    
                    // render player
                    player.render(gc);
                    
                    // render coins
                    for (Sprite coin : coins)
                        coin.render(gc);
                    
                    // render debug area
                    renderDebugArea(gc);
                    
                    // render score
                    String pointsText = "Score: " + score.value;
                    gc.setFill(Color.WHITE);
                    gc.fillText(pointsText, 350, 30);
                    gc.strokeText(pointsText, 350, 30);
                }
                
                // NEW
                if ("NEW".equals(game_state)) {
                    // show instructions
                    String text = "Press space to start";
                    gc.fillText(text, 100, 256);
                    gc.strokeText(text, 100, 256);
                }
                
                // PAUSED
                if ("PAUSED".equals(game_state)) {
                    // show instructions
                    String text = "PAUSED";
                    gc.fillText(text, 100, 256);
                    gc.strokeText(text, 100, 256);
                    String text2 = "Press space to continue.";
                    gc.fillText(text2, 70, 300);
                    gc.strokeText(text2, 70, 300);
                }
                
                // WON
                if ("WON".equals(game_state)) {
                    // show instructions
                    String text = "You won with score " + score.value;
                    gc.fillText(text, 50, 256);
                    gc.strokeText(text, 50, 256);
                    String againtext = "Press space to play again.";
                    gc.fillText(againtext, 70, 300);
                    gc.strokeText(againtext, 70, 300);
                }
                
                // LOST
                if ("LOST".equals(game_state)) {
                    // show instructions
                    String text = "You lost!";
                    gc.fillText(text, 50, 256);
                    gc.strokeText(text, 50, 256);
                    String againtext = "Press space to play again.";
                    gc.fillText(againtext, 70, 300);
                    gc.strokeText(againtext, 70, 300);
                }
            }
        }.start();

        theStage.show();
    }
    
    public void getPlayerLocation() {

        topleft_x = ((Double) player.getTopLeft().getValue0()).intValue();
        topleft_y = ((Double) player.getTopLeft().getValue1()).intValue();
        topright_x = topleft_x + playersize;
        topright_y = topleft_y;
        bottomleft_x = topleft_x;
        bottomleft_y = topleft_y + playersize;
        bottomright_x = topleft_x + playersize;
        bottomright_y = topleft_y + playersize;

        topleft = "[" + topleft_x/playersize + ", " + topleft_y/playersize + "]";
        topright = "[" + topright_x/playersize + ", " + topright_y/playersize + "]";
        bottomleft = "[" + bottomleft_x/playersize + ", " + bottomleft_y/playersize + "]";
        bottomright = "[" + bottomright_x/playersize + ", " + bottomright_y/playersize + "]";

    }
    
    public void renderDebugArea (GraphicsContext gc) { 
    
        int width = maze.getWidth();
        int indent = width + 30;
        int spacing = 30;
        int row = 1;
        
        gc.setFill(Color.BLUE);
        gc.fillRect(maze.getWidth(), 0, stage_width, stage_height);

        // setup font for box
        setFont(gc, 20);

        row = 3;
        
        // draw a box
        gc.fillRect(indent -5 , spacing * row, spacing * 6, spacing * 6);
        gc.strokeRect(indent -5, spacing * row, spacing * 6, spacing * 6);
        
        row = 4;
        
        gc.fillText(topleft, indent, row * spacing);
        gc.strokeText(topleft, indent, row * spacing);        

        gc.fillText(topright, indent + spacing * 3, row * spacing);
        gc.strokeText(topright, indent + spacing * 3, row * spacing);
        
        row = 8;
        
        gc.fillText(bottomleft, indent, row * spacing);
        gc.strokeText(bottomleft, indent, row * spacing);
        
        gc.fillText(bottomright, indent + spacing * 3, row * spacing);
        gc.strokeText(bottomright, indent + spacing * 3, row * spacing);
        
        row = 11;
        
        String text = "Shortcuts";
        gc.fillText(text, indent, ++row * spacing);
        gc.strokeText(text, indent, row * spacing);
        
        text = "R = Reset";
        gc.fillText(text, indent, ++row * spacing);
        gc.strokeText(text, indent, row * spacing);        

        text = "G = Toggle Grid";
        gc.fillText(text, indent, ++row * spacing);
        gc.strokeText(text, indent, row * spacing);        

        text = "C = Toggle Clipping";
        gc.fillText(text, indent, ++row * spacing);
        gc.strokeText(text, indent, row * spacing);       
        
    }
    
    public String pwd() {
        System.out.println("pwd: " + System.getProperty("user.dir"));
        return System.getProperty("user.dir");
    }
    
    // play audio clip from file
    public void playAudio(String filename, boolean loop) {
        try {
            audioclip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
            File file = new File(filename);
            audioclip.open(AudioSystem.getAudioInputStream(file));
            if (loop) {
                audioclip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            else {
                audioclip.start();
            }
        }
        catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            System.out.println(e.toString());
            System.out.println("I was looking in " + pwd() + "/" + filename);
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