package a3;

import java.nio.*;
import javax.swing.*;
import java.lang.Math;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;

import org.joml.*;

public class Code extends JFrame implements GLEventListener {
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[6];
	private float cameraX, cameraY, cameraZ;
	// private float cubeLocX, cubeLocY, cubeLocZ;

	// allocate variables for animation
	private double tf;
	private double startTime;
	private double elapsedTime;

	// allocate variables for rocket ship
	private float rocketLocX, rocketLocY, rocketLocZ;
	private int numRocketVertices;
	private ImportedModel rocket;

	// allocate variables for alien ship
	private float alienLocX, alienLocY, alienLocZ;
	private int numAlienVertices;
	private ImportedModel alien;

	// allocate variables for moon
	private float moonLocX, moonLocY, moonLocZ;
	private int numMoonVertices;
	private ImportedModel moon;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f(); // perspective matrix
	private Matrix4f vMat = new Matrix4f(); // view matrix
	private Matrix4f mMat = new Matrix4f(); // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private int mvLoc, pLoc;
	private float aspect;

	// create a matrix stack for the scene
	private Matrix4fStack mvStack = new Matrix4fStack(100);

	public Code() {
		setTitle("Chapter 4 - program 3");
		setSize(600, 600);
		GLProfile glp = GLProfile.getMaxProgrammableCore(true);
		GLCapabilities caps = new GLCapabilities(glp);
		myCanvas = new GLCanvas(caps);
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);

		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;

		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(50.0f), aspect, 0.1f, 1000.0f);
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// initialize time variables
		
		tf = elapsedTime % 10.0;

		// push view matrix onto the stack
		mvStack.pushMatrix();
		mvStack.translate(-cameraX, -cameraY, -cameraZ);

		// draw the moon using buffer #0
		//
		mvStack.pushMatrix();
		mvStack.translate(moonLocX, moonLocY, moonLocZ);

		//scale the moon to be 3x bigger
		mvStack.pushMatrix();
		mvStack.scale(3.0f, 3.0f, 3.0f);

		// rotate the moon
		mvStack.pushMatrix();
		mvStack.rotate((float)tf, 0.0f, 1.0f, 0.0f);
		

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_DEPTH_TEST);


		gl.glDrawArrays(GL_TRIANGLES, 0, numMoonVertices);

		// pop the stack
		mvStack.popMatrix();
		mvStack.popMatrix();

		// draw the rocket ship using buffer #2
		//
		// rocket orbits the sun
		mvStack.pushMatrix();
		mvStack.translate((float) Math.sin(tf)*6.0f, 0.0f, (float) Math.cos(tf)*6.0f);

		//scale the rocket to me 1/10th the size of the moon
		mvStack.pushMatrix();
		mvStack.scale(0.1f, 0.1f, 0.1f);

		// rotate the rocket in the same way it is moving
		mvStack.pushMatrix();
		mvStack.rotate(87.0f, (float)Math.sin(tf)*4.0f, 1.0f, (float)Math.cos(tf)*4.0f);


		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glDrawArrays(GL_TRIANGLES, 0, numRocketVertices);

		//pop the stack 
		mvStack.popMatrix();
		mvStack.popMatrix();


		// draw the alien ship using buffer #4
		mvStack.pushMatrix();
		mvStack.translate((float) Math.sin(tf*3.0f)*3.0f, (float) Math.cos(tf)*1.0f, 3.0f);

		// rotate the alien ship
		mvStack.pushMatrix();
		mvStack.rotate((float)tf, 0.0f, 1.0f, 0.0f);

		//scale the alien ship to be 1/10th the size of the moon
		mvStack.pushMatrix();
		mvStack.scale(0.1f, 0.1f, 0.1f);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glDrawArrays(GL_TRIANGLES, 0, numAlienVertices);

		// pop the stack
		mvStack.popMatrix();
		mvStack.popMatrix();
		mvStack.popMatrix();
		mvStack.popMatrix();
		mvStack.popMatrix();
		mvStack.popMatrix();

		


	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		// import models
		rocket = new ImportedModel("rShip.obj");
		alien = new ImportedModel("aShip.obj");
		moon = new ImportedModel("moon.obj");

		renderingProgram = Utils.createShaderProgram("a3/vertShader.glsl", "a3/fragShader.glsl");
		setupVertices();
		cameraX = 0.0f;
		cameraY = 0.0f;
		cameraZ = 20.0f;
		// cubeLocX = 0.0f; cubeLocY = -2.0f; cubeLocZ = 0.0f;

		// intialize rocket ship
		rocketLocX = -4.0f;
		rocketLocY = 0.0f;
		rocketLocZ = 0.0f;

		// intialize alien ship
		alienLocX = 5.0f;
		alienLocY = 0.0f;
		alienLocZ = 0.0f;

		// initialize moon location
		moonLocX = 0.0f;
		moonLocY = 0.0f;
		moonLocZ = 0.0f;

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

		// setting up coords alien ship
		//

		numAlienVertices = alien.getNumVertices();

		Vector3f[] alienVertices = alien.getVertices();
		Vector3f[] alienNormals = alien.getNormals();

		float[] alienPvalues = new float[numAlienVertices * 3];
		float[] alienNvalues = new float[numAlienVertices * 3];

		for (int i = 0; i < numAlienVertices; i++) {
			alienPvalues[i * 3] = (float) (alienVertices[i].x());
			alienPvalues[i * 3 + 1] = (float) (alienVertices[i].y());
			alienPvalues[i * 3 + 2] = (float) (alienVertices[i].z());
			alienNvalues[i * 3] = (float) (alienNormals[i].x());
			alienNvalues[i * 3 + 1] = (float) (alienNormals[i].y());
			alienNvalues[i * 3 + 2] = (float) (alienNormals[i].z());
		}

		// setting up coords for moon
		//

		numMoonVertices = moon.getNumVertices();

		Vector3f[] moonVertices = moon.getVertices();
		Vector3f[] moonNormals = moon.getNormals();

		float[] moonPvalues = new float[numMoonVertices * 3];
		float[] moonNvalues = new float[numMoonVertices * 3];

		for (int i = 0; i < numMoonVertices; i++) {
			moonPvalues[i * 3] = (float) (moonVertices[i].x());
			moonPvalues[i * 3 + 1] = (float) (moonVertices[i].y());
			moonPvalues[i * 3 + 2] = (float) (moonVertices[i].z());
			moonNvalues[i * 3] = (float) (moonNormals[i].x());
			moonNvalues[i * 3 + 1] = (float) (moonNormals[i].y());
			moonNvalues[i * 3 + 2] = (float) (moonNormals[i].z());
		}

		// buffer setup
		//
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		// moon vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer moonBuf = Buffers.newDirectFloatBuffer(moonPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, moonBuf.limit() * 4, moonBuf, GL_STATIC_DRAW);

		// moon normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer moonNBuf = Buffers.newDirectFloatBuffer(moonNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, moonNBuf.limit() * 4, moonNBuf, GL_STATIC_DRAW);

		// rocket vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer rocketBuf = Buffers.newDirectFloatBuffer(rocketPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketBuf.limit() * 4, rocketBuf, GL_STATIC_DRAW);

		// rocket normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer rocketNBuf = Buffers.newDirectFloatBuffer(rocketNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketNBuf.limit() * 4, rocketNBuf, GL_STATIC_DRAW);

		// alien vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer alienBuf = Buffers.newDirectFloatBuffer(alienPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, alienBuf.limit() * 4, alienBuf, GL_STATIC_DRAW);

		// alien normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer alienNBuf = Buffers.newDirectFloatBuffer(alienNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, alienNBuf.limit() * 4, alienNBuf, GL_STATIC_DRAW);

	}

	public static void main(String[] args) {
		new Code();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}
}