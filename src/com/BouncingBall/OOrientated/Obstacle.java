package com.BouncingBall.OOrientated;

import java.awt.Color;
import java.awt.Graphics;

public abstract class Obstacle {
    int x, y, width, height;
    Color borderColour;
    Color fillColour;

    Obstacle(int x, int y, Color borderColour, Color fillColour){
        this.x = x;
        this.y = y;
        //this.width = x + width - 1;
        //this.height = y + height - 1;
        this.borderColour = borderColour;
        this.fillColour = fillColour;
    }

    Obstacle(int x, int y){
        this(x, y, Color.BLACK, Color.YELLOW);
    }

    public void set(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = x + width - 1;
        this.height = y + height - 1;
    }

    public abstract void draw(Graphics g);

    public static final class CircleObstacle extends Obstacle{
        CircleObstacle(int x, int y, int radius, Color borderColour, Color fillColour){
            super(x, y, borderColour, fillColour);
            this.width = radius*2;
            this.height = height*2;
        }
        CircleObstacle(int x, int y, int radius){
            super(x, y);
            this.width = radius*2;
            this.height = radius*2;
        }

        public void draw(Graphics g){
            g.setColor(fillColour);
            g.fillOval(x, y, width, height);
            g.setColor(borderColour);
            g.drawOval(x, y, width, height);
        }
    }
}
