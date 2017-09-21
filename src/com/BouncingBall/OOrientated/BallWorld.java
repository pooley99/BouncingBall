package com.BouncingBall.OOrientated;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

/**
 * BallWorld
 * takes width and height as constructor
 * Creates ball and container box, draws these on a canvas object and adds these to the BallWorld main panel,
 * adds a listener for resizing the JPanel and starts the game.
 * gameStart creates new thread with continuos update, repaint, wait(refresh rate) loop
 * gameUpdate moves the ball one step and detects collision with container box
 *
 * Sub-Class DrawCanvas
 * @Override paintComponent draws box and ball
 * @Override getPreferredSize returns the canvas width and height
 */

public class BallWorld extends JPanel {

    private Ball ball;
    private ContainerBox box;

    private DrawCanvas canvas;
    private int canvasWidth;
    private int canvasHeight;

    private static final int UPDATE_RATE = 30;
    private static final float EPSILON_TIME = 1e-2f;

    public BallWorld(int width, int height){

        this.canvasWidth = width;
        this.canvasHeight = height;

        Random rand = new Random();
        int radius = 50;
        int x = rand.nextInt(this.canvasWidth - radius * 2 - 20 ) + radius + 10;
        int y = rand.nextInt(this.canvasHeight - radius * 2 - 20) + radius + 10;
        int speed = 5;
        int angleInDeg = rand.nextInt(360);
        this.ball = new Ball(x, y, radius, speed, angleInDeg, Color.BLUE);

        this.box = new ContainerBox(0, 0, this.canvasWidth, this.canvasHeight, Color.BLACK, Color.WHITE);
        this.canvas = new DrawCanvas();
        this.setLayout(new BorderLayout());
        this.add(this.canvas, BorderLayout.CENTER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = (Component)e.getSource();
                Dimension dim = c.getSize();
                canvasWidth = dim.width;
                canvasHeight = dim.height;
                box.set(0, 0, canvasWidth, canvasHeight);
            }
        });

        gameStart();
    }

    public void gameStart(){
        Thread gameThread = new Thread(() -> {
                while(true) {
                    /*gameUpdate();
                    repaint();
                    try{
                        //refresh rate and allows control to be relinquished to EDT for event handling and repainting
                        Thread.sleep(1000/UPDATE_RATE);
                    } catch(InterruptedException ex){}
                }*/
                    long beginTimeMillis, timeTakenMillis, timeLeftMillis;
                    beginTimeMillis = System.currentTimeMillis();

                    //Execute one game step
                    gameUpdate();
                    //refresh display
                    repaint();

                    //provide the necessary delay to meet the target rate
                    timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
                    timeLeftMillis = 1000L / UPDATE_RATE - timeTakenMillis;
                    if (timeLeftMillis < 5) timeLeftMillis = 5; //set a minimum

                    //Delay and give other thread a chance
                    try {
                        Thread.sleep(timeLeftMillis);
                    } catch (InterruptedException ex) {

                    }
                }
        });
        gameThread.start();
    }

    public void gameUpdate(){
/*        //Detect collision with container
        ball.intersect(this.box);
        //update the ball's state with collision
        ball.update();*/

        float timeLeft = 1.0f; //one time step to begin
        //Repeat until the one time-step is up
        do{
            //Need to find the earliest collision time among all objects
            float  earliestCollisionTime = timeLeft;
            //Special case here as there is only one moving ball
            ball.intersect(box, timeLeft);
            if(ball.earliestCollisionResponse.t < earliestCollisionTime){
                earliestCollisionTime = ball.earliestCollisionResponse.t;
            }

            //Update all the objects for earliestCollisionTime
            ball.update(earliestCollisionTime);

            //Testing Only - Show collision position
            if(earliestCollisionTime > 0.05){
                repaint();
                try{
                    Thread.sleep((long)(1000L / UPDATE_RATE * earliestCollisionTime));
                } catch (InterruptedException ex){

                }
            }
            timeLeft -= earliestCollisionTime;
        } while(timeLeft > EPSILON_TIME);

    }

    class DrawCanvas extends JPanel{

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            box.draw(g);
            ball.draw(g);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.PLAIN, 12));
            g.drawString("Ball " + ball.toString(), 20, 30);
        }

        @Override
        public Dimension getPreferredSize(){
            return (new Dimension(canvasWidth, canvasHeight));
        }
    }
}
