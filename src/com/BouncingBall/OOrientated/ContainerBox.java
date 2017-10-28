package com.BouncingBall.OOrientated;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Container Box
 *Creates a container box with specified coordinates, width, height background and border color
 * set resizes the container
 * draw
 */

public class ContainerBox extends BallContainer{

    ContainerBox(int x, int y, int width, int height, Color fillColour, Color borderColour){
        super(x, y, width, height, fillColour, borderColour);
    }

    ContainerBox(int x, int y, int width, int height){
        super(x, y, width, height);
    }

    public float[] getCenterXY(){
        int centerX = x + (maxX - x)/2;
        int centerY = y + (maxY - y)/2;
        return new float[] {centerX, centerY};
    }

    public float getRadius(){
        return 0.0f;
    }

    public void draw(Graphics g){
        g.setColor(this.fillColour);
        g.fillRect(this.x, this.y, this.maxX - this.x - 1, this.maxY - this.y - 1);
        g.setColor(this.borderColour);
        g.drawRect(this.x, this.y, this.maxX - this.x - 1, this.maxY - this.y - 1);
    }

}
/*public class ContainerBox {

    int minX, minY, maxX, maxY;
    Color colorFilled;
    Color colorBorder;

    public ContainerBox(int x, int y, int width, int height, Color colorFilled, Color colorBorder){
        this.minX = x;
        this.minY = y;
        this.maxX = x + width - 1; //adjust for 0 start
        this.maxY = y + height - 1;
        this.colorFilled = colorFilled;
        this.colorBorder = colorBorder;
    }

    public ContainerBox(int x, int y, int width, int height){
        this(x, y, width, height, Color.BLACK, Color.YELLOW );
    }

    public void set(int x, int y, int width, int height){
        this.minX = x;
        this.minY = y;
        this.maxX = x + width - 1;
        this.maxY = y + height - 1;
    }

    public void draw(Graphics g){
        g.setColor(this.colorFilled);
        g.fillRect(this.minX, this.minY, this.maxX - this.minX - 1, this.maxY - this.minY - 1);
        g.setColor(this.colorBorder);
        g.drawRect(this.minX, this.minY, this.maxX - this.minX - 1, this.maxY - this.minY - 1);

    }

}*/
