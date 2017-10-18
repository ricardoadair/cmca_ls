//package samples.lightspeed.integration.jni;

import static com.luciad.view.lightspeed.util.TLspViewTransformationUtil.setup2DView;
import static com.luciad.view.lightspeed.util.TLspViewTransformationUtil.setup3DView;

import java.awt.Color;

//import static samples.lightspeed.common.FitUtil.fitOnLayers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.datamodel.TLcdCoreDataTypes;
import com.luciad.datamodel.TLcdDataModel;
import com.luciad.datamodel.TLcdDataModelBuilder;
import com.luciad.datamodel.TLcdDataObject;
import com.luciad.datamodel.TLcdDataType;
import com.luciad.datamodel.TLcdDataTypeBuilder;
import com.luciad.format.database.TLcdPrimaryKeyAnnotation;
import com.luciad.format.magneticnorth.ILcdMagneticNorthMap;
import com.luciad.geodesy.ILcdEllipsoid;
import com.luciad.geodesy.TLcdEllipsoid;
import com.luciad.geodesy.TLcdGeodeticDatum;
import com.luciad.gui.TLcdAWTUtil;
import com.luciad.gui.swing.TLcdOverlayLayout;
import com.luciad.gui.swing.navigationcontrols.ALcdCompassNavigationControl;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelReference;
import com.luciad.model.TLcd2DBoundsIndexedModel;
import com.luciad.model.TLcdModelDescriptor;
import com.luciad.model.TLcdVectorModel;
import com.luciad.reference.ILcdGeoReference;
import com.luciad.reference.ILcdGeocentricReference;
import com.luciad.reference.ILcdGeodeticReference;
import com.luciad.reference.TLcdGeodeticReference;
import com.luciad.reference.TLcdGridReference;
import com.luciad.reference.format.TLcdEPSGReferenceParser;
import com.luciad.shape.ILcdPoint;
import com.luciad.shape.shape2D.ILcd2DEditablePoint;
import com.luciad.shape.shape2D.TLcd2DEditablePointList;
import com.luciad.shape.shape2D.TLcdLonLatBounds;
import com.luciad.shape.shape2D.TLcdLonLatCircle;
import com.luciad.shape.shape2D.TLcdLonLatPoint;
import com.luciad.shape.shape2D.TLcdLonLatPolygon;
import com.luciad.shape.shape2D.TLcdXYCircle;
import com.luciad.shape.shape2D.TLcdXYPoint;
import com.luciad.shape.shape2D.TLcdXYPolygon;
import com.luciad.shape.shape3D.ILcd3DEditablePoint;
import com.luciad.shape.shape3D.ILcd3DEditablePointList;
import com.luciad.shape.shape3D.TLcd3DEditablePointList;
import com.luciad.shape.shape3D.TLcdLonLatHeightLine;
import com.luciad.shape.shape3D.TLcdLonLatHeightPoint;
import com.luciad.shape.shape3D.TLcdLonLatHeightPolygon;
import com.luciad.shape.shape3D.TLcdLonLatHeightPolyline;
import com.luciad.shape.shape3D.TLcdLonLatHeightPolypoint;
import com.luciad.shape.shape3D.TLcdXYZLine;
import com.luciad.shape.shape3D.TLcdXYZPoint;
import com.luciad.shape.shape3D.TLcdXYZPolygon;
import com.luciad.shape.shape3D.TLcdXYZPolyline;
import com.luciad.shape.shape3D.TLcdXYZPolypoint;
import com.luciad.tea.ALcdTerrainElevationProvider;
import com.luciad.tea.TLcdFixedLevelBasedRasterElevationProvider;
import com.luciad.tea.TLcdHeightProviderAdapter;
import com.luciad.transformation.ILcdModelXYWorldTransformation;
import com.luciad.transformation.ILcdModelXYZWorldTransformation;
import com.luciad.transformation.TLcdDefaultModelXYZWorldTransformation;
import com.luciad.transformation.TLcdGeoReference2GeoReference;
import com.luciad.util.ILcdFireEventMode;
import com.luciad.util.ILcdSelectionListener;
import com.luciad.util.TLcdConstant;
import com.luciad.util.TLcdHasGeometryAnnotation;
import com.luciad.util.TLcdOutOfBoundsException;
import com.luciad.util.concurrent.TLcdLockUtil;
import com.luciad.util.height.ILcdHeightProvider;
import com.luciad.util.measure.ILcdLayerMeasureProviderFactory;
import com.luciad.util.measure.ILcdModelMeasureProviderFactory;
import com.luciad.view.ILcdXYZWorldReference;
import com.luciad.view.lightspeed.ILspView;
import com.luciad.view.lightspeed.TLspContext;
import com.luciad.view.lightspeed.camera.ALspViewXYZWorldTransformation;
import com.luciad.view.lightspeed.camera.TLspViewXYZWorldTransformation2D;
import com.luciad.view.lightspeed.camera.TLspViewXYZWorldTransformation3D;
import com.luciad.view.lightspeed.controller.ILspController;
import com.luciad.view.lightspeed.controller.ruler.TLspRulerController;
import com.luciad.view.lightspeed.controller.ruler.TLspRulerController.MeasureMode;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.layer.TLspCompositeLayerFactory;
import com.luciad.view.lightspeed.layer.TLspPaintState;
import com.luciad.view.lightspeed.layer.shape.TLspShapeLayerBuilder;
import com.luciad.view.lightspeed.layer.style.TLspLayerStyle;
import com.luciad.view.lightspeed.painter.grid.TLspLonLatGridLayerBuilder;
import com.luciad.view.lightspeed.painter.label.style.TLspDataObjectLabelTextProviderStyle;
import com.luciad.view.lightspeed.services.effects.TLspAmbientLight;
import com.luciad.view.lightspeed.services.effects.TLspHeadLight;
import com.luciad.view.lightspeed.style.TLspLineStyle;
import com.luciad.view.lightspeed.style.TLspTextStyle;
import com.luciad.view.lightspeed.style.complexstroke.ALspComplexStroke.PolylineBuilder;
import com.luciad.view.lightspeed.swing.TLspBalloonManager;
import com.luciad.view.lightspeed.swing.TLspScaleIndicator;
import com.luciad.view.lightspeed.swing.navigationcontrols.TLspCompassNavigationControl;
import com.luciad.view.lightspeed.swing.navigationcontrols.TLspNavigationControlsBuilder;
import com.luciad.view.lightspeed.util.TLspViewNavigationUtil;
import com.luciad.view.swing.ALcdBalloonDescriptor;
import com.luciad.view.swing.ILcdBalloonContentProvider;
import com.luciad.view.swing.TLcdModelElementBalloonDescriptor;

import org.apache.batik.util.gui.LanguageDialog.Panel;
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
  ScheduledExecutorService timer;
  ScheduledExecutorService timer_delete;
  
  //Distance
  JPanel distance_panel;
  JCheckBox terrainModeCheckbox;
  //Menu
  JPanel menu_panel;
  JCheckBox distance_ac_Checkbox;
  JCheckBox distance_flir_Checkbox;
  JCheckBox distance_select_track_Checkbox;
  JCheckBox distance_free_Checkbox;
  //String file_med = "Data/Dted/Alps/dmed";
  String file_med = "";
  private TerrainRulerController fTerrainRulerController = new TerrainRulerController(createNavigationController());
  ILspLayer dtedLayer = null;
  boolean load_file_med = false;
  private boolean distance_mode = false;
  private boolean distance_mode_manual = false;
  private static final TLcdLonLatPoint FIRST_POINT = new TLcdLonLatPoint(10, 45);
  int track_layer_distance = 0;
  
  //Layers
  int mission_ID = 0;
  private final Map<Integer, Map<String, Object >> fPolygonTrackLayers;
  LayerFactory layerFactory = new LayerFactory();
  int track_layer_points_tracks = 0;
  int track_layer_points_marks = 0;
  int rangos_armas_layer_id = 0;
  int track_layer_draw_polygons = 0;
  private boolean create_polygon_user_clicks = false;
  int polygon_user_clicks_track_id = -1;
  int rec_polygon_id = -1;
  DecimalFormat df = new DecimalFormat("#.0000");
  DecimalFormat df_line = new DecimalFormat("#.00"); 
  //AC
  TLcdLonLatHeightPoint ac_point = null;
  int track_layer_ac_id = 0;
  int track_ac_id = -2;
  int track_distance_ac_id = -4;
  String ac = "0,0";
  //FLIR
  TLcdLonLatHeightPoint flir_point = null;
  int track_layer_flir_id = 0;
  int track_flir_id = -3;
  int track_distance_flir_id = -5;
  String flir = "0,0";
  //SelectTrack
  static boolean isSelectAnyElement = false;
  static TLcdLonLatHeightPoint select_track_point = null;
  int track_distance_select_track_id = -6;
  
  
  //Icons Paths
  String base_path = "";
  //String base_path = "/home/ricardoadair/CMCA/git/git_cmca/LVC_CMCA/";
  String icon_folder_path = "data/Iconos/";
  String track_icon_name = "Track.png";
  String marca_icon_name = "Marca.png";
  String ac_icon_name = "AC.png";
  String flir_icon_name = "Pos_Camera.png";
  boolean update_styles = false;
  String track_icon_path = "";
  String marck_icon_path = "";
  String ac_icon_path = "";
  String flir_icon_path = "";

  public SampleApplicationProxy1(long aNativePeer) {
    super(aNativePeer);

    getView().getServices().getGraphicsEffects().add(new TLspAmbientLight());
    getView().getServices().getGraphicsEffects().add(new TLspHeadLight(getView()));

    fAtomicInteger = new AtomicInteger();
    fTrackLayers = new HashMap<>();
    fPolygonTrackLayers = new HashMap<>();
    update_icons_path();

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
      
//      final Component compass;
//      try {
//        compass = new TLspCompassNavigationControl(
//        		( base_path.endsWith("/") ? base_path : base_path + "/" ) + 
//    			( icon_folder_path.endsWith("/") ? icon_folder_path : icon_folder_path + "/" ), getView());
//        overlay.add(compass);
//        layout.putConstraint(compass, TLcdOverlayLayout.Location.NORTH_WEST, TLcdOverlayLayout.ResolveClash.VERTICAL);
//      } catch (IOException ignored) {
//      }
     
      

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
      //Laber for coords
      coords = new JLabel("");
      coords.setBackground(Color.LIGHT_GRAY);
      coords.setForeground(Color.BLACK);
      coords.setOpaque(true);
      overlay.add(coords);
      layout.putConstraint(coords, TLcdOverlayLayout.Location.NORTH, TLcdOverlayLayout.ResolveClash.VERTICAL);
      
      PopupMenu jpop = new PopupMenu();
      jpop.add("uno");
      overlay.add(jpop);
      
      //Panel to distance
      distance_panel = buildMeasureModePanel();
      distance_panel.setVisible(false);
      overlay.add(distance_panel);
      layout.putConstraint(distance_panel, TLcdOverlayLayout.Location.NORTH_WEST, TLcdOverlayLayout.ResolveClash.VERTICAL);
      
      //Panel to distance
      menu_panel = buildMenuPanel();
      overlay.add(menu_panel);
      layout.putConstraint(menu_panel, TLcdOverlayLayout.Location.NORTH_WEST, TLcdOverlayLayout.ResolveClash.VERTICAL);
      
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
      
      //Cheking opacity
      
	  //prueba_opacity_layer_id = addTrackLayerPolygon("2", "EPSG:4326");
      //addPolygon(prueba_opacity_layer_id, 9999, generate3DRandomPolygon(), "Polygono " + 9999, 0);
	  /*prueba_opacity_layer_id = addTrackLayer("2", "EPSG:4326");
	  addTrack(prueba_opacity_layer_id, 1434, -80, 15, 0, "Poligono Prueba opacity", 0);*/
      
      //prueba_opacity_layer_id = addTrackLayerLine("prueba line", "EPSG:4326");
      //addCircle(prueba_opacity_layer_id, 999, generateRandomPoint(), "Distance AC", 0, 1000);
      
    });

    getView().addLayer(TLspLonLatGridLayerBuilder.newBuilder().build());
    navigationUtil = new TLspViewNavigationUtil(getView());
    
    timer = Executors.newSingleThreadScheduledExecutor();
    timer_delete = Executors.newSingleThreadScheduledExecutor();
	timer.scheduleAtFixedRate(update_points, 1, 1, TimeUnit.SECONDS);
    
	timer.scheduleAtFixedRate(update, 200, 200, TimeUnit.MILLISECONDS);
	
	//timer.scheduleAtFixedRate(deleteExtraIconFiles, 1, 5, TimeUnit.SECONDS);
	
	
	//timer.scheduleAtFixedRate(update_all_layers_opacity, 10, 5, TimeUnit.SECONDS);
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
		  if(mission_ID!=0)
		  {
			  update_map();
		  }
	  }
	};
	
	final Runnable update= new Runnable() {
	  public void run() {
		  if(mission_ID!=0)
		  {
			  //Update Coords
			  updateCoords();
			  //Update polygon user clicks
			  if(track_layer_draw_polygons != 0)
			  {
				  draw_polygon_user_clicks();
			  }
			  else {
				  track_layer_draw_polygons = addTrackLayerPolygon("1", "EPSG:4326");
			  }
			  if(distance_mode_manual) 
			  {
				  paintDistanceLines();
			  }
		  }
	  }
	};
	
	final Runnable deleteExtraIconFiles= new Runnable() {
	  public void run() {
		  delete_files();
	  }
	};
	
	private JPanel buildMeasureModePanel() {
	    JRadioButton geodeticMeasureModeRadioButton = new JRadioButton( "Geodésica" );
	    geodeticMeasureModeRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        fTerrainRulerController.setMeasureMode(MeasureMode.MEASURE_GEODETIC);
	      }
	    });

	    JRadioButton rhumbLineMeasureModeRadioButton = new JRadioButton( "Acimut" );
	    rhumbLineMeasureModeRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        fTerrainRulerController.setMeasureMode(MeasureMode.MEASURE_RHUMB);
	      }
	    });
	    geodeticMeasureModeRadioButton.setSelected(true);

	    terrainModeCheckbox = new JCheckBox( "Sobre terreno" );
	    terrainModeCheckbox.setSelected(false);
	    terrainModeCheckbox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean useTerrain = e.getStateChange() == ItemEvent.SELECTED;
	        fTerrainRulerController.setUseTerrain(useTerrain);
	      }
	    });
	    fTerrainRulerController.setUseTerrain(false);
	    
	    ButtonGroup measureModesGroup = new ButtonGroup();
	    measureModesGroup.add(geodeticMeasureModeRadioButton);
	    measureModesGroup.add(rhumbLineMeasureModeRadioButton);

	    JPanel measureModePanel = new JPanel( new GridLayout( 3, 1 ) );
	    measureModePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
	    measureModePanel.add(geodeticMeasureModeRadioButton);
	    measureModePanel.add(rhumbLineMeasureModeRadioButton);
	    measureModePanel.add(terrainModeCheckbox);

	    //return TitledPanel.createTitledPanel( "Measure mode", measureModePanel );
	    return measureModePanel;
	  }
	
	private JPanel buildMenuPanel() {
	    
	    distance_ac_Checkbox = new JCheckBox( "Distancia AC" );
		distance_ac_Checkbox.setSelected(false);
		distance_ac_Checkbox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean distance_ac = e.getStateChange() == ItemEvent.SELECTED;
	        if(distance_ac)
	        {
	        	distance_flir_Checkbox.setSelected(false);
				distance_select_track_Checkbox.setSelected(false);
				distance_free_Checkbox.setSelected(false);
				stopDistanceMode();
	        }
	        distanceTo();
	      }
	    });
		distance_flir_Checkbox = new JCheckBox( "Distancia FLIR" );
		distance_flir_Checkbox.setSelected(false);
		distance_flir_Checkbox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean distance_flir = e.getStateChange() == ItemEvent.SELECTED;
	        if(distance_flir)
	        {
	        	distance_ac_Checkbox.setSelected(false);
				distance_select_track_Checkbox.setSelected(false);
				distance_free_Checkbox.setSelected(false);
				stopDistanceMode();
	        }
	        distanceTo();
	      }
	    });
		distance_select_track_Checkbox = new JCheckBox( "Distancia Track seleccionado" );
		distance_select_track_Checkbox.setSelected(false);
		distance_select_track_Checkbox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean distance_select_track = e.getStateChange() == ItemEvent.SELECTED;
	        if(distance_select_track)
	        {
        		distance_ac_Checkbox.setSelected(false);
        		distance_flir_Checkbox.setSelected(false);
        		distance_free_Checkbox.setSelected(false);
        		stopDistanceMode();
	        }
	        distanceTo();
	      }
	    });
		
		distance_free_Checkbox = new JCheckBox( "Distancia libre" );
		distance_free_Checkbox.setSelected(false);
		distance_free_Checkbox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean free_select_track = e.getStateChange() == ItemEvent.SELECTED;
	        if(free_select_track)
	        {
	        	distance_ac_Checkbox.setSelected(false);
        		distance_flir_Checkbox.setSelected(false);
        		distance_select_track_Checkbox.setSelected(false);
        		startDistanceMode();
	        }
	        else {
	        	stopDistanceMode();
	        }
	      }
	    });
	    
	    //ButtonGroup measureModesGroup = new ButtonGroup();
	    //measureModesGroup.add(distance_ac_button);
	    //measureModesGroup.add(distance_flir_button);
	    //measureModesGroup.add(distance_ac_Checkbox);
	    //measureModesGroup.add(distance_flir_Checkbox);
		//measureModesGroup.add(distance_select_track_Checkbox);

	    JPanel menuPanel = new JPanel( new GridLayout( 4, 1 ) );
	    menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
	    menuPanel.add(distance_ac_Checkbox);
	    menuPanel.add(distance_flir_Checkbox);
	    menuPanel.add(distance_select_track_Checkbox);
	    menuPanel.add(distance_free_Checkbox);
	    return menuPanel;
	  }
	
	public void centerMap(double lat, double lng) 
	{
		TLcdLonLatPoint mouse = new TLcdLonLatPoint(lat,lng);
		try {
			//navigationUtil.center(mouse,new TLcdGeodeticReference());
			navigationUtil.animatedCenter(mouse,new TLcdGeodeticReference());
		} catch (TLcdOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//north();
	}
	
	public void north() {
//		TLcdLonLatPoint north = new TLcdLonLatPoint(90,0);
//		navigationUtil.rotateTo(north, 0, 0);
		
		 if (getView().getViewXYZWorldTransformation() instanceof TLspViewXYZWorldTransformation3D) {
		      TLspViewXYZWorldTransformation3D aTargetSFCT = (TLspViewXYZWorldTransformation3D) getView().getViewXYZWorldTransformation();
		      double clampedYaw = 0;
		      //aTargetSFCT.lookAt(aTargetSFCT.getReferencePoint(), aTargetSFCT.getDistance(), clampedYaw, aTargetSFCT.getPitch(), aTargetSFCT.getRoll());
		      navigationUtil.rotateTo(aTargetSFCT.getReferencePoint(),clampedYaw, aTargetSFCT.getPitch());
		 }
//		 else if(getView().getViewXYZWorldTransformation() instanceof TLspViewXYZWorldTransformation2D) {
//			TLspViewXYZWorldTransformation2D aTargetSFCT = (TLspViewXYZWorldTransformation2D) getView().getViewXYZWorldTransformation();
//		    // correct the rotation using difference in angle
//		    double clampedRotation = aTargetSFCT.getRotation();
//		      double angleToNorth = viewAzimuth(getView(), aTargetSFCT, null);
//		      if (!Double.isNaN(angleToNorth)) {
//		        clampedRotation += angleToNorth;
//		        while (clampedRotation > 360.0) {
//		          clampedRotation -= 360.0;
//		        }
//		      }
//		    // Use the clamped values
//		    aTargetSFCT.lookAt(aTargetSFCT.getWorldOrigin(), aTargetSFCT.getViewOrigin(), aTargetSFCT.getScaleX(), aTargetSFCT.getScaleY(), clampedRotation);
//		 }
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
	    //if (layer != null) {
	    	System.out.println( "newOpacity" + newOpacity );
	    	ILspLayer aLayer = getView().getLayer(aLayerId);
	    	aLayer.setLayerStyle(aLayer.getLayerStyle().asBuilder().opacity(newOpacity).build());
	    	
	    	 TLspLayerStyle layerStyle = aLayer.getLayerStyle();
	    	    aLayer.setLayerStyle(createNightBackgroundLayerStyle().asBuilder().opacity(layerStyle.getOpacity()-0.1f).build());
	    	    ILcdModel model = aLayer.getModel();
	    	    model.fireCollectedModelChanges();
	    	
	    	//fBackgroundLayer.setLayerStyle(fCurrentBackgroundStyle.asBuilder().opacity(layerStyle.getOpacity()).build());
	    	//layer.setLayerStyle(createNightBackgroundLayerStyle());
	    	
//	    	ILcdModel model = layer.getModel();
//	        try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
//	          model.elementChanged(layer, ILcdModel.FIRE_LATER);
//	        }
//	        model.fireCollectedModelChanges();
	   // }
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
	
	public void setBasePath(String new_base_path) {
		base_path = new_base_path;
		update_icons_path();
	}
	
	public void update_icons_path() {
		track_icon_path = (
			( base_path.endsWith("/") ? base_path : base_path + "/" ) + 
			( icon_folder_path.endsWith("/") ? icon_folder_path : icon_folder_path + "/" ) +
			track_icon_name
		);
		marck_icon_path = (
			( base_path.endsWith("/") ? base_path : base_path + "/" )+ 
			( icon_folder_path.endsWith("/") ? icon_folder_path : icon_folder_path + "/" ) +
			marca_icon_name
		);
		ac_icon_path = (
			( base_path.endsWith("/") ? base_path : base_path + "/" )+ 
			( icon_folder_path.endsWith("/") ? icon_folder_path : icon_folder_path + "/" ) +
			ac_icon_name
		);
		flir_icon_path = (
			( base_path.endsWith("/") ? base_path : base_path + "/" )+ 
			( icon_folder_path.endsWith("/") ? icon_folder_path : icon_folder_path + "/" ) +
			flir_icon_name
		);
	}
	
	public void updateAllIconStyles()
	{
		update_styles = true;
		update_icons_path();
		System.out.println("updateAllIconStyles");
		updateTracksIconStyle();
		updateMarcksIconStyle();
		updateAcIconStyle();
		updateFlirIconStyle();
		update_styles = false;
		
		timer_delete.schedule(deleteExtraIconFiles, 2, TimeUnit.SECONDS);
		//timer_delete.scheduleAtFixedRate(deleteExtraIconFiles, 5, 5, TimeUnit.SECONDS);

	}
	
	public void FileCopy(String sourceFile, String destinationFile) {
		//System.out.println("Desde: " + sourceFile);
		//System.out.println("Hacia: " + destinationFile);

		try {
			File inFile = new File(sourceFile);
			File outFile = new File(destinationFile);

			FileInputStream in = new FileInputStream(inFile);
			FileOutputStream out = new FileOutputStream(outFile);

			int c;
			while( (c = in.read() ) != -1)
				out.write(c);

			in.close();
			out.close();
		} catch(IOException e) {
			System.err.println(e);
		}
	}
	
	/*JPanel panel_icon = new JPanel();
	 Container overlay = getView().getOverlayComponent();
	 TLcdOverlayLayout layout = (TLcdOverlayLayout) overlay.getLayout();
	 JLabel img = new JLabel(" "); 
	 ImageIcon image = new ImageIcon(track_icon_path);
	 panel_icon.add(img);
	 img.setIcon(image); 
	 img.setSize(100,100); 
	 img.setLocation(0,0); 
	 img.setVisible(true); 
	 overlay.add(panel_icon);
	 layout.putConstraint(panel_icon, TLcdOverlayLayout.Location.WEST, TLcdOverlayLayout.ResolveClash.VERTICAL);*/
	//addTrack(track_layer_points_tracks, 98243, -100, 22, 10, "ada", 0);
	
	List<String> files_to_delete = new ArrayList<String>();
	public void delete_files() {
		if(files_to_delete.size() > 0)
		{
			for (String file : files_to_delete) {
				File fichero = new File(file);
			    fichero.delete();
			}
			files_to_delete.clear();
		}
	}
	
	public void updateTracksIconStyle() 
	{
		//Tracks
		int id_layer = track_layer_points_tracks;
		String icon_path = track_icon_path;
		
		removeTrackLayer(id_layer);
		String new_icon_path = icon_path.substring(0, icon_path.lastIndexOf("."))+ "_new" + System.nanoTime() + icon_path.substring(icon_path.lastIndexOf("."));
		System.out.println("updateTracksIconStyle " + new_icon_path);
		FileCopy(icon_path, new_icon_path);
		layerFactory.setIconPath(new_icon_path);
		id_layer = addTrackLayerPointWithIcon("Traks layer", "EPSG:4326");
		files_to_delete.add(new_icon_path);
		
		track_layer_points_tracks = id_layer;
	}
	
	public void updateMarcksIconStyle() 
	{
		//Marks
		/*removeTrackLayer(track_layer_points_marks);
		System.out.println("updateMarcksIconStyle "+marck_icon_path);
		layerFactory.setIconPath(marck_icon_path);
		track_layer_points_marks = addTrackLayerPointWithIcon("Marks layer", "EPSG:4326");*/
		
		int id_layer = track_layer_points_marks;
		String icon_path = marck_icon_path;
		
		removeTrackLayer(id_layer);
		String new_icon_path = icon_path.substring(0, icon_path.lastIndexOf("."))+ "_new" + System.nanoTime() + icon_path.substring(icon_path.lastIndexOf("."));
		System.out.println("updateMarcksIconStyle " + new_icon_path);
		FileCopy(icon_path, new_icon_path);
		layerFactory.setIconPath(new_icon_path);
		id_layer = addTrackLayerPointWithIcon("Marks layer", "EPSG:4326");
		files_to_delete.add(new_icon_path);
		
		track_layer_points_marks = id_layer;
	}
	
	public void updateAcIconStyle() 
	{
		//AC
		/*removeTrackLayer(track_layer_ac_id);
		System.out.println("updateAcIconStyle "+ac_icon_path);
		layerFactory.setIconPath(ac_icon_path);
		track_layer_ac_id = addTrackLayerPointWithIcon("AC layer", "EPSG:4326");*/
		
		int id_layer = track_layer_ac_id;
		String icon_path = ac_icon_path;
		
		removeTrackLayer(id_layer);
		String new_icon_path = icon_path.substring(0, icon_path.lastIndexOf("."))+ "_new" + System.nanoTime() + icon_path.substring(icon_path.lastIndexOf("."));
		System.out.println("updateAcIconStyle " + new_icon_path);
		FileCopy(icon_path, new_icon_path);
		layerFactory.setIconPath(new_icon_path);
		id_layer = addTrackLayerPointWithIcon("AC layer", "EPSG:4326");
		files_to_delete.add(new_icon_path);
		
		track_layer_ac_id = id_layer;
				
	}
	
	public void updateFlirIconStyle() 
	{
		//FLIR
		/*removeTrackLayer(track_layer_flir_id);
		System.out.println("updateFlirIconStyle "+flir_icon_path);
		layerFactory.setIconPath(flir_icon_path);
		track_layer_flir_id = addTrackLayerPointWithIcon("Flir layer", "EPSG:4326");*/
		
		int id_layer = track_layer_flir_id;
		String icon_path = flir_icon_path;
		
		removeTrackLayer(id_layer);
		String new_icon_path = icon_path.substring(0, icon_path.lastIndexOf("."))+ "_new" + System.nanoTime() + icon_path.substring(icon_path.lastIndexOf("."));
		System.out.println("updateFlirIconStyle " + new_icon_path);
		FileCopy(icon_path, new_icon_path);
		layerFactory.setIconPath(new_icon_path);
		id_layer = addTrackLayerPointWithIcon("Flir layer", "EPSG:4326");
		files_to_delete.add(new_icon_path);
		
		track_layer_flir_id = id_layer;
		
	}
	
	public void setAc(String new_ac)
	{
		ac = new_ac;
		if(!ac.equals("") && ac.contains(",")) {
			String[] split = ac.split(",");
			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);
			double z = split.length > 2 ? Double.parseDouble(split[2]) *1000 : 0.0; //m.
			ac_point = new TLcdLonLatHeightPoint(x,y,z);
			//System.out.println(ac_point);
		}
	}
	
	public void setFlir(String new_flir)
	{
		flir = new_flir;
		if(!flir.equals("") && flir.contains(",")) {
			String[] split = flir.split(",");
			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);
			double z = split.length > 2 ? Double.parseDouble(split[2]) *1000 : 0.0; //m.
			flir_point = new TLcdLonLatHeightPoint(x,y,z);
			//System.out.println(flir_point);
		}
	}
	
	public void setFileElevationInformation(String new_file_name)
	{
		file_med = new_file_name;
	}
	
	public String getFileElevationInformation()
	{
		return file_med;
	}
	
	public void update_map()
	{
		if( update_styles == false)
		{
			  if(track_layer_points_tracks == 0 && track_layer_points_marks == 0) 
			  {
				  create_points();
			  }
			  else
			  {
				  update_points();
			  }
		}
	}
		
	public void create_points() {
		//Tracks
		layerFactory.setIconPath(track_icon_path);
		track_layer_points_tracks = addTrackLayerPointWithIcon("Traks layer", "EPSG:4326");
		//Marks
		layerFactory.setIconPath(marck_icon_path);
		track_layer_points_marks = addTrackLayerPointWithIcon("Marks layer", "EPSG:4326");
		layerFactory.setTextColor("#f45c42");
		//AC
		layerFactory.setIconPath(ac_icon_path);
		track_layer_ac_id = addTrackLayerPointWithIcon("AC layer", "EPSG:4326");
		//FLIR
		layerFactory.setIconPath(flir_icon_path);
		track_layer_flir_id = addTrackLayerPointWithIcon("Flir layer", "EPSG:4326");
		//Rangos de arma
		layerFactory.setTextColor("#0036F5");
		layerFactory.setLineColor("#2C4DC9");
		layerFactory.setHaloColor("#ffffff");
		rangos_armas_layer_id = addTrackLayerLine("Rangos de arma", "EPSG:4326");
		layerFactory.setDefautlsColor();
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
      //Distance Layer
      if(aSource.endsWith(".img")) {
		  dtedLayer = LspDataUtil
	          .instance()
	          .model(aSource)
	          .layer()
	          .addToView(getView())
	          .getLayer();
		  load_file_med = true;
      }
      else
      {
    	  ILspLayer layer = LspDataUtil
	          .instance()
	          .model(aSource)
	          .layer()
	          .addToView(getView())
	          .getLayer();
      	//fitOnLayers(getView().getOverlayComponent(), getView(), true, layer);
      }
    } catch (Exception aE) {
      aE.printStackTrace();
    }
  }
  
  public void setVisibleLayer(int visible,String aSource)
	{
	   System.out.println("JAVAVIS: " + aSource + ": " + visible);
	
		boolean b_vis=false;
		if (visible==1)
			b_vis=true;
	    
	    for (int i=0;i<getView().layerCount();i++)
	    {
		   String act_label = getView().getLayer(i).getLabel();
		   if (aSource.toLowerCase().replace("_", " ").contains(act_label.toLowerCase())) {
			   getView().getLayer(i).setVisible(b_vis);
		   }
	    }
	}

  public void reSortLayer(int pos,String aSource) {
	   System.out.println("JAVA: " + aSource + ": " + pos);
	   
	   for (int i=0;i<getView().layerCount();i++)
	   {
		   String act_label = getView().getLayer(i).getLabel();
		   //System.out.println("\t" + getView().getLayer(i).getLabel());
		   if (aSource.toLowerCase().replace("_", " ").contains(act_label.toLowerCase())) {
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
  
  public void setMisionID(int ID) {
	  System.out.println("setMisionID " + ID);
	  conection.setId_mision(String.valueOf(ID));
	  mission_ID = ID;
	  return;
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
  
  public void distanceMode() {
	  if(distance_mode == false)
	  {
		  startDistanceMode();
	  }
	  else
	  {
		  stopDistanceMode();
	  }
  }
    
  public double rad(double x)
  {
      return x*Math.PI/180;
  }
  
  boolean create_line_ac = false;
  boolean create_line_flir = false;
  boolean create_line_select_track = false;
  
  public void paintDistanceLines()
  {
	  if(track_layer_distance != 0 && fTrackLayers.size() > 0)
	  {
			ILspLayer layer = fTrackLayers.get(track_layer_distance);
			if (layer != null && distance_mode_manual) 
			{		  
			  boolean ac = distance_ac_Checkbox.isSelected();
			  boolean flir = distance_flir_Checkbox.isSelected();
			  boolean select_track = distance_select_track_Checkbox.isSelected();
			  ILcdPoint mouse_position = fMouseEventHandler.getMousePosition();
			  if(ac) 
			  {
				if(create_line_ac == false) 
				{
					addLine(track_layer_distance, track_distance_ac_id, ac_point, mouse_position, "Distance AC", 0);
					create_line_ac = true;
				}
				else  
				{
					updateLine(track_layer_distance, track_distance_ac_id, ac_point, mouse_position, 0);
				}
			  }
			  else 
			  {
				  removeTrack(track_layer_distance, track_distance_ac_id);
			  }
			  
			  if(flir) 
			  {
				if(create_line_flir == false) 
				{
					addLine(track_layer_distance, track_distance_flir_id, flir_point, mouse_position, "Distance FLIR", 0);
					create_line_flir = true;
				}
				else  
				{
					updateLine(track_layer_distance, track_distance_flir_id, flir_point, mouse_position, 0);
				}
			  }
			  else 
			  {
				  removeTrack(track_layer_distance, track_distance_flir_id);
			  }
			  
			  if(select_track && isSelectAnyElement) 
			  {
				if(create_line_select_track == false) 
				{
					addLine(track_layer_distance, track_distance_select_track_id, select_track_point, mouse_position, "Distance Select Track", 0);
					create_line_select_track = true;
				}
				else  
				{
					updateLine(track_layer_distance, track_distance_select_track_id, select_track_point, mouse_position, 0);
				}
			  }
			  else 
			  {
				  removeTrack(track_layer_distance, track_distance_select_track_id);
			  }
	       }
	  }
  }
  
  public void distanceTo()
  {
	 
	 boolean ac = distance_ac_Checkbox.isSelected();
	 boolean flir = distance_flir_Checkbox.isSelected();
	 boolean select_track = distance_select_track_Checkbox.isSelected();
	 if(ac || flir || select_track)
	 {
		 if(track_layer_distance == 0)
		 {
			distance_mode_manual = true;
			layerFactory.setTextColor("#0036F5");
			layerFactory.setLineColor("#2C4DC9");
			layerFactory.setHaloColor("#ffffff");
		 	track_layer_distance = addTrackLayerLine("Distance Manual", "EPSG:4326");
		 	layerFactory.setDefautlsColor();
		 }
	 }
	 else
	 {
		 distance_mode_manual = false;
		 removeTrackLayer(track_layer_distance);
		 track_layer_distance = 0;
		 create_line_ac = false;
		 create_line_flir = false;
		 create_line_select_track = false;
	 }

  }
  
  public double getDistanceTwoPoints(ILcdPoint start_point, ILcdPoint end_point) {
	 if(start_point != null && end_point != null)
	 {
		 double R = 6378.137; // earth's mean radius in km
		 double dLat  = rad(end_point.getX() - start_point.getX());
		 double dLong = rad(end_point.getY() - start_point.getY());
		
		 double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		          Math.cos(rad(start_point.getX())) * Math.cos(rad(end_point.getX())) * Math.sin(dLong/2) * Math.sin(dLong/2);
		 double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		 double distance = R * c; //in km.
		 //return distance;
		 return distance * 1000; // in m.
	 }
	 return 0.0;
  }
  
  public void startDistanceMode() {
	System.out.println("startDistanceMode");
	defaul_controller = getView().getController();
	distance_panel.setVisible(true);
	distance_mode = true;
	getView().setController(fTerrainRulerController);
	LspDataUtil.instance().grid().addToView(getView());

	if(load_file_med == false)
	{
		track_layer_distance = addTrackLayerDistance("Distance", "EPSG:4326");
		dtedLayer = fTrackLayers.get(track_layer_distance);
		terrainModeCheckbox.setEnabled(false);
	}
	else {
		if ( dtedLayer != null ) {
			terrainModeCheckbox.setEnabled(true);
		  fTerrainRulerController.setTerrainElevationProvider(createAltitudeProvider(dtedLayer.getModel()));
		}
	}

  }
  
  ILspController defaul_controller;
  
  public void stopDistanceMode() {
	  if(distance_mode)
	  {
		  System.out.println("stopDistanceMode");
		  distance_panel.setVisible(false);
		  distance_mode = false;
		  
		  if(load_file_med == false)
		  {
			  removeTrackLayer(track_layer_distance);
			  track_layer_distance = 0;
		  }
		  getView().setController(defaul_controller);
	  }
  }
  
  private TLcdHeightProviderAdapter createAltitudeProvider(ILcdModel aModel) {
    return new TLcdHeightProviderAdapter(createHeightProvider(aModel), (ILcdGeoReference) aModel.getModelReference());
  }

  private ILcdHeightProvider createHeightProvider(ILcdModel aModel) {
    return HeightProviderUtil.getHeightProvider(aModel, (ILcdGeoReference) aModel.getModelReference(), FIRST_POINT, HeightProviderUtil.DTEDLevel.LEVEL_1);
  }
	  
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
  
  public int addTrackLayerPointWithIcon(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 1);
  }
  
  public int addTrackLayerPolygon(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 2);
  }
  
  public int addTrackLayerDistance(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 3);
  }
  
  public int addTrackLayerPolyline(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 4);
  }
  
  public int addTrackLayerLine(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 5);
  }
  
  public int addTrackLayer(String aLayerName, String aEPSG, int type_layer) {
    try {
    	ILcdModelReference reference = new TLcdEPSGReferenceParser().parseModelReference(aEPSG);
      ILspLayer layer;
      TLspCompositeLayerFactory factory;
      switch (type_layer) {
		case 1:
			factory = new TLspCompositeLayerFactory(layerFactory);
			TLcdVectorModel model_p = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor(aLayerName, "IconPoints", "Points with icon"));
			Collection<ILspLayer> layers_points = factory.createLayers(model_p);
			layer = layers_points.iterator().next();
			break;
		case 2:
			factory = new TLspCompositeLayerFactory(layerFactory);
			TLcdVectorModel model_polygon = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor(aLayerName, "SolidShapes", "Solid fill shapes"));
			Collection<ILspLayer> layers_polygon = factory.createLayers(model_polygon);
			layer = layers_polygon.iterator().next();
			break;
		case 3:
			if(!file_med.equals(""))
			{
				layer = LspDataUtil.instance().model(file_med).layer().label("Alps").addToView(getView()).fit().getLayer();	
			}
			else
			{
				ILcdModel model = new TLcd2DBoundsIndexedModel(reference, new TLcdModelDescriptor(aLayerName, "Alps", aLayerName));
			    layer = TLspShapeLayerBuilder.newBuilder()
                 .model(model)
                 .build();
			}
			break;
		case 4:
			factory = new TLspCompositeLayerFactory(layerFactory);
			TLcdVectorModel model_polyline = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor(aLayerName, "Polyline", "Polyline"));
			Collection<ILspLayer> layers_polyline = factory.createLayers(model_polyline);
			layer = layers_polyline.iterator().next();
			break;
		case 5:
			factory = new TLspCompositeLayerFactory(layerFactory);
			TLcdVectorModel model_line = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor(aLayerName, "Line", "Line"));
			Collection<ILspLayer> layers_line = factory.createLayers(model_line);
			layer = layers_line.iterator().next();
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
        else
        {
        	//System.out.println("nothing select");
        	//select_track_point = null;
        	//isSelectAnyElement = false;
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
  public void addTrack(int aLayerId, int aTrackId, double aX, double aY, double aZ, String aCallSign, long aTimeStamp)
  {
	  Map<String,Object> data = new HashMap<>();
	  data.put("name", "");
      data.put("description", ""); 
      data.put("course", ""); 
      data.put("speed", "");
      data.put("category", ""); 
      addTrack(aLayerId, aTrackId, aX, aY, aZ, aCallSign, aTimeStamp, data);
  }
  
  public void addTrack(int aLayerId, int aTrackId, double aX, double aY, double aZ, String aCallSign, long aTimeStamp, Map<String,Object> data) {
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

        String name = data.containsKey("name") ? data.get("name").toString() : "";
        String description = data.containsKey("description") ? data.get("description").toString() : "";
        String course = data.containsKey("course") ? data.get("course").toString() : "";
        String speed = data.containsKey("speed") ? data.get("speed").toString() : "";
        String category = data.containsKey("category") ? data.get("category").toString() : "";
        
        track.setValue(TrackDataTypes.ID, aTrackId);
        track.setValue(TrackDataTypes.LOCATION, location);
        //track.setValue(TrackDataTypes.TIMESTAMP, aTimeStamp);
        track.setValue(TrackDataTypes.CALLSIGN, aCallSign);
        if(!name.equals(""))
        {
        	track.setValue(TrackDataTypes.NAME, name);
        }
        if(!description.equals(""))
        {
        	track.setValue(TrackDataTypes.DESCRIPTION, description);
        }
        if(!course.equals(""))
        {
        	track.setValue(TrackDataTypes.COURSE, course);
        }
        if(!speed.equals(""))
        {
        	track.setValue(TrackDataTypes.SPEED, speed);
        }
        if(!category.equals(""))
        {
        	track.setValue(TrackDataTypes.CATEGORY, category);
        }
        model.addElement(track, ILcdModel.FIRE_LATER);
      }
      model.fireCollectedModelChanges();
    }
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
          //track.setValue(TrackDataTypes.TIMESTAMP, aTimeStamp);
          model.elementChanged(track, ILcdModel.FIRE_LATER);
        }
        model.fireCollectedModelChanges();
      }
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
  
  public void addPolygon(int aLayerId, int aTrackId, TLcd3DEditablePointList polygon, String aCallSign, long aTimeStamp)
  {
	  Map<String,Object> data = new HashMap<>();
	  data.put("name", "");
      data.put("description", ""); 
      addPolygon(aLayerId, aTrackId, polygon, aCallSign, aTimeStamp, data);
  }
  
  public void addPolygon(int aLayerId, int aTrackId, TLcd3DEditablePointList polygon, String aCallSign, long aTimeStamp, Map<String,Object> data) {
	    ILspLayer layer = fTrackLayers.get(aLayerId);
	    if (layer != null) {
	      ILcdModel model = layer.getModel();
	      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
	        TLcdDataObject track = new TLcdDataObject(PolygonDataTypes.TRACK_PLAN_DATA_TYPE);
	        ILcd3DEditablePointList location;
	        if (model.getModelReference() instanceof ILcdGeodeticReference) {
	          //location = new TLcdLonLatHeightPolygon(generate3DRandomPolygon()) {
	          location = new TLcdLonLatHeightPolygon(polygon) {
	            @Override
	            public String toString() {
					/*float x = data.containsKey("lat") ? (float)data.get("lat") : 0.0f;
					float y = data.containsKey("lng") ? (float)data.get("lng") : 0.0f;
					float z = data.containsKey("hgt") ? (float)data.get("hgt") : 0.0f;
					TLcdLonLatHeightPoint  polygon_point = new TLcdLonLatHeightPoint(x,y,z);
					return getFormattedTrackLocation(polygon_point)*/
					return getFormattedTrackLocation(this);
	            }
	          };
	        } else {
	          //location = new TLcdXYZPolygon(generate3DRandomPolygon()) {
	           location = new TLcdXYZPolygon(polygon) {
	            @Override
	            public String toString() {
	            	/*float x = data.containsKey("lat") ? (float)data.get("lat") : 0.0f;
					float y = data.containsKey("lng") ? (float)data.get("lng") : 0.0f;
					float z = data.containsKey("hgt") ? (float)data.get("hgt") : 0.0f;
					TLcdLonLatHeightPoint  polygon_point = new TLcdLonLatHeightPoint(x,y,z);
					return getFormattedTrackLocation(polygon_point);*/
					return getFormattedTrackLocation(this);
	            }
	          };
	        }
	        
	        String name = data.containsKey("name") ? data.get("name").toString() : "";
	        String description = data.containsKey("description") ? data.get("description").toString() : "";
	        track.setValue(PolygonDataTypes.ID, aTrackId);
	        track.setValue(PolygonDataTypes.LOCATION, location);
	        //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
	        track.setValue(PolygonDataTypes.CALLSIGN, aCallSign);
	        track.setValue(PolygonDataTypes.NAME, name);
	        track.setValue(PolygonDataTypes.DESCRIPTION, description);
	        model.addElement(track, ILcdModel.FIRE_LATER);
	        
	      }
	      catch(Exception e)
	      {
	    	  System.out.println(e);
	      }
	      model.fireCollectedModelChanges();
	    }
  }
  
  public void updatePolygon(int aLayerId, int aTrackId, TLcd3DEditablePointList polygon, long aTimeStamp)
  {
	  Map<String,Object> data = new HashMap<>();
	  data.put("name", "");
      data.put("description", ""); 
      updatePolygon(aLayerId, aTrackId, polygon, aTimeStamp, data);
  }
  
  public void updatePolygon(int aLayerId, int aTrackId, TLcd3DEditablePointList polygon, long aTimeStamp, Map<String,Object> data) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      ILcdDataObject track = getTrack(aLayerId, aTrackId);
      if (track != null) {
        ILcdModel model = layer.getModel();
        try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
          //((ILcd3DEditablePoint) track.getValue(PolygonDataTypes.LOCATION)).move3D(aX, aY, aZ);
        	//((ILcd3DEditablePointList) track.getValue(PolygonDataTypes.LOCATION)).getPointCount()
			ILcd3DEditablePointList location;
			if (model.getModelReference() instanceof ILcdGeodeticReference) {
			  location = new TLcdLonLatHeightPolygon(polygon) {
			    @Override
			    public String toString() {
			    	/*float x = data.containsKey("lat") ? (float)data.get("lat") : 0.0f;
					float y = data.containsKey("lng") ? (float)data.get("lng") : 0.0f;
					float z = data.containsKey("hgt") ? (float)data.get("hgt") : 0.0f;
					TLcdLonLatHeightPoint  polygon_point = new TLcdLonLatHeightPoint(x,y,z);
					return getFormattedTrackLocation(polygon_point);*/
			    	return getFormattedTrackLocation(this);
			    }
			  };
			} else {
			   location = new TLcdXYZPolygon(polygon) {
			    @Override
			    public String toString() {
			    	/*float x = data.containsKey("lat") ? (float)data.get("lat") : 0.0f;
					float y = data.containsKey("lng") ? (float)data.get("lng") : 0.0f;
					float z = data.containsKey("hgt") ? (float)data.get("hgt") : 0.0f;
					TLcdLonLatHeightPoint  polygon_point = new TLcdLonLatHeightPoint(x,y,z);
					return getFormattedTrackLocation(polygon_point);*/
					return getFormattedTrackLocation(this);
			    }
			  };
			}
          track.setValue(PolygonDataTypes.LOCATION, location);
          //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
          //track.setValue(PolygonDataTypes.NAME, "Nombre");
	      //track.setValue(PolygonDataTypes.DESCRIPTION, "Descripción");
          model.elementChanged(track, ILcdModel.FIRE_LATER);
        }
        model.fireCollectedModelChanges();
      }
    }
  }
  
  public void addPolyline(int aLayerId, int aTrackId, TLcd3DEditablePointList polyline, String aCallSign, long aTimeStamp, Map<String,Object> data) {
	    ILspLayer layer = fTrackLayers.get(aLayerId);
	    if (layer != null) {
	      ILcdModel model = layer.getModel();
	      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
	        TLcdDataObject track = new TLcdDataObject(PolygonDataTypes.TRACK_PLAN_DATA_TYPE);
	        ILcd3DEditablePointList location;
	        if (model.getModelReference() instanceof ILcdGeodeticReference) {
	          //location = new TLcdLonLatHeightPolygon(generate3DRandomPolygon()) {
	          location = new TLcdLonLatHeightPolyline(polyline) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(this);
	            }
	          };
	        } else {
	          //location = new TLcdXYZPolygon(generate3DRandomPolygon()) {
	           location = new TLcdXYZPolyline(polyline) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(this);
	            }
	          };
	        }
	        
	        String name = data.containsKey("name") ? data.get("name").toString() : "";
	        String description = data.containsKey("description") ? data.get("description").toString() : "";
	        track.setValue(PolygonDataTypes.ID, aTrackId);
	        track.setValue(PolygonDataTypes.LOCATION, location);
	        //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
	        track.setValue(PolygonDataTypes.CALLSIGN, aCallSign);
	        track.setValue(PolygonDataTypes.NAME, name);
	        track.setValue(PolygonDataTypes.DESCRIPTION, description);
	        model.addElement(track, ILcdModel.FIRE_LATER);
	        
	      }
	      catch(Exception e)
	      {
	    	  System.out.println(e);
	      }
	      model.fireCollectedModelChanges();
	    }
  }
  
  public void updatePolyline(int aLayerId, int aTrackId, TLcd3DEditablePointList polyline, long aTimeStamp, Map<String,Object> data) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      ILcdDataObject track = getTrack(aLayerId, aTrackId);
      if (track != null) {
        ILcdModel model = layer.getModel();
        try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
          //((ILcd3DEditablePoint) track.getValue(PolygonDataTypes.LOCATION)).move3D(aX, aY, aZ);
        	//((ILcd3DEditablePointList) track.getValue(PolygonDataTypes.LOCATION)).getPointCount()
			ILcd3DEditablePointList location;
			if (model.getModelReference() instanceof ILcdGeodeticReference) {
			  location = new TLcdLonLatHeightPolyline(polyline) {
			    @Override
			    public String toString() {
			      return getFormattedTrackLocation(this);
			    }
			  };
			} else {
			   location = new TLcdXYZPolyline(polyline) {
			    @Override
			    public String toString() {
			      return getFormattedTrackLocation(this);
			    }
			  };
			}
          track.setValue(PolygonDataTypes.LOCATION, location);
          //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
          //track.setValue(PolygonDataTypes.NAME, "Nombre");
	      //track.setValue(PolygonDataTypes.DESCRIPTION, "Descripción");
          model.elementChanged(track, ILcdModel.FIRE_LATER);
        }
        model.fireCollectedModelChanges();
      }
    }
	  }
  
  public void addLine(int aLayerId, int aTrackId, ILcdPoint start_point, ILcdPoint end_point, String aCallSign, long aTimeStamp) {
	    ILspLayer layer = fTrackLayers.get(aLayerId);
	    ILcd3DEditablePoint p1 = (ILcd3DEditablePoint) start_point;
	    ILcd3DEditablePoint p2 = (ILcd3DEditablePoint) end_point;
	    if (layer != null) {
	      ILcdModel model = layer.getModel();
	      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
	        TLcdDataObject track = new TLcdDataObject(LineDataTypes.TRACK_PLAN_DATA_TYPE);
	        ILcd3DEditablePointList location;
	        if (model.getModelReference() instanceof ILcdGeodeticReference) {
	          //location = new TLcdLonLatHeightPolygon(generate3DRandomPolygon()) {	
	          location = new TLcdLonLatHeightLine(p1, p2) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(p1);
	            }
	          };
	        } else {
	          //location = new TLcdXYZPolygon(generate3DRandomPolygon()) {
	           location = new TLcdXYZLine(p1, p2) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(p1);
	            }
	          };
	        }
	        String distance_label = df_line.format(getDistanceTwoPoints(start_point, end_point)) + " m";
	        track.setValue(LineDataTypes.ID, aTrackId);
	        track.setValue(LineDataTypes.LOCATION, location);
	        //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
	        track.setValue(LineDataTypes.CALLSIGN, aCallSign);
	        track.setValue(LineDataTypes.LABEL, distance_label);
	        model.addElement(track, ILcdModel.FIRE_LATER);
	        
	      }
	      catch(Exception e)
	      {
	    	  System.out.println("catch");
	    	  System.out.println(e);
	      }
	      model.fireCollectedModelChanges();
	    }
  	}
  
  public void updateLine(int aLayerId, int aTrackId, ILcdPoint start_point, ILcdPoint end_point, long aTimeStamp){
    ILspLayer layer = fTrackLayers.get(aLayerId);
    ILcd3DEditablePoint p1 = (ILcd3DEditablePoint) start_point;
    ILcd3DEditablePoint p2 = (ILcd3DEditablePoint) end_point;
    if (layer != null) {
      ILcdDataObject track = getTrack(aLayerId, aTrackId);
      if (track != null) {
        ILcdModel model = layer.getModel();
        try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
          //((ILcd3DEditablePoint) track.getValue(PolygonDataTypes.LOCATION)).move3D(aX, aY, aZ);
        	//((ILcd3DEditablePointList) track.getValue(PolygonDataTypes.LOCATION)).getPointCount()
			ILcd3DEditablePointList location;
			if (model.getModelReference() instanceof ILcdGeodeticReference) {
				location = new TLcdLonLatHeightLine(p1, p2) {
			    @Override
			    public String toString() {
			      return getFormattedTrackLocation(p1);
			    }
			  };
			} else {
				location = new TLcdXYZLine(p1, p2) {
			    @Override
			    public String toString() {
			      return getFormattedTrackLocation(p1);
			    }
			  };
			}
			String distance_label = df_line.format(getDistanceTwoPoints(start_point, end_point)) + " m";
			track.setValue(LineDataTypes.LOCATION, location);
			track.setValue(LineDataTypes.LABEL, distance_label);
			model.elementChanged(track, ILcdModel.FIRE_LATER);
        }
        model.fireCollectedModelChanges();
      }
    }
  }
  
	public void addCircle(int aLayerId, int aTrackId, float center_point_x, float center_point_y, float center_point_z, String aCallSign, long aTimeStamp, double radius) {
		TLcdLonLatHeightPoint center = new TLcdLonLatHeightPoint(center_point_x,center_point_y, center_point_z);
		ILcdPoint center_point = center.getPoint(0);
		addCircle(aLayerId, aTrackId, center_point, aCallSign, aTimeStamp, radius);
	}
	  
  	public void addCircle(int aLayerId, int aTrackId, ILcdPoint center_point, String aCallSign, long aTimeStamp, double radius) {
	    ILspLayer layer = fTrackLayers.get(aLayerId);
	    ILcd3DEditablePoint center = (ILcd3DEditablePoint) center_point;
	    if (layer != null) {
	      ILcdModel model = layer.getModel();
	      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
	        TLcdDataObject track = new TLcdDataObject(LineDataTypes.TRACK_PLAN_DATA_TYPE);
	        Object location;
	        if (model.getModelReference() instanceof ILcdGeodeticReference) {
	        	location = new TLcdLonLatCircle(center, radius, TLcdEllipsoid.DEFAULT) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(center);
	            }
	          };
	        } else {
	          //location = new TLcdXYZPolygon(generate3DRandomPolygon()) {
	           location = new TLcdXYCircle(center, radius) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(center);
	            }
	          };
	        }
	        track.setValue(LineDataTypes.ID, aTrackId);
	        track.setValue(LineDataTypes.LOCATION, location);
	        //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
	        track.setValue(LineDataTypes.CALLSIGN, aCallSign);
	        track.setValue(LineDataTypes.LABEL, radius / 1000 + " km");
	        model.addElement(track, ILcdModel.FIRE_LATER);
	        
	      }
	      catch(Exception e)
	      {
	    	  System.out.println("catch");
	    	  System.out.println(e);
	      }
	      model.fireCollectedModelChanges();
	    }
	}
  	

    public void updateCircle(int aLayerId, int aTrackId, float center_point_x, float center_point_y, float center_point_z , long aTimeStamp, double radius) {
    	TLcdLonLatHeightPoint center = new TLcdLonLatHeightPoint(center_point_x,center_point_y, center_point_z);
  	  ILcdPoint center_point = center.getPoint(0);
  	  updateCircle(aLayerId, aTrackId, center_point, aTimeStamp, radius);
    }
    
    public void updateCircle(int aLayerId, int aTrackId, ILcdPoint center_point, long aTimeStamp, double radius){
      ILspLayer layer = fTrackLayers.get(aLayerId);
      ILcd2DEditablePoint center = (ILcd2DEditablePoint) center_point;
      if (layer != null) {
        ILcdDataObject track = getTrack(aLayerId, aTrackId);
        if (track != null) {
          ILcdModel model = layer.getModel();
          try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
            //((ILcd3DEditablePoint) track.getValue(PolygonDataTypes.LOCATION)).move3D(aX, aY, aZ);
          	//((ILcd3DEditablePointList) track.getValue(PolygonDataTypes.LOCATION)).getPointCount()
  			Object location;
  			if (model.getModelReference() instanceof ILcdGeodeticReference) {
  				location = new TLcdLonLatCircle(center, radius, TLcdEllipsoid.DEFAULT) {
  			    @Override
  			    public String toString() {
  			      return getFormattedTrackLocation(center);
  			    }
  			  };
  			} else {
  				location = new TLcdXYCircle(center, radius) {
  			    @Override
  			    public String toString() {
  			      return getFormattedTrackLocation(center);
  			    }
  			  };
  			}
  			track.setValue(LineDataTypes.LOCATION, location);
  			track.setValue(LineDataTypes.LABEL, radius / 1000 + " km");
  			model.elementChanged(track, ILcdModel.FIRE_LATER);
          }
          model.fireCollectedModelChanges();
        }
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
	          
	          String type_element = ((ILcdDataObject) object).getDataType().getName();
	          String var_id = "";
	          String var_location = "";
	          switch(type_element) 
	          {
	          	case "Track":
	          		String track_name = TrackDataTypes.NAME;
	          		if(!track_name.equals("AC") && !track_name.equals("FLIR")) {
		          		var_location = TrackDataTypes.LOCATION;
		          		var_id = TrackDataTypes.ID;
	          		}
	          		break;
	          	case "Área":
	          		//var_location = PolygonDataTypes.LOCATION;
	          		//var_id = PolygonDataTypes.ID;
	          		break;
	          	case "Línea":
	          		var_location = LineDataTypes.LOCATION;
	          		var_id = LineDataTypes.ID;
		        	break;
		        default:
		        	break;
	          }
	          select_track_point = null;
	          isSelectAnyElement = false;
	          if(!var_id.equals("") && !var_location.equals(""))
	          {
	        	  //"Lon: -94.720 Lat: 20.280 Height: 0.000"
	        	  String location = ((ILcdDataObject) object).getValue(var_location).toString();
	        	  //int id = Integer.parseInt(((ILcdDataObject) object).getValue(var_id).toString());
	        	  
	        	  String separate_caracter = ";";
	        	  location = location.replaceAll("Lon: ", separate_caracter);
	        	  location = location.replaceAll("Lat: ", separate_caracter);
	        	  location = location.replaceAll("Height: ", separate_caracter);
	        	  location = location.startsWith(separate_caracter) ? location.replaceFirst(separate_caracter, ""): location;
	        	  String split_location[] = location.split(separate_caracter);
	        	  float x = Float.parseFloat(split_location[0]);
	        	  float y = Float.parseFloat(split_location[1]);
	        	  float z = Float.parseFloat(split_location[2]);
	        	  select_track_point = new TLcdLonLatHeightPoint(x,y,z);
	        	  isSelectAnyElement = true;
	          }

	          JScrollPane scroll = new JScrollPane();
	          scroll.setMinimumSize(new Dimension(150, 100));
	          scroll.setMaximumSize(new Dimension(250, 100));

	          scroll.setViewportView(t);
	          //return scroll;
	          
	          JPanel jp_ballon = new JPanel();
	          jp_ballon.setLayout(new VerticalLayout());
	          jp_ballon.add(new JLabel(""));
	          jp_ballon.add(scroll);
	          
	          if(type_element.equals("Línea") )
	          {
	        	  return null;
	          }
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
	      List<JSONObject> tracks_list = conection.getTracks();
	      //Lists to check poins to remove
	      List<Integer> all_tracks_ids_points = getAllTrackIds(track_layer_points_tracks);
	      List<Integer> all_marks_ids_points = getAllTrackIds(track_layer_points_marks);
	      List<Integer> tracks_ids_points_tracks_database = new ArrayList<Integer>();
	      List<Integer> tracks_ids_points_marks_database = new ArrayList<Integer>();
	      //Lists to check polygons to remove
	      List<Integer> all_tracks_ids_polygon = getAllPolygonTrackLayersIds();
	      List<Integer> all_tracks_ids_polygon_database = new ArrayList<Integer>();
	      
	      for(int t=0; t < tracks_list.size(); t++ )
	      {
	              float point_x = (float) tracks_list.get(t).get("x_geoposicion");
	              float point_y = (float) tracks_list.get(t).get("y_geoposicion");
	              float point_z = tracks_list.get(t).containsKey("elevacion") ? (float) tracks_list.get(t).get("elevacion") * 1000 : 0.0f; //m. 
	              Map<String,Object> data = new HashMap<>();
	              data.put("name", ((JSONObject)tracks_list.get(t)).get("nombre").toString());
	              data.put("description", ((JSONObject)tracks_list.get(t)).get("descripcion").toString());
	              data.put("lat", point_x);
	              data.put("lng", point_y);
	              data.put("hgt", point_z);
	
	              int type = (int)tracks_list.get(t).get("tipo_dato");
	              if(type == 1 || type == 2)
	              {
	            	  int layer_type = ( 
	            			  type == 1 
	            			  ? track_layer_points_tracks 
	            			  : 
	            			  (
	            					  type == 2 
	            					  ? track_layer_points_marks
	            					  : 0
	            			  )  
	            	  );
	            	  
	            	  JSONObject datos_json = (JSONObject) ((JSONObject)tracks_list.get(t)).get("datos_json");
	            	  if(datos_json.containsKey("rumbo"))
	            	  {
	            		  data.put("course", datos_json.get("rumbo").toString());
	            	  }
	            	  if(datos_json.containsKey("velocidad"))
	            	  {
	            		  data.put("speed", datos_json.get("velocidad").toString());
	            	  }
	            	  if(datos_json.containsKey("categoria"))
	            	  {
	            		  data.put("category", datos_json.get("categoria").toString());  
	            	  }
	            	  double rango_arma = 0.0;
	            	  if(datos_json.containsKey("rango_arma"))
	            	  {
	            		  rango_arma = Double.parseDouble(datos_json.get("rango_arma").toString()) * 1000; //m.  
	            	  }
	            	  
	            	  ILcdDataObject track = getTrack(layer_type, (int) tracks_list.get(t).get("ID"));
	                  if (track == null) {
	                  	addTrack( layer_type, (int) tracks_list.get(t).get("ID"), point_x, point_y, point_z, "TRACK", 0, data);
	                  }
	              	  else
	              	  {
	              		updateTrack(layer_type, (int) tracks_list.get(t).get("ID"), point_x, point_y, point_z, 0);
	              	  }
	                  
	                    //Rango arma
	                    ILspLayer layer = fTrackLayers.get(rangos_armas_layer_id);
            		    if (layer != null) 
            		    {
            		    	ILcdDataObject track_ra = getTrack(rangos_armas_layer_id, (int) tracks_list.get(t).get("ID"));
	              		    if (track_ra == null) {
	              		    	if(rango_arma > 0.0) 
	              		    	{
	              		    		addCircle(rangos_armas_layer_id, (int) tracks_list.get(t).get("ID"), point_x, point_y, 1000, "Rango Arma Track", 0, rango_arma);
	              		    	}
	              		    }
	              		    else {
	              		    	if(rango_arma > 0.0) 
	    	              		{
	              		    		updateCircle(rangos_armas_layer_id, (int) tracks_list.get(t).get("ID"), point_x, point_y, 1000, 0, rango_arma);	
	    	              		}
	    	              		else 
	    	              		{
	    	              			removeTrack(rangos_armas_layer_id, (int) tracks_list.get(t).get("ID"));
	    	              		}
	              		    }
            		    }
            		    
	                  if(type==1)
	                  {
	                	  tracks_ids_points_tracks_database.add((int) tracks_list.get(t).get("ID"));
	                  }
	                  if(type==2)
	                  {
	                	  tracks_ids_points_marks_database.add((int) tracks_list.get(t).get("ID"));
	                  }
	              }
	              else if(type == 3 || type == 4 || type == 5)
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
		            		  //Plan de vuelo
		            		  if(type == 5)
		            		  {
		            			  track_points.insert3DPoint(ep, new TLcdXYZPoint(lat, lng, 10000));
		            		  }
		            		  //Visión FLIR
		            		  else if(type == 4)
		            		  {
		            			  track_points.insert3DPoint(ep, new TLcdXYZPoint(lat, lng, point_z));
		            		  }
		            		  //Áreas (tipo 3)
		            		  else 
		            		  {
		            			  track_points.insert3DPoint(ep, new TLcdXYZPoint(lat, lng, 0));  
		            		  }
		            		  
		            	  }
		            	  
		            	  
		            	  JSONObject datos_json = (JSONObject) ((JSONObject)tracks_list.get(t)).get("datos_json");
		                  String color_db = datos_json.get("color").toString();
		                  String color = color_db.startsWith("#") ? color_db : "#" + color_db;
		                  if(color.equals("") || color.equals("0")) {
		                	  color = layerFactory.generateColorRandom();
		                  }
		                  //System.out.println("Color polygon: " + color);
		                  layerFactory.setFillColor(color);	 		                  
		                  layerFactory.setIconPath("");                 	
		                  if(isElementInPolygonTrackLayers(id_track) == false){
		                	  //Create layer polygon
		                	  int polygon_new_layerId = 0;
		                	  
		                	  if( type == 5) {
		                		  polygon_new_layerId = addTrackLayerPolyline("PL" + id_track, "EPSG:4326");
		                		  addPolyline(polygon_new_layerId, id_track, track_points, "Polylínea " + id_track, 0, data);
		                	  }
		                	  else {
		                		  polygon_new_layerId = addTrackLayerPolygon("P" + id_track, "EPSG:4326"); 
		                		  addPolygon(polygon_new_layerId, id_track, track_points, "Polygono " + id_track, 0, data);
		                	  }
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
		                	  //Change color
			        	      if(!color.equals(pre_polygon_color)) {
			        	    	  //Update style layer
			        	    	  //Delete layer and polygon
			        	    	  removeTrackLayer(polygon_layerId);
			        	    	  removePolygonTrackLayers(id_track);
			        	    	  //Create again layer and polygon
			        	    	  int polygon_new_layerId = 0;
			        	    	  if( type == 5) {
				        	    	  polygon_new_layerId = addTrackLayerPolyline("P" + id_track, "EPSG:4326");
					        	      addPolyline(polygon_new_layerId, id_track, track_points, "Polylínea " + id_track, 0, data);
			        	    	  }
			        	    	  else {
			        	    		  polygon_new_layerId = addTrackLayerPolygon("P" + id_track, "EPSG:4326");
			        	    		  addPolygon(polygon_new_layerId, id_track, track_points, "Polygono " + id_track, 0, data);
			        	    	  }
				        	      Map<String,Object> new_element = new HashMap<>();
				        	      new_element.put("aLayerId", polygon_new_layerId);
				        	      new_element.put("aLayerColor", color);
				        	      addPolygonTrackLayers(id_track, new_element);
			        	      }
			        	      if( type == 5) {
			        	    	  updatePolyline(polygon_layerId, id_track, track_points, 0, data);
			        	      }
			        	      else {
			        	    	  updatePolygon(polygon_layerId, id_track, track_points, 0, data);
			        	      }
		                  }
		                  all_tracks_ids_polygon_database.add(id_track);

		        	      layerFactory.setDefautlsColor();
	            	  }
	              }
	      }
	      
	      //AC point
	      if(ac_point != null) {
	    	  Map<String,Object> data = new HashMap<>();
              data.put("name", "AC");
              data.put("description", "Posición AC");
		      ILcdDataObject track = getTrack(track_layer_ac_id, track_ac_id);
	          if (track == null) {
	          	addTrack(track_layer_ac_id, track_ac_id, ac_point.getX(), ac_point.getY(), ac_point.getZ(), "AC", 0, data);
	          }
	      	  else
	      	  {
	      		updateTrack(track_layer_ac_id, track_ac_id, ac_point.getX(), ac_point.getY(), ac_point.getZ(), 0);
	      	  }
	      }
	      
	      //FLIR point
	      if(flir_point != null) {
	    	  Map<String,Object> data = new HashMap<>();
              data.put("name", "FLIR");
              data.put("description", "Posición Camara FLIR");
		      ILcdDataObject track = getTrack(track_layer_flir_id, track_flir_id);
	          if (track == null) {
	        	  addTrack(track_layer_flir_id, track_flir_id, flir_point.getX(), flir_point.getY(), flir_point.getZ(), "FLIR", 0, data);
	          }
	      	  else
	      	  {
	      		  updateTrack(track_layer_flir_id, track_flir_id, flir_point.getX(), flir_point.getY(), flir_point.getZ(), 0);
	      	  }
	      }
	      
	      //Remove points tracks
	      List<Integer> track_ids_points_traks_remove = all_tracks_ids_points.stream()
	    	        .filter(i -> !tracks_ids_points_tracks_database.contains(i))
	    	        .collect(Collectors.toList());
	      for (Integer track_id_point_remove : track_ids_points_traks_remove) {
	    	  removeTrack(track_layer_points_tracks, track_id_point_remove);
	    	  removeTrack(rangos_armas_layer_id, track_id_point_remove);
	      }
	      //Remove points marcks
	      List<Integer> track_ids_points_marks_remove = all_marks_ids_points.stream()
	    	        .filter(i -> !tracks_ids_points_marks_database.contains(i))
	    	        .collect(Collectors.toList());
	      for (Integer track_id_point_remove : track_ids_points_marks_remove) {
	    	  removeTrack(track_layer_points_marks, track_id_point_remove);
	    	  removeTrack(rangos_armas_layer_id, track_id_point_remove);
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
	  /*catch(Exception e) {
		  System.out.println(e.getMessage());
	  }*/
	  catch(SQLException q) {
		  System.out.println("----- DATA BASE Exception -----");
		  System.out.println(q.getMessage());
	  }
	  catch(Exception e) {
		  System.out.println("----- Exception -----");
		  System.out.println(e);
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
		    }
	  }
  }
  
  private ILcdPoint generateRandomPoint() {
	  int min_lat = 95;
	  int max_lat = 100;
	  int min_lng = 18;
	  int max_lng = 20;
	  double lat = (Math.random()*(max_lat-min_lat+1)+min_lat)*(-1);
	  double lng = Math.random()*(max_lng-min_lng+1)+min_lng;
	  TLcdLonLatHeightPoint p = new TLcdLonLatHeightPoint(lat,lng,0);
	  //System.out.println(lat);
	  //System.out.println(lng);
	  ILcdPoint point = p.getPoint(0);
	  System.out.println(point);
	  return point;  
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
