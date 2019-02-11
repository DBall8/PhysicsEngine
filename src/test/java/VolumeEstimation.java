import PhysicsEngine.Material;
import PhysicsEngine.math.MalformedPolygonException;
import PhysicsEngine.math.Polygon;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VolumeEstimation {

    private final static double MARGIN_OF_ERROR = 0.01;
    List<TestUnit> tests = new ArrayList<>();

    public void initialize()
    {
        try {
            Polygon square = new Polygon(new float[]{
                    0, 0,
                    10, 0,
                    10, 10,
                    0, 10
            });
            tests.add(new TestUnit("Square", square, 100));

            Polygon rightTriangle = new Polygon(new float[]{
                    0, 0,
                    10, 0,
                    10, 10
            });
            tests.add(new TestUnit("Right Triangle", rightTriangle, 50));

            Polygon longTriangle = new Polygon(new float[]{
                    0, 0,
                    0, 2,
                    100, 0
            });
            tests.add(new TestUnit("Long Triangle", longTriangle, 100));

            Polygon longTriangle2 = new Polygon(new float[]{
                    0, 0,
                    100, 0,
                    0, 2
            });
            tests.add(new TestUnit("Long Triangle 2", longTriangle2, 100));

            Polygon bigSquare = new Polygon(new float[]{
                    0, 0,
                    200, 0,
                    200, 200,
                    0, 200
            });
            tests.add(new TestUnit("Big Square", bigSquare, 40000));

            Polygon massiveSquare = new Polygon(new float[]{
                    0, 0,
                    1000, 0,
                    1000, 1000,
                    0, 1000
            });
            tests.add(new TestUnit("Massive Square", massiveSquare, 1000000));

            Polygon rotatedSquare = new Polygon(new float[]{
                    0, 0,
                    20, 10,
                    0, 20,
                    -20, 10
            });
            tests.add(new TestUnit("Rotated Square", rotatedSquare, 400));

            Polygon kite = new Polygon(new float[]{
                    0, 0,
                    80, 30,
                    95, 0,
                    80, -30
            });
            tests.add(new TestUnit("Kite", kite, 2850));

        } catch (MalformedPolygonException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testVolumes()
    {
        initialize();

        for(TestUnit test: tests)
        {
            debugPrint(test.polygon, test.name, test.actualArea);
        }

        for(TestUnit test: tests)
        {
            assertEquals(test.polygon.estimateVolume2(), test.actualArea, test.actualArea*MARGIN_OF_ERROR);
        }
    }

    public void debugPrint(Polygon poly, String name, float actual)
    {
        System.out.format("%s:\n Actual: %f -- Old: %f -- New: %f\n\n", name, actual, poly.estimateVolume(), poly.estimateVolume2());
    }

    private class TestUnit
    {
        String name;
        Polygon polygon;
        float actualArea;

        public TestUnit(String name, Polygon polygon, float actualArea)
        {
            this.name = name;
            this.polygon = polygon;
            this.actualArea = actualArea;
        }
    }

}
