package com.BouncingBall.Physics;

import com.BouncingBall.OOrientated.Ball;

public class CollisionPhysics {
    //Working copy for computing response in intersect(ContainerBox box), to avoid repeatedly allocating objects.
    private static CollisionResponse tempResponse = new CollisionResponse();
    private static CollisionResponse p1Response = new CollisionResponse();
    private static CollisionResponse p2Response = new CollisionResponse();

    /**
     *
     * @param timeLimit assumed to be 1
     * @param response contains the first collision to occur in the next timestep
     */
    public static void pointIntersectsRectangleOuter(Ball ball, float rectX1, float rectY1, float rectX2, float rectY2,
            float timeLimit, CollisionResponse response){

        response.reset(); //reset detected collision time to infinity

        //test the intersect with the 4 borders
        //Right border
        pointIntersectsLineVertical(ball, rectX2, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        //Left border
        pointIntersectsLineVertical(ball, rectX1, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        //Top border
        pointIntersectsLineHorizontal(ball, rectY1, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        //Bottom border
        pointIntersectsLineHorizontal(ball, rectY2, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }
    }

    public static void pointIntersectsLineVertical(Ball ball, float lineX, float timeLimit, CollisionResponse response){

        response.reset();

        float speedX = ball.getSpeedXY()[0];
        if(speedX == 0){
            return;
        }

        float distance;
        float pX = ball.getPositionXY()[0];
        if(lineX > pX){
            distance = lineX - pX - ball.getRadius();
        }else {
            distance = lineX - pX + ball.getRadius();
        }

        float t = distance / speedX;
        //if within timeLimit
        if(t > 0 && t <= timeLimit){
            response.t = t;
            response.newSpeedX = -speedX; //reflected in x
            response.newSpeedY = ball.getSpeedXY()[1]; //not reflected in y
        }
    }

    public static void pointIntersectsLineHorizontal(Ball ball, float lineY, float timeLimit, CollisionResponse response){

        response.reset();

        float speedY = ball.getSpeedXY()[1];
        if(speedY == 0){
            return;
        }

        float distance;
        float pY = ball.getPositionXY()[1];
        if(lineY > pY){
            distance = lineY - pY - ball.getRadius();
        }else {
            distance = lineY - pY + ball.getRadius();
        }

        float t = distance / speedY;
        //if within timeLimit
        if(t > 0 && t <= timeLimit){
            response.t = t;
            response.newSpeedX = ball.getSpeedXY()[0]; //not reflected in x
            response.newSpeedY = -speedY; //reflected in y
        }
    }

    public static void pointIntersectsCircleOuter(Ball ball, float outerCenterX, float outerCenterY, float outerRadius,
                                                  float timeLimit, CollisionResponse response){
        response.reset();

        float t = pointIntersectsCircleOuterDetection(ball,
                outerCenterX, outerCenterY, outerRadius);

        if(t > 0 && t <= timeLimit){
            float impactX = ball.getPositionXY()[0] + ball.getSpeedXY()[0] * t;
            float impactY = ball.getPositionXY()[1] + ball.getSpeedXY()[1] * t;

            pointIntersectsLineNormalResponse(ball.getPositionXY()[0], ball.getPositionXY()[1], ball.getSpeedXY()[0], ball.getSpeedXY()[1],
                    outerCenterX, outerCenterY, impactX, impactY,
                    response, t);
        }
    }

    public static float pointIntersectsCircleOuterDetection(Ball ball, float outerCenterX, float outerCenterY, float outerRadius){

        double offsetX = ball.getPositionXY()[0] - outerCenterX;
        double offsetY = ball.getPositionXY()[1] - outerCenterY;
        double effectiveRadius = outerRadius - ball.getRadius();
        double offsetXSq = offsetX * offsetX;
        double offsetYSq = offsetY * offsetY;
        double speedX = ball.getSpeedXY()[0];
        double speedY = ball.getSpeedXY()[1];
        double speedXSq = speedX * speedX;
        double speedYSq = speedY * speedY;
        double radiusSq = effectiveRadius * effectiveRadius;

        double termA = speedXSq + speedYSq;
        double termB = 2 * (speedX*offsetX + speedY*offsetY);
        double termC = offsetXSq + offsetYSq - radiusSq;

        // x = (-b + sqrt(b^2 - 4ac))/2a

        double termB2Minus4AC = termB * termB - 4 * termA * termB;

        if(termB2Minus4AC < 0){
            return Float.MAX_VALUE;
        }

        double termB2Minus4ACSqrt = Math.sqrt(termB2Minus4AC);
        double t1 = (-termB + termB2Minus4ACSqrt)/(2*termA);
        double t2 = (-termB - termB2Minus4ACSqrt)/(2*termA);

        if(t1 > 0 && t2 > 0){
            return (float)Math.min(t1, t2);
        }else if(t1 > 0){
            return (float)t1;
        }else if(t2 > 0){
            return (float)t2;
        }else {
            return Float.MAX_VALUE;
        }


    }

    public static void pointIntersectsLineNormalResponse(float pX, float pY, float speedX, float speedY,
                                                         float centerX, float centerY, float impactX, float impactY,
                                                         CollisionResponse response, float t){

        response.t = t;

        double lineAngle = Math.atan2(impactY-centerY, impactX-centerX);
        double[] result = rotate(speedX, speedY, lineAngle);
        double speedP = result[0];
        double speedN = result[1];

        //Reflect along the collision (P),  no change along normal
        double speedPAfter = -speedP;
        double speedNAfter = speedN;

        result = rotate(speedPAfter, speedNAfter, -lineAngle);
        response.newSpeedX = (float)result[0];
        response.newSpeedY = (float)result[1];

    }

    public static void movingPointIntersectsPoint(Ball ball1, Ball ball2,
                                                  float timeLimit, CollisionResponse thisResponse, CollisionResponse anotherResponse){

        thisResponse.reset();
        anotherResponse.reset();

        float time = movingPointIntersectsPointDetection(ball1, ball2);
        if(time > 0 && time < timeLimit){
            movingPointIntersectsPointResponse(ball1,
                    ball2,
                    time, thisResponse, anotherResponse);
        }
    }

    public static float movingPointIntersectsPointDetection(Ball ball1, Ball ball2){

        //detection occurs when the distance between two balls, r, is r = r1 + r2
        //  |point2 - point1|^2 = r^2
        //  point1 = origin1 + t1*velocity1
        //  point2 = origin2 + t2*velocity2
        //sub into above and rearrange such that the common terms are grouped
        //  |point + t*velocity|^2 = r^2
        //where point = point2 - point1, t = t1 = t2, velocity = velocity2 - velocity1
        //let point and velocity be represented as maxtrices in 2D space
        //express as a quadratic equation then solve in the quadratic formula
        //  (pointX + t*velocityX)^2 + (pointY + t*velocityY)^2 = r^2
        //  (velocityX^2 + velocityY^2)t^2 + 2(pointX*velocityX + pointY*velocityY)t + (pointX^2 + pointY^2 - r^2) = 0
        //  a*t^2 + b*t + c = 0
        //  t = (-b +- (b^2 -4ac)^1/2)/2a

        double collisionX = ball1.getPositionXY()[0] - ball2.getPositionXY()[0];
        double collisionY = ball1.getPositionXY()[1] - ball2.getPositionXY()[1];
        double speedX = ball1.getSpeedXY()[0] - ball2.getSpeedXY()[0];
        double speedY = ball1.getSpeedXY()[1] - ball2.getSpeedXY()[1];
        double radiusSq = (ball1.getRadius() + ball2.getRadius())*(ball1.getRadius() + ball2.getRadius());
        double speedXSq = speedX*speedX;
        double speedYSq = speedY*speedY;
        double speedSq = speedXSq + speedYSq; //a

        double termB2minus4AC = radiusSq*speedSq - (collisionX*speedY - collisionY*speedX)*(collisionX*speedY - collisionY*speedX);
        if(termB2minus4AC < 0){
            return Float.MAX_VALUE;
        }

        double termMinusB = -speedX*collisionX - speedY*collisionY;
        double term2a = speedSq;
        double rootB2Minus4AC = Math.sqrt(termB2minus4AC);
        double t1 = (termMinusB + rootB2Minus4AC)/term2a;
        double t2 = (termMinusB - rootB2Minus4AC)/term2a;

        if(t1 > 0 && t2 > 0){
            return (float)Math.min(t1, t2);
        }else if(t1 > 0){
            return (float)t1;
        }else if(t2 > 0){
            return (float)t2;
        }else {
            return Float.MAX_VALUE;
        }
    }

    public static void movingPointIntersectsPointResponse(Ball ball1, Ball ball2,
                                                          float time, CollisionResponse thisResponse, CollisionResponse anotherResponse){
        //calculated using the equations:
        //  v3 = [2*m2*v2 + (m1 - m2)*v1] / (m1 + m2)
        //  v4 = [2*m1*v1 + (m2 - m1)*v2] / (m1 + m2)
        //These will be split for the x and y speeds
        //This equation is derived from the law of conservation of momentum:
        //  m1*v1 + m2*v2 = m1*v3 + m2*v4
        //and the law of conservation of kinetic energy:
        //  1/2*m1*v1^2 + 1/2*m2*v2^2 = 1/2*m1*v3^2 + 1/2*m2*v4^2

        //update the time
        thisResponse.t = time;
        anotherResponse.t = time;

        //ball locations during point of impact
        double p1ImpactX = thisResponse.getImpactX(ball1.getPositionXY()[0], ball1.getSpeedXY()[0]);
        double p1ImpactY = thisResponse.getImpactY(ball1.getPositionXY()[1], ball1.getSpeedXY()[1]);
        double p2ImpactX = anotherResponse.getImpactX(ball2.getPositionXY()[0], ball2.getSpeedXY()[0]);
        double p2ImpactY = anotherResponse.getImpactY(ball2.getPositionXY()[1], ball2.getSpeedXY()[1]);

        //angle from head on collision
        //defines line of collision
        double lineAngle = Math.atan2(p2ImpactY-p1ImpactY, p2ImpactX-p1ImpactX);

        //rotate x,y coords into p,n coords
        //p is line of collision, n is normal
        double[] result = rotate(ball1.getSpeedXY()[0], ball1.getSpeedXY()[1], lineAngle);
        double speed1P = result[0];
        double speed1N = result[1];
        result = rotate(ball2.getSpeedXY()[0], ball2.getSpeedXY()[1], lineAngle);
        double speed2P = result[0];
        double speed2N = result[1];

        // Collision possible only if speed1P - speed2P > 0
        // Needed if the two balls overlap in their initial positions
        // Do not declare collision, so that they continue their course of movement
        // until they are separated.
        if(speed1P - speed2P <= 0){
            thisResponse.reset();
            anotherResponse.reset();
            return;
        }

        double mass1 = ball1.getMass();
        double mass2 = ball2.getMass();
        double sumMass = mass1 + mass2;
        double diffMass = mass1 - mass2;

      //along the collision, P (head-on collision)
        double speed1PAfter, speed1NAfter, speed2PAfter, speed2NAfter;
        speed1PAfter = (2*mass2*speed2P + diffMass*speed1P)/sumMass;
        speed2PAfter = (2*mass1*speed1P - diffMass*speed2P)/sumMass;

        //No change in normal, N, as head-on collision
        speed1NAfter = speed1N;
        speed2NAfter = speed2N;

        //rotate back to x,y coords
        result = rotate(speed1PAfter, speed1NAfter, -lineAngle);
        thisResponse.newSpeedX = (float)result[0];
        thisResponse.newSpeedY = (float)result[1];
        result = rotate(speed2PAfter, speed2NAfter, -lineAngle);
        anotherResponse.newSpeedX = (float)result[0];
        anotherResponse.newSpeedY = (float)result[1];

    }

    public static void movingPointIntersectsOvalObstacle(Ball ball, int x, int y, int width, int height, float timeLimit, CollisionResponse response){

    }

    private static double[] rotateResult = new double[2];
    private static double[] rotate(double x, double y, double theta){
        //double[] result = new double[2];
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        rotateResult[0] = x*cosTheta + y*sinTheta;
        rotateResult[1] = -x*sinTheta + y*cosTheta;
        return rotateResult;
    }

}
