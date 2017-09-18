package com.BouncingBall.OOrientated;
import java.awt.*;

/**
 * Container Box
 *Creates a container box with specified coordinates, width, height background and border color
 * set resizes the container
 * draw
 */
public class ContainerBox {

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

}
