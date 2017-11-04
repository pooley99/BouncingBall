package com.BouncingBall.OOrientated;

import java.awt.*;

/**
 * Rectanglur Obstacle
 * Defined by the top left corner(x, y) and the width and height. Also stores the maxX and maxY.
 */

//TODO: balls become stuck on 2 horizontal sides


public class ObstacleRect extends Obstacle{

    float x, y, width, height, maxX, maxY;
    float[] rectXs = new float[4];
    float[] rectYs = new float[4];
    ObstacleRect(float x, float y, float width, float height, Color fillColour, Color borderColour){
        super(fillColour, borderColour);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxX = x + width - 1;
        this.maxY = y + height - 1;
        this.rectXs[0] = x;
        this.rectYs[0] = y;
        this.rectXs[1] = x;
        this.rectYs[1] = y + height;
        this.rectXs[2] = x + width;
        this.rectYs[2] = y;
        this.rectXs[3] = x + width;
        this.rectYs[3] = y + height;

    }
    ObstacleRect(float x, float y, float width, float height){
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxX = x + width - 1;
        this.maxY = y + height - 1;
        this.rectXs[0] = x;
        this.rectYs[0] = y;
        this.rectXs[1] = x;
        this.rectYs[1] = y + height;
        this.rectXs[2] = x + width;
        this.rectYs[2] = y;
        this.rectXs[3] = x + width;
        this.rectYs[3] = y + height;
    }

    public float[] getXY(){
        return new float[]{x, y};
    }

    @Override
    public void move(float x, float y) {
        float xChange = this.x - x;
        float yChange = this.y - y;
        this.x = x;
        this.y = y;
        this.maxX -= xChange;
        this.maxY -= yChange;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(fillColour);
        g.fillRect((int)x, (int)y, (int)width, (int)height);
        g.setColor(borderColour);
        g.drawRect((int)x, (int)y, (int)width, (int)height);
    }
}
