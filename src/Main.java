//import java.awt.Dimension;
//import java.awt.Point;
//import java.awt.Toolkit;
//import java.util.List;
//import java.util.Map;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//
//import com.luciad.model.ILcdModel;
//import com.luciad.model.TLcdModelDescriptor;
//import com.luciad.model.TLcdVectorModel;
//import com.luciad.shape.shape2D.ILcd2DEditableBounds;
//import com.luciad.shape.shape2D.TLcdXYBounds;
//import com.luciad.view.lightspeed.ILspAWTView;
//
//public class Main {
//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		
//		new SampleApplicationProxy1(1111111111);
//		
////		try{
////	    	  LuciadBDConnection conection = new LuciadBDConnection("localhost", "cmca_2", "root", "root", "8.0");
////	    	  conection.setId_mision("1");
////	          List<JSONObject> tracks_list = conection.getTracks();
////	          for(int t=0; t < tracks_list.size(); t++ ) 
////	          {
////	        	  float point_x = (float) tracks_list.get(t).get("x_geoposicion");
////	        	  float point_y = (float) tracks_list.get(t).get("y_geoposicion");
////
////	        	  System.out.println("-->"+point_x+","+point_y);
////	          }
////	      }
////	      catch(Exception e) {
////	    	  System.out.println("----- DATA BASE Exception -----");
////	    	  System.out.println(e.getMessage());
////	      }
//		
////		final ILspAWTView view = getView();    
////		final ILcdModel model = new TLcdVectorModel(null, new TLcdModelDescriptor("", "", "Dim layer"));
////		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
////		double width = screenSize.getWidth();
////		double height = screenSize.getHeight();
////		final ILcd2DEditableBounds screenBounds = new TLcdXYBounds(0, 0, width, height);
////		model.addElement(screenBounds, ILcdModel.NO_EVENT);
//
//	}
//
//}

//import javax.swing.JFrame;
//
//import com.jogamp.opengl.GL2;
//import com.jogamp.opengl.GLAutoDrawable;
//import com.jogamp.opengl.GLCapabilities;
//import com.jogamp.opengl.GLEventListener;
//import com.jogamp.opengl.GLProfile;
//import com.jogamp.opengl.awt.GLCanvas;
//import com.jogamp.opengl.glu.GLU;
//import com.jogamp.opengl.util.FPSAnimator;
//
//public class Main implements GLEventListener {
//	
//	private float rtri; // for angle of rotation
//	private GLU glu = new GLU();
//	
//	@Override
//	public void display(GLAutoDrawable drawable) 
//	{
//		final GL2 gl = drawable.getGL().getGL2();
//		
//		//Draw every face of the triangle
//		// Clear The Screen And The Depth Buffer
//		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
//		gl.glLoadIdentity(); // Reset The View
//		gl.glTranslatef(-0.5f, 0.0f, -6.0f); // Move the triangle
//		gl.glRotatef(rtri, rtri, 0.0f, rtri);
//		gl.glBegin(GL2.GL_TRIANGLES);
//		
//		// drawing triangle in all dimensions
//		// FrontBasicFrame
//		gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
//		gl.glVertex3f(1.0f, 2.0f, 0.0f); // Top Of Triangle (Front)
//		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Left Of Triangle (Front)
//		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Right Of Triangle (Front)
//		
//		// Right
//		gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
//		gl.glVertex3f(1.0f, 2.0f, 0.0f); // Top Of Triangle (Right)
//		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Left Of Triangle (Right)
//		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Right Of Triangle (Right)
//		
//		// Left
//		gl.glColor3f(0.0f, 1.0f, 0.0f); // green
//		gl.glVertex3f(1.0f, 2.0f, 0.0f); // Top Of Triangle (Back)
//		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Left Of Triangle (Back)
//		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Right Of Triangle (Back)
//		
//		// left
//		gl.glColor3f(1.0f, 1.0f, 0.0f); // yellow
//		gl.glVertex3f(1.0f, 2.0f, 0.0f); // Top Of Triangle (Left)
//		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Left Of Triangle (Left)
//		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Right Of Triangle (Left)
//		
//		gl.glEnd(); // Done Drawing 3d triangle (Pyramid)
//		gl.glFlush();
//		rtri += 0.2f;
//	}
//	
//	@Override
//	public void dispose(GLAutoDrawable arg0) {
//	// TODO Auto-generated method stub
//	
//	}
//	
//	@Override
//	public void init(GLAutoDrawable arg0) {
//	// TODO Auto-generated method stub
//	
//	}
//	
//	@Override
//	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
//	
//		// TODO Auto-generated method stub
//		final GL2 gl = drawable.getGL().getGL2();
//		if (height <= 1)
//		height = 1;
//		
//		final float h = (float) width / (float) height;
//		gl.glViewport(0, 0, width, height);
//		gl.glMatrixMode(GL2.GL_PROJECTION);
//		gl.glLoadIdentity();
//		
//		glu.gluPerspective(45.0f, h, 1.0, 20.0);
//		gl.glMatrixMode(GL2.GL_MODELVIEW);
//		gl.glLoadIdentity();
//	}
//	
//	public static void main(String[] args) {
//	
//		// getting the capabilities object of GL2 profile
//		final GLProfile profile = GLProfile.get(GLProfile.GL2);
//		GLCapabilities capabilities = new GLCapabilities(profile);
//		
//		// The canvas
//		final GLCanvas glcanvas = new GLCanvas(capabilities);
//		Main b = new Main();
//		glcanvas.addGLEventListener(b);
//		glcanvas.setSize(400, 400);
//		
//		// creating frame
//		final JFrame frame = new JFrame("Ejemplo JOGL");
//		
//		// adding canvas to frame
//		frame.getContentPane().add(glcanvas);
//		frame.setSize(frame.getContentPane().getPreferredSize());
//		frame.setVisible(true);
//		
//		// Instantiating and Initiating Animator
//		final FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);
//		animator.start();
//	}
//
//}


//class Main implements ILcdGLPainter {
//  private TLcdGLPaintablePainter2 fEllipsePainter;
//  private TLcdGLPaintablePainter2 fPolygonPainter;
//
//
//  public MultiPainter() {
//    TLcdGLExtrudedEllipsePaintableFactory ellipsePaintableFactory =
//            new TLcdGLExtrudedEllipsePaintableFactory();
//    ellipsePaintableFactory.setDefaultMinimumZ( 0 );
//    ellipsePaintableFactory.setDefaultMaximumZ( 50000 );
//    fEllipsePainter = new TLcdGLPaintablePainter2( ellipsePaintableFactory );
//
//    TLcdGLExtrudedPolygonPaintableFactory polygonPaintableFactory =
//            new TLcdGLExtrudedPolygonPaintableFactory();
//    polygonPaintableFactory.setDefaultMinimumZ( 0 );
//    polygonPaintableFactory.setDefaultMaximumZ( 50000 );
//    fPolygonPainter = new TLcdGLPaintablePainter2( polygonPaintableFactory );
//  }
//
//
//  public void paint(
//          ILcdGLDrawable aGLDrawable,
//          Object aObject,
//          ILcdGLPaintMode aMode,
//          ILcdGLContext aContext
//  ) {
//    ILcdGLPainter painter = getPainter( aObject );
//    if ( painter != null )
//      painter.paint( aGLDrawable, aObject, aMode, aContext );
//  }
//
//
//  private ILcdGLPainter getPainter( Object aObject ) {
//    if ( aObject instanceof ILcdEllipse )
//      return fEllipsePainter;
//    else if ( aObject instanceof ILcdPolygon )
//      return fPolygonPainter;
//    else
//      return null;
//  }
//}
