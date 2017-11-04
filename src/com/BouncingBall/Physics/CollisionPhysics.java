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
    public static void pointIntersectsRectangleOuter(float pX, float pY, float speedX, float speedY, float radius,
                                                     float rectX1, float rectY1, float rectX2, float rectY2,
                                                     float timeLimit, CollisionResponse response){

        response.reset(); //reset detected collision time to infinity

        //test the intersect with the 4 borders
        //Right border
        pointIntersectsLineVertical(pX, pY, speedX, speedY, radius, rectX2, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        //Left border
        pointIntersectsLineVertical(pX, pY, speedX, speedY, radius, rectX1, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        //Top border
        pointIntersectsLineHorizontal(pX, pY, speedX, speedY, radius, rectY1, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        //Bottom border
        pointIntersectsLineHorizontal(pX, pY, speedX, speedY, radius, rectY2, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }
    }

    public static void pointIntersectsLineVertical(float pX, float pY, float speedX, float speedY, float radius,
                                                   float lineX, float timeLimit, CollisionResponse response){

        response.reset();

        if(speedX == 0){
            return;
        }

        float distance;
        if(lineX > pX){
            distance = lineX - pX - radius;
        }else {
            distance = lineX - pX + radius;
        }

        float t = distance / speedX;
        //if within timeLimit
        if(t > 0 && t <= timeLimit){
            response.t = t;
            response.newSpeedX = -speedX; //reflected in x
            response.newSpeedY = speedY; //not reflected in y
        }
    }

    public static void pointIntersectsLineHorizontal(float pX, float pY, float speedX, float speedY, float radius,
                                                     float lineY, float timeLimit, CollisionResponse response){

        response.reset();

        if(speedY == 0){
            return;
        }

        float distance;
        if(lineY > pY){
            distance = lineY - pY - radius;
        }else {
            distance = lineY - pY + radius;
        }

        float t = distance / speedY;
        //if within timeLimit
        if(t > 0 && t <= timeLimit){
            response.t = t;
            response.newSpeedX = speedX; //not reflected in x
            response.newSpeedY = -speedY; //reflected in y
        }
    }

    public static void pointIntersectsCircleOuter(float pX, float pY, float speedX, float speedY, float ballRadius,
                                                  float outerCenterX, float outerCenterY, float outerRadius,
                                                  float timeLimit, CollisionResponse response){
        response.reset();

        float t = pointIntersectsCircleOuterDetection(pX, pY, speedX, speedY, ballRadius,
                outerCenterX, outerCenterY, outerRadius);

        if(t > 0 && t <= timeLimit){

            float impactX = pX + speedX * t;
            float impactY = pY + speedY * t;

            pointIntersectsLineNormalResponse(pX, pY, speedX, speedY,
                    outerCenterX, outerCenterY, impactX, impactY, t, response);
        }
    }

    public static float pointIntersectsCircleOuterDetection(float pX, float pY, float speedX, float speedY, float ballRadius,
                                                            float outerCenterX, float outerCenterY, float outerRadius){

        double offsetX = pX - outerCenterX;
        double offsetY = pY - outerCenterY;
        double effectiveRadius = outerRadius - ballRadius;
        double offsetXSq = offsetX * offsetX;
        double offsetYSq = offsetY * offsetY;
        double speedXSq = speedX * speedX;
        double speedYSq = speedY * speedY;
        double radiusSq = effectiveRadius * effectiveRadius;

        double termA = speedXSq + speedYSq;
        double termB = 2 * (speedX*offsetX + speedY*offsetY);
        double termC = offsetXSq + offsetYSq - radiusSq;

        double t = quadraticEquationMin(termA, termB, termC);
        return (float)(t!=Double.MAX_VALUE ? t : Float.MAX_VALUE);

    }

    public static void pointIntersectsLineNormalResponse(float pX, float pY, float speedX, float speedY,
                                                         float lineX1, float lineY1, float lineX2, float lineY2,
                                                         float t, CollisionResponse response){

        response.t = t;

        //rotate so tangent is normal, and collision is p
        double lineAngle = Math.atan2(lineY2-lineY1, lineX2-lineX1);
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

    public static void pointIntersectsMovingPoint(float p1X, float p1Y, float speed1X, float speed1Y, float radius1,
                                                  float p2X, float p2Y, float speed2X, float speed2Y, float radius2,
                                                  float timeLimit, CollisionResponse thisResponse, CollisionResponse anotherResponse){

        thisResponse.reset();
        anotherResponse.reset();

        float time = pointIntersectsMovingPointDetection(p1X, p1Y, speed1X, speed1Y, radius1,
                                                         p2X, p2Y, speed2X, speed2Y, radius2);
        if(time > 0 && time < timeLimit){
            pointIntersectsMovingPointResponse(p1X, p1Y, speed1X, speed1Y, radius1,
                                               p2X, p2Y, speed2X, speed2Y, radius2,
                                               time, thisResponse, anotherResponse);
        }
    }

    public static float pointIntersectsMovingPointDetection(float p1X, float p1Y, float speed1X, float speed1Y, float radius1,
                                                            float p2X, float p2Y, float speed2X, float speed2Y, float radius2){

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

        double collisionX = p1X - p2X;
        double collisionY = p1Y - p2Y;
        double speedX = speed1X - speed2X;
        double speedY = speed1Y - speed2Y;
        double radiusSq = (radius1 + radius2)*(radius1 + radius2);
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

    public static void pointIntersectsMovingPointResponse(float p1X, float p1Y, float speed1X, float speed1Y, float radius1,
                                                          float p2X, float p2Y, float speed2X, float speed2Y, float radius2,
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
        double p1ImpactX = thisResponse.getImpactX(p1X, speed1X);
        double p1ImpactY = thisResponse.getImpactY(p1Y, speed1Y);
        double p2ImpactX = anotherResponse.getImpactX(p2X, speed2X);
        double p2ImpactY = anotherResponse.getImpactY(p2Y, speed2Y);

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


    public static void pointIntersectsPolygon(float pX, float pY, float speedX, float speedY, float radius,
                                              float[] polyXs, float[] polyYs, int numPoints,
                                              float timeLimit, CollisionResponse response){

        //TODO: assume size of polyXs == polyYs
        //assume radius >= 0
        //assume timeLimit > 0

        response.reset();
        float lineX1, lineY1, lineX2, lineY2;
        for(int i = 0; i < numPoints; i++){
            lineX1 = polyXs[i];
            lineY1 = polyYs[i];
            lineX2 = polyXs[(i + 1) % numPoints];
            lineY2 = polyYs[(i + 1) % numPoints];

            pointIntersectsLineSegmentNoEndPoints(pX, pY, speedX, speedY, radius,
                                                  lineX1, lineY1, lineX2, lineY2,
                                                  timeLimit, tempResponse);
            if(tempResponse.t < response.t){
                response.copy(tempResponse);
            }

            pointIntersectsPoint(pX, pY, speedX, speedY, radius,
                                 lineX1, lineY1, 0.0f,
                                 timeLimit, tempResponse);
            if(tempResponse.t < response.t){
                response.copy(tempResponse);
            }

        }

    }

    public static void pointIntersectsLineSegment(float pX, float pY, float speedX, float speedY, float radius,
                                                  float lineX1, float lineY1, float lineX2, float lineY2,
                                                  float timeLimit, CollisionResponse response){

        response.reset();
        pointIntersectsLineSegmentNoEndPoints(pX, pY, speedX, speedY, radius,
                                              lineX1, lineY1, lineX2, lineY2,
                                              timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        pointIntersectsPoint(pX, pY, speedX, speedY, radius, lineX1, lineY1, 0.0f, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }

        pointIntersectsPoint(pX, pY, speedX, speedY, radius, lineX2, lineY2, 0.0f, timeLimit, tempResponse);
        if(tempResponse.t < response.t){
            response.copy(tempResponse);
        }


    }

    public static void pointIntersectsLineSegmentNoEndPoints(float pX, float pY, float speedX, float speedY, float radius,
                                                        float lineX1, float lineY1, float lineX2, float lineY2,
                                                        float timeLimit, CollisionResponse response){

        //response.reset();

        if(lineX1 == lineX2){
            pointIntersectsLineVertical(pX, pY, speedX, speedY, radius, lineX1, timeLimit, response);
            double impactY = response.getImpactY(pY, speedY);
            if (!(impactY >= lineY1 && impactY <= lineY2 || impactY >= lineY2 && impactY <= lineY1)) {
                response.reset();  // no collision
            }
            return;
        }else if(lineY1 == lineY2){
            pointIntersectsLineHorizontal(pX, pY, speedX, speedY, radius, lineY1, timeLimit, response);
            // Need to confirm that the point of impact is within the line-segment
            double impactX = response.getImpactX(pX, speedX);
            if (!(impactX >= lineX1 && impactX <= lineX2 || impactX >= lineX2 && impactX <= lineX1)) {
                response.reset();
            }
            return;
        }

        response.reset();

        float[] result = pointIntersectsLineDetection(pX, pY, speedX, speedY, radius,
                                                lineX1, lineY1, lineX2, lineY2,
                                                timeLimit, response);
        float t = result[0];
        float lambda = result[1];

        if(t >= 0 && t <= 1 && lambda >= 0 && lambda <= 1){
            pointIntersectsLineResponse(pX, pY, speedX, speedY, lineX1, lineY1, lineX2, lineY2, t, response);
        }
    }

    public static float[] pointIntersectsLineDetection(float pX, float pY, float speedX, float speedY, float radius,
                                                       float lineX1, float lineY1, float lineX2, float lineY2,
                                                       float timeLimit, CollisionResponse response){

        double lineVectorX = lineX2 - lineX1;
        double lineVectorY = lineY2 - lineY1;

        double lineAngle = Math.atan2(lineVectorY, lineVectorX);
        double rotatedY = rotate(pX - lineX1, pY - lineY1, lineAngle)[1];

        double lineX1Offset = lineX1;
        double lineY1Offset = lineY1;
        if(rotatedY > 0){
            lineX1Offset -= radius * Math.sin(lineAngle);
            lineY1Offset += radius * Math.cos(lineAngle);
        } else{
            lineX1Offset += radius * Math.sin(lineAngle);
            lineY1Offset -= radius * Math.cos(lineAngle);
        }

        double det = speedY * lineVectorX - speedX * lineVectorY;
        if(det == 0){
            return new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
        }

        double xDiff = lineX1Offset - pX;
        double yDiff = lineY1Offset - pY;

        double t = (lineVectorX * yDiff - lineVectorY * xDiff) / det;
        double lambda = (speedX * yDiff - speedY * xDiff) / det;

        return new float[]{(float)t, (float)lambda};

    }

    public static void pointIntersectsLineResponse(float pX, float pY, float speedX, float speedY,
                                                   float lineX1, float lineY1, float lineX2, float lineY2,
                                                   float t, CollisionResponse response){

        response.t = t;

        //rotate so tangent is normal, and collision is p
        double lineAngle = Math.atan2(lineY2-lineY1, lineX2-lineX1);
        double[] result = rotate(speedX, speedY, lineAngle);
        double speedP = result[0];
        double speedN = result[1];

        //Reflect along the collision (P),  no change along normal
        double speedPAfter = speedP;
        double speedNAfter = -speedN;

        result = rotate(speedPAfter, speedNAfter, -lineAngle);
        response.newSpeedX = (float)result[0];
        response.newSpeedY = (float)result[1];

    }

    public static void pointIntersectsPoint(float p1X, float p1Y, float speedX, float speedY, float radius1,
                                            float p2X, float p2Y, float radius2,
                                            float timeLimit, CollisionResponse response){

        float t = pointIntersectsMovingPointDetection(p1X, p1Y, speedX, speedY, radius1,
                                                      p2X, p2Y, 0.0f, 0.0f, radius2);

        if(t >= 0 && t <= 1){
            pointIntersectsPointResponse(p1X, p1Y, speedX, speedY, p2X, p2Y, t, response);
        }

    }

    public static void pointIntersectsPointResponse(float p1X, float p1Y, float speedX, float speedY,
                                                    float p2X, float p2Y, float t, CollisionResponse response){

        response.t = t;
        double impactX = response.getImpactX(p1X, speedX);
        double impactY = response.getImpactY(p1Y, speedY);

        double lineAngle = Math.atan2(p2Y - impactY, p2X - impactX);
        double[] result = rotate(speedX, speedY, lineAngle);
        double speedP = result[0];
        double speedN = result[1];

        if(speedP <= 0){
            response.reset();
            return;
        }

        double speedPAfter = -speedP;
        double speedNAfter = speedN;

        result = rotate(speedPAfter, speedNAfter, -lineAngle);
        response.newSpeedX = (float)result[0];
        response.newSpeedY = (float)result[1];

    }


    /*public static void movingPointIntersectsOvalObstacle(Ball ball, int x, int y, int width, int height, float timeLimit, CollisionResponse response){

    }*/

    //having static static array reduces time needed for creation of array everytime rotate is called
    private static double[] rotateResult = new double[2];
    private static double[] rotate(double x, double y, double theta){
        //double[] result = new double[2];
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        rotateResult[0] = x*cosTheta + y*sinTheta;
        rotateResult[1] = -x*sinTheta + y*cosTheta;
        return rotateResult;
    }

    //creating static array when value is returned
    private static double[] quadraticEquation(double a, double b, double c){
        // x = (-b + sqrt(b^2 - 4ac))/(2a)
        double termB2minus4AC = b*b - 4*a*c;
        if(termB2minus4AC < 0){
            return new double[]{Double.MAX_VALUE};
        }
        double sqrtB2minus4AC = Math.sqrt(termB2minus4AC);
        double x1 = (-b + sqrtB2minus4AC)/(2*a);
        double x2 = (-b - sqrtB2minus4AC)/(2*a);

        return new double[] {x1, x2};
    }

    private static double quadraticEquationMin(double a, double b, double c){
        double[] result = quadraticEquation(a, b, c);

        if(result[0] == Double.MAX_VALUE){
            return Double.MAX_VALUE;
        }

        if(result[0] > 0 && result[1] > 0){
            return Math.min(result[0], result[1]);
        }else if(result[0] > 0){
            return result[0];
        }else if(result[1] > 0){
            return result[1];
        }else {
            return Double.MAX_VALUE;
        }
    }

}
