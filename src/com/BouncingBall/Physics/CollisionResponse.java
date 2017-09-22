package com.BouncingBall.Physics;

/**
 * If collision occurs, this object stores the collision time and the computed respnoses,
 * new speed (newSpeedX, newSpeedY)
 */
public class CollisionResponse {
    //detected collision time
    public float t;
    //Time threshold to be subtracted from collision time
    //to prevent moving over the bound. Assume that t <= 1
    private static final float T_EPSILON = 0.005f;

    public float newSpeedX;
    public float newSpeedY;

    public CollisionResponse(){
        reset();
    }

    //reset time to max (infinity)
    public void reset(){
        this.t = Float.MAX_VALUE;
    }

    //copy a response too the current response
    public void copy(CollisionResponse anotherResponse){
        this.t = anotherResponse.t;
        this.newSpeedX = anotherResponse.newSpeedX;
        this.newSpeedY = anotherResponse.newSpeedY;
    }

    public float getNewX(float currentX, float speedX){
        //subtract by a small thread to make sure does not cross the bounds
        if(this.t > T_EPSILON){
            //has effect of moving it back a small step ensuring does not stick to bound
            return (float)(currentX + speedX * (this.t - T_EPSILON));
        }else{
            return currentX;
        }
    }
    public float getNewY(float currentY, float speedY){
        //subtract by a small thread to make sure does not cross the bounds
        if(this.t > T_EPSILON){
            //has effect of moving it back a small step ensuring does not stick to bound
            return (float)(currentY + speedY * (this.t - T_EPSILON));
        }else{
            return currentY;
        }
    }

}
