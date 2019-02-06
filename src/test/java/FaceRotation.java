import PhysicsEngine.math.Face;
import PhysicsEngine.math.Formulas;
import PhysicsEngine.math.Point;
import PhysicsEngine.math.Vec2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FaceRotation {

    @Test
    public void testFaces(){
        Face face1 = new Face(new Point(0, -10), new Point(10, 0));
        test(face1);
        face1 = new Face(new Point(10, 0), new Point(0, 10));
        test(face1);
        face1 = new Face(new Point(0, 10), new Point(-10, 0));
        test(face1);
        face1 = new Face(new Point(-10, 0), new Point(0, -10));
        test(face1);

        face1 = new Face(new Point(-3, 53), new Point(345, -5));
        test(face1);

        face1 = new Face(new Point(60, -90), new Point(12, -10));
        test(face1);

        face1 = new Face(new Point(1, 1), new Point(130, 1));
        test(face1);
    }

    public void test(Face face){
        Vec2 normal = face.getVec().tangent();
        normal.normalize();

        Vec2 pointVecFromCenter = face.getP1().getVec();
        if(Formulas.dotProduct(normal, pointVecFromCenter) < 0)
        {
            normal.mult(-1.0f);
        }

        Vec2 faceVec = face.getVec();
        if(faceVec.getY() != 0) {
            float faceAngle = (float) Math.acos(-normal.getY());
            if(normal.getX() > 0) faceAngle *= -1.0f;
//            System.out.println(Formulas.toDegrees(faceAngle));
            face.rotateAboutOrigin(faceAngle, true);
        }

        if(Float.isNaN(face.getP1().getY())) assertEquals(true, false);

        assertEquals(face.getP1().getY(), face.getP2().getY(), 0.01);
    }
}