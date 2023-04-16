package a3;

import org.joml.*;

public class Camera {
    private Vector3f location;
    private Vector3f u, v, n;
    private Matrix4f viewMatrix, viewRotationMatrix, viewTranslationMatrix;

    public Camera() {
        location = new Vector3f(0.0f, 0.0f, 25.0f);
        u = new Vector3f(1.0f, 0.0f, 0.0f);
        v = new Vector3f(0.0f, 1.0f, 0.0f);
        n = new Vector3f(0.0f, 0.0f, -1.0f);
        viewMatrix = new Matrix4f();
        viewRotationMatrix = new Matrix4f();
        viewTranslationMatrix = new Matrix4f();
    }

    // SETTERS
    //
    public void setU(Vector3f newU) {
        u.set(newU);
    }

    public void setV(Vector3f newV) {
        v.set(newV);
    }

    public void setN(Vector3f newN) {
        n.set(newN);
    }

    public void setLocation(Vector3f l) {
        location.set(l);
    }

    // GETTERS
    //
    public Vector3f getU() {
        return new Vector3f(u);
    }

    public Vector3f getV() {
        return new Vector3f(v);
    }

    public Vector3f getN() {
        return new Vector3f(n);
    }

    public Vector3f getLocation() {
        return new Vector3f(location);
    }
    
    protected Matrix4f getViewMatrix() {
        viewTranslationMatrix.set(1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                -location.x(), -location.y(), -location.z(), 1.0f);

        viewRotationMatrix.set(u.x(), v.x(), -n.x(), 0.0f,
                u.y(), v.y(), -n.y(), 0.0f,
                u.z(), v.z(), -n.z(), 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);

        viewMatrix.set(viewRotationMatrix);
        viewMatrix.mul(viewTranslationMatrix);

        return viewMatrix;
    }

    // CAMERA MOVEMENT
    //
    public void nMovement(float distance) {
        Vector3f temp = getLocation();
        Vector3f fDir = getN();
        fDir.mul(distance);
        Vector3f newLoc = temp.add(fDir);
        setLocation(newLoc);
    }

    public void uMovement(float distance) {
        Vector3f temp = getLocation();
        Vector3f fDir = getU();
        fDir.mul(distance);
        Vector3f newLoc = temp.add(fDir);
        setLocation(newLoc);
    }

    public void vMovement(float distance) {
        Vector3f temp = getLocation();
        Vector3f fDir = getV();
        fDir.mul(distance);
        Vector3f newLoc = temp.add(fDir);
        setLocation(newLoc);
    }

    public void pitch(float speed) {
        Vector3f camLoc = getLocation();
        Vector3f camU = getU();
        Vector3f camV = getV();
        Vector3f camN = getN();

        camU.rotateAxis(speed, camV.x(), camV.y(), camV.z());
        camN.rotateAxis(speed, camV.x(), camV.y(), camV.z());

        setU(camU);
        setN(camN);
    }

    public void pan(float speed) {
        Vector3f camLoc = getLocation();
        Vector3f camU = getU();
        Vector3f camV = getV();
        Vector3f camN = getN();

        camV.rotateAxis(speed, camU.x(), camU.y(), camU.z());
        camN.rotateAxis(speed, camU.x(), camU.y(), camU.z());

        setV(camV);
        setN(camN);

    }

}
