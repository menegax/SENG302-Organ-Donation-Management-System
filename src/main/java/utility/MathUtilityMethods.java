package utility;

import javafx.geometry.Point2D;

public class MathUtilityMethods {

	
    /**
     * Calculates the angle centering on the second parameter
     * Gives angle in radians from -pi to pi (-ve anti-clockwise)
     * @param stationaryPoint the point to draw the angle from
     * @param previousPoint the point at the centre of the angle
     * @param currentPoint the point to draw the angle to
     * @return the angle between the point
     */
    public static double calculateAngle(Point2D stationaryPoint, Point2D previousPoint, Point2D currentPoint) {
        double p2s = calculateDisplacement(previousPoint, stationaryPoint);
        double p2c = calculateDisplacement(previousPoint, currentPoint);
        double s2c = calculateDisplacement(stationaryPoint, currentPoint);
        double angle = Math.PI - Math.acos((Math.pow(p2s, 2) + Math.pow(p2c, 2) - Math.pow(s2c, 2)) / (2 * p2s * p2c));
        if (angleClockwise(stationaryPoint, previousPoint, currentPoint)) {
            return angle;
        } else {
            return -angle;
        }
    }

    /**
     * Calculates the cross product of the angle and whether it is formed in a clockwise direction or not
     * @param stationaryPoint the point to draw the angle from
     * @param previousPoint the point at the centre of the angle
     * @param currentPoint the point to draw the angle to
     * @return whether the angle is orientated clockwise or not
     */
    private static boolean angleClockwise(Point2D stationaryPoint, Point2D previousPoint, Point2D currentPoint) {
        return (currentPoint.getX() - stationaryPoint.getX()) * (previousPoint.getY() - stationaryPoint.getY()) - (currentPoint.getY() - stationaryPoint.getY())*(previousPoint.getX() - stationaryPoint.getX()) >= 0;
    }

    /**
     * Returns the scalar displacement between two points
     * @param start the first point
     * @param end the second point to calculate the displacement to
     * @return the displacement between the two points
     */
    public static double calculateDisplacement(Point2D start, Point2D end) {
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));
    }
}
