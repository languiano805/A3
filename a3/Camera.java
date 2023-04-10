package a3;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Vector3f defaultU
    private Vector3f defaultV;
    private Vector3f defaultN;
    private Vector3f location;
    private Vector3f defaultLocation;
    private Vector3f u;
    private Vector3f v;
    private Vector3f n;

    public Camera() {
        defaultLocation = new Vector3f(0.0f, 0.0f, 1.0f);
        defaultU = new Vector3f(1.0f, 0.0f, 0.0f);
        defaultV = new Vector3f(0.0f, 1.0f, 0.0f);
        defaultN = new Vector3f(0.0f, 0.0f, 1.0f);
        location = new Vector3f(defaultLocation);
        u = new Vector3f(defaultU);
        v = new Vector3f(defaultV);
        n = new Vector3f(defaultN);
    }

    
    





    

    


    
}
