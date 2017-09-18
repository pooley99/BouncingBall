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

    private static final int UPDATE_RATE = 30;

    private Ball ball;
    private ContainerBox box;

    private DrawCanvas canvas;
    private int canvasWidth;
    private int canvasHeight;

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
                while(true){
                    gameUpdate();
                    repaint();
                    try{
                        //refresh rate and allows control to be relinquished to EDT for event handling and repainting
                        Thread.sleep(1000/UPDATE_RATE);
                    } catch(InterruptedException ex){}
                }
        });
        gameThread.start();
    }

    public void gameUpdate(){
        ball.moveOneStepWithCollisionDetection(box);
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
