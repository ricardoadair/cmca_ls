//package samples.lightspeed.integration.jni;

import static com.luciad.view.lightspeed.util.TLspViewTransformationUtil.setup2DView;
import static com.luciad.view.lightspeed.util.TLspViewTransformationUtil.setup3DView;

import java.awt.Color;

//import static samples.lightspeed.common.FitUtil.fitOnLayers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.PopupMenu;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.datamodel.TLcdCoreDataTypes;
import com.luciad.datamodel.TLcdDataModel;
import com.luciad.datamodel.TLcdDataModelBuilder;
import com.luciad.datamodel.TLcdDataObject;
import com.luciad.datamodel.TLcdDataType;
import com.luciad.datamodel.TLcdDataTypeBuilder;
import com.luciad.format.database.TLcdPrimaryKeyAnnotation;
import com.luciad.gui.TLcdAWTUtil;
import com.luciad.gui.swing.TLcdOverlayLayout;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelReference;
import com.luciad.model.TLcd2DBoundsIndexedModel;
import com.luciad.model.TLcdModelDescriptor;
import com.luciad.model.TLcdVectorModel;
import com.luciad.reference.ILcdGeocentricReference;
import com.luciad.reference.ILcdGeodeticReference;
import com.luciad.reference.TLcdGeodeticReference;
import com.luciad.reference.format.TLcdEPSGReferenceParser;
import com.luciad.shape.ILcdPoint;
import com.luciad.shape.shape2D.ILcd2DEditablePoint;
import com.luciad.shape.shape2D.TLcd2DEditablePointList;
import com.luciad.shape.shape2D.TLcdLonLatBounds;
import com.luciad.shape.shape2D.TLcdLonLatPoint;
import com.luciad.shape.shape2D.TLcdLonLatPolygon;
import com.luciad.shape.shape2D.TLcdXYPoint;
import com.luciad.shape.shape2D.TLcdXYPolygon;
import com.luciad.shape.shape3D.ILcd3DEditablePoint;
import com.luciad.shape.shape3D.ILcd3DEditablePointList;
import com.luciad.shape.shape3D.TLcd3DEditablePointList;
import com.luciad.shape.shape3D.TLcdLonLatHeightPoint;
import com.luciad.shape.shape3D.TLcdLonLatHeightPolygon;
import com.luciad.shape.shape3D.TLcdLonLatHeightPolypoint;
import com.luciad.shape.shape3D.TLcdXYZPoint;
import com.luciad.shape.shape3D.TLcdXYZPolygon;
import com.luciad.shape.shape3D.TLcdXYZPolypoint;
import com.luciad.util.ILcdFireEventMode;
import com.luciad.util.ILcdSelectionListener;
import com.luciad.util.TLcdHasGeometryAnnotation;
import com.luciad.util.TLcdOutOfBoundsException;
import com.luciad.util.concurrent.TLcdLockUtil;
import com.luciad.util.measure.ILcdLayerMeasureProviderFactory;
import com.luciad.util.measure.ILcdModelMeasureProviderFactory;
import com.luciad.view.ILcdXYZWorldReference;
import com.luciad.view.lightspeed.controller.ILspController;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.layer.TLspCompositeLayerFactory;
import com.luciad.view.lightspeed.layer.shape.TLspShapeLayerBuilder;
import com.luciad.view.lightspeed.layer.style.TLspLayerStyle;
import com.luciad.view.lightspeed.painter.grid.TLspLonLatGridLayerBuilder;
import com.luciad.view.lightspeed.services.effects.TLspAmbientLight;
import com.luciad.view.lightspeed.services.effects.TLspHeadLight;
import com.luciad.view.lightspeed.style.complexstroke.ALspComplexStroke.PolylineBuilder;
import com.luciad.view.lightspeed.swing.TLspBalloonManager;
import com.luciad.view.lightspeed.swing.TLspScaleIndicator;
import com.luciad.view.lightspeed.swing.navigationcontrols.TLspNavigationControlsBuilder;
import com.luciad.view.lightspeed.util.TLspViewNavigationUtil;
import com.luciad.view.swing.ALcdBalloonDescriptor;
import com.luciad.view.swing.ILcdBalloonContentProvider;
import com.luciad.view.swing.TLcdModelElementBalloonDescriptor;

import org.jdesktop.swingx.VerticalLayout;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

/*import samples.common.BalloonViewSelectionListener;
import samples.common.dataObjectDisplayTree.DataObjectDisplayTree;
import samples.common.serviceregistry.ServiceRegistry;
import samples.lightspeed.common.LspDataUtil;
import samples.lightspeed.common.LuciadLogoIcon;
import samples.lightspeed.common.MouseLocationComponent;*/

/**
 * An extension of {@link LightspeedViewProxy} that adds application-specific bridging calls.
 * <p/>
 * This sample proxy adds:
 * <ul>
 *   <li>{@link #setMapReference}: change the view's world reference</li>
 *   <li>{@link #loadData}: add a layer to the view based on the standard model decoders and layer factories.</li>
 *   <li>{@link #addTrackLayer}, etc.: add a track layer for updates come from C++.</li>
 *   <li>{@link #objectSelected}: get track layer selection events on the native side.</li>
 * </ul>
 * It also places several Swing overlay components on top of the view, including a scale indicator.
 * <p/>
 * Note that you <b>must</b> instantiate this proxy when the appropriate OpenGL context is <i>current</i>.
 * <p/>
 * For more information, see the developer article <i>How to integrate LuciadLightspeed in a C++ application</i> on the <a href="http://dev.luciad.com/">Luciad Developer Portal</a>.
 */
public class SampleApplicationProxy1 extends LightspeedViewProxy {

  private final Map<Integer, ILspLayer> fTrackLayers;
  private final AtomicInteger fAtomicInteger;
  private Component navigationControls;
  private TLspScaleIndicator scaleIndicator;
  TLspViewNavigationUtil navigationUtil;
  public JLabel coords;
    
  //Layers
  private final Map<Integer, Map<String, Object >> fPolygonTrackLayers;
  LayerFactory layerFactory = new LayerFactory();
  int track_layer_points = 0;
  int track_layer_draw_polygons = 0;
  private boolean create_polygon_user_clicks = false;
  int polygon_user_clicks_track_id = -1;
  int rec_polygon_id = -1;
  DecimalFormat df = new DecimalFormat("#.0000"); 
  String ac = "0,0"; 
  String flir = "0,0";

  public SampleApplicationProxy1(long aNativePeer) {
    super(aNativePeer);

    getView().getServices().getGraphicsEffects().add(new TLspAmbientLight());
    getView().getServices().getGraphicsEffects().add(new TLspHeadLight(getView()));

    fAtomicInteger = new AtomicInteger();
    fTrackLayers = new HashMap<>();
    fPolygonTrackLayers = new HashMap<>();

    // Add navigation control, scale indicator and mouse readout to the overlay component.
    TLcdAWTUtil.invokeAndWait(() -> {
      Container overlay = getView().getOverlayComponent();
      TLcdOverlayLayout layout = (TLcdOverlayLayout) overlay.getLayout();

      navigationControls = TLspNavigationControlsBuilder
          .newBuilder(getView())
          .alwaysActive(true)
          .build();
      overlay.add(navigationControls);
      layout.putConstraint(navigationControls, TLcdOverlayLayout.Location.NORTH_EAST, TLcdOverlayLayout.ResolveClash.VERTICAL);

      scaleIndicator = new TLspScaleIndicator(getView());
      // updates the scale indicator depending on where you pan, not only based on the zoom level
      scaleIndicator.setScaleAtCenterOfMap(true);
      JLabel scaleIndicatorLabel = scaleIndicator.getLabel();
      overlay.add(scaleIndicatorLabel);
      layout.putConstraint(scaleIndicatorLabel, TLcdOverlayLayout.Location.SOUTH_EAST, TLcdOverlayLayout.ResolveClash.VERTICAL);

      /*JLabel luciadLogo = new JLabel(new LuciadLogoIcon());
      overlay.add(luciadLogo);
      layout.putConstraint(luciadLogo, TLcdOverlayLayout.Location.SOUTH_WEST, TLcdOverlayLayout.ResolveClash.VERTICAL);
*/
      coords = new JLabel("");
      coords.setBackground(Color.LIGHT_GRAY);
      coords.setForeground(Color.BLACK);
      coords.setOpaque(true);
      overlay.add(coords);
      PopupMenu jpop = new PopupMenu();
      jpop.add("uno");
      overlay.add(jpop);
      layout.putConstraint(coords, TLcdOverlayLayout.Location.NORTH, TLcdOverlayLayout.ResolveClash.VERTICAL);
      
      TLspBalloonManager mgr = new TLspBalloonManager(
          getView(),
          overlay,
          TLcdOverlayLayout.Location.NO_LAYOUT,
          new BalloonContentProvider()
      );
      mgr.setBalloonsEnabled(true);
      
      setMapReference("EPSG:4978");

      BalloonViewSelectionListener listener = new BalloonViewSelectionListener(getView(), mgr);
      getView().addLayeredListener(listener);
      getView().addLayerSelectionListener(listener);
      getView().getRootNode().addHierarchyPropertyChangeListener(listener);

/*      Iterable<ILcdModelMeasureProviderFactory> measureProviderFactories = ServiceRegistry.getInstance().query(ILcdModelMeasureProviderFactory.class);
      Iterable<ILcdLayerMeasureProviderFactory> layerMeasureProviderFactories = ServiceRegistry.getInstance().query(ILcdLayerMeasureProviderFactory.class);
      overlay.add(
          new MouseLocationComponent(
              getView(),
              getView().getOverlayComponent(),
              getView().getOverlayComponent(),
              measureProviderFactories,
              layerMeasureProviderFactories
          ),
          TLcdOverlayLayout.Location.SOUTH
      );*/

//      
      //Paint Points
//      track_layer_points = addTrackLayer("2", "EPSG:4326");
//      addTrack(track_layer_points, 115, 0, 0, 0, "Puntos", 0);
//      paintPoints();
      
      
      //Cheking opacity
      
	  /*prueba_opacity_layer_id = addTrackLayerPolygon("Prueba opacity", "EPSG:4326");
	  addTrack(prueba_opacity_layer_id, 115, 0, 0, 0, "Poligono Prueba opacity", 0);
      addPolygon(prueba_opacity_layer_id, 9999, generate3DRandomPolygon(), "Polygono " + 9999, 0);*/
      
    });

    getView().addLayer(TLspLonLatGridLayerBuilder.newBuilder().build());
    navigationUtil = new TLspViewNavigationUtil(getView());
    
    ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
	timer.scheduleAtFixedRate(update_points, 1, 1, TimeUnit.SECONDS);
    
	timer.scheduleAtFixedRate(update, 200, 200, TimeUnit.MILLISECONDS);
	
	//timer.scheduleAtFixedRate(update_all_layers_opacity, 15, 5, TimeUnit.SECONDS);
  }
  
  
  int prueba_opacity_layer_id = 0;
  float opacity = 0.9f;
  
  final Runnable update_all_layers_opacity = new Runnable() {
	  public void run() {
		  /*for (Map.Entry<Integer, ILspLayer> layer : fTrackLayers.entrySet())
		  {
			  System.out.println("Update opacity " + layer.getKey());// + "/" + layer.getValue());
			  //ILspLayer aLayer = layer.getValue();
			  int aLayerId = layer.getKey();
			  //float alphaChange = 0.05f;
//		      float currentOpacity = aLayer.getLayerStyle().getOpacity();
//		      float newOpacity = currentOpacity - alphaChange;
			  double newOpacity = Math.random();
		      System.out.println("newOpacity " + newOpacity);
			  //opacity(aLayerId, (float)(newOpacity));
		  }*/
		  if(prueba_opacity_layer_id != 0)
		  {
			  opacity = opacity - 0.1f;
			  if(0< opacity && opacity < 1)
			  {
				  opacity(prueba_opacity_layer_id, opacity);
			  
			  }
		  }
	  }
	};
  
  final Runnable update_points = new Runnable() {
	  public void run() {
		  update_map();
	  }
	};
	
	final Runnable update= new Runnable() {
	  public void run() {
		  //Update Coords
		  updateCoords();
		  //Update polygon user clicks
		  if(track_layer_draw_polygons != 0)
		  {
			  draw_polygon_user_clicks();
		  }
		  else {
			  track_layer_draw_polygons = addTrackLayerPolygon("1", "EPSG:4326");
			  addTrack(track_layer_draw_polygons, 115, 0, 0, 0, "Poligonos", 0);
		  }
//		  ILcdPoint last_position = fMouseEventHandler.getLastClickMousePosition();
//		  if(last_position != null)
//		  {
//			  centerMap(last_position.getX(), last_position.getY());
//		  }
	  }
	};
	
	public void centerMap(double lat, double lng) 
	{
//		try {
//			navigationUtil.fit(new TLcdLonLatBounds( lat, lng, 100, 100 ), new TLcdGeodeticReference());
//		} catch (TLcdOutOfBoundsException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		TLcdLonLatPoint mouse = new TLcdLonLatPoint(lat,lng);
		try {
			navigationUtil.center(mouse,new TLcdGeodeticReference());
		} catch (TLcdOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void zoom(double aFactor)
	{
		navigationUtil.zoom(aFactor);
	}
	
	public void opacity(int aLayerId, float newOpacity) {
		/*int wheelRotation = e.getWheelRotation();
      float alphaChange = 0.05f * (-wheelRotation);
      float currentOpacity = aLayers.get(0).getLayerStyle().getOpacity();
      float newOpacity = currentOpacity + alphaChange;*/
		ILspLayer layer = fTrackLayers.get(aLayerId);
	    if (layer != null) {
	    	System.out.println( "newOpacity" + newOpacity );
	    	layer.setLayerStyle(layer.getLayerStyle().asBuilder().opacity(newOpacity).build());
	    	System.out.println( "getOpacity" + layer.getLayerStyle().getOpacity() );
	    }
	}
	
	 private static TLspLayerStyle createNightBackgroundLayerStyle() {
		//desaturate factor
	    float dF = 0.85f;
	    float desaturateMatrix[] = new float[]{
	    		0.21f * dF + (1 - dF), 
	    		0.72f * dF, 
	    		0.07f * dF, 
	    		0.0f, 
	    		0.0f,
	    		0.21f * dF, 
	    		0.72f * dF + (1 - dF), 
	    		0.07f * dF, 
	    		0.0f, 
	    		0.0f, 
	    		0.21f * dF, 
	    		0.72f * dF, 
	    		0.07f * dF + (1 - dF), 
	    		0.0f, 
	    		0.0f,
	            0.00f, 
	            0.00f, 
	            0.00f, 
	            1.0f, 
	            0.0f
	    };
	    return TLspLayerStyle.newBuilder()
	                         .contrast(0.9f)
	                         .brightness(0.65f)
	                         .colorMatrix(desaturateMatrix)
	                         .build();
	  }
	
	public void updateCoords()
	{
		ILcdPoint mouse_position = fMouseEventHandler.getMousePosition();
		String new_coords = "";
		if(mouse_position != null && !ac.equals("") && !flir.equals("")) 
		{
			new_coords = "A/C:" + ac  +  "             Mouse:" + df.format(mouse_position.getX()) +"," + df.format(mouse_position.getY()) + "                FLIR: "+ flir;
			coords.setText(new_coords);
		}
	}
	
	public void setAc(String new_ac)
	{
		ac = new_ac;
	}
	
	public void setFlir(String new_flir)
	{
		flir = new_flir;
	}
	
	public void update_map()
	{
	  if(track_layer_points==0) 
	  {
		  create_points();
	  }
	  else
	  {
		  update_points();
	  }
	}
		
	public void create_points() {
		//track_layer_points = addPointTrackLayer("2", "EPSG:4326");
		track_layer_points = addTrackLayer("2", "EPSG:4326");
		addTrack(track_layer_points, 115, 0, 0, 0, "Puntos", 0);
		paintPoints();
	}
	
	public void update_points() {
		paintPoints();
	}
	
	public void draw_polygon_user_clicks() {
		//Paint Polygon
	    paintPolygonUserClicks();
	}
	
  /**
   * Sets the world reference of the view.
   *
   * @param aEPSG the EPSG code of the reference.
   */
  public void setMapReference(String aEPSG) {
    try {
      ILcdXYZWorldReference reference = (ILcdXYZWorldReference) new TLcdEPSGReferenceParser().parseModelReference(aEPSG);
      if (reference != null) {
        if (reference instanceof ILcdGeocentricReference) {
          setup3DView(getView(), reference, true);
        } else {
          setup2DView(getView(), reference, true);
        }
      }
    } catch (ParseException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Decodes the specified data and adds a layer for it to the map.
   *
   * @param aSource the path of the data
   */
  public void loadData(String aSource) {
    System.out.println("loadData(" + aSource + ")");
    try {
      ILspLayer layer = LspDataUtil
          .instance()
          .model(aSource)
          .layer()
          .addToView(getView())
          .getLayer();
      //fitOnLayers(getView().getOverlayComponent(), getView(), true, layer);
    } catch (Exception aE) {
      aE.printStackTrace();
    }
  }

  public void reSortLayer(int pos,String aSource) {
	   System.out.println("JAVA: " + aSource + ": " + pos);
	   
	   for (int i=0;i<getView().layerCount();i++)
	   {
		   String act_label = getView().getLayer(i).getLabel();
		   //System.out.println("\t" + getView().getLayer(i).getLabel());
		   if (aSource.toLowerCase().contains(act_label.toLowerCase())) {
			   System.out.println("*");
			   getView().moveLayerAt(pos, getView().getLayer(i));
		   }
	   }
	   /*try {
	     ILspLayer layer = LspDataUtil
	         .instance()
	         .model(aSource)
	         .layer()
	         .addToView(getView())
	         .getLayer();
	     //getView().moveLayerAt(0,layer);
	     //fitOnLayers(getView().getOverlayComponent(), getView(), true, layer);
	   } catch (Exception aE) {
	     aE.printStackTrace();
	   }*/
	   
	 }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////
  //Map to Polygon Layers
  public void addPolygonTrackLayers( int polygon_id, Map<String,Object> element) {
	  fPolygonTrackLayers.put(polygon_id, element);
  }
  
  public void removePolygonTrackLayers( int polygon_id) {
	  fPolygonTrackLayers.remove(polygon_id);
  }
  
  public Map<String,Object> getPolygonTrackLayers( int polygon_id) {
	  return fPolygonTrackLayers.get(polygon_id);
  }
  
  public boolean isElementInPolygonTrackLayers( int polygon_id) {
	  return fPolygonTrackLayers.containsKey(polygon_id);
  }
  
  public List<Integer> getAllPolygonTrackLayersIds(){
	  List<Integer> all_tracks_ids_polygon = new ArrayList<Integer>(fPolygonTrackLayers.keySet());
	  return all_tracks_ids_polygon;
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /**
   * Adds a new layer to the view which can be used to add tracks.
   *
   * @param aLayerName the layer name.
   * @param aEPSG the EPSG code of the reference to be used for the track layer.
   * @return the identifier for the added track layer.
   */
  
  public int addTrackLayer(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 0);
  }
  
  public int addTrackLayerPoint(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 1);
  }
  
  public int addTrackLayerPolygon(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 2);
  }
  
  public int addTrackLayer(String aLayerName, String aEPSG, int type_layer) {
    try {
      ILcdModelReference reference = new TLcdEPSGReferenceParser().parseModelReference(aEPSG);
      ILspLayer layer;
      TLspCompositeLayerFactory factory;
      switch (type_layer) {
		case 1:
			factory = new TLspCompositeLayerFactory(layerFactory);
			TLcdVectorModel model_point = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor("", "Single Points", "Single Points"));
			Collection<ILspLayer> layers_points = factory.createLayers(model_point);
			layer = layers_points.iterator().next();
			break;
		case 2:
			factory = new TLspCompositeLayerFactory(layerFactory);
			TLcdVectorModel model_polygon = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor("", "SolidShapes", "Solid fill shapes"));
			Collection<ILspLayer> layers_polygon = factory.createLayers(model_polygon);
			layer = layers_polygon.iterator().next();
			break;
		default:
			ILcdModel model = new TLcd2DBoundsIndexedModel(reference, new TLcdModelDescriptor(aLayerName, "Tracks", aLayerName));
		    layer = TLspShapeLayerBuilder.newBuilder()
		                                             .model(model)
		                                             .build();
			break;
	  }
      int layerId = fAtomicInteger.incrementAndGet();
      fTrackLayers.put(layerId, layer);
      
      // View operations need to be executed on the paint thread.
      getView().getGLDrawable().invokeLater((gl) -> {
        getView().addLayer(layer);
        return true;
      });

      layer.addSelectionListener((ILcdSelectionListener) aSelectionEvent -> {
        if (aSelectionEvent.getSelection().getSelectionCount() == 1) {
          // Call the native method to pass the selected object to the native application.
          objectSelected(getNativePeer(), aSelectionEvent.selectedElements().nextElement());
        }
      });

      return layerId;
    } catch (ParseException e) {
      throw new IllegalStateException(e);
    }
  }


  /**
   * Removes a track layer from the view.
   *
   * @param aLayerId the identifier of the track layer to remove from the view.
   */
  public void removeTrackLayer(int aLayerId) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      fTrackLayers.remove(aLayerId);
      // View operations need to be executed on the paint thread.
      getView().getGLDrawable().invokeLater((gl) -> {
        getView().removeLayer(layer);
        return true;
      });
    }
  }

  /**
   * Adds a new track with the specified properties to the specified track layer.
   *
   * @param aLayerId the identifier of the track layer
   * @param aTrackId the identifier for the track
   * @param aX the X coordinate
   * @param aY the Y coordinate
   * @param aZ the Z coordinate
   * @param aCallSign the call sign
   * @param aTimeStamp the time stamp
   */
  public void addTrack(int aLayerId, int aTrackId, double aX, double aY, double aZ, String aCallSign, long aTimeStamp) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      ILcdModel model = layer.getModel();
      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
        TLcdDataObject track = new TLcdDataObject(TrackDataTypes.TRACK_PLAN_DATA_TYPE);
        ILcd3DEditablePoint location;
        if (model.getModelReference() instanceof ILcdGeodeticReference) {
          location = new TLcdLonLatHeightPoint(aX, aY, aZ) {
            @Override
            public String toString() {
              return getFormattedTrackLocation(this);
            }
          };
        } else {
          location = new TLcdXYZPoint(aX, aY, aZ) {
            @Override
            public String toString() {
              return getFormattedTrackLocation(this);
            }
          };
        }

        track.setValue(TrackDataTypes.ID, aTrackId);
        track.setValue(TrackDataTypes.LOCATION, location);
        track.setValue(TrackDataTypes.TIMESTAMP, aTimeStamp);
        track.setValue(TrackDataTypes.CALLSIGN, aCallSign);
        model.addElement(track, ILcdModel.FIRE_LATER);
      }
      model.fireCollectedModelChanges();
    }
  }
  
  /**
   * Start polygon recording.
   *
   * @param aLayerId the identifier of the track layer to remove from the view.
   */
  public void startRec(int ID) {
	  System.out.println("JAVA Start: " + ID);
	  rec_polygon_id = ID;
	  fMouseEventHandler.setDrawPolygonMode(true);
	  return;
  }
  
  /**
   * Stop polygon recording.
   *
   * @param aLayerId the identifier of the track layer to remove from the view.
   */
  public void stopRec(int ID) {
	  System.out.println("JAVA Stop: " + ID);
	  fMouseEventHandler.setDrawPolygonMode(false);
	  
	  JSONObject track;
		try {
			track = conection.getTrackbyID(ID);
			JSONObject datos_json = (JSONObject)track.get("datos_json");
			if(datos_json.get("puntos_extras") == null)
			{
				conection.initilizeDatosJSON(datos_json);
			}
			TLcd3DEditablePointList clicks_points = fMouseEventHandler.getPaintPoints();
			//int totalExtraPoints = conection.getTotalExtraPoints(track);
			conection.cleanAllExtraPoints(track);
			for(int p = 0; p < clicks_points.getPointCount();p++) {
			  ILcdPoint point = clicks_points.getPoint(p);
			  conection.insertExtraPoint(track, (float) point.getX(), (float)point.getY());
			}
			conection.updateTrack(track);
			fMouseEventHandler.clearPaintPoints();
			//removeTrackLayer(track_layer_draw_polygons);
			updatePolygon(track_layer_draw_polygons, polygon_user_clicks_track_id, fMouseEventHandler.getPaintPoints(), 0);
			create_polygon_user_clicks = false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  rec_polygon_id = -1;
	  return;
  }
  
  /**
   * Adds a new track with the specified properties to the specified track layer.
   *
   * @param aLayerId the identifier of the track layer
   * @param aTrackId the identifier for the track
   * @param aX the X coordinate
   * @param aY the Y coordinate
   * @param aZ the Z coordinate
   * @param aCallSign the call sign
   * @param aTimeStamp the time stamp
   */
  public void addPolygon(int aLayerId, int aTrackId, TLcd3DEditablePointList polygon, String aCallSign, long aTimeStamp) {
	    ILspLayer layer = fTrackLayers.get(aLayerId);
	    if (layer != null) {
	      ILcdModel model = layer.getModel();
	      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
	        TLcdDataObject track = new TLcdDataObject(TrackDataTypes.TRACK_PLAN_DATA_TYPE);
	        ILcd3DEditablePointList location;
	        if (model.getModelReference() instanceof ILcdGeodeticReference) {
	          //location = new TLcdLonLatHeightPolygon(generate3DRandomPolygon()) {
	          location = new TLcdLonLatHeightPolygon(polygon) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(this);
	            }
	          };
	        } else {
	          //location = new TLcdXYZPolygon(generate3DRandomPolygon()) {
	           location = new TLcdXYZPolygon(polygon) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(this);
	            }
	          };
	        }

	        track.setValue(TrackDataTypes.ID, aTrackId);
	        track.setValue(TrackDataTypes.LOCATION, location);
	        track.setValue(TrackDataTypes.TIMESTAMP, aTimeStamp);
	        track.setValue(TrackDataTypes.CALLSIGN, aCallSign);
	        model.addElement(track, ILcdModel.FIRE_LATER);
	        
	      }
	      catch(Exception e)
	      {
	    	  System.out.println(e);
	      }
	      model.fireCollectedModelChanges();
	    }
  }

  	protected String getFormattedTrackLocation(TLcdXYZPolypoint tLcdXYZPolypoint) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected String getFormattedTrackLocation(TLcdLonLatHeightPolypoint tLcdLonLatHeightPolypoint) {
		// TODO Auto-generated method stub
		return null;
	}

  private String getFormattedTrackLocation(ILcdPoint aTrack) {
    return String.format("Lon: %.3f Lat: %.3f Height: %.3f", aTrack.getX(), aTrack.getY(), aTrack.getZ());
  }

  /**
   * Updates an existing track with the specified properties.
   *
   * @param aLayerId the identifier of the track layer
   * @param aTrackId the identifier for the track
   * @param aX the new X coordinate
   * @param aY the new Y coordinate
   * @param aZ the new Z coordinate
   * @param aTimeStamp the new timestamp
   */
  public void updateTrack(int aLayerId, int aTrackId, double aX, double aY, double aZ, long aTimeStamp) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      ILcdDataObject track = getTrack(aLayerId, aTrackId);
      if (track != null) {
        ILcdModel model = layer.getModel();
        try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
          ((ILcd3DEditablePoint) track.getValue(TrackDataTypes.LOCATION)).move3D(aX, aY, aZ);
          track.setValue(TrackDataTypes.TIMESTAMP, aTimeStamp);
          model.elementChanged(track, ILcdModel.FIRE_LATER);
        }
        model.fireCollectedModelChanges();
      }
    }
  }
  
  public void updatePolygon(int aLayerId, int aTrackId, TLcd3DEditablePointList polygon, long aTimeStamp) {
	    ILspLayer layer = fTrackLayers.get(aLayerId);
	    if (layer != null) {
	      ILcdDataObject track = getTrack(aLayerId, aTrackId);
	      if (track != null) {
	        ILcdModel model = layer.getModel();
	        try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
	          //((ILcd3DEditablePoint) track.getValue(TrackDataTypes.LOCATION)).move3D(aX, aY, aZ);
	        	//((ILcd3DEditablePointList) track.getValue(TrackDataTypes.LOCATION)).getPointCount()
				ILcd3DEditablePointList location;
				if (model.getModelReference() instanceof ILcdGeodeticReference) {
				  location = new TLcdLonLatHeightPolygon(polygon) {
				    @Override
				    public String toString() {
				      return getFormattedTrackLocation(this);
				    }
				  };
				} else {
				   location = new TLcdXYZPolygon(polygon) {
				    @Override
				    public String toString() {
				      return getFormattedTrackLocation(this);
				    }
				  };
				}
	          track.setValue(TrackDataTypes.LOCATION, location);
	          track.setValue(TrackDataTypes.TIMESTAMP, aTimeStamp);
	          model.elementChanged(track, ILcdModel.FIRE_LATER);
	        }
	        model.fireCollectedModelChanges();
	      }
	    }
	  }

  /**
   * Removes a track from the specified track layer.
   *
   * @param aLayerId the identifier of the layer
   * @param aTrackId the identifier of the track
   */
  public void removeTrack(int aLayerId, int aTrackId) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      ILcdDataObject track = getTrack(aLayerId, aTrackId);
      if (track != null) {
        ILcdModel model = layer.getModel();
        try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
          model.removeElement(track, ILcdModel.FIRE_LATER);
        }
        model.fireCollectedModelChanges();
      }
    }
  }

  private ILcdDataObject getTrack(int aLayerId, int aTrackId) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      ILcdModel model = layer.getModel();
      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.readLock(model)) {
        Enumeration elements = model.elements();
        while (elements.hasMoreElements()) {
          ILcdDataObject track = (ILcdDataObject) elements.nextElement();
          if ((Integer) track.getValue(TrackDataTypes.ID) == aTrackId) {
            return track;
          }
        }
      }
    }
    return null;
  }
  
  private List<Integer> getAllTrackIds(int aLayerId) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    List<Integer> tracks_ids = new ArrayList<Integer>();
    if (layer != null) {
      ILcdModel model = layer.getModel();
      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.readLock(model)) {
        Enumeration elements = model.elements();
        while (elements.hasMoreElements()) {
          ILcdDataObject track = (ILcdDataObject) elements.nextElement();
          tracks_ids.add((Integer) track.getValue(TrackDataTypes.ID));
        }
      }
    }
    return tracks_ids;
  }

  /**
   * Native method to pass the selected data object to the native application.
   *
   * @param aNativePeer the pointer of the native object which should receive the selected object.
   * @param aDataObject the data object
   */
  public native void objectSelected(long aNativePeer, Object aDataObject);

  /**
   * This class builds the structural description of the track model, and providesread(Points)
   * static access to it. The method getDataModel() provides the full track data model.
   * The public constant TRACK_PLAN_DATA_TYPE refers to the only defined type of this model:
   * tracks.
   */
  private static class TrackDataTypes {

    // The data model for the tracks, fully describing the structure of the data.
    private static final TLcdDataModel TRACK_PLAN_DATA_MODEL;

    // The data model contains a single data type - the track data type.
    static final TLcdDataType TRACK_PLAN_DATA_TYPE;

    static final String ID = "id";
    static final String LOCATION = "location";
    static final String TIMESTAMP = "timestamp";
    static final String CALLSIGN = "callSign";

    static final String TRACK_TYPE = "TrackType"; //Starts with capital, same as Java class

    static {
      // Assign the constants
      TRACK_PLAN_DATA_MODEL = createDataModel();
      TRACK_PLAN_DATA_TYPE = TRACK_PLAN_DATA_MODEL.getDeclaredType(TRACK_TYPE);
    }

    private static TLcdDataModel createDataModel() {
      // Create the builder for the data model.
      // Use some unique name space, to prevent name clashes.  This isn't really needed
      // for the sample but might be useful when exposing it externally.
      TLcdDataModelBuilder builder = new TLcdDataModelBuilder(
          "http://www.mydomain.com/datamodel/TrackModel");

      TLcdDataTypeBuilder geometryType = builder.typeBuilder("GeometryType");
      geometryType.primitive(true).instanceClass(ILcd3DEditablePoint.class);

      // Define the types and their properties (only one type and one property here)
      TLcdDataTypeBuilder trackBuilder = builder.typeBuilder(TRACK_TYPE);
      trackBuilder.addProperty(ID, TLcdCoreDataTypes.INTEGER_TYPE);
      trackBuilder.addProperty(LOCATION, geometryType);
      trackBuilder.addProperty(TIMESTAMP, TLcdCoreDataTypes.LONG_TYPE);
      trackBuilder.addProperty(CALLSIGN, TLcdCoreDataTypes.STRING_TYPE);

      // Finalize the creation
      TLcdDataModel dataModel = builder.createDataModel();

      TLcdDataType type = dataModel.getDeclaredType(TRACK_TYPE);
      // make sure LuciadLightspeed finds the geometry
      type.addAnnotation(new TLcdHasGeometryAnnotation(type.getProperty(LOCATION)));

      // Annotation indicating which property should be used as primary key
      type.addAnnotation(new TLcdPrimaryKeyAnnotation(type.getProperty(ID)));

      return dataModel;
    }
  }

  private static class BalloonContentProvider implements ILcdBalloonContentProvider {

	  @Override
	    public boolean canGetContent(final ALcdBalloonDescriptor aBalloonDescriptor) {
	      return true;
	    }

	    @Override
	    public JComponent getContent(final ALcdBalloonDescriptor aBalloonDescriptor) {
	      if (aBalloonDescriptor instanceof TLcdModelElementBalloonDescriptor) {
	        Object object = aBalloonDescriptor.getObject();

	        if (object instanceof ILcdDataObject) {
	        	
	        	
	        	
	          DataObjectDisplayTree t = new DataObjectDisplayTree();
	          t.setDataObject((ILcdDataObject) object);
	          t.setDataModel(t.getDataModel());
	          
	          JScrollPane scroll = new JScrollPane();
	          scroll.setMinimumSize(new Dimension(150, 100));
	          scroll.setMaximumSize(new Dimension(250, 100));
	          
	          

	          
	          scroll.setViewportView(t);
	          //return scroll;
	          
	          JPanel jp_ballon = new JPanel();
	          jp_ballon.setLayout(new VerticalLayout());
	          jp_ballon.add(new JLabel("uno"));
	          jp_ballon.add(scroll);
	          
	          return jp_ballon;
	          
	          
	        }
	      }
	      return null;
	    }
  }
  
  /*
   * Point
   */
  public void clearTrackLayer(int aLayerId) 
  {
	if(aLayerId != 0 && fTrackLayers.size() > 0) {
		ILspLayer layer = fTrackLayers.get(aLayerId);
		if (layer != null) {
		  ILcdModel model = layer.getModel();
		  model.removeAllElements(ILcdModel.FIRE_NOW);
		}
	}
  }
  
  public void paintPoints() 
  {
	  try{
		  //clearTrackLayer(track_layer_points);
	      List<JSONObject> tracks_list = conection.getTracks();
	      //Lists to check poins to remove
	      List<Integer> all_tracks_ids_points = getAllTrackIds(track_layer_points);
	      List<Integer> tracks_ids_points_database = new ArrayList<Integer>();
	      //Lists to check polygons to remove
	      List<Integer> all_tracks_ids_polygon = getAllPolygonTrackLayersIds();
	      List<Integer> all_tracks_ids_polygon_database = new ArrayList<Integer>();
	      
	      for(int t=0; t < tracks_list.size(); t++ )
	      {
	              float point_x = (float) tracks_list.get(t).get("x_geoposicion");
	              float point_y = (float) tracks_list.get(t).get("y_geoposicion"); 
	
	              int type = (int)tracks_list.get(t).get("tipo_dato");
	              if(type == 1 || type == 2)
	              {
	            	  
	            	  ILcdDataObject track = getTrack(track_layer_points, (int) tracks_list.get(t).get("ID"));
	                  if (track == null) {
	                  	addTrack(track_layer_points, (int) tracks_list.get(t).get("ID"), point_x, point_y, 0, "TRACK", 0);
	                  }
	              	  else
	              	  {
	              		updateTrack(track_layer_points, (int) tracks_list.get(t).get("ID"), point_x, point_y, 0, 0);
	              	  }
	                  tracks_ids_points_database.add((int) tracks_list.get(t).get("ID"));
	              }
	              else if(type == 3)
	              {
	            	  int id_track = (int) tracks_list.get(t).get("ID");
	            	  //In recording mode the original polygon will'n be paint
	            	  if(rec_polygon_id != id_track)
	            	  {
		            	  TLcd3DEditablePointList track_points = new TLcd3DEditablePointList();
		            	  List<JSONObject> extra_points = conection.getExtraPoints((JSONObject)tracks_list.get(t));
		            	  for(int ep=0;ep < extra_points.size();ep++) {
		            		  JSONObject extra_point = extra_points.get(ep);
		            		  float lat = Float.parseFloat( extra_point.get("latitud").toString());
		            		  float lng = Float.parseFloat( extra_point.get("longitud").toString());
		            		  track_points.insert3DPoint(ep, new TLcdXYZPoint(lat, lng, 0));
		            	  }
		            	  
		            	  
		            	  JSONObject datos_json = (JSONObject) ((JSONObject)tracks_list.get(t)).get("datos_json");
		                  String color = datos_json.get("color").toString();
		                  if(color.equals("") || color.equals("0")) {
		                	  color = layerFactory.generateColorRandom();
		                  }
		                  //System.out.println("Color polygon: " + color);
		                  layerFactory.setFillColor(color);
		                  	
		                  if(isElementInPolygonTrackLayers(id_track) == false){
		                	  //Create layer polygon
		                	  int polygon_new_layerId = 0;
		                	  polygon_new_layerId = addTrackLayerPolygon("P" + id_track, "EPSG:4326");
			    			  addTrack(polygon_new_layerId, 115, 0, 0, 0, "Poligono " + id_track, 0);
			        	      addPolygon(polygon_new_layerId, id_track, track_points, "Polygono " + id_track, 0);
			        	      Map<String,Object> new_element = new HashMap<>();
			        	      new_element.put("aLayerId", polygon_new_layerId);
			        	      new_element.put("aLayerColor", color);
			        	      addPolygonTrackLayers(id_track, new_element);
		                  }
		                  else{
		                	//Update layer polygon
		                	  Map<String,Object> element = getPolygonTrackLayers(id_track);
		                	  int polygon_layerId = (int)element.get("aLayerId");
		                	  String pre_polygon_color= element.get("aLayerColor").toString();
			        	      if(!color.equals(pre_polygon_color)) {
			        	    	//Update style layer
				        	      /*TLcdVectorModel model_polygon = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor("", "SolidShapes", "Solid fill shapes"));
				        	      TLspLayerStyle aLayerStyle = layerFactory.getSolidFillLayer(model_polygon, false);
				        	      getView().getLayer(polygon_layerId).setLayerStyle(aLayerStyle);*/
			        	      }
			        	      updatePolygon(polygon_layerId, id_track, track_points, 0);
		                  }
		                  all_tracks_ids_polygon_database.add(id_track);
	
		        	      layerFactory.setDefautlsColor();
	            	  }
	              }
	      }
	      //Remove points 
	      List<Integer> track_ids_points_remove = all_tracks_ids_points.stream()
	    	        .filter(i -> !tracks_ids_points_database.contains(i))
	    	        .collect(Collectors.toList());
	      for (Integer track_id_point_remove : track_ids_points_remove) {
	    	  removeTrack(track_layer_points, track_id_point_remove);
	      }
	      //Remove polygons layers
	      List<Integer> track_ids_polygon_remove = all_tracks_ids_polygon.stream()
	    	        .filter(i -> !all_tracks_ids_polygon_database.contains(i))
	    	        .collect(Collectors.toList());
	      
	      
	      for (Integer track_id_polygon_remove : track_ids_polygon_remove) {
	    	  Map<String,Object> element = getPolygonTrackLayers(track_id_polygon_remove);
        	  int polygon_layerId = (int)element.get("aLayerId");
	    	  removeTrackLayer(polygon_layerId);
	    	  removePolygonTrackLayers(track_id_polygon_remove);
	      }
	  }
	  catch(Exception e) {
		  System.out.println("----- DATA BASE Exception -----");
		  System.out.println(e.getMessage());
	  }
  }
  
  public void paintPoint(int aLayerId, TLcdXYPoint point) 
  {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      ILcdModel model = layer.getModel();
      model.addElement( point, ILcdModel.FIRE_NOW);
    }
  }
  
//  public List<TLcdXYPoint> readPoints()
//  {
//	  List<TLcdXYPoint> list_point = new ArrayList<TLcdXYPoint>();
//	  try{
//    	  LuciadBDConnection conection = new LuciadBDConnection("localhost", "cmca", "root", "root");
//          List<JSONObject> tracks_list = conection.getTracks();
//          
//          for(int t=0; t < tracks_list.size(); t++ ) 
//          {
//        	  float point_x = (float) tracks_list.get(t).get("x_geoposicion");
//        	  float point_y = (float) tracks_list.get(t).get("y_geoposicion");
//        	  list_point.add(new TLcdXYPoint(point_x, point_y));
//        	  //System.out.println("-->"+point_x+","+point_y);
//          }
//          return list_point;
//      }
//      catch(Exception e) {
//    	  System.out.println("----- DATA BASE Exception -----");
//    	  System.out.println(e.getMessage());
////    	  int points_count = (int)(Math.random()*(7-3+1)+3);
////    	  int min_lat = 95;
////    	  int max_lat = 100;
////    	  int min_lng = 18;
////    	  int max_lng = 20;
////    	  for(int p = 0; p <= points_count;p++) {
////    		  double lat = (Math.random()*(max_lat-min_lat+1)+min_lat)*(-1);
////    		  double lng = Math.random()*(max_lng-min_lng+1)+min_lng;
////    		  list_point.add(new TLcdXYPoint(lat,lng));
////    	  }
////    	  return list_point;
//    	  return null;
//      }
//  }
  
  
  /*
   *Polygons
   * 
   * */
  public void paintPolygonUserClicks() {
	  if(track_layer_draw_polygons != 0 && fTrackLayers.size() > 0)
	  {
		    ILspLayer layer = fTrackLayers.get(track_layer_draw_polygons);
		    if (layer != null) {
		      ILcdModel model = layer.getModel();
		      TLcd3DEditablePointList clicks_points = fMouseEventHandler.getPaintPoints();
		      if(clicks_points.getPointCount() > 0)
		      {
				if(create_polygon_user_clicks == false) {
					addPolygon(track_layer_draw_polygons, polygon_user_clicks_track_id,  clicks_points, "Polygono", 0);
					create_polygon_user_clicks = true;
				}
				else  
				{
					updatePolygon(track_layer_draw_polygons, polygon_user_clicks_track_id, clicks_points, 0);
				}
		      }
		      
	//	      ILcdPoint last_point = polygon.getEndPoint();
	//	      Point p_last_point = new Point();
	//	      p_last_point.setLocation(last_point.getX(),last_point.getY());
	//	      navigationControls.setLocation(p_last_point);
	//	      scaleIndicator.setScaleAtCenterOfMap(true);
		      //TLcdXYPolygon
		    }
	  }
  }
  
  private TLcd2DEditablePointList generateRandomPolygon() {
	  TLcd2DEditablePointList point_list_2d = new TLcd2DEditablePointList();
	  int points_count = (int)(Math.random()*(7-3+1)+3);
	  int min_lat = 95;
	  int max_lat = 100;
	  int min_lng = 18;
	  int max_lng = 20;
	  for(int p = 0; p <= points_count;p++) {
		  double lat = (Math.random()*(max_lat-min_lat+1)+min_lat)*(-1);
		  double lng = Math.random()*(max_lng-min_lng+1)+min_lng;
		  point_list_2d.insert2DPoint(p, new TLcdXYPoint(lat,lng));
	  }
	  return point_list_2d;
  }
  
  private TLcd3DEditablePointList generate3DRandomPolygon() {
	  TLcd3DEditablePointList point_list_3d = new TLcd3DEditablePointList();
	  int points_count = (int)(Math.random()*(7-3+1)+3);
	  int min_lat = 95;
	  int max_lat = 100;
	  int min_lng = 18;
	  int max_lng = 20;
	  for(int p = 0; p <= points_count;p++) {
		  double lat = (Math.random()*(max_lat-min_lat+1)+min_lat)*(-1);
		  double lng = Math.random()*(max_lng-min_lng+1)+min_lng;
		  point_list_3d.insert3DPoint(p, new TLcdXYZPoint(lat,lng,0));
	  }
	  return point_list_3d;
  }
  
  private Object createXYPolygon() {  
    // create a geodetic polygon object from the previously created ILcd2DEditablePointList
    // on the given ellipsoid aEllipsoid
    return new TLcdXYPolygon(generateRandomPolygon());
  }
  
  private Object createXYPolygon(TLcd2DEditablePointList points) {  
    // create a geodetic polygon object from the previously created ILcd2DEditablePointList
    // on the given ellipsoid aEllipsoid
    return new TLcdXYPolygon(points);
  }
  
}
