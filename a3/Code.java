package a3;

import java.nio.*;
import java.lang.Math;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLContext;
import org.joml.*;

public class Code extends JFrame implements GLEventListener {
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[2];
	private float cameraX, cameraY, cameraZ;
	// private float cubeLocX, cubeLocY, cubeLocZ;

	// allocate variables for rocket ship
	private float rocketLocX, rocketLocY, rocketLocZ;
	private int numRocketVertices;
	private ImportedModel rocket;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f(); // perspective matrix
	private Matrix4f vMat = new Matrix4f(); // view matrix
	private Matrix4f mMat = new Matrix4f(); // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private int mvLoc, pLoc;
	private float aspect;

	public Code() {
		setTitle("Chapter 4 - program 3");
		setSize(600, 600);
		GLProfile glp = GLProfile.getMaxProgrammableCore(true);
		GLCapabilities caps = new GLCapabilities(glp);
		myCanvas = new GLCanvas(caps);
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);

		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		vMat.translation(-cameraX, -cameraY, -cameraZ);

		// // draw the cube using buffer #0

		// mMat.translation(cubeLocX, cubeLocY, cubeLocZ);

		// mvMat.identity();
		// mvMat.mul(vMat);
		// mvMat.mul(mMat);

		// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		// gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		// gl.glEnableVertexAttribArray(0);

		// gl.glEnable(GL_DEPTH_TEST);
		// gl.glDepthFunc(GL_LEQUAL);

		// gl.glDrawArrays(GL_TRIANGLES, 0, 36);

		// draw the rocket ship using buffer #0
		mMat.translation(rocketLocX, rocketLocY, rocketLocZ);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numRocketVertices);

	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		// import models
		rocket = new ImportedModel("rship.obj");

		renderingProgram = Utils.createShaderProgram("a3/vertShader.glsl", "a3/fragShader.glsl");
		setupVertices();
		cameraX = 0.0f;
		cameraY = 0.0f;
		cameraZ = 8.0f;
		// cubeLocX = 0.0f; cubeLocY = -2.0f; cubeLocZ = 0.0f;

		// intialize rocket ship
		rocketLocX = 0.0f;
		rocketLocY = 0.0f;
		rocketLocZ = 0.0f;
	}

	private void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		// float[] cubePositions =
		// { -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
		// 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
		// 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
		// 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
		// 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
		// -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
		// -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
		// -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
		// -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
		// 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
		// -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
		// 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
		// };

		// gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		// FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(cubePositions);
		// gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit()*4, cubeBuf, GL_STATIC_DRAW);

		// setting up coords rocket ship
		//

		numRocketVertices = rocket.getNumVertices();

		Vector3f[] rocketVertices = rocket.getVertices();
		Vector3f[] rocketNormals = rocket.getNormals();

		float[] rocketPvalues = new float[numRocketVertices * 3];
		float[] rocketNvalues = new float[numRocketVertices * 3];

		for (int i = 0; i < numRocketVertices; i++) {
			rocketPvalues[i * 3] = (float) (rocketVertices[i].x());
			rocketPvalues[i * 3 + 1] = (float) (rocketVertices[i].y());
			rocketPvalues[i * 3 + 2] = (float) (rocketVertices[i].z());
			rocketNvalues[i * 3] = (float) (rocketNormals[i].x());
			rocketNvalues[i * 3 + 1] = (float) (rocketNormals[i].y());
			rocketNvalues[i * 3 + 2] = (float) (rocketNormals[i].z());
		}

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		// rocket ship vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer rocketPBuf = Buffers.newDirectFloatBuffer(rocketPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketPBuf.limit() * 4, rocketPBuf, GL_STATIC_DRAW);

		// rocket ship normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer rocketNBuf = Buffers.newDirectFloatBuffer(rocketNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketNBuf.limit() * 4, rocketNBuf, GL_STATIC_DRAW);

	}

	public static void main(String[] args) {
		new Code();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}
}