package com.BouncingBall.OOrientated;

import java.awt.*;


//TODO fix balls sticking on the polygon
public class ObstaclePoly extends Obstacle {

    float[] polyXs, polyYs;
    int[] polyXsI, polyYsI;
    //anchor point to make manipulation easier
    float x, y;
    int numPoints;
    ObstaclePoly(float[] polyXs, float[] polyYs, int numPoints, Color fillColour, Color borderColour){
        super(fillColour, borderColour);
        if(polyXs.length == numPoints && polyYs.length == numPoints){
            this.polyXs = polyXs;
            this.polyYs = polyYs;
            this.polyXsI = new int[numPoints];
            this.polyYsI = new int[numPoints];
            this.numPoints = numPoints;
        }else {
            System.out.println("Not a complete polygon!");
        }
    }

    ObstaclePoly(float[] polyXs, float[] polyYs, int numPoints){
        super();
        if(polyXs.length == numPoints && polyYs.length == numPoints){
            this.polyXs = polyXs;
            this.polyYs = polyYs;
            this.numPoints = numPoints;
        }else {
            System.out.println("Not a complete polygon!");
        }
    }

    public float[] getXY(){
        return new float[]{polyXs[0], polyYs[0]};
    }

    @Override
    public void move(float x, float y) {

    }

    @Override
    public void draw(Graphics g) {
        g.setColor(fillColour);
        polyXsI = new int[numPoints];
        polyYsI = new int[numPoints];
        for(int i = 0; i < numPoints; i++){
            polyXsI[i] = (int)polyXs[i];
            polyYsI[i] = (int)polyYs[i];
        }
        g.fillPolygon(polyXsI, polyYsI, numPoints);
        g.setColor(borderColour);
        g.drawPolygon(polyXsI, polyYsI, numPoints);
    }
}
