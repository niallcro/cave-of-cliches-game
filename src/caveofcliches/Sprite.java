package caveofcliches;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;

public class Sprite
{
    private Image image;
    private double positionX;
    private double positionY;    
    private double velocityX;
    private double velocityY;
    private double velocity;    
    private double width;
    private double height;
    private double direction;
    public static final double NONE = 0;
    public static final double UP = 1;
    public static final double UP_RIGHT = 2;
    public static final double RIGHT = 3;
    public static final double DOWN_RIGHT = 4;
    public static final double DOWN = 5;
    public static final double DOWN_LEFT = 6;
    public static final double LEFT = 7;
    public static final double UP_LEFT = 8;
    
    public Sprite()
    {
        positionX = 0;
        positionY = 0;    
        velocityX = 0;
        velocityY = 0;
        direction = 0;
    }

    public Sprite(double px, double py, double vx, double vy, double d, Image i)
    {
        positionX = px;
        positionY = py;    
        velocityX = vx;
        velocityY = vy;
        direction = d;
        setImage(i);
    }
    
    public void setImage(Image i)
    {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
    }

    public void setImage(String filename)
    {
        Image i = new Image(filename);
        setImage(i);
    }

    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

/*    public void setVelocity(double x, double y)
    {
        velocityX = x;
        velocityY = y;
    }*/

    public void start(double v, double d)
    {   
        velocity = v;
        direction = d;
        updateVelocity();
    }

    public void updateVelocity()
    {   
        if (direction == UP)        { velocityX = 0;    velocityY = velocity; }
        if (direction == UP_RIGHT)  { velocityX = velocity;    velocityY = -velocity; }
        if (direction == RIGHT)     { velocityX = velocity;    velocityY = 0; }
        if (direction == DOWN_RIGHT){ velocityX = velocity;    velocityY = velocity; }
        if (direction == DOWN)      { velocityX = 0;    velocityY = velocity; }
        if (direction == DOWN_LEFT) { velocityX = -velocity;    velocityY = velocity; }
        if (direction == LEFT)      { velocityX = -velocity;    velocityY = 0; }
        if (direction == UP_LEFT)   { velocityX = -velocity;    velocityY = -velocity; }
    }
    
    public double getVelocity()
    {
        return velocityX + velocityY;
    }

    public void setDirection(double d)
    {
        direction = d;
    }
    
    public double getDirection()
    {
        return direction;
    }
    
    public void bounce(String clockdir)
    {
        // if stationary or wrong string passed, don't do anything
        if(direction != 0 &&
                (clockdir == "CLOCKWISE" || clockdir == "ANTICLOCKWISE")) {
            // if odd
            if (direction % 2 == 1) {
                reverseDirection();
            }
            // if even
            else {
                if(clockdir == "CLOCKWISE") {
                    if(direction == 8) {
                        direction = 2;
                        updateVelocity();
                    } else {
                        direction += 2;
                        updateVelocity();
                    }
                }
                else {
                    if(direction == 2) {
                        direction = 8;
                        updateVelocity();
                    } else {
                        direction -= 2;
                        updateVelocity();
                    }
                }
            }
        }
    }    

    public void reverseDirection()
    {
        // if stationary, don't do anything
        if(direction != 0) {
            if (direction < 5) {
                direction+=4;
                updateVelocity();
            }
            else {
                direction -= 4;
                updateVelocity();
            }
        }
    }
    
    public void addVelocity(double x, double y)
    {
        velocityX += x;
        velocityY += y;
    }

    public void reduceVelocity(double x, double y)
    {
        velocityX -= x;
        velocityY -= y;
    }

    public void stop()
    {
        velocityX = 0;
        velocityY = 0;
        velocity = 0;
    }
    
    public void update(double time)
    {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }

    public void render(GraphicsContext gc)
    {
        gc.drawImage( image, positionX, positionY );
    }

    public Rectangle2D getBoundary()
    {
        return new Rectangle2D(positionX,positionY,width,height);
    }

    public boolean intersects(Sprite s)
    {
        return s.getBoundary().intersects( this.getBoundary() );
    }
    
    public String toString()
    {
        return " Position: [" + positionX + "," + positionY + "]" 
        + " Velocity: [" + velocityX + "," + velocityY + "]";
    }
}