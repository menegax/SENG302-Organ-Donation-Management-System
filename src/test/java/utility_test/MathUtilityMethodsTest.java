package utility_test;

import static org.junit.Assert.*;

import org.junit.Test;
import javafx.geometry.Point2D;
import utility.MathUtilityMethods;

public class MathUtilityMethodsTest {

	@Test
	public void testCalculateAngle() {
		Point2D point1 = new Point2D(5,5);
		Point2D point2 = new Point2D(1,10);
		Point2D point3 = new Point2D(7,26);
		//The correct angle result calculated
		double angle = 1.034;
		//The angle calculated from the calculateAngle method
		double calcAngle = ((double)Math.round((MathUtilityMethods.calculateAngle(point1, point2, point3) * 1000))) / 1000;
		assertTrue(angle == calcAngle);
	}

	@Test
	public void testCalculateDisplacement() {
		Point2D point1 = new Point2D(115, 25);
		Point2D point2 = new Point2D(-12, 55);
		//The correct displacement result calculated
		double disp = 130.0;
		//The displacement calculated from the calculateDisplacement method
		double calcDisp = Math.round(MathUtilityMethods.calculateDisplacement(point1, point2) * 1000) / 1000;
		assertTrue(disp == calcDisp);
	}

}
