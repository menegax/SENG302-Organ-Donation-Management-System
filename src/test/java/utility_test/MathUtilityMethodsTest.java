package utility_test;

import static org.junit.Assert.*;

import org.junit.Test;
import javafx.geometry.Point2D;
import utility.MathUtilityMethods;

public class MathUtilityMethodsTest {

	@Test
	public void testCalculateAngle() {
		Point2D point1 = new Point2D(50,50);
		Point2D point2 = new Point2D(123,1);
		Point2D point3 = new Point2D(594,272);
		//The correct angle result calculated
		double angle = 2.028;
		//The angle calculated from the calculateAngle method
		double calcAngle = Math.round(MathUtilityMethods.calculateAngle(point1, point2, point3) * 1000) / 1000;
		assertTrue(angle == calcAngle);
	}

	@Test
	public void testCalculateDisplacement() {
		Point2D point1 = new Point2D(115, 25);
		Point2D point2 = new Point2D(-12, 55);
		//The correct displacement result calculated
		double disp = 130.495;
		//The displacement calculated from the calculateDisplacement method
		double calcDisp = Math.round(MathUtilityMethods.calculateDisplacement(point1, point2) * 1000) / 1000;
		assertTrue(disp == calcDisp);
	}

}
