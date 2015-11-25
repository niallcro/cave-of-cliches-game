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
import javafx.geometry.Rectangle2D;
import javafx.scene.media.AudioClip;
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
    
    int width = 512;
    int height = 512;
    int ball_v = 200;
    int paddle_v = 300;
    
    Sprite paddle, ball;
    ArrayList<Sprite> moneybagList;
    Image space, wormhole;
    Clip audioclip;
    
    public static void main(String[] args) 
    {
        launch(args);
    }

    @Override
    public void start(Stage theStage)
    {
        
        theStage.setTitle("Destroy All Planets!");

        Group root = new Group();
        Scene theScene = new Scene(root);
        theStage.setScene(theScene);

        Canvas canvas = new Canvas(width, height);
        root.getChildren().add(canvas);

        setupGame();
        
        ArrayList<String> input = new ArrayList<String>();
        
        theScene.setOnKeyPressed(
            new EventHandler<KeyEvent>()
            {
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
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    input.remove(code);
                }
            });

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Font theFont = Font.font("Helvetica", FontWeight.BOLD, 24);
        gc.setFont(theFont);
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        
        LongValue lastNanoTime = new LongValue(System.nanoTime());

        IntValue score = new IntValue(0);
        
        playAudio("AMBIENT.aif", true);
        
        new AnimationTimer()
        {
            String game_state = "NEW";
            
            public void handle(long currentNanoTime)
            {
                // calculate time since last update.
                double elapsedTime = (currentNanoTime - lastNanoTime.value) / 1000000000.0;
                lastNanoTime.value = currentNanoTime;
                
                // paddle logic
                
                paddle.stop();

                if(input.contains("ESCAPE")) {
                    System.exit(0);
                }
                
                if (input.contains("SPACE") && "NEW".equals(game_state)) {
                    setupGame();
                    game_state = "PLAYING";
                    ball.start(ball_v, Sprite.UP_RIGHT);
                    input.remove("SPACE");
                }

                if (input.contains("SPACE") && ("WON".equals(game_state) || "LOST".equals(game_state))) {
                    setupGame();
                    game_state = "PLAYING";
                    ball.start(ball_v, Sprite.UP_RIGHT);
                    input.remove("SPACE");
                }
                
                if (input.contains("SPACE") && "PLAYING".equals(game_state)) {
                    game_state = "PAUSED";
                    input.remove("SPACE");
                    System.out.println("Paused");
                }

                if (input.contains("SPACE") && "PAUSED".equals(game_state)) {
                    game_state = "PLAYING";
                    input.remove("SPACE");
                    System.out.println("Playing");
                }
                
                if (input.contains("LEFT") && !atLeftBoundary(canvas, paddle) && game_state != "PAUSED") {
                    paddle.addVelocity(-paddle_v,0);
                }
                if (input.contains("RIGHT") && !atRightBoundary(canvas, paddle) && game_state != "PAUSED") {
                    paddle.addVelocity(paddle_v,0);
                }
                
                // move bouncing ball 
                
                if (atLeftBoundary(canvas, ball)) {
                    if(ball.getDirection() == Sprite.UP_LEFT) {
                        ball.bounce("CLOCKWISE");
                    }
                    else {
                        ball.bounce("ANTICLOCKWISE");
                    }
                }
                
                if (atRightBoundary(canvas, ball)) {
                    if(ball.getDirection() == Sprite.UP_RIGHT) {
                        ball.bounce("ANTICLOCKWISE");
                    }
                    else {
                        ball.bounce("CLOCKWISE");
                    }
                }
                
                if (atTopBoundary(canvas, ball)) {
                    if(atRightBoundary(canvas, ball) || atLeftBoundary(canvas, ball)) {
                        ball.reverseDirection();
                    }
                    else {
                        if(ball.getDirection() == Sprite.UP_LEFT) {
                            ball.bounce("ANTICLOCKWISE");
                        }
                        else {
                            ball.bounce("CLOCKWISE");
                        }
                    }
                }
                
                if (offScreen(canvas, ball)) {
                    if (!"LOST".equals(game_state)) playAudio("LOST.wav", false);
                    game_state = "LOST";
                }
                
                if (ball.intersects(paddle) && (ball.getDirection() == Sprite.DOWN_LEFT)) {
                    if (isBelowPaddle(canvas, ball)) {
                        System.out.println("Below!");
                        ball.reverseDirection();
                    }
                    else {
                        ball.bounce("CLOCKWISE");
                    }
                }

                if (ball.intersects(paddle) && (ball.getDirection() == Sprite.DOWN_RIGHT)) {
                    if (isBelowPaddle(canvas, ball)) {
                        System.out.println("Below!");
                        ball.reverseDirection();
                    }
                    else {
                        ball.bounce("ANTICLOCKWISE");
                    }
                }
                
                // collision detection
                
                Iterator<Sprite> moneybagIter = moneybagList.iterator();
                
                if("PLAYING".equals(game_state) && !moneybagIter.hasNext()) {
                    if (!"WON".equals(game_state)) playAudio("WON.wav", false);
                    game_state = "WON";
                }
                
                while (moneybagIter.hasNext())
                {
                    Sprite moneybag = moneybagIter.next();
                    if (ball.intersects(moneybag))
                    {
                        playAudio("HIT.wav", false);
                        moneybagIter.remove();
                        score.value++;
                    }
                }
                
                // render
                
                gc.clearRect(0, 0, 512,512);
                
                gc.setLineWidth(3);
                gc.strokeRect(0, 0, width, height);

                gc.drawImage(space, 0, 0);
                gc.drawImage(wormhole, 0, height - wormhole.getHeight());
                
                if("PLAYING".equals(game_state)) {
                    paddle.update(elapsedTime);
                    ball.update(elapsedTime);
                    paddle.render(gc);
                    ball.render(gc);
                    for (Sprite moneybag : moneybagList)
                        moneybag.render(gc);
                }
                
                gc.setLineWidth(1);
                
                if (game_state == "NEW") {
                    // show instructions
                    String text = "Press space to start";
                    gc.fillText(text, 100, 256);
                    gc.strokeText(text, 100, 256);
                }
                if (game_state == "PAUSED") {
                    // show instructions
                    String text = "PAUSED";
                    gc.fillText(text, 100, 256);
                    gc.strokeText(text, 100, 256);
                    String text2 = "Press space to continue.";
                    gc.fillText(text2, 70, 300);
                    gc.strokeText(text2, 70, 300);
                }
                if (game_state == "PLAYING") {
                    String pointsText = "Planets destroyed: " + score.value;
                    gc.fillText(pointsText, 100, 36);
                    gc.strokeText(pointsText, 100, 36);
                }
                if (game_state == "WON") {
                    // show instructions
                    String text = "You won with " + score.value + " planets destroyed!";
                    gc.fillText(text, 50, 256);
                    gc.strokeText(text, 50, 256);
                    String againtext = "Press space to play again.";
                    gc.fillText(againtext, 70, 300);
                    gc.strokeText(againtext, 70, 300);
                }
                if (game_state == "LOST") {
                    // show instructions
                    String text = "You got lost in the wormhole!";
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

    public boolean atLeftBoundary(Canvas canvas, Sprite sprite) {
        return sprite.getBoundary().getMinX() <= 0;
    }

    public boolean atRightBoundary(Canvas canvas, Sprite sprite) {
        return sprite.getBoundary().getMaxX() >= canvas.getWidth();
    }
    
    public boolean atTopBoundary(Canvas canvas, Sprite sprite) {
        return sprite.getBoundary().getMinY() <= 0;
    }

    public boolean atBottomBoundary(Canvas canvas, Sprite sprite) {
        return sprite.getBoundary().getMaxY() >= canvas.getHeight();
    }
    
    public boolean offScreen(Canvas canvas, Sprite sprite) {
        return sprite.getBoundary().getMaxY() > (canvas.getHeight() + sprite.getBoundary().getHeight());
    }

    public boolean isBelowPaddle(Canvas canvas, Sprite sprite) {
        return sprite.getBoundary().getMaxY() > canvas.getHeight();
    }
    
    public void stopGame() {
        ball.stop();
        moneybagList.removeAll(moneybagList);
    }
    
    public void playAudio(String filename, boolean loop) {
        try {
            audioclip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
            File file = new File("src/" + filename);
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
        }
    }
    
    public void setupGame() {
        
        paddle = new Sprite();
        paddle.setImage("paddle.png");
        paddle.setPosition(width/2, height - paddle.getBoundary().getHeight());

        ball = new Sprite();
        ball.setImage("ufo_0.png");
        ball.setPosition(width/2, height * 0.8);

        moneybagList = new ArrayList<Sprite>();
        
        wormhole = new Image("wormhole.png");
        space = new Image("space.png");
        
        for (int i = 0; i < 15; i++)
        {
            Sprite moneybag = new Sprite();
            moneybag.setImage("earth.png");
            double px = 350 * Math.random() + 50;
            double py = 350 * Math.random() + 50;          
            moneybag.setPosition(px,py);
            moneybagList.add(moneybag);
        }
    }
}