
import static java.lang.Math.max;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;

import com.luciad.geodesy.TLcdGeodeticDatum;
import com.luciad.gui.ALcdAction;
import com.luciad.gui.ILcdAction;
import com.luciad.gui.TLcdActionAtLocationEvent;
import com.luciad.gui.TLcdIconFactory;
import com.luciad.gui.TLcdImageIcon;
import com.luciad.input.touch.TLcdTouchDevice;
import com.luciad.model.ILcdModelReference;
import com.luciad.projection.TLcdPolarStereographic;
import com.luciad.reference.TLcdGridReference;
import com.luciad.reference.format.TLcdEPSGReferenceParser;
import com.luciad.shape.ALcdBounds;
import com.luciad.shape.ILcdBounds;
import com.luciad.shape.ILcdPoint;
import com.luciad.shape.shape2D.TLcd2DEditablePointList;
import com.luciad.shape.shape2D.TLcdXYPoint;
import com.luciad.shape.shape3D.ILcd3DEditablePoint;
import com.luciad.shape.shape3D.TLcd3DEditablePointList;
import com.luciad.shape.shape3D.TLcdXYZPoint;
import com.luciad.transformation.TLcdDefaultModelXYWorldTransformation;
import com.luciad.transformation.TLcdDefaultModelXYZWorldTransformation;
import com.luciad.util.ILcdFilter;
import com.luciad.util.ILcdFireEventMode;
import com.luciad.util.TLcdOutOfBoundsException;
import com.luciad.view.TLcdAWTEventFilterBuilder;
import com.luciad.view.animation.ALcdAnimationManager;
import com.luciad.view.animation.ILcdAnimation;
import com.luciad.view.gxy.ILcdGXYView;
import com.luciad.view.gxy.TLcdGXYContext;
import com.luciad.view.lightspeed.ILspView;
import com.luciad.view.lightspeed.TLspContext;
import com.luciad.view.lightspeed.TLspExternalView;
import com.luciad.view.lightspeed.TLspViewBuilder;
import com.luciad.view.lightspeed.camera.ALspViewXYZWorldTransformation;
import com.luciad.view.lightspeed.controller.ALspController;
import com.luciad.view.lightspeed.controller.ILspController;
import com.luciad.view.lightspeed.controller.TLspClickActionController;
import com.luciad.view.lightspeed.controller.TLspKeyActionController;
import com.luciad.view.lightspeed.controller.navigation.TLspPanController;
import com.luciad.view.lightspeed.controller.navigation.TLspZoomController;
import com.luciad.view.lightspeed.controller.navigation.TLspZoomToController;
import com.luciad.view.lightspeed.controller.selection.TLspSelectControllerModel;
import com.luciad.view.lightspeed.controller.selection.TLspSelectPointInput;
import com.luciad.view.lightspeed.controller.touch.TLspTouchAndHoldActionController;
import com.luciad.view.lightspeed.controller.touch.TLspTouchNavigateController;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.layer.TLspDomainObjectContext;
import com.luciad.view.lightspeed.layer.TLspPaintRepresentation;
import com.luciad.view.lightspeed.util.TLspViewNavigationUtil;


/**
 * This Java class is used as a convenience proxy for bridging to C++.
 * <p/>
 * It contains the basic methods to drive a {@link TLspExternalView LuciadLightspeed view} from C++.
 * <ul>
 *   <li>{@link #destroy}: to destroy the view when it is no longer needed</li>
 *   <li>{@link #display}: to repaint the view</li>
 *   <li>{@link #viewInvalidated}: to invalidate the view</li>
 *   <li>{@link #handleMouseMove}, etc.: to pass native UI events to the view</li>
 *   <li>{@link #setSize}: call when the native window size changes</li>
 * </ul>
 * <p/>
 * Note that you <b>must</b> instantiate this proxy when the appropriate OpenGL context is <i>current</i>.
 * <p/>
 * To add your own application bridging calls, you can subclass this class, as is demonstrated in {@link SampleApplicationProxy}.
 * <p/>
 * For more information, see the developer article <i>How to integrate LuciadLightspeed in a C++ application</i> on the <a href="http://dev.luciad.com/">Luciad Developer Portal</a>.
 *
 * @see TLspExternalView
 */
public class LightspeedViewProxy {
  //Data Base conection
  private String DataBase_host = "localhost";
  private String DataBase_name = "cmca";
  private String DataBase_user = "root";
  private String DataBase_pass = "root";
  LuciadBDConnection conection = null;
  
  private final long fNativePeer;
  private final TLspExternalView fView;
  public final MouseEventHandler fMouseEventHandler;

  public LightspeedViewProxy(long aNativePeer) {
    fNativePeer = aNativePeer;

    fView = TLspViewBuilder.newBuilder().buildExternalView();

    fView.getGLDrawable().invokeLater(aGLDrawable -> {
      fView.addViewInvalidationListener(aEvent -> LightspeedViewProxy.this.viewInvalidated(getNativePeer()));
      return false;
    });
    //fView.setController(createActionController());
    
    try {
		initDataBaseController();
		
		
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    fMouseEventHandler = new MouseEventHandler(fView);
    
  }
  
  private void initDataBaseController() throws ClassNotFoundException, SQLException {
	  conection = new LuciadBDConnection(DataBase_host, DataBase_name, DataBase_user, DataBase_pass);
  }

  public long getNativePeer() {
    return fNativePeer;
  }

  public void destroy() {
    fView.destroy();
  }

  public void display() {
    fView.display();
  }

  public void setSize(int aWidth, int aHeight) {
    fView.setSize(max(1, aWidth), max(1, aHeight));
  }

  public native void viewInvalidated(long aNativePeer);

  public TLspExternalView getView() {
    return fView;
  }

  public void handleMousePressed(int aX, int aY, int aButtonId, int aModifiers) {
    fMouseEventHandler.handleMousePressed(aX, aY, aButtonId, aModifiers);
  }

  public void handleMouseReleased(int aX, int aY, int aButtonId, int aModifiers) {
    fMouseEventHandler.handleMouseReleased(aX, aY, aButtonId, aModifiers);
  }

  public void handleMouseMove(int aX, int aY, int aModifiers) {
    fMouseEventHandler.handleMouseMove(aX, aY, aModifiers);
  }

  public void handleMouseWheel(int aX, int aY, int aScrollAmount, int aWheelRotation, int aModifiers) {
    fMouseEventHandler.handleMouseWheel(aX, aY, aScrollAmount, aWheelRotation, aModifiers);
  }

  public void handleOnFocus() {
	  System.out.println("aaaa");
    fMouseEventHandler.handleOnFocus();
  }

  public void handleOnBlur() {
	  System.out.println("bbbb");
    fMouseEventHandler.handleOnBlur();
  }
  
  public static ALspController createNavigationController() {
    // First we create the controllers we want to chain.
    ALspController zoomToController = createZoomToController();
    ALspController panController = createPanController();
    ALspController zoomController = createZoomController();

    //Chain the controllers together, events will be offered to the first and trickle down.
    zoomToController.appendController(panController);
    zoomToController.appendController(zoomController);

    //Set general properties on the top of the chain.
    zoomToController.setIcon(TLcdIconFactory.create(TLcdIconFactory.HAND_ICON));
    zoomToController.setShortDescription(
        "<html><p>Navigate:</p><p><b>Left mouse</b>: <ul><li>Drag: pan</li>" +
        "<li>Double click: fly to</li></ul></p><p><b>Mouse wheel</b>: zoom</p>" +
        "<p><b>Right mouse</b>: rotate</p></html>"
    );

    return zoomToController;
  }
  
  /*
   * Fly-to controller with left mouse button filter. This controller will only use double
   * click events, so in combination with the applied filter, only left mouse double
   * clicks or right mouse double clicks will trigger a fly-to.
   * Left mouse zooms in and right mouse zooms out.
   */
  private static ALspController createZoomToController() {
    TLspZoomToController zoomToController = new TLspZoomToController();
    zoomToController.setAWTFilter(TLcdAWTEventFilterBuilder.newBuilder().
        leftMouseButton().or().rightMouseButton().build());
    return zoomToController;
  }
  
  /*
   * Panning is the backup left mouse button behaviour (if editing is not possible), as well
   * as the default action mapped to the middle mouse button.
   */
  public static ALspController createPanController() {
    // Use a pan controller that consumes events during panning, e.g. mouse wheel events.
    TLspPanController panController = new TLspPanController();
    panController.setEnableInertia(true);
    panController.setAWTFilter(TLcdAWTEventFilterBuilder.newBuilder().
        leftMouseButton().or().
                                                            middleMouseButton().or().
                                                            mouseWheelFilter().build());
    return panController;
  }
  
  /*
   * Zooming is the default action mapped to the mouse-wheel.
   */
  public static ALspController createZoomController() {
    TLspZoomController zoomController = new TLspZoomController();
    zoomController.setAWTFilter(TLcdAWTEventFilterBuilder.newBuilder().
        mouseWheelFilter().build());
    return zoomController;
  }
  

  /**
   * Utility class to handle external mouse events. This class will generate the
   * correct AWT events and pass them to the view.
   */
  public static class MouseEventHandler {

    final TLspExternalView fView;
    final Component fEventSource;

    private static final long MOUSE_CLICK_INTERVAL = 200;

    private long fMouseClickedTime;
    private boolean fIsDragging;
    private boolean fIsButtonPressed;
    private int fLastButton = 0;
    
    private boolean draw_polygon_mode = false;
    private TLcd3DEditablePointList paint_points = new TLcd3DEditablePointList();
    private int paint_points_count = 0;
    private ILcdPoint mouse_position_lat_lng_point = null;
    private ILcdPoint last_move_mouset_lat_lng_point = null;
    private ILcdPoint last_clik_mouse_lat_lng_point = null;
    
    //private LuciadBDConnection conection;

    /**
     * Creates a new mouse event handler.
     *
     * @param aView the view to which the events should be passed.
     */
    private MouseEventHandler(TLspExternalView aView) {
      fView = aView;
      fEventSource = fView.getOverlayComponent();
    }

    public void handleMousePressed(int aX, int aY, int aButtonId, int aModifiers) {
      fIsButtonPressed = true;
      fLastButton = aButtonId;

      int modifiers = getButtonModifier(fLastButton) | aModifiers;
      MouseEvent mouseEvent = new MouseEvent(fEventSource, MouseEvent.MOUSE_PRESSED,
                                             System.currentTimeMillis(), modifiers,
                                             aX, aY, 1, false, aButtonId);
      handleAWTEvent(mouseEvent);
    }

    public void handleMouseReleased(int aX, int aY, int aButtonId, int aModifiers) {

      if (!fIsButtonPressed) {
        System.err.println("Native application called LightspeedViewProxy.handleMouseReleased, but no mouse button is pressed.");
        return;
      }

      int clickCount = 1;
      long mouseReleaseTime = System.currentTimeMillis();
      MouseEvent mouseEvent = new MouseEvent(fEventSource, MouseEvent.MOUSE_RELEASED,
                                             mouseReleaseTime, aModifiers,
                                             aX, aY, clickCount, false, aButtonId);
      
      //////////////////////////////////////////////////////////////////////////////////////////////////////////
      //Polygons
      if(aButtonId == MouseEvent.BUTTON1) {
    	  if (mouseEvent.getClickCount() == 1 && !mouseEvent.isConsumed()) {
    		  //e.consume();
	    	  ILcdPoint lat_lng_point = convertPoint(mouseEvent.getPoint());
	    	  if(lat_lng_point != null) {
	    		  if(draw_polygon_mode) {
		    		  paint_points.insert3DPoint(paint_points_count, new TLcdXYZPoint(lat_lng_point.getX(),lat_lng_point.getY(), 0));
		    		  paint_points_count=paint_points_count+1;
	    		  }
	    	      last_clik_mouse_lat_lng_point = lat_lng_point;
	    		  //System.out.println( aX + "," + aY + " -> " + lat_lng_point.getX() + "," + lat_lng_point.getY());
	    	  }
    	  }
//    	  else if (mouseEvent.getClickCount() == 2 && !mouseEvent.isConsumed()) {
//    		  //Double click and finish polygon
//    	  }
      }
      //////////////////////////////////////////////////////////////////////////////////////////////////////////

      handleAWTEvent(mouseEvent);

      if (!fIsDragging) {
        mouseEvent = new MouseEvent(fEventSource, MouseEvent.MOUSE_CLICKED,
                                    mouseReleaseTime, aModifiers,
                                    aX, aY, clickCount, false, aButtonId);
        handleAWTEvent(mouseEvent);

        if (mouseReleaseTime - fMouseClickedTime < MOUSE_CLICK_INTERVAL) {
          mouseEvent = new MouseEvent(fEventSource, MouseEvent.MOUSE_CLICKED,
                                      mouseReleaseTime, aModifiers,
                                      aX, aY, clickCount + 1, false, aButtonId);
          handleAWTEvent(mouseEvent);
        }
        fMouseClickedTime = mouseReleaseTime;
      }

      fIsDragging = false;
      fIsButtonPressed = false;
      fLastButton = -1;
    }

    public void handleMouseMove(int aX, int aY, int aModifiers) {
      fIsDragging = fIsButtonPressed;
      MouseEvent mouseEvent;
      if (fIsDragging) {
        int modifiers = getButtonModifier(fLastButton) | aModifiers;
        mouseEvent = new MouseEvent(fEventSource, MouseEvent.MOUSE_DRAGGED,
                                               System.currentTimeMillis(), modifiers,
                                               aX, aY, 0, false, fLastButton);
        handleAWTEvent(mouseEvent);
      } else {
        mouseEvent = new MouseEvent(fEventSource, MouseEvent.MOUSE_MOVED,
                                               System.currentTimeMillis(), aModifiers,
                                               aX, aY, 0, false);
        handleAWTEvent(mouseEvent);       
      }
      
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
      	mouse_position_lat_lng_point = convertPoint(mouseEvent.getPoint());
		//Polygons
		if(draw_polygon_mode && paint_points.getPointCount() > 0) {
			last_move_mouset_lat_lng_point = convertPoint(mouseEvent.getPoint());
		//System.out.println( aX + "," + aY + " -> " + lat_lng_point.getX() + "," + lat_lng_point.getY());
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public void handleMouseWheel(int aX, int aY, int aScrollAmount, int aWheelRotation, int aModifiers) {
      MouseWheelEvent mouseWheelEvent = new MouseWheelEvent(fEventSource,
                                                            MouseEvent.MOUSE_WHEEL,
                                                            System.currentTimeMillis(), aModifiers,
                                                            aX, aY, 0, false,
                                                            MouseWheelEvent.WHEEL_UNIT_SCROLL,
                                                            aScrollAmount, aWheelRotation);
      handleAWTEvent(mouseWheelEvent);
    }

    public void handleOnFocus() {
      System.out.println("OnFocus");
      FocusEvent event = new FocusEvent(fEventSource, FocusEvent.FOCUS_GAINED);
      handleAWTEvent(event);
    }

    public void handleOnBlur() {
      System.out.println("OnBlur");
      FocusEvent event = new FocusEvent(fEventSource, FocusEvent.FOCUS_LOST);
      handleAWTEvent(event);
    }

    public int getButtonModifier(int aButtonId) {
      switch (aButtonId) {
      case MouseEvent.BUTTON1:
        return InputEvent.BUTTON1_DOWN_MASK;
      case MouseEvent.BUTTON2:
        return InputEvent.BUTTON2_DOWN_MASK;
      case MouseEvent.BUTTON3:
        return InputEvent.BUTTON3_DOWN_MASK;
      default:
        return 0;
      }
    }

    private void handleAWTEvent(final AWTEvent e) {
      fView.handleAWTEvent(e);
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public TLcd3DEditablePointList getPaintPoints(){
    	return paint_points;
    }
    
    public void clearPaintPoints() {
    	paint_points = new TLcd3DEditablePointList();
    	paint_points_count = 0;
    	last_move_mouset_lat_lng_point = null;
    }
    
    public ILcdPoint getLastMovePoint() {
    	return last_move_mouset_lat_lng_point;
    }
    
    public int getPaintPointsCount() {
    	return paint_points.getPointCount();
    }
    
    public void setDrawPolygonMode(boolean value) {
    	draw_polygon_mode = value;
    }
    
    public boolean getDrawPolygonMode() {
    	return draw_polygon_mode;
    }
    
    public ILcdPoint getMousePosition() {
    	return mouse_position_lat_lng_point;
    }
    
    public ILcdPoint getLastClickMousePosition() {
    	return last_clik_mouse_lat_lng_point;
    }
    
    //Convert Points function
    private ILcdPoint convertPoint(Point aViewPoint) {
        ILcdModelReference modelReference;
		try {
			modelReference = new TLcdEPSGReferenceParser().parseModelReference("EPSG:4326");
		    //ILcdPoint modelPoint = null;
		    ILcdPoint modelPoint = null;
		    try {
		      modelPoint = getCoordinates(aViewPoint, modelReference);
		      return modelPoint;
		    } catch (TLcdOutOfBoundsException e) {
		    	return null;
		    }
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	  }
     
    protected ILcdPoint getCoordinates(Point aAWTPoint, ILcdModelReference aReference) throws TLcdOutOfBoundsException { 
		double scaleX = (double) fView.getWidth() / fView.getOverlayComponent().getWidth(); 
		double scaleY = (double) fView.getHeight() / fView.getOverlayComponent().getHeight(); 
	    ILcdPoint worldPoint = fView.getServices().getTerrainSupport().getPointOnTerrain( 
	        new TLcdXYPoint(aAWTPoint.x * scaleX, aAWTPoint.y * scaleY), new TLspContext(null, fView)
	    ); 
	    if (worldPoint == null) { 
	      return null; 
	    } 
	    TLcdXYZPoint modelPoint = new TLcdXYZPoint(); 
	    TLcdDefaultModelXYZWorldTransformation transformation = new TLcdDefaultModelXYZWorldTransformation(); 
	    transformation.setXYZWorldReference(fView.getXYZWorldReference()); 
	    transformation.setModelReference(aReference); 
	    transformation.worldPoint2modelSFCT(worldPoint, modelPoint); 
	    return modelPoint; 
    } 
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  }
}
