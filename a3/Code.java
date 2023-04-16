package a3;

import java.nio.*;
import javax.swing.*;

import java.awt.Component;
import java.lang.Math;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;

//import object for mouse
import com.jogamp.newt.event.*;

import org.joml.*;

import java.awt.event.*;

public class Code extends JFrame implements GLEventListener, java.awt.event.MouseListener, MouseMotionListener {
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int renderingProgramLight;
	private int vao[] = new int[1];
	private int vbo[] = new int[13];
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
	private Sphere mySphere;
	private int numSphereVerts;
	private float moonLocX, moonLocY, moonLocZ;
	private int numMoonVertices;
	private ImportedModel moon;
	private int moonTexture;

	// allocate variables to light object
	private Sphere myLightSphere;
	private int numLightSphereVerts;
	private float lightLocX, lightLocY, lightLocZ;
	private int lightTexture;

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
	// boolean to keep track if lighting is on or off
	private boolean lightOn = true;

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

	private int renderingProgramCubeMap, skyboxTexture;

	// camera objects

	private Camera camera = new Camera();
	private Vector3f cameraLocation = new Vector3f(camera.getLocation());

	// angle
	private float angle = 0.1f;
	private float amt = 0.0f;

	// mouse variables
	private float mouseX, mouseY;
	private float mouseZ = 10.0f;
	private float oldMouseX, oldMouseY, oldMouseZ;

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
					// move camera forward
					camera.nMovement(1.0f);

				}
			}
		});

		// key listener s to move backward
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_S) {
					// move camera backward
					camera.nMovement(-1.0f);

				}
			}
		});

		// key listener a to move left
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) {
					camera.uMovement(-1.0f);
				}
			}
		});

		// key listener d to move right
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_D) {
					camera.uMovement(1.0f);
				}
			}
		});

		// key listener e to move up
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_E) {
					camera.vMovement(1.0f);
				}
			}
		});

		// key listener q to move down
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_Q) {
					camera.vMovement(-1.0f);
				}
			}
		});

		// key listener up to rotate camera up
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
					camera.pan(0.1f);

				}
			}
		});

		// key listener down to rotate camera down
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
					camera.pan(-0.1f);

				}
			}
		});

		// key listener left to rotate camera left
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
					camera.pitch(0.1f);

				}
			}
		});

		// key listener right to rotate camera right
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
					camera.pitch(-0.1f);

				}
			}
		});

		// event listener that tracks mouse movment
		myCanvas.addMouseMotionListener(new java.awt.event.MouseMotionListener() {
			public void mouseDragged(java.awt.event.MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();

			}

			public void mouseMoved(java.awt.event.MouseEvent e) {
				// mouseX = e.getX();
				// mouseY = e.getY();
			}

		});

		//event listener that tracks mouse scroll
		myCanvas.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
			public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
				//if mouse wheel scrolls up then decrease mouseZ, if scrolls down then increase
				if (e.getWheelRotation() < 0) {
					mouseZ -= 1.0f;
				} else {
					mouseZ += 1.0f;
				}
			}
		});

		// key listerner "l" that turns light on and off
		myCanvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_L) {
					if (lightOn) {
						lightOn = false;
					} else {
						lightOn = true;

					}
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

		vMat = camera.getViewMatrix();

		// draw cube map
		gl.glUseProgram(renderingProgramCubeMap);

		vLoc = gl.glGetUniformLocation(renderingProgramCubeMap, "v_matrix");
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));

		pLoc = gl.glGetUniformLocation(renderingProgramCubeMap, "p_matrix");
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxTexture);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glDisable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		gl.glEnable(GL_DEPTH_TEST);

		gl.glUseProgram(renderingProgram);

		elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;

		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");

		// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// initialize time variables

		tf = elapsedTime % 100;
		amt += (float) elapsedTime * 0.00000000003f;

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
		currentLightPos.set(mouseX, mouseY, mouseZ);


		installLights();

		mStack.invert(invTrMat);
		invTrMat.transpose(invTrMat);

		// change the material type
		materialType(3);

		gl.glUniformMatrix4fv(mLoc, 1, false, mStack.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));

		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glActiveTexture(GL_TEXTURE0);

		gl.glBindTexture(GL_TEXTURE_2D, moonTexture);
		gl.glUniform1i(gl.glGetUniformLocation(renderingProgram, "texture0"), 0);
		// wrap the texture
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);

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
		mStack.rotate(10.0f, (float) Math.cos(tf) * 4.0f, 0.0f, (float) Math.sin(tf) * -4.0f);

		// change material properties to iron
		matAmb = Utils.ironAmbient();
		matDif = Utils.ironDiffuse();
		matSpe = Utils.ironSpecular();
		matShi = Utils.ironShininess();

		// change the material type to iron
		materialType(1);

		gl.glUniformMatrix4fv(mLoc, 1, false, mStack.get(vals));
		// gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		// gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
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

		// change material type to alien
		materialType(2);

		gl.glUniformMatrix4fv(mLoc, 1, false, mStack.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
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

		// prints out mouse coords only when they are updated
		if (mouseX != oldMouseX || mouseY != oldMouseY) {
			// System.out.println("Mouse X: " + mouseX + " Mouse Y: " + mouseY);
			oldMouseX = mouseX;
			oldMouseY = mouseY;
		}

	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		// import models
		rocket = new ImportedModel("rocketSketchFab.obj");
		alien = new ImportedModel("aShip.obj");
		

		renderingProgram = Utils.createShaderProgram("a3/vertShader.glsl", "a3/fragShader.glsl");

		// set up the skybox shader files
		renderingProgramCubeMap = Utils.createShaderProgram("a3/vertCShader.glsl", "a3/fragCShader.glsl");

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
		lightTexture = Utils.loadTexture("gold.jpg");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(50.0f), aspect, 0.1f, 1000.0f);

		skyboxTexture = Utils.loadCubeMap("cubeMap"); // folder containing the skybox feature
		gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

	}

	private void installLights() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		// save the light position in a float array
		lightPos[0] = currentLightPos.x();
		lightPos[1] = currentLightPos.y();
		lightPos[2] = currentLightPos.z();

		if(!lightOn)
		{
			for(int i = 0; i <= 2; i++ )
			{
				lightAmbient[i] = 0.0f;
				lightDiffuse[i] = 0.0f;
				lightSpecular[i] = 0.0f;
			}

		}
		else 
		{
			for(int i = 0; i <= 2; i++ )
			{
				lightAmbient[i] = 0.1f;
				lightDiffuse[i] = 1.0f;
				lightSpecular[i] = 1.0f;
			}
		}

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

		// cube
		float[] cubeVertexPositions = { -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
				-1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
		};

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

		mySphere = new Sphere(24);
		numSphereVerts = mySphere.getIndices().length;

		int[] indices = mySphere.getIndices();
		Vector3f[] sphereVertices = mySphere.getVertices();
		Vector2f[] sphereTexCoords = mySphere.getTexCoords();
		Vector3f[] sphereNormals = mySphere.getNormals();

		float[] spherePvalues = new float[numSphereVerts * 3];
		float[] sphereTvalues = new float[numSphereVerts * 2];
		float[] sphereNvalues = new float[numSphereVerts * 3];

		for (int i = 0; i < numSphereVerts; i++) {
			spherePvalues[i * 3] = (float) (sphereVertices[indices[i]].x());
			spherePvalues[i * 3 + 1] = (float) (sphereVertices[indices[i]].y());
			spherePvalues[i * 3 + 2] = (float) (sphereVertices[indices[i]].z());
			sphereTvalues[i * 2] = (float) (sphereTexCoords[indices[i]].x());
			sphereTvalues[i * 2 + 1] = (float) (sphereTexCoords[indices[i]].y());
			sphereNvalues[i * 3] = (float) (sphereNormals[indices[i]].x());
			sphereNvalues[i * 3 + 1] = (float) (sphereNormals[indices[i]].y());
			sphereNvalues[i * 3 + 2] = (float) (sphereNormals[indices[i]].z());
		}

		// setting up coords for light
		//
		myLightSphere = new Sphere(24);
		numLightSphereVerts = myLightSphere.getIndices().length;

		int[] lightIndices = myLightSphere.getIndices();
		Vector3f[] lightSphereVertices = myLightSphere.getVertices();
		Vector2f[] lightSphereTexCoords = myLightSphere.getTexCoords();

		float[] lightSpherePvalues = new float[numLightSphereVerts * 3];
		float[] lightSphereTvalues = new float[numLightSphereVerts * 2];
		float[] lightSphereNvalues = new float[numLightSphereVerts * 3];

		for (int i = 0; i < numLightSphereVerts; i++) {
			lightSpherePvalues[i * 3] = (float) (lightSphereVertices[lightIndices[i]].x());
			lightSpherePvalues[i * 3 + 1] = (float) (lightSphereVertices[lightIndices[i]].y());
			lightSpherePvalues[i * 3 + 2] = (float) (lightSphereVertices[lightIndices[i]].z());
			lightSphereTvalues[i * 2] = (float) (lightSphereTexCoords[lightIndices[i]].x());
			lightSphereTvalues[i * 2 + 1] = (float) (lightSphereTexCoords[lightIndices[i]].y());
			lightSphereNvalues[i * 3] = (float) (lightSphereVertices[lightIndices[i]].x());
			lightSphereNvalues[i * 3 + 1] = (float) (lightSphereVertices[lightIndices[i]].y());
			lightSphereNvalues[i * 3 + 2] = (float) (lightSphereVertices[lightIndices[i]].z());
		}


		// buffer setup
		//
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		// cube buffer
		//
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer cvertBuf = Buffers.newDirectFloatBuffer(cubeVertexPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, cvertBuf.limit() * 4, cvertBuf, GL_STATIC_DRAW);

		// MOON BUFFERS
		//

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer sphereBuf = Buffers.newDirectFloatBuffer(spherePvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sphereBuf.limit() * 4, sphereBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer sphereTBuf = Buffers.newDirectFloatBuffer(sphereTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sphereTBuf.limit() * 4, sphereTBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer sphereNBuf = Buffers.newDirectFloatBuffer(sphereNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sphereNBuf.limit() * 4, sphereNBuf, GL_STATIC_DRAW);


		// ROCKET BUFFERS
		//

		// rocket vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer rocketBuf = Buffers.newDirectFloatBuffer(rocketPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketBuf.limit() * 4, rocketBuf, GL_STATIC_DRAW);

		// rocket texture buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer rocketTBuf = Buffers.newDirectFloatBuffer(rocketTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketTBuf.limit() * 4, rocketTBuf, GL_STATIC_DRAW);

		// rocket normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer rocketNBuf = Buffers.newDirectFloatBuffer(rocketNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, rocketNBuf.limit() * 4, rocketNBuf, GL_STATIC_DRAW);

		// ALIEN BUFFERS
		//

		// alien vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer alienBuf = Buffers.newDirectFloatBuffer(alienPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, alienBuf.limit() * 4, alienBuf, GL_STATIC_DRAW);

		// alien texture buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		FloatBuffer alienTBuf = Buffers.newDirectFloatBuffer(alienTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, alienTBuf.limit() * 4, alienTBuf, GL_STATIC_DRAW);

		// alien normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		FloatBuffer alienNBuf = Buffers.newDirectFloatBuffer(alienNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, alienNBuf.limit() * 4, alienNBuf, GL_STATIC_DRAW);

		// LIGHT BUFFERS
		//

		// light vertex buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		FloatBuffer lightBuf = Buffers.newDirectFloatBuffer(lightSpherePvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, lightBuf.limit() * 4, lightBuf, GL_STATIC_DRAW);

		// light texture buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		FloatBuffer lightTBuf = Buffers.newDirectFloatBuffer(lightSphereTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, lightTBuf.limit() * 4, lightTBuf, GL_STATIC_DRAW);

		// light normal buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		FloatBuffer lightNBuf = Buffers.newDirectFloatBuffer(lightSphereNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, lightNBuf.limit() * 4, lightNBuf, GL_STATIC_DRAW);

	}

	public static void main(String[] args) {
		new Code();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}

	public void materialType(int x) {
		// swithc statement to change material type
		switch (x) {
			case 1:
				// set material for iron
				matAmb = Utils.ironAmbient();
				matDif = Utils.ironDiffuse();
				matSpe = Utils.ironSpecular();
				matShi = Utils.ironShininess();
				break;
			case 2:
				// set material for aline ship
				matAmb = Utils.emeraldAmbient();
				matDif = Utils.emeraldDiffuse();
				matSpe = Utils.emeraldSpecular();
				matShi = Utils.emeraldShininess();
				break;
			default:
				// set material for moon
				matAmb = Utils.moonAmbient();
				matDif = Utils.moonDiffuse();
				matSpe = Utils.moonSpecular();
				matShi = Utils.moonShininess();
				break;
		}
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
	}

}