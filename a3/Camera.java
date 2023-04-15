package a3;

import org.joml.*;

public class Camera {
    private Vector3f location;
    private Vector3f u, v, n;
    private Matrix4f v_Matrix, rotMatrix, transMatrix;

    public Camera() {
        location = new Vector3f(0.0f, 1.5f, 8f);
        u = new Vector3f(1.0f, 0.0f, 0.0f);
        v = new Vector3f(0.0f, 1.0f, 0.0f);
        n = new Vector3f(0.0f, 0.0f, -1.0f);
        v_Matrix = new Matrix4f();
        rotMatrix = new Matrix4f();
        transMatrix = new Matrix4f();
    }

    

    
    





    

    


    
}
