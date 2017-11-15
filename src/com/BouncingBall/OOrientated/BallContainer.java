package com.BouncingBall.OOrientated;

import java.awt.Color;
import java.awt.Graphics;

public abstract class BallContainer  {
    int x, y, maxX, maxY;
    Color fillColour;
    Color borderColour;

    BallContainer(int x, int y, int width, int height, Color fillColour, Color borderColour){
        this.x = x;
        this.y = y;
        this.maxX = x + width - 1;
        this.maxY = x + height - 1;
        this.fillColour = fillColour;
        this.borderColour = borderColour;

    }

    BallContainer(int x, int y, int width, int height){
        this(x, y, width, height, Color.BLACK, Color.YELLOW);
    }

    public void set(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.maxX = x + width - 1;
        this.maxY = y + height - 1;
    }

    public abstract float[] getCenterXY();
    public abstract float getRadius();
    public abstract void draw(Graphics g);

}



