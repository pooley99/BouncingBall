package com.BouncingBall.OOrientated;

import java.awt.Color;
import java.awt.Graphics;

public abstract class Obstacle {
    //int x, y, width, height;
    Color borderColour;
    Color fillColour;

    Obstacle(Color fillColour, Color borderColour){
        //this.x = x;
        //this.y = y;
        //this.width = x + width - 1;
        //this.height = y + height - 1;
        this.borderColour = borderColour;
        this.fillColour = fillColour;
    }

    Obstacle(){
        this(Color.WHITE, Color.YELLOW);
    }

    public abstract float[] getXY();
    public abstract void move(float x, float y);
    public abstract void draw(Graphics g);

}
