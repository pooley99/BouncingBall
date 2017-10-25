package com.BouncingBall.OOrientated;

import java.awt.Color;
import java.awt.Graphics;

public class ContainerOval {

    int x, y, width, height;
    int diameter;
    Color borderColour;
    Color fillColour;

    ContainerOval(int x, int y, int width, int height, Color borderColour, Color fillColour){
        this.x = x;
        this.y = y;
        this.width = x + width - 1;
        this.height = y + height - 1;
        this.diameter = Math.min(this.width, this.height);
        this.borderColour = borderColour;
        this.fillColour = fillColour;
    }

    ContainerOval(int x, int y, int width, int height){
        this(x, y, width, height, Color.BLACK, Color.YELLOW);
    }

    public void set(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = x + width - 1;
        this.height = y + height - 1;
    }

    public float[] getCenterXY(){
        int centerX = x + width/2;
        int centerY = y + height/2;
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
