package com.BouncingBall.OOrientated;

import java.awt.*;

/**
 * Circular Obstacle
 * Defined by the centerpoint(x, y) and the diameter. Radius is also stored.
 */

public class ObstacleCircle extends Obstacle {

    float x, y, radius, diameter;
    ObstacleCircle(float x, float y, float radius, Color fillColour, Color borderColour){
        super(fillColour, borderColour);
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.diameter = radius*2;
    }
    ObstacleCircle(float x, float y, float radius){
        super();
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.diameter = radius*2;
    }

    public float[] getXY(){
        return new float[]{x, y };
    }

    public void move(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g){
        g.setColor(fillColour);
        g.fillOval((int)(x - radius), (int)(y - radius), (int)(diameter), (int)(diameter));
        g.setColor(borderColour);
        g.drawOval((int)(x - radius), (int)(y - radius), (int)(diameter), (int)(diameter));
    }
}
