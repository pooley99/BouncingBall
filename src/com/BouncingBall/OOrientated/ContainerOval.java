package com.BouncingBall.OOrientated;

import java.awt.Color;
import java.awt.Graphics;

public class ContainerOval extends BallContainer{

    int diameter;
    ContainerOval(int x, int y, int width, int height, Color fillColour, Color borderColour){
        super(x, y, width, height, fillColour, borderColour);
        this.diameter = Math.min(width, height);
    }

    ContainerOval(int x, int y, int width, int height){
        super(x, y, width, height);
        this.diameter = Math.min(width, height);
    }

    public float[] getCenterXY(){
        int centerX = x + diameter/2;
        int centerY = y + diameter/2;
        return new float[] {centerX, centerY};
    }

    public float getRadius(){
        return (float)diameter/2;
    }

    public void draw(Graphics g){
        g.setColor(this.fillColour);
        g.fillOval(this.x, this.y, this.diameter, this.diameter);
        g.setColor(this.borderColour);
        g.drawOval(this.x, this.y, this.diameter, this.diameter);
    }

}

/*
public class ContainerOval {

    int x, y, maxX, maxY;
    int diameter;
    Color borderColour;
    Color fillColour;

    ContainerOval(int x, int y, int width, int height, Color fillColour, Color borderColour){
        this.x = x;
        this.y = y;
        this.maxX = x + width - 1;
        this.maxY = y + height - 1;
        this.diameter = Math.min(width, height);
        this.borderColour = borderColour;
        this.fillColour = fillColour;
    }

    ContainerOval(int x, int y, int width, int height){
        this(x, y, width, height, Color.BLACK, Color.YELLOW);
    }

    public void set(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.diameter = x + Math.min(width, height) - 1;
        this.maxX = x + width - 1;
        this.maxY = y + height - 1;
    }

    public float[] getCenterXY(){
        int centerX = x + diameter/2;
        int centerY = y + diameter/2;
        return new float[] {centerX, centerY};
    }

    public float getRadius(){
        return (float)(diameter/2);
    }

    public void draw(Graphics g){
        g.setColor(this.fillColour);
        g.fillOval(this.x, this.y, this.diameter, this.diameter);
        g.setColor(this.borderColour);
        g.drawOval(this.x, this.y, this.diameter, this.diameter);
    }

}
*/
