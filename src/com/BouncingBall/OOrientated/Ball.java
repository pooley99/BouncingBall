package com.BouncingBall.OOrientated;
import com.BouncingBall.Physics.CollisionPhysics;
import com.BouncingBall.Physics.CollisionResponse;

import java.awt.*;
import java.util.Formatter;

/**
 * Ball Class
 * Calculates the x and y speed from an angle and speed as more intuitive. creates ball with radius and color.
 * draw method creates rectangle bound oval of ball color
 * getSpeed
 * getMoveAngle
 * getMass: radius^3/1000
 * getKineticEnergy: 0.5mv^2
 * @Override toString: to display position, radius, speed, angle, kinetic energy
 * intersect: tests for collision with the border
 * update: moves the ball on one time-step
 */
public class Ball {

    float x, y;
    float speedX, speedY;
    float radius;
    private Color color;
    CollisionResponse earliestCollisionResponse = new CollisionResponse();
    private CollisionResponse tempResponse = new CollisionResponse();
    CollisionResponse thisRepsonse = new CollisionResponse();
    CollisionResponse anotherResponse = new CollisionResponse();

    private static final Color DEFAULT_COLOR = Color.BLUE;

    private StringBuilder sb = new StringBuilder();
    private Formatter formatter = new Formatter(this.sb);


    public Ball(float x, float y, float radius, float speed, float angleInDeg, Color color){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speedX = (float)(speed * Math.cos(Math.toRadians(angleInDeg)));
        //negative as y screen coords start at top of screen
        this.speedY = (float)(-speed * Math.sin(Math.toRadians(angleInDeg)));
        this.color = color;
    }

    //overload with default color
    public Ball(float x, float y, float radius, float speed, float angleInDeg){
        this(x, y, radius, speed, angleInDeg, DEFAULT_COLOR);
    }

    //draw ball
    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval((int)(x-radius), (int)(y-radius), (int)(2*radius), (int)(2*radius));
    }

    //move the ball on one step then check for collision with container box
    public void moveOneStepWithCollisionDetection(ContainerBox box){
        float ballMinX = box.minX + radius;
        float ballMinY = box.minY + radius;
        float ballMaxX = box.maxX - radius;
        float ballMaxY = box.maxY - radius;

        this.x += this.speedX;
        this.y += this.speedY;

        if(x < ballMinX){
            this.speedX = -this.speedX;
            this.x = ballMinX;
        //as the anchor point is at the top left
        } else if(x > ballMaxX){
            this.speedX = -this.speedX;
            this.x = ballMaxX;
        }

        if(y < ballMinY){
            this.speedY = -this.speedY;
            this.y = ballMinY;
        //as the anchor point is at the top left
        }else if(y > ballMaxY){
            this.speedY = -this.speedY;
            this.y = ballMaxY;
        }
    }

    public float getSpeed(){
        return (float)Math.sqrt(this.speedX * this.speedX + this.speedY * this.speedY);
    }

    public float getMoveAngle(){
        return (float)Math.toDegrees(Math.atan2(-this.speedY, this.speedX));
    }

    public float getMass(){
        return this.radius * this.radius * this.radius / 1000f;
    }

    public float getKineticEnergy(){
        return 0.5f * getMass() * (this.speedX * this.speedX + this.speedY * this.speedY);
    }

    @Override
    public String toString(){
        this.sb.delete(0, this.sb.length());
        //formatter first number after % dictates number of spaces built into string, after . dictates how many decimal numbers are included
        this.formatter.format("@(%3.0f, %3.0f) r = %3.0f V = (%2.0f, %2.0f)" +
        "S = %4.1f \u0398 = %4.0f KE = %4.0f",
        this.x, this.y, this.radius, this.speedX, this.speedY, getSpeed(), getMoveAngle(), getKineticEnergy());
        return sb.toString();
    }


    public void intersect(ContainerBox box, float timeLimit){
        CollisionPhysics.pointIntersectsRectangleOuter(this.x, this.y, this.speedX, this.speedY, this.radius,
                box.minX, box.minY, box.maxX, box.maxY,
                timeLimit, tempResponse);
        if(tempResponse.t < this.earliestCollisionResponse.t){
            this.earliestCollisionResponse.copy(tempResponse);
        }
    }

    public void intersect(Ball another, float timeLimit){
        CollisionPhysics.movingPointIntersectsMovingPoint(this.x, this.y, this.speedX, this.speedY, this.radius,
                another.x, another.y, another.speedX, another.speedY, another.radius,
                timeLimit, thisRepsonse, anotherResponse);
        if (anotherResponse.t < another.earliestCollisionResponse.t){
            another.earliestCollisionResponse.copy(anotherResponse);
        }
        if(thisRepsonse.t < this.earliestCollisionResponse.t){
            this.earliestCollisionResponse.copy(thisRepsonse);
        }
    }

    /**
     * Update the states of the ball for one time-step
     * Move for one time-step if no collision occurs; otherwise move up to
     * the earliest detected collision
     */
    public void update(float time){
        if(earliestCollisionResponse.t <= time){
            //This ball collided
            this.x = earliestCollisionResponse.getNewX(this.x, this.speedX);
            this.y = earliestCollisionResponse.getNewY(this.y, this.speedY);
            this.speedX = earliestCollisionResponse.newSpeedX;
            this.speedY = earliestCollisionResponse.newSpeedY;
        }else {
            this.x += this.speedX*time;
            this.y += this.speedY*time;
        }
        earliestCollisionResponse.reset();
    }

}
