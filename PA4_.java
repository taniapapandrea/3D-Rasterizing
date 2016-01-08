//****************************************************************************
//****************************************************************************
// Description: 
//   
//   This is an extension of the original sketching tool.  
//
//     The following keys control the program:
//
	//      Q,q: quit 
	//		S,s: toggle specular light
	//		D,d: toggle diffuse light
	//		A,a: toggle ambient light
	//		F,f: turn on flat shader
	//		P,p: turn on Phong shader
	//		>:	 increase the step number for examples
	//		<:   decrease the step number for examples
	//     +,-:  increase or decrease spectral exponent
	//		`,1,2,3,4,5: toggle on/off various lights
	//		x,c,v: individually scale sphere, ellipsoid or torus (respectively)
	//		X,C,V: individually rotate sphere, ellipsoid or torus (respectively)
	//		b,n,m: individually translate sphere, ellipsoid or torus (respectively)
	//		B,N,M: individually toggle through materials for sphere, ellipsoid or torus (respectively)
	//		;: globally translate up
	//		.: globally translate down
	//		,: globally translate left
	//		/: globally translate right
//
//****************************************************************************
// History :
//   Aug 2004 Created by Jianming Zhang based on the C
//   code by Stan Sclaroff
//  Nov 2014 modified to include test cases for shading example for PA4
//  Dec 2015 modified by Tania Papandrea to include ellipsoid mesh, depth buffer,
//  materials, light capabilities and Phong shader
//	Jan 2016 modified by Tania Papandrea to remove cylinder and cube


import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*; 
import java.awt.image.*;
import java.nio.ByteBuffer;
import java.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl


public class PA4_ extends JFrame
	implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	
	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH=512;
	private final int DEFAULT_WINDOW_HEIGHT=512;
	private final float DEFAULT_LINE_WIDTH=1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	private BufferedImage buff;
	public int[][] d_buff;
	@SuppressWarnings("unused")
	private ColorType color;
	
	private ArrayList<Vector3D> lineSegs;
	private ArrayList<Vector3D> triangles;
	private int Nsteps;

	/** The quaternion which controls the rotation of the world. */
    private Quaternion viewing_quaternion = new Quaternion();
    private Vector3D viewing_center = new Vector3D((float)(DEFAULT_WINDOW_WIDTH/2),(float)(DEFAULT_WINDOW_HEIGHT/2),(float)0.0);
    /** The last x and y coordinates of the mouse press. */
    private int last_x = 0, last_y = 0;
    /** Whether the world is being rotated. */
    private boolean rotate_world = false;
    
    //global variables for display information
	private boolean ellipsoid_scaled = false;
	private boolean sphere_scaled = false;
	private boolean torus_scaled = false;
	private boolean ellipsoid_rotated = false;
	private boolean sphere_rotated = false;
	private boolean torus_rotated = false;
	private boolean ellipsoid_translated = false;
	private boolean sphere_translated = false;
	private boolean torus_translated = false;
	private int ellipsoid_mat = 1;
	private int sphere_mat = 1;
	private int torus_mat = 1;
	private int global__x=0;
	private int global__y=0;
	private int buff_x;
	private int buff_y;
	private int renderingModel=0;
	private int ns=5;
	private InfiniteLight[] lights;
	private boolean light1_=false;
	private boolean light2_=false;
	private boolean light3_=false;
	private boolean light4_=false;
	private boolean light5_=false;
	private boolean light0_=true;
	private boolean ambient=true;
	private boolean diffuse=true;
	private boolean specular=true;

	
	public PA4_()
	{
	    capabilities = new GLCapabilities(null);
	    capabilities.setDoubleBuffered(true);  // Enable Double buffering

	    canvas  = new GLCanvas(capabilities);
	    canvas.addGLEventListener(this);
	    canvas.addMouseListener(this);
	    canvas.addMouseMotionListener(this);
	    canvas.addKeyListener(this);
	    canvas.setAutoSwapBufferMode(true); // true by default. Just to be explicit
	    canvas.setFocusable(true);
	    getContentPane().add(canvas);

	    animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60 FPS

	    Nsteps = 12;

	    setTitle("CS480/680 Lab 11");
	    setSize( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setResizable(false);
	    
	    color = new ColorType(1.0f,0.0f,0.0f);
	    lineSegs = new ArrayList<Vector3D>();
	    triangles = new ArrayList<Vector3D>();
	}

	public void run()
	{
		animator.start();
	}

	public static void main( String[] args )
	{
	    PA4_ P = new PA4_();
	    P.run();
	}

	//*********************************************** 
	//  GLEventListener Interfaces
	//*********************************************** 
	public void init( GLAutoDrawable drawable) 
	{
	    GL gl = drawable.getGL();
	    gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
	    gl.glLineWidth( DEFAULT_LINE_WIDTH );
	    Dimension sz = this.getContentPane().getSize();
	    buff = new BufferedImage(sz.width,sz.height,BufferedImage.TYPE_3BYTE_BGR);
	    buff_x = sz.width;
	    buff_y = sz.height;
	    clearPixelBuffer();
	}

	// Redisplaying graphics
	public void display(GLAutoDrawable drawable)
	{
	    GL2 gl = drawable.getGL().getGL2();
	    WritableRaster wr = buff.getRaster();
	    DataBufferByte dbb = (DataBufferByte) wr.getDataBuffer();
	    byte[] data = dbb.getData();

	    gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
	    gl.glDrawPixels (buff.getWidth(), buff.getHeight(),
                GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(data));
        drawTestCase();
	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		// deliberately left blank
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
	      boolean deviceChanged)
	{
		// deliberately left blank
	}
	
	void clearPixelBuffer()
	{
		lineSegs.clear();
    	triangles.clear();
		Graphics2D g = buff.createGraphics();
	    g.setColor(Color.BLACK);
	    g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
	    g.dispose();
	}
	
	// drawTest
	void drawTestCase()
	{  
		clearPixelBuffer();
		shadeTest();
	}


	//*********************************************** 
	//          KeyListener Interfaces
	//*********************************************** 
	public void keyTyped(KeyEvent key)
	{
	//      Q,q: quit 
	//		S,s: toggle specular light
	//		D,d: toggle diffuse light
	//		A,a: toggle ambient light
	//		F,f: turn on flat shader
	//		P,p: turn on Phong shader
	//		>:	 increase the step number for examples
	//		<:   decrease the step number for examples
	//     +,-:  increase or decrease spectral exponent
	//		`,1,2,3,4,5: toggle on/off various lights
	//		x,c,v: individually scale sphere, ellipsoid or torus (respectively)
	//		X,C,V: individually rotate sphere, ellipsoid or torus (respectively)
	//		b,n,m: individually translate sphere, ellipsoid or torus (respectively)
	//		B,N,M: individually toggle through materials for sphere, ellipsoid or torus (respectively)
	//		;: globally translate up
	//		.: globally translate down
	//		,: globally translate left
	//		/: globally translate right

	    switch ( key.getKeyChar() ) 
	    {
	    //lighting components
	    case 'S':
	    case 's':
	    	specular = !specular;
	    	break;
	    case 'd':
	    case 'D':
	    	diffuse=!diffuse;
	    	break;
	    case 'a':
	    case 'A':
	    	ambient=!ambient;
	    	break;
	    case 'Q' :
	    case 'q' : 
	    	new Thread()
	    	{
	          	public void run() { animator.stop(); }
	        }.start();
	        System.exit(0);
	        break;
	        
	    //rendering type
	    case 'f':
	    case 'F':
	    	renderingModel=0;
	    	break;
	    case 'p':
	    case 'P':
	    	renderingModel=1;
	    	break;
	    	
	    //lights    
	    case '1':
	    	light1_=!light1_;
	    	break;
	    case '2':
	    	light2_=!light2_;
	    	break;
	    case '3':
	    	light3_=!light3_;
	    	break;
	    case '4':
	    	light4_=!light4_;
	    	break;
	    case '5':
	    	light5_=!light5_;
	    	break;
	    case '`':
	    	light0_=!light0_;
	    	break;
	    	
	    // triangle count
	    case '<':  
	        Nsteps = Nsteps < 4 ? Nsteps: Nsteps / 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '>':
	        Nsteps = Nsteps > 190 ? Nsteps: Nsteps * 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	        
	    //specular exponent
	    case '+':
	    	ns++;
	        drawTestCase();
	    	break;
	    case '-':
	    	if(ns>0)
	    		ns--;
	        drawTestCase();
	    	break;
	    	
	    //INDIVIDUAL SCALES
	    case 'x':
	    	sphere_scaled = !sphere_scaled;
	    	break;
	    case 'c':
	    	ellipsoid_scaled = !ellipsoid_scaled;
	    	break;
	    case 'v':
	    	torus_scaled = !torus_scaled;
	    	break;
	    	
	    //INDIVIDUAL ROTATIONS
	    case 'X':
	    	sphere_rotated = !sphere_rotated;
	    	break;
	    case 'C':
	    	ellipsoid_rotated = !ellipsoid_rotated;
	    	break;
	    case 'V':
	    	torus_rotated = !torus_rotated;
	    	break;
	    	
	    //INDIVIDUAL TRANSLATIONS
	    case 'b':
	    	sphere_translated = !sphere_translated;
	    	break;
	    case 'n':
	    	ellipsoid_translated = !ellipsoid_translated;
	    	break;
	    case 'm':
	    	torus_translated = !torus_translated;
	    	break;
	    	
	    //TOGGLING MATERIALS
	    case 'B':
	    	sphere_mat++;
	    	if (sphere_mat>2)
	    		sphere_mat=0;
	    	break;
	    case 'N':
	    	ellipsoid_mat++;
	    	if (ellipsoid_mat>2)
	    		ellipsoid_mat=0;
	    	break;
	    case 'M':
	    	torus_mat++;
	    	if (torus_mat>2)
	    		torus_mat=0;
	    	break;
	    	
	    //GLOBAL TRANSLATION
	    case ',':
	    	global__x-=10;
	    	break;
	    case '.':
	    	global__y+=10;
	    	break;
	    case ';':
	    	global__y-=10;
	    	break;
	    case '/':
	    	global__x+=10;
	    	break;
	    default :
	        break;
	    }
	}

	public void keyPressed(KeyEvent key)
	{
	    switch (key.getKeyCode()) 
	    {
	    case KeyEvent.VK_ESCAPE:
	    	new Thread()
	        {
	    		public void run()
	    		{
	    			animator.stop();
	    		}
	        }.start();
	        System.exit(0);
	        break;
	      default:
	        break;
	    }
	}

	public void keyReleased(KeyEvent key)
	{
		// deliberately left blank
	}

	//************************************************** 
	// MouseListener and MouseMotionListener Interfaces
	//************************************************** 
	public void mouseClicked(MouseEvent mouse)
	{
		// deliberately left blank
	}
	  public void mousePressed(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      last_x = mouse.getX();
	      last_y = mouse.getY();
	      rotate_world = true;
	    }
	  }

	  public void mouseReleased(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      rotate_world = false;
	    }
	  }

	public void mouseMoved( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	/**
	   * Updates the rotation quaternion as the mouse is dragged.
	   * 
	   * @param mouse
	   *          The mouse drag event object.
	   */
	  public void mouseDragged(final MouseEvent mouse) {
	    if (this.rotate_world) {
	      // get the current position of the mouse
	      final int x = mouse.getX();
	      final int y = mouse.getY();

	      // get the change in position from the previous one
	      final int dx = x - this.last_x;
	      final int dy = y - this.last_y;

	      // create a unit vector in the direction of the vector (dy, dx, 0)
	      final float magnitude = (float)Math.sqrt(dx * dx + dy * dy);
	      if(magnitude > 0.0001)
	      {
	    	  // define axis perpendicular to (dx,-dy,0)
	    	  // use -y because origin is in upper lefthand corner of the window
	    	  final float[] axis = new float[] { -(float) (dy / magnitude),
	    			  (float) (dx / magnitude), 0 };

	    	  // calculate appropriate quaternion
	    	  final float viewing_delta = 3.1415927f / 180.0f;
	    	  final float s = (float) Math.sin(0.5f * viewing_delta);
	    	  final float c = (float) Math.cos(0.5f * viewing_delta);
	    	  final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s
	    			  * axis[2]);
	    	  this.viewing_quaternion = Q.multiply(this.viewing_quaternion);

	    	  // normalize to counteract acccumulating round-off error
	    	  this.viewing_quaternion.normalize();

	    	  // save x, y as last x, y
	    	  this.last_x = x;
	    	  this.last_y = y;
	          drawTestCase();
	      }
	    }

	  }
	  
	public void mouseEntered( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	public void mouseExited( MouseEvent mouse)
	{
		// Deliberately left blank
	} 


	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}
	
	//************************************************** 
	// DRAWING THE SHAPES
	//************************************************** 

	void shadeTest(){
		boolean phong;
		if (renderingModel==0){
			phong=false;
		} else{
			phong=true;
		}
		float sphere_radius, ellipsoid_radius, torus_radius;
		
		//scale if appropriate
		if (sphere_scaled)
			sphere_radius=(float)80.0;
		else 
			sphere_radius=(float)40.0;
		if (ellipsoid_scaled)
			ellipsoid_radius=(float)40.0;
		else 
			ellipsoid_radius=(float)20.0;
		if (torus_scaled)
			torus_radius=(float)40.0;
		else 
			torus_radius=(float)30.0;
		
		// create all shapes
		Sphere3D sphere = new Sphere3D((float)(400.0), (float)(128.0), (float)(100.0), sphere_radius, Nsteps, Nsteps);
		Ellipsoid ellipsoid = new Ellipsoid((float)(128.0), (float)(200.0), (float)(128.0), (float)3*ellipsoid_radius, (float)1*ellipsoid_radius, ellipsoid_radius, Nsteps, Nsteps);
        Torus3D torus = new Torus3D((float)(250.0), (float)(384.0), (float)(128.0), (float).8*torus_radius, (float)1.25*torus_radius, Nsteps, Nsteps);
        
        //translate if appropriate
        if (torus_translated)
        	torus.set_center(169, 450, 170);
        if (ellipsoid_translated)
        	ellipsoid.set_center(95, 60, 60);
        if (sphere_translated)
        	sphere.set_center(460, 100, 75);
        
        //rotate if appropriate
        if (torus_rotated){
        	Quaternion torus_= new Quaternion((float).3, (float)0, (float)1, (float)0);
        	torus_.normalize();
        	torus.mesh.rotateMesh(torus_, torus.get_center());
        }
        if (ellipsoid_rotated)
        	ellipsoid.mesh.rotateMesh(new Quaternion((float).3, (float)0, (float)1, (float)0), ellipsoid.get_center());
        if (sphere_rotated)
        	sphere.mesh.rotateMesh(new Quaternion((float).3, (float)0, (float)1, (float)0), sphere.get_center());
        
        Vector3D view_vector = new Vector3D((float)0.0,(float)1.0,(float)1.0);
      
        // material properties for the sphere and torus
        // ambient, diffuse, and specular coefficients
        // specular exponent is a global variable
        ColorType blue_kd = new ColorType(0.0f,0.5f,0.9f);
        ColorType blue_ka = new ColorType(0.0f,0.0f,0.0f);
        ColorType blue_ks = new ColorType(1.0f,1.0f,1.0f);
        int blue_ns = 90;
        Material red = new Material(blue_ka, blue_kd, blue_ks, blue_ns);
        ColorType red_kd = new ColorType(0.9f,0.3f,0.1f);
        ColorType red_ka = new ColorType(0.0f,0.0f,0.0f);
        ColorType red_ks = new ColorType(1.0f,1.0f,1.0f);
        int red_ns = 20;
        Material blue = new Material(red_ka, red_kd, red_ks, red_ns);
        ColorType green_kd = new ColorType(0.1f,0.9f,0.3f);
        ColorType green_ka = new ColorType(0.0f,0.0f,0.0f);
        ColorType green_ks = new ColorType(1.0f,1.0f,1.0f);
        int green_ns = 1;
        Material green = new Material(green_ka, green_kd, green_ks, green_ns);
        Material[] mats = {blue, red, green};

        //LIGHT DEFINITIONS
        //regular light
    	ColorType light_color = new ColorType(1.0f,1.0f,1.0f);
    	Vector3D light_direction = new Vector3D((float)0.0,(float)(-1.0/Math.sqrt(2.0)),(float)(1.0/Math.sqrt(2.0)));
    	InfiniteLight light0 = new InfiniteLight(light_color,light_direction, ambient, diffuse, specular, 0, new Vector3D((float)0, (float)0, (float)200), false);
        
    	//green point light coming from our direction
    	ColorType light_color1 = new ColorType(0.5f,0.7f,0.0f);
    	Vector3D light_direction1 = new Vector3D((float)0.0,(float)(-1.0/Math.sqrt(2.0)),(float)(1.0/Math.sqrt(2.0)));
    	InfiniteLight light1 = new InfiniteLight(light_color1,light_direction1, ambient, diffuse, specular, 0, new Vector3D((float)555, (float)555, (float)200), true);
    	
    	//pink point light coming from behind
    	ColorType light_color2 = new ColorType(0.7f,0.1f,0.7f);
    	Vector3D light_direction2 = new Vector3D((float)0.0,(float)(-1.0/Math.sqrt(2.0)),(float)(1.0/Math.sqrt(2.0)));
    	InfiniteLight light2 = new InfiniteLight(light_color2,light_direction2, ambient, diffuse, specular, 0, new Vector3D((float)370, (float)330, (float)0), true);
    	
    	//blue point light coming from top left
    	ColorType light_color3 = new ColorType(0.3f,0.4f,0.8f);
    	Vector3D light_direction3 = new Vector3D((float)0.0,(float)(-1.0/Math.sqrt(2.0)),(float)(1.0/Math.sqrt(2.0)));
    	InfiniteLight light3 = new InfiniteLight(light_color3,light_direction3, ambient, diffuse, specular, 0, new Vector3D((float)250, (float)250, (float)250), true);
    	
    	ColorType light_color4 = new ColorType(1.0f,1.0f,1.0f);
    	Vector3D light_direction4 = new Vector3D((float)0.0,(float)(-1.0/Math.sqrt(2.0)),(float)(1.0/Math.sqrt(2.0)));
    	InfiniteLight light4 = new InfiniteLight(light_color4,light_direction4, ambient, diffuse, specular, 0, new Vector3D((float)0, (float)0, (float)200), true);
    	
        //LIGHT SETTINGS
		lights = new InfiniteLight[] {};
		if (light0_){
			lights = Arrays.copyOf(lights, lights.length+1);
			lights[lights.length-1]=light0;
		}
		if (light1_){
			lights = Arrays.copyOf(lights, lights.length+1);
			lights[lights.length-1]=light1;
		}
		if (light2_){
			lights = Arrays.copyOf(lights, lights.length+1);
			lights[lights.length-1]=light2;
		}
		if (light3_){
			lights = Arrays.copyOf(lights, lights.length+1);
			lights[lights.length-1]=light3;
		}
		if (light4_){
			lights = Arrays.copyOf(lights, lights.length+1);
			lights[lights.length-1]=light4;
		}
        
        // normal to the plane of a triangle
        // to be used in backface culling / backface rejection
        Vector3D triangle_normal = new Vector3D();
        
        // a triangle mesh
        Mesh3D mesh;
            
		int i, j, n, m, material;
		
		// temporary variables for triangle 3D vertices and 3D normals
		Vector3D v0,v1, v2, n0, n1, n2;
		
		// projected triangle, with vertex colors
		Vector3D[] tri = {new Vector3D(), new Vector3D(), new Vector3D()};
		
	    d_buff = new int[buff_x][buff_y];
	    //fill d_buff with 999s
	    for (int k=0; k<buff_x; k++){
	    	for (int l=0; l<buff_y; l++){
	    		d_buff[k][l]=0;
	    	}
	    }
		
		for(int k=0;k<3;++k)
		{
			if(k==0){
				mesh=ellipsoid.mesh;
				n=ellipsoid.get_n();
				m=ellipsoid.get_m();
				material=ellipsoid_mat;
			} else if (k==1){
				mesh=torus.mesh;
				n=torus.get_n();
				m=torus.get_m();
				material=torus_mat;
			} else {
				mesh=sphere.mesh;
				n=sphere.get_n();
				m=sphere.get_m();	
				material=sphere_mat;
			}
			
			// rotate the surface's 3D mesh using quaternion
			mesh.rotateMesh(viewing_quaternion, viewing_center);
					
			// draw triangles for the current surface, using vertex colors
			// this works for Gouraud and flat shading only (not Phong)
			if (phong==false){
				//flat shading
				for(i=0; i < m-1; ++i)
			    {
					for(j=0; j < n-1; ++j)
					{
						v0 = mesh.v[i][j];
						v1 = mesh.v[i][j+1];
						v2 = mesh.v[i+1][j+1];
						triangle_normal = computeTriangleNormal(v0,v1,v2);
						
						if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
						{	
							// flat shading: use the normal to the triangle itself
							n2 = n1 = n0 =  triangle_normal;
							
							for (int l=0; l<lights.length; l++){
								tri[0].c = lights[l].applyLight(mats[material], view_vector, triangle_normal, tri[0].c, v0);
								tri[1].c = lights[l].applyLight(mats[material], view_vector, triangle_normal, tri[1].c, v1);
								tri[2].c = lights[l].applyLight(mats[material], view_vector, triangle_normal, tri[2].c, v2);
							}

							tri[0].x = (int)v0.x+global__x;
							tri[0].y = (int)v0.y+global__y;
							tri[0].z = (int)v0.z;
							tri[1].x = (int)v1.x+global__x;
							tri[1].y = (int)v1.y+global__y;
							tri[1].z = (int)v0.z;
							tri[2].x = (int)v2.x+global__x;
							tri[2].y = (int)v2.y+global__y;
							tri[2].z = (int)v2.z;
		
							SketchBase.drawTriangle(buff,d_buff,tri[0],tri[1],tri[2],false);      
						}
						
						v0 = mesh.v[i][j];
						v1 = mesh.v[i+1][j+1];
						v2 = mesh.v[i+1][j];
						triangle_normal = computeTriangleNormal(v0,v1,v2);
						
						if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
						{	
							// flat shading: use the normal to the triangle itself
							n2 = n1 = n0 =  triangle_normal;
							for (int l=0; l<lights.length; l++){
								tri[0].c = lights[l].applyLight(mats[material], view_vector, triangle_normal, tri[0].c, v0);
								tri[1].c = lights[l].applyLight(mats[material], view_vector, triangle_normal, tri[1].c, v1);
								tri[2].c = lights[l].applyLight(mats[material], view_vector, triangle_normal, tri[2].c, v2);
							}	
				
							tri[0].x = (int)v0.x+global__x;
							tri[0].y = (int)v0.y+global__y;
							tri[0].z = (int)v0.z;
							tri[1].x = (int)v1.x+global__x;
							tri[1].y = (int)v1.y+global__y;
							tri[1].z = (int)v0.z;
							tri[2].x = (int)v2.x+global__x;
							tri[2].y = (int)v2.y+global__y;
							tri[2].z = (int)v2.z;
							
							SketchBase.drawTriangle(buff,d_buff,tri[0],tri[1],tri[2],false);      
						}
					}	
			    }
			} else {
				//Phong shading
				PhongVector3D[] tri_p = {new PhongVector3D(), new PhongVector3D(), new PhongVector3D()};
				for(i=0; i < m-1; ++i)
			    {
					for(j=0; j < n-1; ++j)
					{
						v0 = mesh.v[i][j];
						v1 = mesh.v[i][j+1];
						v2 = mesh.v[i+1][j+1];
						triangle_normal = computeTriangleNormal(v0,v1,v2);
						
						if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
						{	
							// vertex normals
							n0 = mesh.n[i][j];
							n1 = mesh.n[i][j+1];
							n2 = mesh.n[i+1][j+1];
							
							//set vertex coordinates
							tri_p[0].x = (int)v0.x+global__x;
							tri_p[0].y = (int)v0.y+global__y;
							tri_p[0].z = (int)v0.z;
							tri_p[1].x = (int)v1.x+global__x;
							tri_p[1].y = (int)v1.y+global__y;
							tri_p[1].z = (int)v0.z;
							tri_p[2].x = (int)v2.x+global__x;
							tri_p[2].y = (int)v2.y+global__y;
							tri_p[2].z = (int)v2.z;
							
							//set nx, ny and nz values to be the normal coordinates
							tri_p[0].nx = n0.x;
							tri_p[0].ny = n0.y;
							tri_p[0].nz = n0.z;
							tri_p[1].nx = n1.x;
							tri_p[1].ny = n1.y;
							tri_p[1].nz = n1.z;
							tri_p[2].nx = n2.x;
							tri_p[2].ny = n2.y;
							tri_p[2].nz = n2.z;
		
							for (int l=0; l<lights.length; l++){
								PhongSketchBase.drawTriangle(buff,d_buff,tri_p[0],tri_p[1],tri_p[2], lights[l], mats[material], view_vector);      
						
							}
						}
						
						v0 = mesh.v[i][j];
						v1 = mesh.v[i+1][j+1];
						v2 = mesh.v[i+1][j];
						triangle_normal = computeTriangleNormal(v0,v1,v2);
						
						if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
						{	
							// vertex normals
							n0 = mesh.n[i][j];
							n1 = mesh.n[i+1][j+1];
							n2 = mesh.n[i+1][j];
							
							tri_p[0].x = (int)v0.x+global__x;
							tri_p[0].y = (int)v0.y+global__y;
							tri_p[0].z = (int)v0.z;
							tri_p[1].x = (int)v1.x+global__x;
							tri_p[1].y = (int)v1.y+global__y;
							tri_p[1].z = (int)v0.z;
							tri_p[2].x = (int)v2.x+global__x;
							tri_p[2].y = (int)v2.y+global__y;
							tri_p[2].z = (int)v2.z;
							
							//set nx, ny and nz values to be the normal coordinates
							tri_p[0].nx = n0.x;
							tri_p[0].ny = n0.y;
							tri_p[0].nz = n0.z;
							tri_p[1].nx = n1.x;
							tri_p[1].ny = n1.y;
							tri_p[1].nz = n1.z;
							tri_p[2].nx = n2.x;
							tri_p[2].ny = n2.y;
							tri_p[2].nz = n2.z;
							for (int l=0; l<lights.length; l++){
								PhongSketchBase.drawTriangle(buff,d_buff,tri_p[0],tri_p[1],tri_p[2], lights[l], mats[material], view_vector);
							}							
						}
					}	
			    }
			}
		}
	}

	// helper method that computes the unit normal to the plane of the triangle
	// degenerate triangles yield normal that is numerically zero
	private Vector3D computeTriangleNormal(Vector3D v0, Vector3D v1, Vector3D v2)
	{
		Vector3D e0 = v1.minus(v2);
		Vector3D e1 = v0.minus(v2);
		Vector3D norm = e0.crossProduct(e1);
		
		if(norm.magnitude()>0.000001)
			norm.normalize();
		else 	// detect degenerate triangle and set its normal to zero
			norm.set((float)0.0,(float)0.0,(float)0.0);

		return norm;
	}

}