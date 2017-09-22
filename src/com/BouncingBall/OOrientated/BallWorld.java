package com.BouncingBall.OOrientated;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

    private boolean paused = false;
    private ControlPanel control;
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

        control = new ControlPanel();
        this.setLayout(new BorderLayout());
        this.add(canvas, BorderLayout.CENTER);
        this.add(control, BorderLayout.SOUTH);

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

                    if (!paused) {
                        //Execute one game step
                        gameUpdate();
                        //refresh display
                        repaint();
                    }

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

            /*//Testing Only - Show collision position
            if(earliestCollisionTime > 0.05){
                repaint();
                try{
                    Thread.sleep((long)(1000L / UPDATE_RATE * earliestCollisionTime));
                } catch (InterruptedException ex){

                }
            }*/
            timeLeft -= earliestCollisionTime;
        } while(timeLeft > EPSILON_TIME);

    }

    class ControlPanel extends JPanel{
        public ControlPanel() {
            // A checkbox to toggle pause/resume movement
            JCheckBox pauseControl = new JCheckBox();
            this.add(new JLabel("Pause"));
            this.add(pauseControl);
            pauseControl.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    paused = !paused;  // Toggle pause/resume flag
                }
            });

            // A slider for adjusting the speed of the ball
            int minSpeed = 2;
            int maxSpeed = 20;
            JSlider speedControl = new JSlider(JSlider.HORIZONTAL, minSpeed, maxSpeed,
                    (int)ball.getSpeed());
            this.add(new JLabel("Speed"));
            this.add(speedControl);
            speedControl.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider)e.getSource();
                    if (!source.getValueIsAdjusting()) {
                        int newSpeed = (int)source.getValue();
                        int currentSpeed = (int)ball.getSpeed();
                        ball.speedX *= (float)newSpeed / currentSpeed ;
                        ball.speedY *= (float)newSpeed / currentSpeed;
                    }
                }
            });

            // A slider for adjusting the radius of the ball
            int minRadius = 10;
            int maxRadius = ((canvasHeight > canvasWidth) ? canvasWidth: canvasHeight) / 2 - 8;
            JSlider radiusControl = new JSlider(JSlider.HORIZONTAL, minRadius,
                    maxRadius, (int)ball.radius);
            this.add(new JLabel("Ball Radius"));
            this.add(radiusControl);
            radiusControl.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider)e.getSource();
                    if (!source.getValueIsAdjusting()) {
                        float newRadius = source.getValue();
                        ball.radius = newRadius;
                        // Reposition the ball such as it is inside the box
                        if (ball.x - ball.radius < box.minX) {
                            ball.x = ball.radius + 1;
                        } else if (ball.x + ball.radius > box.maxX) {
                            ball.x = box.maxX - ball.radius - 1;
                        }
                        if (ball.y - ball.radius < box.minY) {
                            ball.y = ball.radius + 1;
                        } else if (ball.y + ball.radius > box.maxY) {
                            ball.y = box.maxY - ball.radius - 1;
                        }
                    }
                }
            });
        }
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
