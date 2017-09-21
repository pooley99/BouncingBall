package com.BouncingBall.Physics;

public class CollisionPhysics {
    //Working copy for computing response in intersect(ContainerBox box), to avoid repeatedly allocating objects.
    private static CollisionResponse tempResponse = new CollisionResponse();

    /**
     *
     * @param timeLimit assumed to be 1
     * @param response contains the first collision to occur in the next timestep
     */
    public static void pointIntersectsRectangleOuter(
            float pointX, float pointY, float speedX, float speedY, float radius,
            float rectX1, float rectY1, float rectX2, float rectY2,
            float timeLimit, CollisionResponse response){

        response.reset(); //reset detected collision time to infinity

        //test the intersect with the 4 borders
        //Right border
        pointIntersectsLineVertical(pointX, pointY, speedX, speedY, radius,
                rectX2, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        //Left border
        pointIntersectsLineVertical(pointX, pointY, speedX, speedY, radius,
                rectX1, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        //Top border
        pointIntersectsLineHorizontal(pointX, pointY, speedX, speedY, radius,
                rectY1, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        //Bottom border
        pointIntersectsLineHorizontal(pointX, pointY, speedX, speedY, radius,
                rectY2, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }
    }

    public static void pointIntersectsLineVertical(
            float pointX, float pointY, float speedX, float speedY, float radius,
            float lineX, float timeLimit, CollisionResponse response){

        response.reset();

        if(speedX == 0){
            return;
        }

        float distance;
        if(lineX > pointX){
            distance = lineX - pointX - radius;
        }else {
            distance = lineX - pointX + radius;
        }

        float t = distance / speedX;
        //if within timeLimit
        if(t > 0 && t <= timeLimit){
            response.t = t;
            response.newSpeedX = -speedX; //reflected in x
            response.newSpeedY = speedY; //not reflected in y
        }
    }

    public static void pointIntersectsLineHorizontal(
            float pointX, float pointY, float speedX, float speedY, float radius,
            float lineY, float timeLimit, CollisionResponse response){

        response.reset();

        if(speedY == 0){
            return;
        }

        float distance;
        if(lineY > pointY){
            distance = lineY - pointY - radius;
        }else {
            distance = lineY - pointY + radius;
        }

        float t = distance / speedY;
        //if within timeLimit
        if(t > 0 && t <= timeLimit){
            response.t = t;
            response.newSpeedX = speedX; //not reflected in x
            response.newSpeedY = -speedY; //reflected in y
        }
    }



}
