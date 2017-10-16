package com.BouncingBall.Physics;

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

    public static void movingPointIntersectsMovingPoint(
            float point1X, float point1Y, float speed1X, float speed1Y, float radius1,
            float point2X, float point2Y, float speed2X, float speed2Y, float radius2,
            float timeLimit, CollisionResponse thisResponse, CollisionResponse anotherResponse){

        thisResponse.reset();
        anotherResponse.reset();

        float time = movingPointIntersectsMovingPointDetection(point1X, point1Y, speed1X, speed1Y, radius1,
                point2X, point2Y, speed2X, speed2Y, radius2,
                timeLimit, p1Response, p2Response);
/*        if(tempResponse.t < thisResponse.t){
            thisResponse.copy(p1Response);
        }

        if (p2Response.t < anotherResponse.t){
            anotherResponse.copy(p2Response);
        }*/
        if(time > 0 && time < timeLimit){
            movingPointIntersectsMovingPointResponse(point1X, point1Y, speed1X, speed1Y, radius1,
                    point2X, point2Y, speed2X, speed2Y, radius2,
                    time, thisResponse, anotherResponse);


        }
    }

    public static float movingPointIntersectsMovingPointDetection(
            float point1X, float point1Y, float speed1X, float speed1Y, float radius1,
            float point2X, float point2Y, float speed2X, float speed2Y, float radius2,
            float timeLimit, CollisionResponse thisResponse, CollisionResponse anotherResponse){

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

        //thisResponse.reset();
        //anotherResponse.reset();

        double collisionX = point1X - point2X;
        double collisionY = point1Y - point2Y;
        double speedX = speed1X - speed2X;
        double speedY = speed1Y - speed2Y;
        double radiusSq = (radius1 + radius2)*(radius1 + radius2);
        /*double a = (Math.pow(speedX, 2) + Math.pow(speedY, 2));
        double b = 2*(collisionX*speedX + collisionY*speedY);
        double c = (float)(Math.pow(collisionX, 2) + Math.pow(collisionY, 2) - radiusSq);
        double t1 = (float)(-b + Math.sqrt((double)(Math.pow(b, 2) - 4*a*c)))/(a);
        double t2 = (float)(-b - Math.sqrt((double)(Math.pow(b, 2) - 4*a*c)))/(a);*/
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
        /*float time = 0;
        if(t1 <= 0){
            if(t2 > 0){
                time = (float)t2;
            }
        }else if(t2 <= 0){
            if(t1 > 0){
                time = (float)t1;
            }
        }else if(t1 == t2){
            time = (float)t1;
        }else if(t1 <= t2){
            time = (float)t1;
        }else if(t2 <= t1){
            time = (float)t2;
        }
        return time;*/
    }

    public static void movingPointIntersectsMovingPointResponse(
            float point1X, float point1Y, float speed1X, float speed1Y, float radius1,
            float point2X, float point2Y, float speed2X, float speed2Y, float radius2,
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
        double p1ImpactX = thisResponse.getImpactX(point1X, speed1X);
        double p1ImpactY = thisResponse.getImpactY(point1Y, speed1Y);
        double p2ImpactX = anotherResponse.getImpactX(point2X, speed2X);
        double p2ImpactY = anotherResponse.getImpactY(point2Y, speed2Y);

        //angle from head on collision
        //defines line of collision
        double lineAngle = Math.atan2(p2ImpactY-p1ImpactY, p2ImpactX-p1ImpactX);

        //rotate x,y coords into p,n coords
        //p is line of collision, n is normal
        double[] result = rotate(speed1X, speed1Y, lineAngle);
        double speed1P = result[0];
        double speed1N = result[1];
        result = rotate(speed2X, speed2Y, lineAngle);
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


        double mass1 = radius1 * radius1 * radius1;
        double mass2 = radius2 * radius2 * radius2;
        double sumMass = mass1 + mass2;
        double diffMass = mass1 - mass2;
      /* //first ball speed response v3
        thisResponse.newSpeedX = (float)((2*mass2*speed2X + diffMass*speed1X)/sumMass);
        thisResponse.newSpeedY = (float)((2*mass2*speed2Y + diffMass*speed1Y)/sumMass);
        //second ball speed response v4
        anotherResponse.newSpeedX = (float)((2*mass1*speed1X - diffMass*speed2X)/sumMass);
        anotherResponse.newSpeedY = (float)((2*mass1*speed1Y - diffMass*speed2Y)/sumMass);*/

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
