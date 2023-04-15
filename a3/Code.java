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
	private int vbo[] = new int[9];
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
	private int rocketTexture;

	// allocate variables for alien ship
	private float alienLocX, alienLocY, alienLocZ;
	private int numAlienVertices;
	private ImportedModel alien;
	private int alienTexture;

	// allocate variables for moon
	private float moonLocX, moonLocY, moonLocZ;
	private int numMoonVertices;
	private ImportedModel moon;
	private int moonTexture;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f(); // perspective matrix
	private Matrix4f vMat = new Matrix4f(); // view matrix
	private Matrix4f mMat = new Matrix4f(); // model matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose matrix
	private int mLoc, pLoc, nLoc, vLoc;
	private int globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, mdiffLoc, mspecLoc, mshiLoc, mambLoc;
	private float aspect;

	// lighting
	private Vector3f intialLightLoc = new Vector3f(5.0f, 2.0f, 2.0f);
	private Vector3f currentLightPos = new Vector3f();
	private float[] lightPos = new float[3];

	// white light properties
	float[] globalAmbient = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float[] lightAmbient = new float[] { 0.1f, 0.1f, 0.1f, 1.0f };
	float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] lightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	// gold material properties
	float[] matAmb = Utils.moonAmbient();
	float[] matDif = Utils.moonDiffuse();
	float[] matSpe = Utils.moonSpecular();
	float matShi = Utils.moonShininess();

	// angle
	private float angle = 0.1f;

	// create a matrix stack for the scene
	private Matrix4fStack mStack = new Matrix4fStack(100);

	public Code() {
		setTitle("Chapter 4 - program 3");
		setSize(600, 600);
		GLProfile glp = GLProfile.getMaxProgrammable(true);
		GLCapabilities caps = new GLCapabilities(glp);
		myCanvas = new GLCanvas(caps);
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);

		// key listeners
		//

		// key listenr w to move forward
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_W) {
					cameraZ = cameraZ - 0.3f;
					
				}
			}
		});

		// key listener s to move backward
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_S) {
					cameraZ = cameraZ + 0.3f;
					
				}
			}
		});

		// key listener a to move left
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) {
					cameraX = cameraX - 0.3f;
				}
			}
		});

		// key listener d to move right
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_D) {
					cameraX = cameraX + 0.3f;
				}
			}
		});

		// key listener e to move up
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_E) {
					cameraY = cameraY + 0.3f;
				}
			}
		});

		// key listener q to move down
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_Q) {
					cameraY = cameraY - 0.3f;
				}
			}
		});

		// key listener up to rotate camera up
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
					vMat.rotateX((float) Math.toRadians(-2.0f));

				}
			}
		});

		// key listener down to rotate camera down
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
					vMat.rotateX((float) Math.toRadians(2.0f));

				}
			}
		});

		// key listener left to rotate camera left
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
					vMat.rotateY((float) Math.toRadians(-2.0f));

				}
			}
		});

		// key listener right to rotate camera right
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
					vMat.rotateY((float) Math.toRadians(2.0f));

				}
			}
		});

		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(renderingProgram);

		elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;

		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");

		vMat.translation(-cameraX, -cameraY, -cameraZ);

		// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// initialize time variables

		tf = elapsedTime % 100;

		// push view matrix onto the stack

		mStack.pushMatrix();
		mStack.translation(-cameraX, -cameraY, -cameraZ);

		

		// draw the moon using buffer #0
		//
		mStack.pushMatrix();
		mStack.translation(moonLocX, moonLocY, moonLocZ);

		// scale the moon to be 3x bigger
		mStack.pushMatrix();
		mStack.scale(3.0f, 3.0f, 3.0f);

		// rotate the moon
		mStack.pushMatrix();
		mStack.rotate((float) tf, 0.0f, 1.0f, 0.0f);

		// install lights
		currentLightPos.set(intialLightLoc);
		installLights();

		mStack.invert(invTrMat);
		invTrMat.transpose(invTrMat);


		//change the material type
		materialType(3);

		gl.glUniformMatrix4fv(mLoc, 1, false, mStack.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));

		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glActiveTexture(GL_TEXTURE0);

		gl.glBindTexture(GL_TEXTURE_2D, moonTexture);
		gl.glUniform1i(gl.glGetUniformLocation(renderingProgram, "texture0"), 0);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numMoonVertices);

		// pop the stack
		mStack.popMatrix();
		mStack.popMatrix();
		

		// draw the rocket ship using buffer #2
		//
		// rocket orbits the sun
		mStack.pushMatrix();
		mStack.translate((float) Math.cos(tf) * 6.0f, 0.0f, (float) Math.sin(tf) *
		6.0f);

		// scale the rocket to me 1/10th the size of the moon
		mStack.pushMatrix();
		mStack.scale(0.05f, 0.05f, 0.05f);

		// rotate the rocket in the same way it is moving
		mStack.pushMatrix();
		mStack.rotate(10.0f, (float) Math.cos(tf) * 4.0f, 0.0f, (float) Math.sin(tf) *-4.0f);

		//change material properties to iron
		matAmb = Utils.ironAmbient();
		matDif = Utils.ironDiffuse();
		matSpe = Utils.ironSpecular();
		matShi = Utils.ironShininess();

		//change the material type to iron
		materialType(1);

		gl.glUniformMatrix4fv(mLoc, 1, false, mStack.get(vals));
		// gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		// gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		// print the normal buffer is bound to

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, rocketTexture);
		gl.glUniform1i(gl.glGetUniformLocation(renderingProgram, "texture0"), 0);

		gl.glDrawArrays(GL_TRIANGLES, 0, numRocketVertices);

		// pop the stack
		mStack.popMatrix();
		mStack.popMatrix();

		// // draw the alien ship using buffer #4
		mStack.pushMatrix();
		mStack.translate((float) Math.sin(tf * 3.0f) * 3.0f, (float) Math.cos(tf) *
		1.0f, 3.0f);

		// rotate the alien ship
		mStack.pushMatrix();
		mStack.rotate((float) tf, 0.0f, 1.0f, 0.0f);

		// scale the alien ship to be 1/10th the size of the moon
		mStack.pushMatrix();
		mStack.scale(0.1f, 0.1f, 0.1f);

		//change material type to alien
		materialType(2);

		gl.glUniformMatrix4fv(mLoc, 1, false, mStack.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, alienTexture);
		gl.glUniform1i(gl.glGetUniformLocation(renderingProgram, "texture0"), 0);

		gl.glDrawArrays(GL_TRIANGLES, 0, numAlienVertices);

		// // pop the stack
		mStack.popMatrix();
		mStack.popMatrix();
		mStack.popMatrix();
		mStack.popMatrix();
		mStack.popMatrix();
		mStack.popMatrix();

		materialType(3);

	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		// import models
		rocket = new ImportedModel("rocketSketchFab.obj");
		alien = new ImportedModel("aShip.obj");
		moon = new ImportedModel("moon.obj");

		renderingProgram = Utils.createShaderProgram("a3/vertShader.glsl", "a3/fragShader.glsl");
		setupVertices();
		cameraX = 0.0f;
		cameraY = 0.0f;
		cameraZ = 25.0f;
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

		// textures
		moonTexture = Utils.loadTexture("moon.jpg");
		rocketTexture = Utils.loadTexture("rShipTex.jpg");
		alienTexture = Utils.loadTexture("aShipTex.jpg");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(50.0f), aspect, 0.1f, 1000.0f);

	}

	private void installLights() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		// save the light position in a float array
		lightPos[0] = currentLightPos.x();
		lightPos[1] = currentLightPos.y();
		lightPos[2] = currentLightPos.z();

		// get the locations of the light and material fields in the shader
		globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
		ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
		diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
		specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
		posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");
		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");

		// get the locations and material fields in the shader
		gl.glProgramUniform4fv(renderingProgram, globalAmbLoc, 1, globalAmbient, 0);
		gl.glProgramUniform3fv(renderingProgram, ambLoc, 1, lightAmbient, 0);
		gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, lightDiffuse, 0);
		gl.glProgramUniform4fv(renderingProgram, specLoc, 1, lightSpecular, 0);
		gl.glProgramUniform3fv(renderingProgram, posLoc, 1, lightPos, 0);
		gl.glProgramUniform4fv(renderingProgram, mambLoc, 1, matAmb, 0);
		gl.glProgramUniform4fv(renderingProgram, mdiffLoc, 1, matDif, 0);
		gl.glProgramUniform4fv(renderingProgram, mspecLoc, 1, matSpe, 0);
		gl.glProgramUniform1f(renderingProgram, mshiLoc, matShi);
	}

	private void setupVertices() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		numRocketVertices = rocket.getNumVertices();

		Vector3f[] rocketVertices = rocket.getVertices();
		Vector2f[] rocketTexCoords = rocket.getTexCoords();
		Vector3f[] rocketNormals = rocket.getNormals();

		float[] rocketPvalues = new float[numRocketVertices * 3];
		float[] rocketTvalues = new float[numRocketVertices * 2];
		float[] rocketNvalues = new float[numRocketVertices * 3];

		for (int i = 0; i < numRocketVertices; i++) {
			rocketPvalues[i * 3] = (float) (rocketVertices[i].x());
			rocketPvalues[i * 3 + 1] = (float) (rocketVertices[i].y());
			rocketPvalues[i * 3 + 2] = (float) (rocketVertices[i].z());
			rocketTvalues[i * 2] = (float) (rocketTexCoords[i].x());
			rocketTvalues[i * 2 + 1] = (float) (rocketTexCoords[i].y());
			rocketNvalues[i * 3] = (float) (rocketNormals[i].x());
			rocketNvalues[i * 3 + 1] = (float) (rocketNormals[i].y());
			rocketNvalues[i * 3 + 2] = (float) (rocketNormals[i].z());
		}

		// setting up coords alien ship
		//

		numAlienVertices = alien.getNumVertices();

		Vector3f[] alienVertices = alien.getVertices();
		Vector2f[] alienTexCoords = alien.getTexCoords();
		Vector3f[] alienNormals = alien.getNormals();

		float[] alienPvalues = new float[numAlienVertices * 3];
		float[] alienTvalues = new float[numAlienVertices * 2];
		float[] alienNvalues = new float[numAlienVertices * 3];

		for (int i = 0; i < numAlienVertices; i++) {
			alienPvalues[i * 3] = (float) (alienVertices[i].x());
			alienPvalues[i * 3 + 1] = (float) (alienVertices[i].y());
			alienPvalues[i * 3 + 2] = (float) (alienVertices[i].z());
			alienTvalues[i * 2] = (float) (alienTexCoords[i].x());
			alienTvalues[i * 2 + 1] = (float) (alienTexCoords[i].y());
			alienNvalues[i * 3] = (float) (alienNormals[i].x());
			alienNvalues[i * 3 + 1] = (float) (alienNormals[i].y());
			alienNvalues[i * 3 + 2] = (float) (alienNormals[i].z());
		}

		// setting up coords for moon
		//

		numMoonVertices = moon.getNumVertices();

		Vector3f[] moonVertices = moon.getVertices();
		Vector2f[] moonTextCoords = moon.getTexCoords();
		Vector3f[] moonNormals = moon.getNormals();

		float[] moonPvalues = new float[numMoonVertices * 3];
		float[] moonTvalues = new float[numMoonVertices * 2];
		float[] moonNvalues = new float[numMoonVertices * 3];

		for (int i = 0; i < numMoonVertices; i++) {
			moonPvalues[i * 3] = (float) (moonVertices[i].x());
			moonPvalues[i * 3 + 1] = (float) (moonVertices[i].y());
			moonPvalues[i * 3 + 2] = (float) (moonVertices[i].z());
			moonTvalues[i * 2] = (float) (moonTextCoords[i].x());
			moonTvalues[i * 2 + 1] = (float) (moonTextCoords[i].y());
			moonNvalues[i * 3] = (float) (moonNormals[i].x());
			moonNvalues[i * 3 + 1] = (float) (moonNormals[i].y());
			moonNvalues[i * 3 + 2] = (float) (moonNormals[i].z());
		}

		// buffer setup
		//
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		// MOON BUFFERS
		//

		// moon vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer moonBuf = Buffers.newDirectFloatBuffer(moonPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, moonBuf.limit() * 4, moonBuf, GL_STATIC_DRAW);

		// moon texture buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer moonTBuf = Buffers.newDirectFloatBuffer(moonTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, moonTBuf.limit() * 4, moonTBuf, GL_STATIC_DRAW);

		// moon normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer moonNBuf = Buffers.newDirectFloatBuffer(moonNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, moonNBuf.limit() * 4, moonNBuf, GL_STATIC_DRAW);

		// ROCKET BUFFERS
		//

		// rocket vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer rocketBuf = Buffers.newDirectFloatBuffer(rocketPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketBuf.limit() * 4, rocketBuf, GL_STATIC_DRAW);

		// rocket texture buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer rocketTBuf = Buffers.newDirectFloatBuffer(rocketTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketTBuf.limit() * 4, rocketTBuf, GL_STATIC_DRAW);

		// rocket normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer rocketNBuf = Buffers.newDirectFloatBuffer(rocketNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketNBuf.limit() * 4, rocketNBuf, GL_STATIC_DRAW);

		// ALIEN BUFFERS
		//

		// alien vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer alienBuf = Buffers.newDirectFloatBuffer(alienPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, alienBuf.limit() * 4, alienBuf, GL_STATIC_DRAW);

		// alien texture buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer alienTBuf = Buffers.newDirectFloatBuffer(alienTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, alienTBuf.limit() * 4, alienTBuf, GL_STATIC_DRAW);

		// alien normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
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

	public void materialType(int x) 
	{
		//swithc statement to change material type
		switch (x) 
		{
		case 1:
			//set material for iron
			matAmb = Utils.ironAmbient();
			matDif = Utils.ironDiffuse();
			matSpe = Utils.ironSpecular();
			matShi = Utils.ironShininess();
			break;
		case 2:
			//set material for aline ship
			matAmb = Utils.emeraldAmbient();
			matDif = Utils.emeraldDiffuse();
			matSpe = Utils.emeraldSpecular();
			matShi = Utils.emeraldShininess();
			break;
		default:
			//set material for moon
			matAmb = Utils.moonAmbient();
			matDif = Utils.moonDiffuse();
			matSpe = Utils.moonSpecular();
			matShi = Utils.moonShininess();
			break;
		}
	}
}