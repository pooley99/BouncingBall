package com.BouncingBall.OOrientated;

import java.awt.*;
import java.awt.event.*;
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

    //private Ball ball;
    //private ContainerBox box;
    //private ContainerOval ovalBox;
    private BallContainer box;

    private DrawCanvas canvas;
    private int canvasWidth;
    private int canvasHeight;

    private boolean paused = false;
    private boolean reset = false;

    private int containerType = 1;
    private String[] containers = {"Box", "Circle"};

    private ControlPanel control;
    private static final int UPDATE_RATE = 30;
    private static final float EPSILON_TIME = 1e-2f;

    private static final int MAX_BALLS = 25;
    private static int startNumBalls = 5;
    private int currentNumBalls;
    private Ball[] balls = new Ball[MAX_BALLS];

    private static final int MAX_OBSTACLES = 10;
    private int currentNumObstacles;
    private Obstacle[] obstacles = new Obstacle[MAX_OBSTACLES];

    public BallWorld(int width, int height){

        final int controlHeight = 30;
        this.canvasWidth = width;
        this.canvasHeight = height - controlHeight;

        setBox("Circle");
        setBalls(startNumBalls);
        setObstacles(2);

        //currentNumObstacles = 1;
        //obstacles[0] = new CircleObstacle(400, 250, 30);

        //this.box = new ContainerBox(0, 0, this.canvasWidth, this.canvasHeight, Color.BLACK, Color.WHITE);
        //this.box = new ContainerOval(0, 0, this.canvasWidth, this.canvasHeight, Color.BLACK, Color.WHITE);
        this.canvas = new DrawCanvas();
        control = new ControlPanel();
        this.setLayout(new BorderLayout());
        this.add(this.canvas, BorderLayout.CENTER);
        this.add(control, BorderLayout.SOUTH);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = (Component)e.getSource();
                Dimension dim = c.getSize();
                canvasWidth = dim.width;
                canvasHeight = dim.height - controlHeight;
                //box.move(0, 0, canvasWidth, canvasHeight);
                box.set(0, 0, canvasWidth, canvasHeight);
            }
        });

        gameStart();
    }

    public void setBox(String str){

        if (str == "Box") {
            this.box = new ContainerBox(0, 0, this.canvasWidth, this.canvasHeight, Color.BLACK, Color.WHITE);
        }else if (str == "Circle"){
            this.box = new ContainerOval(0, 0, this.canvasWidth, this.canvasHeight, Color.BLACK, Color.WHITE);
        }

    }

    public void setBalls(int num){

        /*Random rand = new Random();
        int radius = 50;
        int x = rand.nextInt(this.canvasWidth - radius * 2 - 20 ) + radius + 10;
        int y = rand.nextInt(this.canvasHeight - radius * 2 - 20) + radius + 10;
        int speed = 5;
        int angleInDeg = rand.nextInt(360);
        this.ball = new Ball(x, y, radius, speed, angleInDeg, Color.BLUE);*/

        /*Random rand = new Random();
        int radiusLimit = 6;
        int speedLimit = 5;
        int angleLimit = 360;

        int x, y, radius, speed, angle, xLimit, yLimit
        for(int i = 0; i < currentNumBalls; i++){
            angle = rand.nextInt(angleLimit);
            speed = rand.nextInt(speedLimit);
            radius = rand.nextInt(radiusLimit) * 10;
            xLimit = (this.canvasWidth - radius * 2 - 20);
            x = rand.nextInt(xLimit) + radius + 10;
            if(box instanceof ContainerBox){
                int yLimit = (this.canvasHeight - radius * 2 - 20);
            } else if(box instanceof ContainerOval){
                int yBase = this.canvasHeight/2;
                int
                int yLimit = (this.canvasHeight * () - radius * 2 - 20);
            }
            y = rand.nextInt(yLimit) + radius + 10;

            balls[i] = new Ball(x, y, radius, speed, angle, Color.Yellow);
        }*/


        currentNumBalls = num;
        balls[0] = new Ball(100, 410, 25, 3, 34, Color.YELLOW);
        balls[1] = new Ball(500, 350, 25, 2, -114, Color.YELLOW);
        balls[2] = new Ball(530, 400, 30, 3, 14, Color.GREEN);
        balls[3] = new Ball(400, 400, 30, 3, 14, Color.GREEN);
        balls[4] = new Ball(400, 50, 35, 1, -47, Color.PINK);
        balls[5] = new Ball(480, 320, 35, 4, 47, Color.PINK);
        balls[6] = new Ball(500, 150, 40, 1, -114, Color.ORANGE);
        balls[7] = new Ball(200, 240, 40, 2, 60, Color.ORANGE);
        balls[8] = new Ball(250, 380, 50, 3, -42, Color.BLUE);
        balls[9] = new Ball(300, 400, 70, 6, -84, Color.CYAN);
        balls[10] = new Ball(500, 170, 90, 6, -42, Color.MAGENTA);

        for(int i = currentNumBalls; i < MAX_BALLS; i++){
            balls[i] = new Ball(200, canvasHeight - 150, 30, 5, 45, Color.RED);
        }
    }

    public void setObstacles(int num){
        currentNumObstacles = num;
        float[] pXs = new float[]{500, 550, 600};
        float[] pYs = new float[]{500, 450, 500};
        obstacles[0] = new ObstaclePoly(pXs, pYs, 3);
        obstacles[1] = new ObstacleRect(300, 300, 50, 50);

        //obstacles[0] = new ObstacleCircle(500, 500, 30);
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

                    if (reset){
                        setBalls(startNumBalls);
                        repaint();
                        reset = false;
                    }

                    //provide the necessary delay to meet the target rate
                    timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
                    timeLeftMillis = 1000L / UPDATE_RATE - timeTakenMillis;
                    if (timeLeftMillis < 5) timeLeftMillis = 5; //move a minimum

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
            float tMin = timeLeft;
            //Special case here as there is only one moving ball

            for(int i = 0; i < currentNumBalls; i++){
                for(int j = 0; j < currentNumBalls; j++){
                    if (i < j) {
                        balls[i].intersect(balls[j], tMin);
                        if(balls[i].earliestCollisionResponse.t < tMin){
                            tMin = balls[i].earliestCollisionResponse.t;
                        }
                    }
                }
            }

            for (int i = 0; i < currentNumBalls; i++) {
                //balls[i].intersect(box, timeLeft);
                balls[i].intersect(box, timeLeft);
                if(balls[i].earliestCollisionResponse.t < tMin){
                    tMin = balls[i].earliestCollisionResponse.t;
                }
            }

            for(int i =0; i < currentNumBalls; i++){
                for(int j=0; j<currentNumObstacles; j++){
                    balls[i].intersect(obstacles[j], timeLeft);
                    if(balls[i].earliestCollisionResponse.t < tMin){
                        tMin = balls[i].earliestCollisionResponse.t;
                    }
                }
            }

            //Update all the objects for tMin
            for (int i = 0; i < currentNumBalls; i++) {
                balls[i].update(tMin);
            }

            /*//Testing Only - Show collision position
            if(tMin > 0.05){
                repaint();
                try{
                    Thread.sleep((long)(1000L / UPDATE_RATE * tMin));
                } catch (InterruptedException ex){

                }
            }*/
            timeLeft -= tMin;
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
            //store starting speeds
            final float[] ballSavedSpeedXs = new float[MAX_BALLS];
            final float[] ballSavedSpeedYs = new float[MAX_BALLS];
            for(int i=0; i<MAX_BALLS; i++){
                ballSavedSpeedXs[i] = balls[i].speedX;
                ballSavedSpeedYs[i] = balls[i].speedY;
            }
            //as percentage
            int minSpeed = 5;
            int maxSpeed = 200;
            JSlider speedControl = new JSlider(JSlider.HORIZONTAL, minSpeed, maxSpeed,100);
            this.add(new JLabel("Speed"));
            this.add(speedControl);
            speedControl.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider)e.getSource();
                    if (!source.getValueIsAdjusting()) {
                        int percentage = (int)source.getValue();
                        for (int i = 0; i < currentNumBalls; i++) {
                            balls[i].speedX = ballSavedSpeedXs[i] * (float)percentage / 100f ;
                            balls[i].speedY = ballSavedSpeedYs[i] * (float)percentage / 100f;
                        }
                    }
                }
            });

            //Launch Button for remaining balls
            final JButton launchControl = new JButton("Launch New Ball");
            this.add(launchControl);
            launchControl.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(currentNumBalls < MAX_BALLS){
                        currentNumBalls++;
                        if(currentNumBalls == MAX_BALLS){
                            launchControl.setEnabled(false);
                        }
                    }
                }
            });

            JButton resetControl = new JButton("Reset");
            //this.add(new JLabel("Reset"));
            this.add(resetControl);
            resetControl.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    reset = true;
                }
            });

            /*JButton switchContainer = new JButton("Switch Container");
            this.add(switchContainer);
            switchContainer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    containerType++;
                    if(containerType > 2){
                        containerType = 1;
                    }
                    switch(containerType){
                        case 1 : setBalls(startNumBalls);
                                setBox("Box");
                                break;
                        case 2 : setBalls(startNumBalls);
                                setBox("Circle");
                                break;
                    }
                }
            });*/

            JComboBox dropDown = new JComboBox(containers);
            if(box instanceof ContainerBox){
                dropDown.setSelectedIndex(0);
            } else if(box instanceof ContainerOval){
                dropDown.setSelectedIndex(1);
            }
            this.add(dropDown);
            dropDown.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setBalls(startNumBalls);
                    setBox((String)dropDown.getSelectedItem());
                }
            });


            /*// A slider for adjusting the radius of the ball
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
            });*/
        }
    }

    class DrawCanvas extends JPanel{

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            //box.draw(g);
            box.draw(g);
            for (int i = 0; i < currentNumBalls; i++) {
                balls[i].draw(g);
            }

            for(int i = 0; i < currentNumObstacles; i++){
                obstacles[i].draw(g);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.PLAIN, 12));
            for (int i = 0; i < currentNumBalls; i++) {
                g.drawString("Ball " + i + " " + balls[i].toString(), 20, (i+1) * 30);
            }
        }

        @Override
        public Dimension getPreferredSize(){
            return (new Dimension(canvasWidth, canvasHeight));
        }
    }
}
