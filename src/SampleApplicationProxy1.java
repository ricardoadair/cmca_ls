//package samples.lightspeed.integration.jni;

import static com.luciad.view.lightspeed.util.TLspViewTransformationUtil.setup2DView;
import static com.luciad.view.lightspeed.util.TLspViewTransformationUtil.setup3DView;

import java.awt.Color;

//import static samples.lightspeed.common.FitUtil.fitOnLayers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.datamodel.TLcdDataObject;
import com.luciad.format.asterix.TLcdASTERIXTrack;
import com.luciad.geodesy.TLcdEllipsoid;
import com.luciad.gui.ILcdDialogManager;
import com.luciad.gui.TLcdAWTUtil;
import com.luciad.gui.TLcdUndoManager;
import com.luciad.gui.TLcdUserDialog;
import com.luciad.gui.swing.TLcdOverlayLayout;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelReference;
import com.luciad.model.TLcd2DBoundsIndexedModel;
import com.luciad.model.TLcdModelDescriptor;
import com.luciad.model.TLcdVectorModel;
import com.luciad.reference.ILcdGeoReference;
import com.luciad.reference.ILcdGeocentricReference;
import com.luciad.reference.ILcdGeodeticReference;
import com.luciad.reference.TLcdGeodeticReference;
import com.luciad.reference.format.TLcdEPSGReferenceParser;
import com.luciad.shape.ILcdPoint;
import com.luciad.shape.shape2D.ILcd2DEditablePoint;
import com.luciad.shape.shape2D.TLcd2DEditablePointList;
import com.luciad.shape.shape2D.TLcdLonLatCircle;
import com.luciad.shape.shape2D.TLcdLonLatPoint;
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
import com.luciad.tea.TLcdHeightProviderAdapter;
import com.luciad.text.TLcdDistanceFormat;
import com.luciad.util.ILcdSelectionListener;
import com.luciad.util.TLcdDistanceUnit;
import com.luciad.util.TLcdOutOfBoundsException;
import com.luciad.util.concurrent.TLcdLockUtil;
import com.luciad.util.height.ILcdHeightProvider;
import com.luciad.view.ILcdXYZWorldReference;
import com.luciad.view.TLcdAWTEventFilterBuilder;
import com.luciad.view.lightspeed.TLspExternalView;
import com.luciad.view.lightspeed.camera.TLspViewXYZWorldTransformation3D;
import com.luciad.view.lightspeed.controller.ILspController;
import com.luciad.view.lightspeed.controller.ruler.TLspRulerDistanceFormatStyle;
import com.luciad.view.lightspeed.controller.ruler.TLspRulerLabelStyler;
import com.luciad.view.lightspeed.controller.ruler.TLspRulerSegmentLabelContentStyle;
import com.luciad.view.lightspeed.controller.ruler.TLspRulerController.MeasureMode;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.layer.TLspCompositeLayerFactory;
import com.luciad.view.lightspeed.layer.shape.TLspShapeLayerBuilder;
import com.luciad.view.lightspeed.layer.style.TLspLayerStyle;
import com.luciad.view.lightspeed.painter.grid.TLspLonLatGridLayerBuilder;
import com.luciad.view.lightspeed.services.effects.TLspAmbientLight;
import com.luciad.view.lightspeed.services.effects.TLspHeadLight;
import com.luciad.view.lightspeed.style.TLspLabelBoxStyle;
import com.luciad.view.lightspeed.style.TLspTextStyle;
import com.luciad.view.lightspeed.style.styler.TLspCustomizableStyle;
import com.luciad.view.lightspeed.swing.TLspBalloonManager;
import com.luciad.view.lightspeed.swing.TLspScaleIndicator;
import com.luciad.view.lightspeed.swing.navigationcontrols.TLspNavigationControlsBuilder;
import com.luciad.view.lightspeed.util.TLspViewNavigationUtil;
import com.luciad.view.swing.ALcdBalloonDescriptor;
import com.luciad.view.swing.ILcdBalloonContentProvider;
import com.luciad.view.swing.TLcdModelElementBalloonDescriptor;

//import asterix.GXYDataUtil;
//import asterix.HeadingSensitiveDeclutterer;
import asterix.LiveDecodedModel;
import asterix.LiveDecoderResultCallback;
import asterix.SimulatorGXYLayerFactory;
import asterix.TransformationProvider;
import utils.Utilidades;

import org.jdesktop.swingx.VerticalLayout;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.simple.JSONObject;

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
public class SampleApplicationProxy1 extends LightspeedViewProxy 
{
  static CoordinateConversion convert = new CoordinateConversion();
  private static final TLcdLonLatPoint FIRST_POINT = new TLcdLonLatPoint(10, 45);
  int mission_ID = 0;
  ScheduledExecutorService timer;
  ScheduledExecutorService timer_delete;
  DecimalFormat df = new DecimalFormat("###,###.0000");
  DecimalFormat df_line = new DecimalFormat("###,###.00"); 
  
  //Layers
  boolean puntos_creados = false;
  //int track_layer_points_tracks = 0;
  //int track_layer_points_marks = 0;  
  private static Map<Integer,Map<String, Object>> tracksLayers = new HashMap<>();
  int track_layer_points_tracks_predicted = 0;

  
  int rangos_armas_layer_id = 0;
  int track_layer_draw_polygons = 0;
  int polygon_user_clicks_track_id = -1;
  
  //Icons Paths
  boolean update_styles = false;
  List<String> files_to_delete = new ArrayList<String>();
  String base_path = "";
  String icon_folder_path_track = "QtCMCA/UI/Track/";
  String icon_folder_path_marca = "QtCMCA/UI/Marca/";
  String icon_folder_path_ac = "QtCMCA/UI/Aeronave/";
  String icon_folder_path_flir = "QtCMCA/UI/FLIR/";
  String icon_folder_path_military_symbology = "";
  
  String military_symbology_prefix = "Symb_2525_";
  String track_icon_name = "Icon_Track_Normal.png";
  String marca_icon_name = "Icon_Marca_Normal.png";
  String ac_icon_name = "Icon_Aeronave_Normal.png";
  String flir_icon_name = "Icon_FLIR_Normal.png";
  
  String track_icon_path = "";
  String marck_icon_path = "";
  String ac_icon_path = "";
  String flir_icon_path = "";
	  
  //Dictionarys to Tracks Elements
  private final Map<Integer, ILspLayer> fTrackLayers;
  //private final Map<Integer, TLspLayerStyle> fTrackLayersStyles;
  private static Map<Integer,Map<String, Object>> fElementsTracks = new HashMap<>();
  private final Map<Integer, Map<String, Object >> fPolygonTrackLayers;
  private final AtomicInteger fAtomicInteger;
  
  //Asterix
  private LiveDecodedModel fLiveDecodedModel;
  private TransformationProvider fTransformationProvider = null;
  private final Timer fTimer = new Timer(50, new InvalidateLabelsActionListener());
  private final SimulatorGXYLayerFactory fLiveTrackGXYLayerFactory = new SimulatorGXYLayerFactory();
  ILspLayer layer_asterix = null;
  ILspLayer layer_polyline_asterix = null;
  private Map <String, TLcdASTERIXTrack>  tracks_asterix_objects = new HashMap<>();
  private Map <String, TLcd3DEditablePointList>  tracks_asterix_polyline_objects = new HashMap<>();
  
  //LUCIAD Componets
  private Component navigationControls;
  private TLspScaleIndicator scaleIndicator;
  TLspViewNavigationUtil navigationUtil;
  LayerFactory layerFactory = new LayerFactory();
  
  //IU
  Color back_color = new Color(22, 22, 22);
  Color text_color = Color.WHITE;
  Color border_color = new Color(65,65,65);
  private boolean show_hide_panel = false;
  public JLabel coords;
  
  //Tools panel
  JPanel tools_panel;
  JPanel tools_single_panel;
  
  //Radar
  JCheckBox view_radar;
  JCheckBox view_rute;
  
  //Distance
  JPanel distance_mode_panel;
  JCheckBox terrainModeCheckbox;
  
  //Menu Distance
  JCheckBox distance_ac_Checkbox;
  JCheckBox distance_flir_Checkbox;
  JCheckBox distance_select_track_Checkbox;
  JCheckBox distance_free_Checkbox;
  
  //Prediction Tool
  private boolean prediction_select_mode = false;
  private boolean prediction_all_tracks_mode = false;
  JCheckBox prediction_select_time_Checkbox;
  JCheckBox prediction_all_tracks_time_Checkbox;
  JSpinner prediction_time_minutes_spinner;
  JSpinner prediction_time_hours_spinner;
  boolean prediction_point_create = false;
  List<Integer> prediction_tracks_painted = new ArrayList<Integer>();
  TLcdLonLatHeightPoint prediction_time_point = null;
  
  //Polygon recording
  private JButton star_rec = new JButton();
  private JButton stop_rec = new JButton();
  static boolean draw_polygon_mode = false;
  private boolean create_polygon_user_clicks = false;
  int rec_polygon_id = -1;
  String recording_type = "";
  
  //AC
  boolean create_line_ac = false;
  TLcdLonLatHeightPoint ac_point = null;
  int track_layer_ac_id = 0;
  int track_ac_id = -2;
  int track_distance_ac_id = -4;
  String ac = "0,0";
  
  //FLIR
  boolean create_line_flir = false;
  TLcdLonLatHeightPoint flir_point = null;
  int track_layer_flir_id = 0;
  int track_flir_id = -3;
  int track_distance_flir_id = -5;
  String flir = "0,0";
  
  //SelectTrack
  boolean create_line_select_track = false;
  static boolean isSelectAnyElement = false;
  static int select_track_point_id = 0;
  int track_distance_select_track_id = -6;
  public native void objectSelected(long aNativePeer, Object aDataObject);
  
  //Measurement
  boolean load_file_med = false;
  String file_med = "";
  private TerrainRulerController fTerrainRulerController;
  ILspController defaul_controller;
  ILspLayer dtedLayer = null;
  int track_layer_distance = 0;
  private boolean distance_mode = false;
  private boolean distance_mode_manual = false;
  static String unity_id_GD = "GD";
  static String unity_id_GMS = "GMS";
  static String unity_id_UTM = "UTM";
  static String unity_id_MGRS = "MGRS";
  static String default_unit_location = unity_id_GD;
  static String unity_id_meter = "m";
  static String unity_id_kilometer = "km";
  static String unity_id_mile = "mi";
  static String unity_id_nautical_mile = "NM";
  static String unity_id_feet = "ft";
  String default_unit_longitude = unity_id_meter; 
  static Map<String,Map<String, Object>> units_longitude = new HashMap<String,Map<String, Object>>() {
	{
	    put(
	    	unity_id_meter, 
    		new HashMap<String,Object>() {
				{
				    put("name", "Metro");
				    put("abbreviation", "m");
				    put("operation_meters_to", "*");
				    put("value_meters_to", 1);
			  	}
			  }
	    );
	    put(
	    	unity_id_kilometer, 
    		new HashMap<String,Object>() {
				{
				    put("name", "Kilómetro");
				    put("abbreviation", "km");
				    put("operation_meters_to", "/");
				    put("value_meters_to", 1000);
			  	}
			  }
	    );
	    put(
	    	unity_id_mile, 
    		new HashMap<String,Object>() {
				{
				    put("name", "Milla");
				    put("abbreviation", "mi");
				    put("operation_meters_to", "/");
				    put("value_meters_to", 1609.34);//1 mi = 1609.34 m
			  	}
			  }
	    );
	    put(
	    	unity_id_nautical_mile, 
    		new HashMap<String,Object>() {
				{
				    put("name", "Milla náutica");
				    put("abbreviation", "NM");
				    put("operation_meters_to", "/");
				    put("value_meters_to", 1852);// 1ni = 1852 m
			  	}
			  }
	    );
	    put(
    		unity_id_feet, 
    		new HashMap<String,Object>() {
				{
				    put("name", "Pie");
				    put("abbreviation", "ft");
				    put("operation_meters_to", "/");
				    put("value_meters_to", 0.3048);// 1ft = 0.3048 m
			  	}
			  }
	    );
  	}
  };
  
  Utilidades utils = new Utilidades();
  
  public SampleApplicationProxy1(long aNativePeer) 
  {
    super(aNativePeer);
    
    String path = System.getProperty("user.dir");
    path = path.endsWith("/data") ? path.replace("/data", "") : path;
    System.out.println("JAVA run as: " +path);
    setBasePath(path);
    utils.setPath(path);
    
    timer = Executors.newSingleThreadScheduledExecutor();
    timer_delete = Executors.newSingleThreadScheduledExecutor();
    
    fAtomicInteger = new AtomicInteger();
    fTrackLayers = new HashMap<>();
    //fTrackLayersStyles = new HashMap<>();
    fPolygonTrackLayers = new HashMap<>();
    
    Map <String,Object> result_init_config = iniciarConfiguraciones();
    if( (result_init_config.containsKey("result") ? (boolean)result_init_config.get("result") : false) == false ) 
    {
    	String msg = result_init_config.containsKey("msg") ? result_init_config.get("msg").toString() : "Error al iniciar";
    	JOptionPane.showMessageDialog( 
			new Frame(), 
			msg, 
			"Advertencia", 
			JOptionPane.WARNING_MESSAGE 
    	);
    	System.out.println(msg);
    }
    //fTerrainRulerController = new TLspRulerController();
    fTerrainRulerController = new TerrainRulerController(createNavigationController());
    fTerrainRulerController.addUndoableListener(new TLcdUndoManager());
    fTerrainRulerController.setAWTFilter(TLcdAWTEventFilterBuilder.newBuilder().leftMouseButton().or().rightMouseButton().or().keyEvents().build());
    //fTerrainRulerController.appendController(createNavigationController());
    initRulerControllerStyles();

    getView().getServices().getGraphicsEffects().add(new TLspAmbientLight());
    getView().getServices().getGraphicsEffects().add(new TLspHeadLight(getView()));

   

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
      //Laber for coords
      coords = new JLabel("");
      coords.setBackground(Color.LIGHT_GRAY);
      coords.setForeground(Color.BLACK);
      coords.setOpaque(true);
      overlay.add(coords);
      layout.putConstraint(coords, TLcdOverlayLayout.Location.SOUTH, TLcdOverlayLayout.ResolveClash.VERTICAL);
      
      PopupMenu jpop = new PopupMenu();
      jpop.add("uno");
      overlay.add(jpop);
      
      //Tools Panel
      tools_panel = buildToolsPanel();
      overlay.add(tools_panel);
      layout.putConstraint(tools_panel, TLcdOverlayLayout.Location.NORTH_WEST, TLcdOverlayLayout.ResolveClash.VERTICAL);
      
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
      
    });

    getView().addLayer(TLspLonLatGridLayerBuilder.newBuilder().build());
    //getView().addLayer(TLspMGRSGridLayerBuilder.newBuilder().build());
    navigationUtil = new TLspViewNavigationUtil(getView());
    
    
    //if( (result_init_config.containsKey("result") ? (boolean)result_init_config.get("result") : false) == false ) 
    //{
    	timer_delete.schedule(center_mexico, 5, TimeUnit.SECONDS);
    //}
	timer.scheduleAtFixedRate(update_points, 1, 1, TimeUnit.SECONDS);
	timer.scheduleAtFixedRate(update, 200, 200, TimeUnit.MILLISECONDS);
	
	timer.scheduleAtFixedRate(update_flir_ac, 1, 1, TimeUnit.SECONDS);
  }
  
  private boolean leerXml()
  {   
	  System.out.println("Leyendo archivo de configuración " + utils.ARCHIVO_CONFIGURACION );
	    SAXBuilder builder = new SAXBuilder();
	    File xmlFile = new File( utils.ARCHIVO_CONFIGURACION );
	    
	    try 
	    {      
	      Document document = ( Document ) builder.build( xmlFile );
	      Element rootNode = document.getRootElement();
	      java.util.List<Element> list = rootNode.getChildren( "configuraciones" );
	 
	      for ( int i = 0; i < list.size(); i++) 
	      {
	        Element node = (Element) list.get(i);
	        if(node.getChildren().size() == utils.getTagsCount()) 
	        {
	        	for (Map.Entry<String, Object> tag : utils.getTags().entrySet()) 
	        	{
	        	    //System.out.println("clave=" + tag.getKey() + ", valor=" + tag.getValue());
	        		utils.setTagValue( tag.getKey(),  node.getChildText( utils.getTagName(tag.getKey()) ) );
	        	}
	        	//utils.printTagsValues();
	        	return true;
	        }
	      }
	    }
	    catch (IOException io) 
	    {
	      System.out.println(io.getMessage());
	      return false;
		} 
	    catch (JDOMException jdomex) 
	    {
	      System.out.println(jdomex.getMessage());
	      return false;
		}
	    return false;
	}
  
  private Map<String,Object> iniciarConfiguraciones()
  {
  	//Leer Archivo de Configuraciones
  	boolean resultado_leer_xml = leerXml();
  	boolean resultado_iniciar_bd = false;
  	boolean resultado_iniciar_radar = false;
  	String database_host = "";
  	String database_name = "";
  	String database_user = "";
  	String database_pass = "";
  	String sql_version = "";
  	String radar_ip = "";
  	String radar_puerto = "";
  	
  	String database_server_msg = "";
  	String radar_server_msg = "";
  	
  	if(resultado_leer_xml)
  	{
  		database_host = utils.getTagValue("DATABASE_HOST").toString();
  		database_name = utils.getTagValue("DATABASE_NAME").toString();
  		radar_ip = utils.getTagValue("RADAR_SERVER_IP").toString();
  		radar_puerto = utils.getTagValue("RADAR_SERVER_PUERTO").toString();
  		icon_folder_path_military_symbology = utils.getTagValue("MILITARY_SYMBOLOGY_IMAGE_PATH").toString();
  		resultado_iniciar_bd = setDataBaseParams(
			utils.getTagValue("DATABASE_HOST").toString(), 
			utils.getTagValue("DATABASE_NAME").toString(), 
			utils.getTagValue("DATABASE_USER").toString(), 
			utils.getTagValue("DATABASE_PASS").toString(),
			utils.getTagValue("MYSQL_VERSION").toString()
  		);
  		resultado_iniciar_radar = initializeDecoders(
			utils.getTagValue("RADAR_SERVER_IP").toString(), 
			Integer.parseInt(utils.getTagValue("RADAR_SERVER_PUERTO").toString())		
	  	);
  	}
  	else
  	{
  		database_host = "localhost";
  		database_name = "cmca";
  		database_user= "root";
  		database_pass = "root";
  		sql_version = "";
  		radar_ip = "239.192.86.0";
  		radar_puerto = "8600";
  		icon_folder_path_military_symbology = "/home/lvc19892/CMCA/Data/SymbolsImage/MILSTD2525/";
  		
  		resultado_iniciar_bd = setDataBaseParams(
			database_host,
			database_name,
			database_user,
			database_pass,
			sql_version
  		);
  		resultado_iniciar_radar = initializeDecoders(
  			radar_ip, 
  			Integer.parseInt(radar_puerto)	
	  	);
  	}
  	db_connection_successful = resultado_iniciar_bd;
  	database_server_msg = "[" + database_host + ": " + database_name+ "]";
  	radar_server_msg = "[" + radar_ip + ": " + radar_puerto+ "]";
  	
  	Map<String,Object> result = new HashMap<String,Object>() {};
  	result.put( "result", (resultado_iniciar_bd && resultado_iniciar_radar) );
  	result.put( 
		"msg", 
		"" + 
		( resultado_iniciar_bd ? "" : "No hay conexión con la base de datos " + database_server_msg + "." ) + 
		( resultado_iniciar_radar ? "" : (resultado_iniciar_bd ? "\n" : "") + "No se pudo iniciar el monitoreo del radar " + radar_server_msg + ".") +
		( resultado_leer_xml ? "\nVerifique el archivo de configuración " + utils.ARCHIVO_CONFIGURACION : "" )
	);
  	return result;
  }
  
  private float flir_ac_move = 0.01f;
  final Runnable update_flir_ac = new Runnable() {
	  public void run() 
	  {
		  if(ac_point == null)
		  {
			  ac_point = generateRandomPoint();
		  }
		  else
		  {
			  ac_point = new TLcdLonLatHeightPoint(ac_point.getLon()+flir_ac_move,ac_point.getLat()+flir_ac_move, 0);
		  }
		  if(flir_point == null)
		  {
			  flir_point = generateRandomPoint();
		  }
		  else
		  {
			  flir_point = new TLcdLonLatHeightPoint(flir_point.getLon()+flir_ac_move,flir_point.getLat()+flir_ac_move, 0);
		  }
		  setAc(ac_point.getLat()+","+ac_point.getLon());
		  setFlir(flir_point.getLat()+","+flir_point.getLon());		  
	  }
  };
  
  final Runnable center_mexico = new Runnable() {
	  public void run() 
	  {
		  centerMexico();
	  }
	};
	
	public void centerMexico()
	{
		centerMap(19, -100.000);
	}
	
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
				  track_layer_draw_polygons = addTrackLayerNoSelectionPolygon("1", "EPSG:4326");
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
	
	final Runnable deleteAsterixLayers= new Runnable() {
	  public void run() 
	  {
		  if(layer_asterix != null)
		  {  
			  List<ILspLayer> asterix_tracks_remove = new ArrayList<ILspLayer>();
	    	  Enumeration<ILspLayer> layers = getView().layers();
	    	  while(layers.hasMoreElements() ) 
	    	  {
	    		  ILspLayer l = layers.nextElement();
	    		  boolean is_asterix = l.getModel().getModelDescriptor().toString().contains("TLcdASTERIXTrackModelDescriptor");
	    		  int slayer_asterix = layer_asterix.hashCode();
	    		  int sl = l.hashCode();
	    		  
	    		  if(is_asterix && slayer_asterix != sl )
	    		  {
	    			  asterix_tracks_remove.add(l);
	    		  }
	    	  }
	    	  
	    	  for (ILspLayer remoce_layer : asterix_tracks_remove) 
	    	  {
				getView().removeLayer(remoce_layer);
	    	  }
		  }
	 }
  };
		
	/*private TLspCustomizableStyle fLineStyle1;
	  private TLspCustomizableStyle fLineStyle2;
	  private TLspCustomizableStyle fCircleStyle;
	 */ 
	  private TLspCustomizableStyle fSegmentLabelStyle;
	  private TLspCustomizableStyle fSegmentTextStyle;
	  private TLspCustomizableStyle fSegmentLabelContentStyle;

	  private TLspCustomizableStyle fTotalLabelStyle;
	  private TLspCustomizableStyle fTotalTextStyle;
	  private TLspCustomizableStyle fTotalLabelContentStyle;
	  
	  private void initRulerControllerStyles() {
	    /*TLspLineStyle primaryLineStyle = TLspLineStyle.newBuilder()
	                                                  .color(Color.blue)
	                                                  .width(4.0f)
	                                                  .build();

	    TLspLineStyle secondaryLineStyle = TLspLineStyle.newBuilder()
	                                                    .color(Color.white)
	                                                    .width(2.0f)
	                                                    .build();

	    TLspLineStyle circleLineStyle = TLspLineStyle.newBuilder()
	                                                 .elevationMode(ElevationMode.ON_TERRAIN)
	                                                 .color(Color.blue)
	                                                 .width(1.0f)
	                                                 .build();

	    TLspFillStyle circleFillStyle = TLspFillStyle.newBuilder()
	                                                 .elevationMode(ElevationMode.ON_TERRAIN)
	                                                 .color(Color.lightGray)
	                                                 .opacity(0.3f)
	                                                 .build();*/

	    //We create customizable stylers to enable us to easily modify the styles at runtime.
	    //TLspCustomizableStyler lineStyler = new TLspCustomizableStyler(primaryLineStyle, secondaryLineStyle);
	    //TLspCustomizableStyler circleStyler = new TLspCustomizableStyler(circleLineStyle, circleFillStyle);
	    TLspRulerLabelStyler labelStyler = new TLspRulerLabelStyler();

	    //We store the resulting customizable styles in fields to to be able to easily change them.
	    /*for (TLspCustomizableStyle style : lineStyler.getStyles()) {
	      if (style.getStyle() == primaryLineStyle) {
	        fLineStyle1 = style;
	      } else if (style.getStyle() == secondaryLineStyle) {
	        fLineStyle2 = style;
	      }
	    }
	    for (TLspCustomizableStyle style : circleStyler.getStyles()) {
	      if (style.getStyle() == circleLineStyle) {
	        fCircleStyle = style;
	      }
	    }*/
	    for (TLspCustomizableStyle style : labelStyler.getStyles()) {
	      if (TLspRulerLabelStyler.SEGMENT.equals(style.getIdentifier())) {
	        if (style.getStyle() instanceof TLspTextStyle) {
	          fSegmentTextStyle = style;
	        } else if (style.getStyle() instanceof TLspLabelBoxStyle) {
	          fSegmentLabelStyle = style;
	        } else if (style.getStyle() instanceof TLspRulerSegmentLabelContentStyle) {
	          fSegmentLabelContentStyle = style;
	        }
	      } else if (TLspRulerLabelStyler.TOTAL.equals(style.getIdentifier())) {
	        if (style.getStyle() instanceof TLspTextStyle) {
	          fTotalTextStyle = style;
	        } else if (style.getStyle() instanceof TLspLabelBoxStyle) {
	          fTotalLabelStyle = style;
	        } else if (style.getStyle() instanceof TLspRulerDistanceFormatStyle) {
	          fTotalLabelContentStyle = style;
	        }
	      }
	    }

	    //fTerrainRulerController.setLineStyler(lineStyler);
	    //fTerrainRulerController.setCircleStyler(circleStyler);
	    fTerrainRulerController.setLabelStyler(labelStyler);
	  }
	
	 private void updateUnit(TLcdDistanceFormat distanceFormat){
		 distanceFormat.setMaximumFractionDigits(2);
		 //distanceFormat.format(df_line);
		 //NumberFormat s = new DecimalFormat( "#,###.##" );
		 //distanceFormat.format(s);
         TLspRulerSegmentLabelContentStyle segmentContentStyle = (TLspRulerSegmentLabelContentStyle) fSegmentLabelContentStyle.getStyle();
         fSegmentLabelContentStyle.setStyle(segmentContentStyle.asBuilder().distanceFormat(distanceFormat).build());
         TLspRulerDistanceFormatStyle totalContentStyle = (TLspRulerDistanceFormatStyle) fTotalLabelContentStyle.getStyle();
         fTotalLabelContentStyle.setStyle(totalContentStyle.asBuilder().distanceFormat(distanceFormat).build());
	 }
	 
	 private void setColors(Component comp)
	 {
		 comp.setBackground(back_color);
		 comp.setForeground(text_color);
	 }
	 
	 private JPanel buildToolsPanel() {

	    GridBagLayout gbl=new GridBagLayout();
	    GridBagConstraints gbc=new GridBagConstraints();
	    
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(gbl);
		//menuPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		setColors(menuPanel);
			
		tools_single_panel = new JPanel(new GridLayout( 5, 1 ));
		setColors(tools_single_panel);
		//tools_single_panel.setVisible(false);
		tools_single_panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		 
		JButton show_hide_button = new JButton(">>");
		Font font = new Font("Arial Black",Font.PLAIN, 10);
		show_hide_button.setFont(font);
		show_hide_button.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		setColors(show_hide_button);
		
		show_hide_button.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				//It's visible
				if(show_hide_panel == true)
				{
					//Hide Tools panel
					//tools_single_panel.setVisible(false);
					menuPanel.remove(tools_single_panel);
					show_hide_button.setText(">>");
					menuPanel.repaint();
					
				}
				//Is not visible
				else 
				{
					//Show Tools Panel
					//tools_single_panel.setVisible(true);
					gbc.fill=GridBagConstraints.NORTH;
			        gbc.insets=new Insets(10,0,0,0);
			        gbc.anchor=GridBagConstraints.NORTHWEST;
			        gbc.gridwidth = GridBagConstraints.REMAINDER;
					menuPanel.add(tools_single_panel, gbc);
					show_hide_button.setText("<<");
					menuPanel.repaint();
				}
				show_hide_panel = !show_hide_panel;			
			}
		});
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//Measure Panel
		JRadioButton decimaleRadioButton = new JRadioButton( "GD" );
	    decimaleRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	    	  default_unit_location = unity_id_GD;
	      }
	    });
	    decimaleRadioButton.setSelected(true);
	    setColors(decimaleRadioButton);
	    
	    JRadioButton gradeRadioButton = new JRadioButton( "GMS" );
	    gradeRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	    	  default_unit_location = unity_id_GMS;
	      }
	    });
	    setColors(gradeRadioButton);
	    
	    JRadioButton mgrsRadioButton = new JRadioButton( "MGRS" );
	    mgrsRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        default_unit_location = unity_id_MGRS;
	      }
	    });
	    setColors(mgrsRadioButton);
	    
	    JRadioButton utmRadioButton = new JRadioButton( "UTM" );
	    utmRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        default_unit_location = unity_id_UTM;
	      }
	    });
	    setColors(utmRadioButton);
	    
	    JRadioButton metersRadioButton = new JRadioButton(getAbbreviationOfUnity(unity_id_meter));
	    metersRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	    	  default_unit_longitude = unity_id_meter; 
	    	  updateUnit(new TLcdDistanceFormat(TLcdDistanceUnit.METRE_UNIT));
	      }
	    });
	    metersRadioButton.setSelected(true);
	    setColors(metersRadioButton);
	    
	    JRadioButton kmRadioButton = new JRadioButton( getAbbreviationOfUnity(unity_id_kilometer) );
	    kmRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	    	default_unit_longitude = unity_id_kilometer; 
	    	updateUnit(new TLcdDistanceFormat(TLcdDistanceUnit.KM_UNIT));
	      }
	    });
	    setColors(kmRadioButton);
	    
	    JRadioButton mileRadioButton = new JRadioButton( getAbbreviationOfUnity(unity_id_mile) );//1609.34 m
	    mileRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	    	  default_unit_longitude = unity_id_mile;
	    	  updateUnit(new TLcdDistanceFormat(TLcdDistanceUnit.MILE_US_UNIT));
	      }
	    });
	    setColors(mileRadioButton);
	    
	    JRadioButton nauticalMileRadioButton = new JRadioButton( getAbbreviationOfUnity(unity_id_nautical_mile ) ); // 1852 m
	    nauticalMileRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	    	  default_unit_longitude = unity_id_nautical_mile; 
	    	  updateUnit(new TLcdDistanceFormat(TLcdDistanceUnit.NM_UNIT));
	      }
	    });
	    setColors(nauticalMileRadioButton);
	    
	    JRadioButton feetRadioButton = new JRadioButton( getAbbreviationOfUnity(unity_id_feet)); // 0.3048 m
	    feetRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	    	  default_unit_longitude = unity_id_feet; 
	    	  updateUnit(new TLcdDistanceFormat(TLcdDistanceUnit.FT_UNIT));
	      }
	    });
	    setColors(feetRadioButton);
	    
	    ButtonGroup measureSelectModesGroup = new ButtonGroup();
	    measureSelectModesGroup.add(decimaleRadioButton);
	    measureSelectModesGroup.add(gradeRadioButton);
	    measureSelectModesGroup.add(mgrsRadioButton);
	    measureSelectModesGroup.add(utmRadioButton);
	    
	    ButtonGroup measurelongModesGroup = new ButtonGroup();
	    measurelongModesGroup.add(metersRadioButton);
	    measurelongModesGroup.add(kmRadioButton);
	    measurelongModesGroup.add(mileRadioButton);
	    measurelongModesGroup.add(nauticalMileRadioButton);
	    measurelongModesGroup.add(feetRadioButton);
	    
	    JPanel measurePanel = new JPanel( new GridLayout( 1, 1 ) );
	    //measureModePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
	    TitledBorder measurePanel_border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Unidades Geográficas",1,1,null,text_color);
	    measurePanel_border.setBorder(new LineBorder(border_color, 1, true));
	    measurePanel.setBorder(measurePanel_border);
	    JPanel measurelongPanel = new JPanel( new GridLayout( 1, 1 ) );
	    TitledBorder measurelongPanel_border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Unidades de longitud",1,1,null,text_color);
	    measurelongPanel_border.setBorder(new LineBorder(border_color, 1, true));
	    measurelongPanel.setBorder(measurelongPanel_border);
	    setColors(measurePanel);
	    measurePanel.add(decimaleRadioButton);
	    measurePanel.add(gradeRadioButton);
	    measurePanel.add(mgrsRadioButton);
	    measurePanel.add(utmRadioButton);
	    
	    setColors(measurelongPanel);
	    measurelongPanel.add(metersRadioButton);
	    measurelongPanel.add(kmRadioButton);
	    measurelongPanel.add(mileRadioButton);
	    measurelongPanel.add(nauticalMileRadioButton);
	    measurelongPanel.add(feetRadioButton);
	    
	    JPanel mPanel = new JPanel( new GridLayout( 2, 1 ) );
	    setColors(mPanel);
	    mPanel.add(measurePanel);
	    mPanel.add(measurelongPanel);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    //Distance Panel
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
		setColors(distance_ac_Checkbox);
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
		setColors(distance_flir_Checkbox);
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
		setColors(distance_select_track_Checkbox);
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
		setColors(distance_free_Checkbox);
	    
	    JPanel menuDistance = new JPanel( new GridLayout( 4, 1 ) );
	    //menuDistance.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
	    TitledBorder menuDistance_border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Herramientas de medición",1,1,null,text_color);
	    menuDistance_border.setBorder(new LineBorder(border_color, 1, true));
	    menuDistance.setBorder(menuDistance_border);
	    setColors(menuDistance);
	    menuDistance.add(distance_ac_Checkbox);
	    menuDistance.add(distance_flir_Checkbox);
	    menuDistance.add(distance_select_track_Checkbox);
	    menuDistance.add(distance_free_Checkbox);
	      
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    //Measure Mode Panel
	    JRadioButton geodeticMeasureModeRadioButton = new JRadioButton( "Geodésica" );
	    geodeticMeasureModeRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        fTerrainRulerController.setMeasureMode(MeasureMode.MEASURE_GEODETIC);
	      }
	    });
	    setColors(geodeticMeasureModeRadioButton);

	    JRadioButton rhumbLineMeasureModeRadioButton = new JRadioButton( "Acimut" );
	    rhumbLineMeasureModeRadioButton.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        fTerrainRulerController.setMeasureMode(MeasureMode.MEASURE_RHUMB);
	      }
	    });
	    setColors(rhumbLineMeasureModeRadioButton);
	    
	    geodeticMeasureModeRadioButton.setSelected(true);

	    terrainModeCheckbox = new JCheckBox( "Sobre terreno" );
	    terrainModeCheckbox.setSelected(false);
	    terrainModeCheckbox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean useTerrain = e.getStateChange() == ItemEvent.SELECTED;
	        fTerrainRulerController.setUseTerrain(useTerrain);
	      }
	    });
	    setColors(terrainModeCheckbox);
	    fTerrainRulerController.setUseTerrain(false);
	    
	    ButtonGroup measureModesGroup = new ButtonGroup();
	    measureModesGroup.add(geodeticMeasureModeRadioButton);
	    measureModesGroup.add(rhumbLineMeasureModeRadioButton);

	    distance_mode_panel = new JPanel( new GridLayout( 2, 2 ) );
	    //distance_panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
	    TitledBorder distance_mode_panel_border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Distancia libre",1,1,null,text_color);
	    distance_mode_panel_border.setBorder(new LineBorder(border_color, 1, true));
	    distance_mode_panel.setBorder(distance_mode_panel_border);
	    setColors(distance_mode_panel);
	    distance_mode_panel.add(geodeticMeasureModeRadioButton);
	    distance_mode_panel.add(rhumbLineMeasureModeRadioButton);
	    distance_mode_panel.add(terrainModeCheckbox);
	    //distance_mode_panel.setVisible(false);
	    
	    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
	    JPanel menuRadar = new JPanel( new GridLayout( 2, 1 ) );
	    TitledBorder menuRadarborder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Radar",1,1,null,text_color);
	    menuRadarborder.setBorder(new LineBorder(border_color, 1, true));
	    menuRadar.setBorder(menuRadarborder);
	    setColors(menuRadar);
	    view_radar = new JCheckBox( "Ver Radar" );
	    view_radar.setSelected(true);
	    view_radar.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean active = e.getStateChange() == ItemEvent.SELECTED;
	        if(layer_polyline_asterix != null)
	        {
		        if(active)
		        {
		        	view_rute.setEnabled(true);
		        	layer_asterix.setVisible(true);
		        	if(view_rute.isSelected() == true)
		        	{
		        		layer_polyline_asterix.setVisible(true);
		        	}
		        	else
		        	{
		        		layer_polyline_asterix.setVisible(false);
		        	}
		        }
		        else
		        {
		        	view_rute.setEnabled(false);
		        	layer_asterix.setVisible(false);
		        	layer_polyline_asterix.setVisible(false);
		        }
	        }
	      }
	    });
	    setColors(view_radar);
	    view_rute = new JCheckBox( "Ver trayectoria" );
	    view_rute.setSelected(true);
	    view_rute.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean active = e.getStateChange() == ItemEvent.SELECTED;
	        if(layer_polyline_asterix != null)
	        {
		        if(active)
		        {
		        	layer_polyline_asterix.setVisible(true);
		        }
		        else
		        {
		        	layer_polyline_asterix.setVisible(false);
		        }
	        }
	      }
	    });
	    setColors(view_rute);
	    menuRadar.add(view_radar);
	    menuRadar.add(view_rute);
	    
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//Prediction Panel
	    prediction_select_time_Checkbox = new JCheckBox( "Track seleccionado" );
	    prediction_select_time_Checkbox.setSelected(false);
	    prediction_select_time_Checkbox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean active = e.getStateChange() == ItemEvent.SELECTED;
	        if(active)
	        {
	          prediction_all_tracks_time_Checkbox.setSelected(false);
	          prediction_select_mode = true;
	        }
	        else
	        {
	          prediction_select_mode = false;
	          clearTrackLayer(track_layer_points_tracks_predicted);
	          prediction_point_create = false;
	          prediction_tracks_painted.clear();
	        }
	        
	        if(prediction_all_tracks_time_Checkbox.isSelected() || prediction_select_time_Checkbox.isSelected())
	        {
	        	if(prediction_time_hours_spinner.isEnabled() == false)
	        	{
	        		prediction_time_hours_spinner.setEnabled(true);
	        	}
	        	if(prediction_time_minutes_spinner.isEnabled() == false)
	        	{
	        		prediction_time_minutes_spinner.setEnabled(true);
	        	}
	        }
	        else
	        {
	        	if(prediction_time_hours_spinner.isEnabled())
	        	{
	        		prediction_time_hours_spinner.setEnabled(false);
	        	}
	        	if(prediction_time_minutes_spinner.isEnabled())
	        	{
	        		prediction_time_minutes_spinner.setEnabled(false);
	        	}
	        }
	        
	      }
	    });
	    setColors(prediction_select_time_Checkbox);
	    
	    prediction_all_tracks_time_Checkbox = new JCheckBox( "Todos los tracks" );
	    prediction_all_tracks_time_Checkbox.setSelected(false);
	    prediction_all_tracks_time_Checkbox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	        boolean active = e.getStateChange() == ItemEvent.SELECTED;
	        if(active)
	        {
	          prediction_select_time_Checkbox.setSelected(false);
	          prediction_all_tracks_mode = true;
	        }
	        else
	        {
	          prediction_all_tracks_mode = false;
	          clearTrackLayer(track_layer_points_tracks_predicted);
	          prediction_point_create = false;
	          prediction_tracks_painted.clear();
	        }
	        
	        if(prediction_all_tracks_time_Checkbox.isSelected() || prediction_select_time_Checkbox.isSelected())
	        {
	        	if(prediction_time_hours_spinner.isEnabled() == false)
	        	{
	        		prediction_time_hours_spinner.setEnabled(true);
	        	}
	        	if(prediction_time_minutes_spinner.isEnabled() == false)
	        	{
	        		prediction_time_minutes_spinner.setEnabled(true);
	        	}
	        }
	        else
	        {
	        	if(prediction_time_hours_spinner.isEnabled())
	        	{
	        		prediction_time_hours_spinner.setEnabled(false);
	        	}
	        	if(prediction_time_minutes_spinner.isEnabled())
	        	{
	        		prediction_time_minutes_spinner.setEnabled(false);
	        	}
	        }
	        
	      }
	    });
	    setColors(prediction_all_tracks_time_Checkbox);
	    
	    //Creacion del JSpinner y valor incial.	
 		SpinnerModel sm = new SpinnerNumberModel(120, 1, 1440, 1); //default value,lower bound,upper bound,increment by	
 		prediction_time_minutes_spinner = new JSpinner(sm);
 		prediction_time_minutes_spinner.setEnabled(false);
 		//JFormattedTextField tf = ((JSpinner.DefaultEditor)prediction_time_spinner.getEditor()).getTextField();
 		//tf.setEditable(false);
 		prediction_time_minutes_spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//System.out.println("Tiempo predicción"+ prediction_time_minutes_spinner.getValue().toString());
				prediction_time_hours_spinner.setValue( Double.parseDouble(prediction_time_minutes_spinner.getValue().toString()) / 60 );
			}
		
		});
 		setColors(prediction_time_minutes_spinner);
 		
 		SpinnerModel smh = new SpinnerNumberModel(2, 1, 24, 1); //default value,lower bound,upper bound,increment by	
 		prediction_time_hours_spinner = new JSpinner(smh);
 		prediction_time_hours_spinner.setEnabled(false);
 		//JFormattedTextField tf = ((JSpinner.DefaultEditor)prediction_time_spinner.getEditor()).getTextField();
 		//tf.setEditable(false);
 		prediction_time_hours_spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//System.out.println("Tiempo predicción"+ prediction_time_hours_spinner.getValue().toString());
				prediction_time_minutes_spinner.setValue( Double.parseDouble(prediction_time_hours_spinner.getValue().toString()) * 60 );
			}
		
		});
 		setColors(prediction_time_hours_spinner);

	    JPanel menuPredictionPanel = new JPanel( new GridLayout( 2, 1 ) );
	    //menuPredictionPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
	    
	    TitledBorder menuPredictionPanel_border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Herramienta de predicción",1,1,null,text_color);
	    menuPredictionPanel_border.setBorder(new LineBorder(border_color, 1, true));
	    menuPredictionPanel.setBorder(menuPredictionPanel_border);
	    setColors(menuPredictionPanel);
	    
	    JPanel sub_panel_1 = new JPanel( new GridLayout( 2, 1 ) );
	    setColors(sub_panel_1);
	    sub_panel_1.add(prediction_all_tracks_time_Checkbox);
	    sub_panel_1.add(prediction_select_time_Checkbox);
	    
	    
	    JPanel sub_panel = new JPanel( new GridLayout( 2, 2 ) );
	    setColors(sub_panel);
	    sub_panel.add(prediction_time_hours_spinner);
	    JLabel hours = new JLabel("horas");
	    setColors(hours);
	    hours.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
	    sub_panel.add(hours);
	    
	    sub_panel.add(prediction_time_minutes_spinner);
	    JLabel minutes = new JLabel("minutos");
	    setColors(minutes);
	    minutes.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
	    sub_panel.add(minutes);
	    
	    menuPredictionPanel.add(sub_panel_1);
	    menuPredictionPanel.add(sub_panel);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    
	    tools_single_panel.add(mPanel);
	    tools_single_panel.add(menuPredictionPanel);
	    tools_single_panel.add(menuDistance);
	    tools_single_panel.add(menuRadar);
	    
	    //Test play and stop recording
	    star_rec = new JButton("Start");
	    stop_rec = new JButton("Stop");
	    setColors(star_rec);
	    setColors(stop_rec);
	    star_rec.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				startRec(28);
			}
		});
	    stop_rec.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				stopRec(28);
			}
		});
	    JPanel panel_recording = new JPanel();
	    setColors(panel_recording);
	    panel_recording.add(star_rec);
	    panel_recording.add(stop_rec);
	    tools_single_panel.add(panel_recording);
	    
	    //tools_single_panel.add(distance_mode_panel);
		
		gbc.fill=GridBagConstraints.NORTH;
        gbc.anchor=GridBagConstraints.NORTHEAST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets=new Insets(0,0,0,0);
        menuPanel.add(show_hide_button, gbc);
//        gbc.insets=new Insets(10,0,0,0);
//        gbc.anchor=GridBagConstraints.NORTHWEST;
//        gbc.gridwidth = GridBagConstraints.REMAINDER;
        //menuPanel.add(tools_single_panel, gbc);
        
		return menuPanel;
	}
	 	
	public double getPredictionTimeInMinutes()
	{
		return Double.parseDouble(prediction_time_minutes_spinner.getValue().toString());
	}
	
	public void centerMap(double lat, double lng ) 
	{
		TLcdLonLatPoint mouse = new TLcdLonLatPoint(lng, lat);
		/*if(prediction_time_point != null)
		{
			mouse =  new TLcdLonLatPoint(prediction_time_point.getX(),prediction_time_point.getY());
		}*/
		try {
			//navigationUtil.center(mouse,new TLcdGeodeticReference());
			navigationUtil.animatedCenter(mouse,new TLcdGeodeticReference());
			//opacity(track_layer_points_tracks,1.0f);
		} catch (TLcdOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	public void north() {	
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
		float opacity = 1.0f;
	    System.out.println( "newOpacity: " + opacity );	    
	    TLspLayerStyle layerStyle = fTrackLayers.get(aLayerId).getLayerStyle(); 	
	    TLspLayerStyle new_layerStyle = layerStyle.asBuilder().opacity(opacity).build();
	    //fTrackLayersStyles.replace(aLayerId, new_layerStyle );
	    getView().getLayer(aLayerId).setLayerStyle(new_layerStyle);
	    /*TLspRulerSegmentLabelContentStyle segmentContentStyle = (TLspRulerSegmentLabelContentStyle) fSegmentLabelContentStyle.getStyle();
        fSegmentLabelContentStyle.setStyle(segmentContentStyle.asBuilder().distanceFormat(distanceFormat).build());*/
	}
	
	public void updateCoords()
	{
		TLcdLonLatHeightPoint mouse_position = fMouseEventHandler.getMousePosition();
		String new_coords = "";
		String ac_coords = "";
		String mouse_coords = "";
		String flir_coords = "";
		  if(default_unit_location == unity_id_GD)
		  {
			  if(ac_point != null) 
				  ac_coords = df.format(ac_point.getLat()) + "," + df.format(ac_point.getLon());
			  if(mouse_position != null) 
				  mouse_coords = df.format(mouse_position.getLat()) + "," + df.format(mouse_position.getLon());
			  if(flir_point != null) 
				  flir_coords = df.format(flir_point.getLat()) + "," + df.format(flir_point.getLon());
		  }
		  else if(default_unit_location == unity_id_GMS)
		  { 
			  if(ac_point != null) 
				  ac_coords = getFormatDMS(ac_point.getLat(), ac_point.getLon());
			  if(mouse_position != null) 
				  mouse_coords = getFormatDMS(mouse_position.getLat(), mouse_position.getLon());
			  if(flir_point != null) 
				  flir_coords = getFormatDMS(flir_point.getLat(), flir_point.getLon());
		  }
		  else if(default_unit_location == unity_id_UTM)
		  {
			  if(ac_point != null) 
				  ac_coords = getFormatUTM(ac_point.getLat(), ac_point.getLon());
			  if(mouse_position != null) 
				  mouse_coords = getFormatUTM(mouse_position.getLat(), mouse_position.getLon());
			  if(flir_point != null) 
				  flir_coords = getFormatUTM(flir_point.getLat(), flir_point.getLon());
		  }
		  else if(default_unit_location == unity_id_MGRS)
		  {
			  if(ac_point != null) 
				  ac_coords = getFormatMGRS(ac_point.getLat(), ac_point.getLon());
			  if(mouse_position != null) 
				  mouse_coords = getFormatMGRS(mouse_position.getLat(), mouse_position.getLon());
			  if(flir_point != null) 
				  flir_coords = getFormatMGRS(flir_point.getLat(), flir_point.getLon());
		  }
			
		  new_coords = ( 
		    "A/C: " + ac_coords  +  
			"             Mouse: " + mouse_coords + 
			"             FLIR: " + flir_coords 
		  );
		  coords.setText(new_coords);
	}
	
	public void setBasePath(String new_base_path) {
		base_path = new_base_path;
		update_icons_path();
	}
	
	public void update_icons_path() {
		track_icon_path = (
			( base_path.endsWith("/") ? base_path : base_path + "/" ) + 
			( icon_folder_path_track.endsWith("/") ? icon_folder_path_track : icon_folder_path_track + "/" ) +
			track_icon_name
		);
		marck_icon_path = (
			( base_path.endsWith("/") ? base_path : base_path + "/" )+ 
			( icon_folder_path_marca.endsWith("/") ? icon_folder_path_marca : icon_folder_path_marca + "/" ) +
			marca_icon_name
		);
		ac_icon_path = (
			( base_path.endsWith("/") ? base_path : base_path + "/" )+ 
			( icon_folder_path_ac.endsWith("/") ? icon_folder_path_ac : icon_folder_path_ac + "/" ) +
			ac_icon_name
		);
		flir_icon_path = (
			( base_path.endsWith("/") ? base_path : base_path + "/" )+ 
			( icon_folder_path_flir.endsWith("/") ? icon_folder_path_flir : icon_folder_path_flir + "/" ) +
			flir_icon_name
		);
		System.out.println("Track icon " + ( new File(track_icon_path).exists() ? "Correct: " : "Error: " ) + track_icon_path);
		System.out.println("Marck icon " + ( new File(track_icon_path).exists() ? "Correct: " : "Error: " ) + marck_icon_path);
		System.out.println("AC icon " + ( new File(track_icon_path).exists() ? "Correct: " : "Error: " ) +  ac_icon_path);
		System.out.println("FLIR icon "+ ( new File(track_icon_path).exists() ? "Correct: " : "Error: " ) + flir_icon_path);
	}
	
	public void updateAllIconStyles()
	{
		update_styles = true;
		update_icons_path();
		System.out.println("updateAllIconStyles");
		//updateTracksIconStyle();
		//updateMarcksIconStyle();
		updateAcIconStyle();
		updateFlirIconStyle();
		update_styles = false;
		
		timer_delete.schedule(deleteExtraIconFiles, 3, TimeUnit.SECONDS);
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
	
	public void delete_files() 
	{
		if(files_to_delete.size() > 0)
		{
			for (String file : files_to_delete) {
				File fichero = new File(file);
			    fichero.delete();
			}
			files_to_delete.clear();
		}
	}
	
//	public void updateTracksIconStyle() 
//	{
//		//Tracks
//		int id_layer = track_layer_points_tracks;
//		String icon_path = track_icon_path;
//		
//		removeTrackLayer(id_layer);
//		String new_icon_path = icon_path.substring(0, icon_path.lastIndexOf("."))+ "_new" + System.nanoTime() + icon_path.substring(icon_path.lastIndexOf("."));
//		System.out.println("updateTracksIconStyle " + new_icon_path);
//		FileCopy(icon_path, new_icon_path);
//		layerFactory.setIconPath(new_icon_path);
//		id_layer = addTrackLayerPointWithIcon("Traks layer", "EPSG:4326");
//		files_to_delete.add(new_icon_path);
//		
//		track_layer_points_tracks = id_layer;
//	}
	
//	public void updateTracksIconStyle(String new_icon_path) 
//	{
//		//Tracks
//		int id_layer = track_layer_points_tracks;
//		
//		removeTrackLayer(id_layer);
//		System.out.println("updateTracksIconStyle " + new_icon_path);
//		layerFactory.setIconPath(new_icon_path);
//		id_layer = addTrackLayerPointWithIcon("Traks layer", "EPSG:4326");
//		
//		track_layer_points_tracks = id_layer;
//	}
	
//	public void updateMarcksIconStyle() 
//	{
//		//Marks
//		int id_layer = track_layer_points_marks;
//		String icon_path = marck_icon_path;
//		
//		removeTrackLayer(id_layer);
//		String new_icon_path = icon_path.substring(0, icon_path.lastIndexOf("."))+ "_new" + System.nanoTime() + icon_path.substring(icon_path.lastIndexOf("."));
//		System.out.println("updateMarcksIconStyle " + new_icon_path);
//		FileCopy(icon_path, new_icon_path);
//		layerFactory.setIconPath(new_icon_path);
//		id_layer = addTrackLayerPointWithIcon("Marks layer", "EPSG:4326");
//		files_to_delete.add(new_icon_path);
//		
//		track_layer_points_marks = id_layer;
//	}
	
//	public void updateMarcksIconStyle(String new_icon_path) 
//	{
//		//Tracks
//		int id_layer = track_layer_points_marks;
//		
//		removeTrackLayer(id_layer);
//		System.out.println("updateMarcksIconStyle " + new_icon_path);
//		layerFactory.setIconPath(new_icon_path);
//		id_layer = addTrackLayerPointWithIcon("Marks layer", "EPSG:4326");
//		
//		track_layer_points_marks = id_layer;
//	}
	
	public void updateAcIconStyle() 
	{
		//AC
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
	
	public void updateAcIconStyle(String new_icon_path) 
	{
		//Tracks
		int id_layer = track_layer_ac_id;
		
		removeTrackLayer(id_layer);
		System.out.println("updateAcIconStyle " + new_icon_path);
		layerFactory.setIconPath(new_icon_path);
		id_layer = addTrackLayerPointWithIcon("AC layer", "EPSG:4326");
		
		track_layer_ac_id = id_layer;
	}
	
	public void updateFlirIconStyle() 
	{	
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
	
	public void updateFlirIconStyle(String new_icon_path) 
	{
		//Tracks
		int id_layer = track_layer_flir_id;
		
		removeTrackLayer(id_layer);
		System.out.println("updateFlirIconStyle " + new_icon_path);
		layerFactory.setIconPath(new_icon_path);
		id_layer = addTrackLayerPointWithIcon("Flir layer", "EPSG:4326");
		
		track_layer_flir_id = id_layer;
	}
	
	public void setAc(String new_ac)
	{
		ac = new_ac;
		if(!ac.equals("") && ac.contains(",")) {
			String[] split = ac.split(",");
			double latitude = Double.parseDouble(split[0]);
			double longitud = Double.parseDouble(split[1]);
			double altura = split.length > 2 ? Double.parseDouble(split[2]) *1000 : 0.0; //m.
			ac_point = new TLcdLonLatHeightPoint(longitud, latitude, altura);
			//System.out.println(ac_point);
		}
	}
	
	public void setFlir(String new_flir)
	{
		flir = new_flir;
		if(!flir.equals("") && flir.contains(",")) {
			String[] split = flir.split(",");
			double latitude = Double.parseDouble(split[0]);
			double longitud = Double.parseDouble(split[1]);
			double altura = split.length > 2 ? Double.parseDouble(split[2]) *1000 : 0.0; //m.
			flir_point = new TLcdLonLatHeightPoint(longitud, latitude, altura);
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
			  //if(track_layer_points_tracks == 0 && track_layer_points_marks == 0)
			  if(puntos_creados == false)
			  {
				  try {
					create_points();
				  } 
				  catch (SQLException e) {
				  }
			  }
			  else
			  {
				  update_points();
			  }
		}
	}
	
//	public int addTrackMarckLayer(String layer_name, int type, int id_track)
//	{
//		return addTrackMarckLayer( layer_name, type, id_track, "1", "1" );
//	}
	
	public int addTrackMarckLayer(String layer_name, int type, int id_track, String identidad, String categoria)
	{
		int track_marck_layer_id;
		String path_MILSTD2525 = icon_folder_path_military_symbology + military_symbology_prefix ;
    	if(type == 1)
    	{
    		path_MILSTD2525 += "T" + identidad + "_0" + categoria;
        	//Tracks
    		if(new File(path_MILSTD2525).exists() == false) 
    		{
    			System.out.println("No existe: "+ path_MILSTD2525);
    			path_MILSTD2525 = track_icon_path;
    		}
    	}
    	else
    	{
    		path_MILSTD2525 += "M" + identidad + "_0" + categoria;
    		//Marks
    		if(new File(path_MILSTD2525).exists() == false) 
    		{
    			System.out.println("No existe: "+ path_MILSTD2525);
    			path_MILSTD2525 = marck_icon_path;
    		}
    	}

    	layerFactory.setIconPath(path_MILSTD2525);
		//track_layer_points_marks = addTrackLayerPointWithIcon("Tracks/Marks layer", "EPSG:4326");
		track_marck_layer_id = addTrackLayerPointWithIcon(layer_name, "EPSG:4326");
		
    	tracksLayers.put(
    	   id_track, 
		   new HashMap<String,Object>() {
    	   {
    			   put("type", type);
    			   put("layer_id", track_marck_layer_id);
		  	  }
		   }
    	);
    	return track_marck_layer_id;
	}
		
	public void create_points() throws SQLException {
		List<JSONObject> tracks_list = conection.getTracks();
		for(int t=0; t < tracks_list.size(); t++ )
		{
			
			int type = (int)tracks_list.get(t).get("tipo_dato");
            if(type == 1 || type == 2)
            {
            	int id_track = (int) tracks_list.get(t).get("id_dato_tactico");
            	String layer_name = ((JSONObject)tracks_list.get(t)).get("nombre").toString();
            	
            	JSONObject datos_json = (JSONObject) ((JSONObject)tracks_list.get(t)).get("datos_json");
            	String categoria = "1";
            	String identidad = "1";
            	if(datos_json.containsKey("categoria"))
            	{
            		categoria = datos_json.get("categoria").toString();  
            	}
            	if(datos_json.containsKey("identidad"))
            	{
            		identidad = datos_json.get("identidad").toString();  
            	}
            	
            	addTrackMarckLayer(layer_name, type, id_track, identidad, categoria);
            }
		}

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
		//Predicted layer
		layerFactory.setTextColor("#211f01");
		layerFactory.setLineColor("#8e3004");
		layerFactory.setHaloColor("#ffffff");
//		layerFactory.setIconPath(track_icon_path);
		track_layer_points_tracks_predicted = addTrackLayerPredicted("Traks layer Predicted", "EPSG:4326");
		puntos_creados = true;
//		layerFactory.setDefautlsColor();
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
  
  public void setMisionID(int ID) 
  {
	  if(db_connection_successful)
	  {
		  System.out.println("setMisionID " + ID);
		  conection.setId_mision(String.valueOf(ID));
		  mission_ID = ID;
		  return;
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
	  recording_type = "";
		try {
			JSONObject track = conection.getTrackbyID(ID);
			int type = track.containsKey("tipo_dato") ? (int)track.get("tipo_dato") : 0;
			if(type == 5) {
				recording_type = "Polyline";
			}
			else if(type == 3 || type == 4)
			{
				recording_type = "Polygon";
			}
			if(!recording_type.equals("")) {
				fMouseEventHandler.setDrawPolygonMode(true);
				draw_polygon_mode = true;	
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	  draw_polygon_mode = false;
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
			  conection.insertExtraPoint(track, (float) point.getY(), (float)point.getX());
			}
			conection.updateTrack(track);
			fMouseEventHandler.clearPaintPoints();
			//removeTrackLayer(track_layer_draw_polygons);
			updatePolygon(track_layer_draw_polygons, polygon_user_clicks_track_id, fMouseEventHandler.getPaintPoints(), 0);
			create_polygon_user_clicks = false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  rec_polygon_id = -1;
	  recording_type = "";
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
			  TLcdLonLatHeightPoint mouse_position = fMouseEventHandler.getMousePosition();
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
				  double[] element_location = getElementLocation(select_track_point_id);
	        	  if(element_location.length == 3)
	        	  {
		        	  double x = element_location[0];
		        	  double y = element_location[1];
		        	  double z = element_location[2];
		        	  ILcdPoint select_track_point = new TLcdLonLatHeightPoint(x,y,z);
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
		 double dLat  = rad(end_point.getY() - start_point.getY());
		 double dLong = rad(end_point.getX() - start_point.getX());
		
		 double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		          Math.cos(rad(start_point.getY())) * Math.cos(rad(end_point.getY())) * Math.sin(dLong/2) * Math.sin(dLong/2);
		 double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		 double distance = R * c; //in km.
		 //return distance;
		 return distance * 1000; // in m.
	 }
	 return 0.0;
  }
  
  public void startDistanceMode() {
	System.out.println("startDistanceMode *");
	defaul_controller = getView().getController();
	//distance_mode_panel.setVisible(true);
	tools_single_panel.add(distance_mode_panel);
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
  
  public void stopDistanceMode() {
	  if(distance_mode)
	  {
		  System.out.println("stopDistanceMode *");
		  //distance_mode_panel.setVisible(false);
		  tools_single_panel.remove(distance_mode_panel);
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
  
  public int addTrackLayerNoSelectionPolygon(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 6);
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
  
  public int addTrackLayerPredicted(String aLayerName, String aEPSG) {
	  return addTrackLayer(aLayerName, aEPSG, 7);
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
		case 6:
			factory = new TLspCompositeLayerFactory(layerFactory);
			TLcdVectorModel model_no_select_polygon = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor(aLayerName, "SolidShapes", "Solid fill shapes no select"));
			Collection<ILspLayer> layers_no_select_polygon = factory.createLayers(model_no_select_polygon);
			layer = layers_no_select_polygon.iterator().next();
			break;	
		case 7:
			factory = new TLspCompositeLayerFactory(layerFactory);
			TLcdVectorModel model_predited = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor(aLayerName, "Predicted", "Predicted"));
			Collection<ILspLayer> layers_predicted = factory.createLayers(model_predited);
			layer = layers_predicted.iterator().next();
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
      //fTrackLayersStyles.put(layerId, layer.getLayerStyle());
      
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
        	//select_track_point_id = 0;
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
      //fTrackLayersStyles.remove(aLayerId);
      // View operations need to be executed on the paint thread.
      getView().getGLDrawable().invokeLater((gl) -> {
        getView().removeLayer(layer);
        return true;
      });
    }
  }
  
  public void addElementTrack(int id, double latitude, double longitud, double altura) 
  {
	  addElementTrack(id, latitude, longitud, altura, "Track");
  }
  
  public void addElementPolygon(int id, double latitude, double longitud, double altura) 
  {
	  addElementTrack(id, latitude, longitud, altura, "Polygon");
  }
  
  public void addElementPolyline(int id, double latitude, double longitud, double altura) 
  {
	  addElementTrack(id, latitude, longitud, altura, "Polyline");
  }
  
  public void addElementLine(int id, double latitude, double longitud, double altura) 
  {
	  addElementTrack(id, latitude, longitud, altura, "Line");
  }
  
  public void addElementCircle(int id, double latitude, double longitud, double altura) 
  {
	  addElementTrack(id, latitude, longitud, altura, "Circle");
  }
  
  public void addElementTrack(int id, double latitude, double longitud, double altura, String type) 
  {
	  fElementsTracks.put(
			  id, 
			  new HashMap<String,Object>() {
				  {
					  	put("type", type);
					  	put("latitude", latitude);
					  	put("longitud", longitud);
						put("altura", altura);
			  	  }
			  }
	  );
  }
  
  public void updateElement(int id, double latitude, double longitud, double altura) 
  {
	  if(fElementsTracks.containsKey(id))
	  {
		  fElementsTracks.get(id).replace("latitude", latitude);
 		  fElementsTracks.get(id).replace("longitud", longitud);
		  fElementsTracks.get(id).replace("altura", altura);
	  }
  }
  
  public static Map<String, Object> getElement(int id) 
  {
	  if(fElementsTracks.containsKey(id))
	  {
		  return fElementsTracks.get(id);
	  }
	  return null;
  }
  
  public static double[] getElementLocation(int id) 
  {
	  Map<String, Object> element = getElement(id);
	  double[] location =  { 0.0, 0.0, 0.0 };
	  location[0] = element.containsKey("lon") ? (double)element.get("lon") : 0.0 ;
	  location[1] = element.containsKey("lat") ? (double)element.get("lat") : 0.0 ;
	  location[2] = element.containsKey("heigth") ? (double)element.get("heigth") : 0.0 ;
	  return location;
  }
  
  public void removeElementTrack(int id) 
  {
	  if(fElementsTracks.containsKey(id))
	  {
		  fElementsTracks.remove(id);
	  }
  }
  

  public void addTrack(int aLayerId, int aTrackId, double latitude, double longitud, double altura, String aCallSign, long aTimeStamp)
  {
	  Map<String,Object> data = new HashMap<>();
	  data.put("name", "");
      data.put("description", ""); 
      data.put("course", ""); 
      data.put("speed", "");
      data.put("category", "");
      data.put("identity", ""); 
      addTrack(aLayerId, aTrackId, latitude, longitud, altura, aCallSign, aTimeStamp, data);
  }
  
  public void addTrack(int aLayerId, int aTrackId, double latitude, double longitud, double altura, String aCallSign, long aTimeStamp, Map<String,Object> data) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      ILcdModel model = layer.getModel();
      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
        TLcdDataObject track = new TLcdDataObject(TrackDataTypes.TRACK_PLAN_DATA_TYPE);
        ILcd3DEditablePoint location;
        if (model.getModelReference() instanceof ILcdGeodeticReference) {
          location = new TLcdLonLatHeightPoint(longitud, latitude, altura) {
            @Override
            public String toString() {
              return getFormattedTrackLocation(this);
            }
          };
        } else {
          location = new TLcdXYZPoint(longitud, latitude, altura){
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
        String identity = data.containsKey("identity") ? data.get("identity").toString() : "";
        String label = "(" + "A:" + Double.toString(altura) + ","  + "C:" + course + "," + "V:" + speed + ")";
        
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
        if(!identity.equals(""))
        {
        	track.setValue(TrackDataTypes.IDENTITY, identity);
        }
        if(track_layer_ac_id != aLayerId && track_layer_flir_id != aLayerId )
        {
        	track.setValue(TrackDataTypes.LABEL, label);
        }
        model.addElement(track, ILcdModel.FIRE_LATER);
        addElementTrack(aTrackId,longitud,latitude,altura);
      }
      model.fireCollectedModelChanges();
    }
  }
  

  public void updateTrack(int aLayerId, int aTrackId, double latitude, double longitude, double altura, long aTimeStamp) {
	  Map<String,Object> data = new HashMap<>();
	  data.put("name", "");
      data.put("description", ""); 
      data.put("course", ""); 
      data.put("speed", "");
      data.put("category", "");
      data.put("identity", ""); 
	  updateTrack(aLayerId, aTrackId, latitude, longitude, altura, aTimeStamp, data);
  }
  public void updateTrack(int aLayerId, int aTrackId, double latitude, double longitude, double altura, long aTimeStamp, Map<String,Object> data) {
    ILspLayer layer = fTrackLayers.get(aLayerId);
    if (layer != null) {
      ILcdDataObject track = getTrack(aLayerId, aTrackId);
      if (track != null) {
        ILcdModel model = layer.getModel();
        try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
          String course = data.containsKey("course") ? data.get("course").toString() : "";
		  String speed = data.containsKey("speed") ? data.get("speed").toString() : "";
		  String label = "(" + "A:" + Double.toString(altura) + ","  + "C:" + course + "," + "V:" + speed + ")";	
        	
          ((ILcd3DEditablePoint) track.getValue(TrackDataTypes.LOCATION)).move3D(longitude, latitude, altura);
          //track.setValue(TrackDataTypes.TIMESTAMP, aTimeStamp);
          if(track_layer_ac_id != aLayerId && track_layer_flir_id != aLayerId )
          {	 
        	  track.setValue(TrackDataTypes.LABEL, label);
          }
          model.elementChanged(track, ILcdModel.FIRE_LATER);
          updateElement(aTrackId,latitude,longitude,altura);
        }
        model.fireCollectedModelChanges();
      }
    }
  }
  
  
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
	        ILcdPoint first_point = polygon.getPoint(0);
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
					return getFormattedTrackLocation(first_point);
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
					return getFormattedTrackLocation(first_point);
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
	        addElementPolygon(aTrackId, first_point.getX(), first_point.getY(), first_point.getZ());
	        
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
        	ILcdPoint first_point = polygon.getPointCount() > 0 ? polygon.getPoint(0): null;
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
			    	return getFormattedTrackLocation(first_point);
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
					return getFormattedTrackLocation(first_point);
			    }
			  };
			}
          track.setValue(PolygonDataTypes.LOCATION, location);
          //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
          //track.setValue(PolygonDataTypes.NAME, "Nombre");
	      //track.setValue(PolygonDataTypes.DESCRIPTION, "Descripción");
          model.elementChanged(track, ILcdModel.FIRE_LATER);
          if(first_point != null)
          {
          updateElement(aTrackId, first_point.getX(), first_point.getY(), first_point.getZ());
          }
        }
        model.fireCollectedModelChanges();
      }
    }
  }
  
  public void addPolyline(int aLayerId, int aTrackId, TLcd3DEditablePointList polyline, String aCallSign, long aTimeStamp)
  {
	  Map<String,Object> data = new HashMap<>();
	  data.put("name", "");
      data.put("description", ""); 
      addPolyline(aLayerId, aTrackId, polyline, aCallSign, aTimeStamp, data);
  }
  
  public void addPolyline(int aLayerId, int aTrackId, TLcd3DEditablePointList polyline, String aCallSign, long aTimeStamp, Map<String,Object> data) {
	    ILspLayer layer = fTrackLayers.get(aLayerId);
	    if (layer != null) {
	      ILcdModel model = layer.getModel();
	      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
	        TLcdDataObject track = new TLcdDataObject(PolygonDataTypes.TRACK_PLAN_DATA_TYPE);
	        ILcdPoint first_point = polyline.getPoint(0);
	        ILcd3DEditablePointList location;
	        if (model.getModelReference() instanceof ILcdGeodeticReference) {
	          //location = new TLcdLonLatHeightPolygon(generate3DRandomPolygon()) {
	          location = new TLcdLonLatHeightPolyline(polyline) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(first_point);
	            }
	          };
	        } else {
	          //location = new TLcdXYZPolygon(generate3DRandomPolygon()) {
	           location = new TLcdXYZPolyline(polyline) {
	            @Override
	            public String toString() {
	              return getFormattedTrackLocation(first_point);
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
	        addElementPolyline(aTrackId, first_point.getX(), first_point.getY(), first_point.getZ());
	        
	      }
	      catch(Exception e)
	      {
	    	  System.out.println(e);
	      }
	      model.fireCollectedModelChanges();
	    }
  }
  
  public void updatePolyline(int aLayerId, int aTrackId, TLcd3DEditablePointList polyline, long aTimeStamp)
  {
	  Map<String,Object> data = new HashMap<>();
	  data.put("name", "");
      data.put("description", ""); 
      updatePolyline(aLayerId, aTrackId, polyline, aTimeStamp, data);
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
        	ILcdPoint first_point = polyline.getPoint(0);
			ILcd3DEditablePointList location;
			if (model.getModelReference() instanceof ILcdGeodeticReference) {
			  location = new TLcdLonLatHeightPolyline(polyline) {
			    @Override
			    public String toString() {
			      return getFormattedTrackLocation(first_point);
			    }
			  };
			} else {
			   location = new TLcdXYZPolyline(polyline) {
			    @Override
			    public String toString() {
			      return getFormattedTrackLocation(first_point);
			    }
			  };
			}
          track.setValue(PolygonDataTypes.LOCATION, location);
          //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
          //track.setValue(PolygonDataTypes.NAME, "Nombre");
	      //track.setValue(PolygonDataTypes.DESCRIPTION, "Descripción");
          model.elementChanged(track, ILcdModel.FIRE_LATER);
          updateElement(aTrackId, first_point.getX(), first_point.getY(), first_point.getZ());
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
	        double distance_meters = getDistanceTwoPoints(start_point, end_point);
	        double distance_select_unity = meterToSelectUnityConvert(distance_meters);
	        String abrev = getAbbreviationOfSelectUnity();
	        String distance_label = df_line.format(distance_select_unity ) + " " + abrev;
	        track.setValue(LineDataTypes.ID, aTrackId);
	        track.setValue(LineDataTypes.LOCATION, location);
	        //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
	        track.setValue(LineDataTypes.CALLSIGN, aCallSign);
	        track.setValue(LineDataTypes.LABEL, distance_label);
	        model.addElement(track, ILcdModel.FIRE_LATER);
	        addElementLine(aTrackId, start_point.getX(), start_point.getY(), start_point.getZ());
	        
	      }
	      catch(Exception e)
	      {
	    	  System.out.println(e);
	      }
	      model.fireCollectedModelChanges();
	    }
  	}
  
  public double meterToSelectUnityConvert(double value_in_meters) {
	  //default_unit_longitude;
	  Map<String, Object> select_unity_map = units_longitude.get(default_unit_longitude);
	  String operation = select_unity_map.containsKey("operation_meters_to") ? select_unity_map.get("operation_meters_to").toString() : "";
	  double value_to_parse = select_unity_map.containsKey("value_meters_to") ? Double.parseDouble(select_unity_map.get("value_meters_to").toString()) : 0.0;
	  double value_in_select_unity = 0.0;
	  switch (operation) {
		case "*":
			value_in_select_unity = value_in_meters * value_to_parse;
			break;
		case "/":
			value_in_select_unity = value_in_meters / value_to_parse;
			break;
		default:
			break;
		}
	  return value_in_select_unity;
  }

  public String getAbbreviationOfSelectUnity() {
	  Map<String, Object> select_unity_map = units_longitude.get(default_unit_longitude);
	  return select_unity_map.containsKey("abbreviation") ? select_unity_map.get("abbreviation").toString() : "";
  }
  
  public String getAbbreviationOfUnity(String unit_longitude) {
	  Map<String, Object> unity_map = units_longitude.get(unit_longitude);
	  return unity_map.containsKey("abbreviation") ? unity_map.get("abbreviation").toString() : "";
  }
  
  public String getNameOfUnity(String unit_longitude) {
	  Map<String, Object> unity_map = units_longitude.get(unit_longitude);
	  return unity_map.containsKey("name") ? unity_map.get("name").toString() : "";
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
			double distance_meters = getDistanceTwoPoints(start_point, end_point);
	        double distance_select_unity = meterToSelectUnityConvert(distance_meters);
	        String abrev = getAbbreviationOfSelectUnity();
	        String distance_label = df_line.format(distance_select_unity ) + " " + abrev;
			track.setValue(LineDataTypes.LOCATION, location);
			track.setValue(LineDataTypes.LABEL, distance_label);
			model.elementChanged(track, ILcdModel.FIRE_LATER);
			updateElement(aTrackId, start_point.getX(), start_point.getY(), start_point.getZ());
        }
        model.fireCollectedModelChanges();
      }
    }
  }
  
	public void addCircle(int aLayerId, int aTrackId, float center_point_x, float center_point_y, float center_point_z, String aCallSign, long aTimeStamp, double radius) {
		TLcdLonLatHeightPoint center = new TLcdLonLatHeightPoint(center_point_y, center_point_x, center_point_z);
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
	        
	        double radius_select_unity = meterToSelectUnityConvert(radius);
	        String abrev = getAbbreviationOfSelectUnity();
	        String radius_label = df_line.format(radius_select_unity ) + " " + abrev;

	        track.setValue(LineDataTypes.ID, aTrackId);
	        track.setValue(LineDataTypes.LOCATION, location);
	        //track.setValue(PolygonDataTypes.TIMESTAMP, aTimeStamp);
	        track.setValue(LineDataTypes.CALLSIGN, aCallSign);
	        track.setValue(LineDataTypes.LABEL, radius_label);
	        model.addElement(track, ILcdModel.FIRE_LATER);
	        addElementCircle(aTrackId, center_point.getX(), center_point.getY(), center_point.getZ());
	        
	      }
	      catch(Exception e)
	      {
	    	  System.out.println(e);
	      }
	      model.fireCollectedModelChanges();
	    }
	}
  	

    public void updateCircle(int aLayerId, int aTrackId, float center_point_x, float center_point_y, float center_point_z , long aTimeStamp, double radius) {
      TLcdLonLatHeightPoint center = new TLcdLonLatHeightPoint(center_point_y,center_point_x, center_point_z);
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
  			
  			double radius_select_unity = meterToSelectUnityConvert(radius);
 	        String abrev = getAbbreviationOfSelectUnity();
 	        String radius_label = df_line.format(radius_select_unity ) + " " + abrev;
 	        
  			track.setValue(LineDataTypes.LOCATION, location);
  			track.setValue(LineDataTypes.LABEL,radius_label);
  			model.elementChanged(track, ILcdModel.FIRE_LATER);
  			updateElement(aTrackId, center_point.getX(), center_point.getY(), center_point.getZ());
          }
          model.fireCollectedModelChanges();
        }
      }
    }

  	protected String getFormattedTrackLocation(TLcdXYZPolypoint tLcdXYZPolypoint) {
		return null;
	}
	
	protected String getFormattedTrackLocation(TLcdLonLatHeightPolypoint tLcdLonLatHeightPolypoint) {
		return null;
	}
  
  public String getFormatDMS(double lat, double lng) {
	  return (
			  convert.decimalLongitudeToDMS(lng) +
			  "," +
			  convert.decimalLatitudeToDMS(lat)
	  );
  }
	
  public String getFormatDMS(double lat, double lng, double height) {
	  return (
			  "Lat: " + convert.decimalLatitudeToDMS(lat) + 
			  " Lon: " + convert.decimalLongitudeToDMS(lng) + 
			  " Height: " + height
	  );
  }
  
  public String getFormatUTM(double lat, double lng) {
	  return (
			  convert.latLon2UTM(lat, lng)
	  );
  }
  
  public String getFormatUTM(double lat, double lng, double height) {
	  return (
			  "" + convert.latLon2UTM(lat, lng) + 
			  " Height: " + height
	  );
  }
  
  public String getFormatMGRS(double lat, double lng) {
	  return (
			  convert.latLon2MGRUTM(lat, lng)
	  );
  }
  
  public String getFormatMGRS(double lat, double lng, double height) {
	  return (
			  "" + convert.latLon2MGRUTM(lat, lng) + 
			  " Height: " + height
	  );
  }
	
  private String getFormattedTrackLocation( ILcdPoint aTrack ) {
	  TLcdLonLatHeightPoint convert_point = new TLcdLonLatHeightPoint(aTrack.getX(),aTrack.getY(),aTrack.getZ() );
	  return getFormattedTrackLocation(convert_point);
  }
  
  private String getFormattedTrackLocation(TLcdLonLatHeightPoint aTrack) {
	  
	  /*System.out.println( aTrack.getX());
	  String gms = convertLongitudeGradeMinutesSeconds( aTrack.getX());
	  System.out.println(gms);
	  double gd = convertDecimalCoord(gms);
	  System.out.println(gd);
	  System.out.println("***************");*/

	  if(default_unit_location == unity_id_GD)
	  {
		  return String.format("Lat: %.3f Lon: %.3f Height: %.3f", aTrack.getLat(), aTrack.getLon(), aTrack.getZ());
	  }
	  else if(default_unit_location == unity_id_GMS)
	  {
		  return String.format("%s",getFormatDMS(aTrack.getLat(), aTrack.getLon(), aTrack.getZ()));
	  }
	  else if(default_unit_location == unity_id_UTM)
	  {
		  return String.format("%s",getFormatUTM(aTrack.getLat(), aTrack.getLon(), aTrack.getZ()));
	  }
	  else if(default_unit_location == unity_id_MGRS)
	  {
		  return String.format("%s",getFormatMGRS(aTrack.getLat(), aTrack.getLon(), aTrack.getZ()));
	  }
	  return String.format("Lat: %.3f Lon: %.3f Height: %.3f", aTrack.getLat(), aTrack.getLon(), aTrack.getZ());
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
          removeElementTrack(aTrackId);
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
  
  private List<Integer> getAllTrackIds() {
    List<Integer> tracks_ids = new ArrayList<Integer>();
    for(int k : tracksLayers.keySet())
    {
    	int aLayerId = Integer.parseInt( tracksLayers.get(k).get("layer_id").toString() );
    	ILspLayer layer = fTrackLayers.get(aLayerId);
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
  	}
    return tracks_ids;
  }

  /**
   * Native method to pass the selected data object to the native application.
   *
   * @param aNativePeer the pointer of the native object which should receive the selected object.
   * @param aDataObject the data object
   */

  private static class BalloonContentProvider implements ILcdBalloonContentProvider {

	  @Override
	    public boolean canGetContent(final ALcdBalloonDescriptor aBalloonDescriptor) {
		  if(draw_polygon_mode == false)
	      {
			  return true;
	      }
		  return false;
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
	          		var_location = PolygonDataTypes.LOCATION;
	          		var_id = PolygonDataTypes.ID;
	          		break;
	          	case "Línea":
	          		var_location = LineDataTypes.LOCATION;
	          		var_id = LineDataTypes.ID;
		        	break;
		        default:
		        	break;
	          }
	          select_track_point_id = 0;
	          isSelectAnyElement = false;
	          if(!var_id.equals("") && !var_location.equals(""))
	          {
	        	  select_track_point_id = Integer.parseInt(((ILcdDataObject) object).getValue(var_id).toString());
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
	      
	      //tracksLayers
	      List<Integer> all_tracks_marks_ids_points = getAllTrackIds();
	      //List<Integer> all_tracks_ids_points = getAllTrackIds();
	      //List<Integer> all_marks_ids_points = getAllTrackIds();
	      //List<Integer> tracks_ids_points_tracks_database = new ArrayList<Integer>();
	      //List<Integer> tracks_ids_points_marks_database = new ArrayList<Integer>();
	      List<Integer> tracks_ids_points_marks_tracks_database = new ArrayList<Integer>();
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
//	            	  int layer_type = ( 
//	            			  type == 1 
//	            			  ? track_layer_points_tracks 
//	            			  : 
//	            			  (
//	            					  type == 2 
//	            					  ? track_layer_points_marks
//	            					  : 0
//	            			  )  
//	            	  );
	            	  int id = (int) tracks_list.get(t).get("id_dato_tactico");
	            	  int layer_type = tracksLayers.containsKey(id) ? (int) tracksLayers.get(id).get("layer_id") : -1;
	            	  
	            	  JSONObject datos_json = (JSONObject) ((JSONObject)tracks_list.get(t)).get("datos_json");
	            	  double course = 0.0;
	            	  if(datos_json.containsKey("rumbo"))
	            	  {
	            		  data.put("course", datos_json.get("rumbo").toString());
	            		  course = Double.parseDouble(datos_json.get("rumbo").toString());
	            	  }
	            	  double speed = 0.0;
	            	  if(datos_json.containsKey("velocidad"))
	            	  {
	            		  data.put("speed", datos_json.get("velocidad").toString());
	            		  speed = Double.parseDouble(datos_json.get("velocidad").toString());
	            	  }
	            	  String identidad = "1";
	            	  if(datos_json.containsKey("identidad"))
	            	  {
	            		  data.put("identity", datos_json.get("identidad").toString());  
	            		  identidad = datos_json.get("identidad").toString();
	            	  }
	            	  String categoria = "1";
	            	  if(datos_json.containsKey("categoria"))
	            	  {
	            		  data.put("category", datos_json.get("categoria").toString());  
	            		  categoria = datos_json.get("categoria").toString();
	            	  }
	            	  double rango_arma = 0.0;
	            	  if(datos_json.containsKey("rango_arma"))
	            	  {
	            		  rango_arma = Double.parseDouble(datos_json.get("rango_arma").toString()) * 1000; //m.  
	            	  }
	            	  
	            	  ILcdDataObject track = getTrack(layer_type, (int) tracks_list.get(t).get("id_dato_tactico"));
	                  if (track == null) 
	                  {
	                	  if(layer_type == -1)
	                	  {
	                		  layer_type = addTrackMarckLayer(((JSONObject)tracks_list.get(t)).get("nombre").toString(), type, id, identidad, categoria);
	                	  }
	                  	  addTrack( layer_type, (int) tracks_list.get(t).get("id_dato_tactico"), point_x, point_y, point_z, "TRACK", 0, data);
	                  }
	              	  else
	              	  {
	              		updateTrack(layer_type, (int) tracks_list.get(t).get("id_dato_tactico"), point_x, point_y, point_z, 0, data);
	              		//String old_identity = track.getValue(TrackDataTypes.IDENTITY).toString();
	              		//System.out.println(old_identity);
	              		//String old_category = track.getValue(TrackDataTypes.CATEGORY).toString();
	              		//System.out.println(old_category);
//	              		if( (!old_identity.equals(identidad)) || (!old_category.equals(categoria)) )
//	              		{
//	              			System.out.println("Cambiando icono");
//	              			int layer_id = Integer.parseInt(tracksLayers.get(id).get("layer_id").toString());
//	              			removeTrackLayer(layer_id);
//	          	    	  	tracksLayers.remove(id);
//	          	    	  	layer_type = addTrackMarckLayer(((JSONObject)tracks_list.get(t)).get("nombre").toString(), type, id, identidad, categoria);
//	          	    	  	addTrack( layer_type, (int) tracks_list.get(t).get("id_dato_tactico"), point_x, point_y, point_z, "TRACK", 0, data);
//	              		}
	              	  }
	                  
	                //Prediccion de ubicación de todos los tracks
	                if(prediction_all_tracks_mode && prediction_select_mode == false && type == 1 ) 
              		{
	                	 Map<String,Object> predicted_coords = convert.estimaDirectaByKmXH(point_x, point_y, getPredictionTimeInMinutes(), course, speed);
	                	  
	                	  prediction_time_point = new TLcdLonLatHeightPoint(
	            			  Double.parseDouble(predicted_coords.get("longitude").toString()),
	            			  Double.parseDouble(predicted_coords.get("latidude").toString()),
	            			  point_z
	                	  );
	                	  if(prediction_tracks_painted.contains((int) tracks_list.get(t).get("id_dato_tactico")) == false)
	                	  {
	                		  addTrack( 
		                	    track_layer_points_tracks_predicted, 
		                	    (int) tracks_list.get(t).get("id_dato_tactico"), 
		                	    Double.parseDouble(predicted_coords.get("latidude").toString()),
		                	    Double.parseDouble(predicted_coords.get("longitude").toString()), 
		                	    point_z, 
		                	    "TRACK", 
		                	    0, 
		                	    data
		                	  );
	                		  addLine(track_layer_points_tracks_predicted, Integer.parseInt(tracks_list.get(t).get("id_dato_tactico").toString() + "" + tracks_list.get(t).get("id_dato_tactico").toString() ), new TLcdLonLatHeightPoint(point_y,point_x,point_z), prediction_time_point, "Distance Prediction", 0);
	                		  prediction_tracks_painted.add((int) tracks_list.get(t).get("id_dato_tactico"));
	                	  }
	                	  else
	                	  {
	                		  updateTrack( 
	                      	    track_layer_points_tracks_predicted, 
	                      	    (int) tracks_list.get(t).get("id_dato_tactico"), 
	                      	    Double.parseDouble(predicted_coords.get("latidude").toString()),
	                      	    Double.parseDouble(predicted_coords.get("longitude").toString()), 
	                      	    point_z, 
	                      	    0,
	                      	    data  
	                      	  ); 
	                		  updateLine(track_layer_points_tracks_predicted, Integer.parseInt(tracks_list.get(t).get("id_dato_tactico").toString() + "" + tracks_list.get(t).get("id_dato_tactico").toString() ), new TLcdLonLatHeightPoint(point_y,point_x,point_z), prediction_time_point, 0);
	                	  }
              		}
	                //Prediccion de ubicación de un track seleccionado
	                else if(prediction_select_mode && prediction_all_tracks_mode == false && ((int)tracks_list.get(t).get("id_dato_tactico")) == select_track_point_id && type == 1) 
                  	{
                	  Map<String,Object> predicted_coords = convert.estimaDirectaByKmXH(point_x, point_y, getPredictionTimeInMinutes(), course, speed);
                	  
                	  prediction_time_point = new TLcdLonLatHeightPoint(
            			  Double.parseDouble(predicted_coords.get("longitude").toString()),
            			  Double.parseDouble(predicted_coords.get("latidude").toString()),
            			  point_z
                	  );
                	  
                	  if(prediction_point_create == false)
                	  {
                		  addTrack( 
	                	    track_layer_points_tracks_predicted, 
	                	    -255, 
	                	    Double.parseDouble(predicted_coords.get("latidude").toString()),
	                	    Double.parseDouble(predicted_coords.get("longitude").toString()), 
	                	    point_z, 
	                	    "TRACK", 
	                	    0, 
	                	    data
	                	  );
                		  addLine(track_layer_points_tracks_predicted, -256, new TLcdLonLatHeightPoint(point_y,point_x,point_z), prediction_time_point, "Distance Prediction", 0);
                		  prediction_point_create = true;
                	  }
                	  else
                	  {
                		  updateTrack( 
                      	    track_layer_points_tracks_predicted, 
                      	    -255, 
                      	    Double.parseDouble(predicted_coords.get("latidude").toString()),
                      	    Double.parseDouble(predicted_coords.get("longitude").toString()), 
                      	    point_z, 
                      	    0,
                      	    data
                      	  ); 
                		  updateLine(track_layer_points_tracks_predicted, -256, new TLcdLonLatHeightPoint(point_y,point_x,point_z), prediction_time_point, 0);
                	  }
                  	}
              		/////////
	                  
					  //Rango arma
					  ILspLayer layer = fTrackLayers.get(rangos_armas_layer_id);
					  if (layer != null) 
					  {
						ILcdDataObject track_ra = getTrack(rangos_armas_layer_id, (int) tracks_list.get(t).get("id_dato_tactico"));
						if (track_ra == null) {
							if(rango_arma > 0.0) 
							{
								addCircle(rangos_armas_layer_id, (int) tracks_list.get(t).get("id_dato_tactico"), point_x, point_y, 1000, "Rango Arma Track", 0, rango_arma);
							}
						}
						else {
							if(rango_arma > 0.0) 
							{
								updateCircle(rangos_armas_layer_id, (int) tracks_list.get(t).get("id_dato_tactico"), point_x, point_y, 1000, 0, rango_arma);	
							}
							else 
							{
								removeTrack(rangos_armas_layer_id, (int) tracks_list.get(t).get("id_dato_tactico"));
							}
						}
					  }
            		    
//	                  if(type==1)
//	                  {
//	                	  tracks_ids_points_tracks_database.add((int) tracks_list.get(t).get("id_dato_tactico"));
//	                  }
//	                  if(type==2)
//	                  {
//	                	  tracks_ids_points_marks_database.add((int) tracks_list.get(t).get("id_dato_tactico"));
//	                  }
	                  tracks_ids_points_marks_tracks_database.add((int) tracks_list.get(t).get("id_dato_tactico"));
	              }
	              else if(type == 3 || type == 4 || type == 5)
	              {
	            	  int id_track = (int) tracks_list.get(t).get("id_dato_tactico");
	            	  //In recording mode the original polygon will'n be paint
	            	  if(rec_polygon_id != id_track)
	            	  {
		            	  TLcd3DEditablePointList track_points = new TLcd3DEditablePointList();
		            	  List<JSONObject> extra_points = conection.getExtraPoints((JSONObject)tracks_list.get(t));
		            	  for (JSONObject extra_point : extra_points) 
		            	  {
		            		  float lat = Float.parseFloat( extra_point.get("latitud").toString());
		            		  float lng = Float.parseFloat( extra_point.get("longitud").toString());
		            		  int index = Integer.parseInt(extra_point.get("index").toString());
		            		  //Plan de vuelo
		            		  if(type == 5)
		            		  {
		            			  track_points.insert3DPoint(index, new TLcdXYZPoint(lng, lat, 10000));
		            		  }
		            		  //Visión FLIR
		            		  else if(type == 4) 
		            		  {
		            			  track_points.insert3DPoint(index, new TLcdXYZPoint(lng, lat,  point_z));
		            		  }
		            		  //Áreas (tipo 3)
		            		  else 
		            		  {
		            			  track_points.insert3DPoint(index, new TLcdXYZPoint(lng, lat, 0));  
		            		  }
		            	  }
		            	  
		            	  JSONObject datos_json = (JSONObject) ((JSONObject)tracks_list.get(t)).get("datos_json");
		                  String color_db = datos_json.get("color").toString();
		                  String color = color_db.startsWith("#") ? color_db : "#" + color_db;
		                  if(color.equals("") || color.equals("0")) {
		                	  color = layerFactory.generateColorRandom();
		                  }
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
		      
        	  double course = 0.0;
        	  data.put("course", course);
        	  double speed = 0.0;
        	  data.put("speed", speed);
        	  
	          if (track == null) {
	          	addTrack(track_layer_ac_id, track_ac_id, ac_point.getLat(), ac_point.getLon(), ac_point.getHeight(), "AC", 0, data);
	          }
	      	  else
	      	  {
	      		updateTrack(track_layer_ac_id, track_ac_id, ac_point.getLat(), ac_point.getLon(), ac_point.getHeight(), 0, data);
	      	  }
	      }
	      //FLIR point
	      if(flir_point != null) {
	    	  Map<String,Object> data = new HashMap<>();
              data.put("name", "FLIR");
              data.put("description", "Posición Camara FLIR");
		      ILcdDataObject track = getTrack(track_layer_flir_id, track_flir_id);
		      
		      double course = 0.0;
        	  data.put("course", course);
        	  double speed = 0.0;
        	  data.put("speed", speed);
        	  
	          if (track == null) {
	        	  addTrack(track_layer_flir_id, track_flir_id, flir_point.getLat(), flir_point.getLon(), flir_point.getHeight(), "FLIR", 0, data);
	          }
	      	  else
	      	  {
	      		  updateTrack(track_layer_flir_id, track_flir_id, flir_point.getLat(), flir_point.getLon(), flir_point.getHeight(), 0, data);
	      	  }
	      }
	      //Remove points tracks
//	      List<Integer> track_ids_points_traks_remove = all_tracks_marks_ids_points.stream()
//	    	        .filter(i -> !tracks_ids_points_tracks_database.contains(i))
//	    	        .collect(Collectors.toList());
//	      for (Integer track_id_point_remove : track_ids_points_traks_remove) {
//	    	  removeTrack(track_layer_points_tracks, track_id_point_remove);
//	    	  removeTrack(rangos_armas_layer_id, track_id_point_remove);
//	      }
//	      //Remove points marcks
//	      List<Integer> track_ids_points_marks_remove = all_tracks_marks_ids_points.stream()
//	    	        .filter(i -> !tracks_ids_points_marks_database.contains(i))
//	    	        .collect(Collectors.toList());
//	      for (Integer track_id_point_remove : track_ids_points_marks_remove) {
//	    	  removeTrack(track_layer_points_marks, track_id_point_remove);
//	    	  removeTrack(rangos_armas_layer_id, track_id_point_remove);
//	      }
	      
	      //Remove points tracks y marcks
	      List<Integer> track_ids_points_marks_remove = all_tracks_marks_ids_points.stream()
	    	        .filter(i -> !tracks_ids_points_marks_tracks_database.contains(i))
	    	        .collect(Collectors.toList());
//	      System.out.println("track_ids_points_marks_remove" );
//	      utils.printIntList(track_ids_points_marks_remove);
//	      System.out.println( "all_tracks_marks_ids_points" );
//	      utils.printIntList(all_tracks_marks_ids_points);
//	      System.out.println("tracks_ids_points_marks_tracks_database" );
//	      utils.printIntList(tracks_ids_points_marks_tracks_database);
	      
	      for (Integer track_id_point_remove : track_ids_points_marks_remove) {
	    	  //removeTrack(layer_id, track_id_point_remove);
	    	  int layer_id = Integer.parseInt(tracksLayers.get(track_id_point_remove).get("layer_id").toString());
	    	  removeTrackLayer(layer_id);
	    	  removeTrack(rangos_armas_layer_id, track_id_point_remove);
	    	  tracksLayers.remove(track_id_point_remove);
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
		      TLcdLonLatHeightPoint mouse_position = fMouseEventHandler.getMousePosition();
		      if(clicks_points.getPointCount() > 0)
		      {
				if(create_polygon_user_clicks == false) {
					if(recording_type.equals("Polygon"))
					{
						addPolygon(track_layer_draw_polygons, polygon_user_clicks_track_id,  clicks_points, "Polygono", 0);
					}
					else if(recording_type.equals("Polyline"))
					{
						addPolyline(track_layer_draw_polygons, polygon_user_clicks_track_id,  clicks_points, "Polylinea", 0);
					}
					create_polygon_user_clicks = true;
				}
				else  
				{
					//updatePolygon(track_layer_draw_polygons, polygon_user_clicks_track_id, clicks_points, 0);
					TLcd3DEditablePointList polygon_preview = new TLcd3DEditablePointList();
					for(int i=0;i<clicks_points.getPointCount();i++) {
						ILcdPoint p = clicks_points.getPoint(i);
						polygon_preview.insert3DPoint(i, new TLcdXYZPoint(p.getX(),p.getY(),0));
					}
					polygon_preview.insert3DPoint(clicks_points.getPointCount(), new TLcdXYZPoint(mouse_position.getX(), mouse_position.getY(), 0));
					if(recording_type.equals("Polygon"))
					{
						updatePolygon(track_layer_draw_polygons, polygon_user_clicks_track_id, polygon_preview, 0);
					}
					else if(recording_type.equals("Polyline"))
					{
						updatePolyline(track_layer_draw_polygons, polygon_user_clicks_track_id, polygon_preview, 0);
					}
				}
		      }
		    }
	  }
  }
  
  private TLcdLonLatHeightPoint generateRandomPoint() {
	  int min_lng = 95;
	  int max_lng = 100;
	  int min_lat = 18;
	  int max_lat = 20;
	  double lat = Math.random()*(max_lat-min_lat+1)+min_lat;
	  double lng = (Math.random()*(max_lng-min_lng+1)+min_lng)*(-1);
	  TLcdLonLatHeightPoint p = new TLcdLonLatHeightPoint(lng,lat,0);
	  //System.out.println(p);
	  return p;
	  //ILcdPoint point = p.getPoint(0);
	  //return point;  
  }
  
  private TLcd2DEditablePointList generateRandomPolygon() {
	  TLcd2DEditablePointList point_list_2d = new TLcd2DEditablePointList();
	  int points_count = (int)(Math.random()*(7-3+1)+3);
	  int min_lng = 95;
	  int max_lng = 100;
	  int min_lat = 18;
	  int max_lat = 20;
	  for(int p = 0; p <= points_count;p++) {
		  double lat = Math.random()*(max_lat-min_lat+1)+min_lat;
		  double lng = (Math.random()*(max_lng-min_lng+1)+min_lng)*(-1);
		  point_list_2d.insert2DPoint(p, new TLcdXYPoint(lng,lat));
	  }
	  return point_list_2d;
  }
  
  private TLcd3DEditablePointList generate3DRandomPolygon() {
	  TLcd3DEditablePointList point_list_3d = new TLcd3DEditablePointList();
	  int points_count = (int)(Math.random()*(7-3+1)+3);
	  int min_lng = 95;
	  int max_lng = 100;
	  int min_lat = 18;
	  int max_lat = 20;
	  for(int p = 0; p <= points_count;p++) {
		  double lat = Math.random()*(max_lat-min_lat+1)+min_lat;
		  double lng = (Math.random()*(max_lng-min_lng+1)+min_lng)*(-1);
		  point_list_3d.insert3DPoint(p, new TLcdXYZPoint(lng,lat,0));
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
  
  public TransformationProvider getTransformationProvider() {
	    return fTransformationProvider;
  }
  
  //@Override
  protected boolean initializeDecoders(String radar_server_ip, int radar_server_puerto) {
	  
    //Create and configure a live ASTERIX model decoder. This decoder reads data
    //from the specified input stream and updates the given modelList accordingly.
    TransformationProvider transformationProvider = getTransformationProvider();

    fLiveDecodedModel = new LiveDecodedModel(radar_server_ip, radar_server_puerto, transformationProvider, new LiveDecoderResultCallback(getView().getOverlayComponent()) {
      @Override
      public void trackModelAdded(LiveDecodedModel aModel, ILcdModel aTrackModel) 
      {
        /*GXYDataUtil.instance()
        .model(aTrackModel)
        .layer(fLiveTrackGXYLayerFactory)
        .labelingAlgorithm(new HeadingSensitiveDeclutterer())
        .addToView( getView());*/
    	  
    	if(layer_asterix == null)
    	{
    		layer_asterix = LspDataUtil
				.instance()
				.model(aTrackModel)
				.layer(fLiveTrackGXYLayerFactory)
				.getLayer();
			getView().addLayer(layer_asterix);
			layer_asterix.getModel().removeAllElements(ILcdModel.FIRE_NOW);
			//System.out.println("Se creo layer de asterix");
			
    	}
    	if(layer_polyline_asterix == null)
    	{
    		layerFactory.setTextColor("#0036F5");
    		layerFactory.setLineColor("#ffffff");
    		layerFactory.setHaloColor("#000000");
    		TLspCompositeLayerFactory factory = new TLspCompositeLayerFactory(layerFactory);
			TLcdVectorModel model_polyline = new TLcdVectorModel(new TLcdGeodeticReference(), new TLcdModelDescriptor("Rutas Asterix", "PolylineNoSelect", "PolylineNoSelect"));
			Collection<ILspLayer> layers_polyline = factory.createLayers(model_polyline);
			layer_polyline_asterix = layers_polyline.iterator().next();
			layerFactory.setDefautlsColor();
			getView().addLayer(layer_polyline_asterix);
			layer_polyline_asterix.getModel().removeAllElements(ILcdModel.FIRE_NOW);
    	}
    	  
    	fTimer.setRepeats(true);
        fTimer.start();
        
        LspDataUtil
			.instance()
			.model(aTrackModel)
			.layer(fLiveTrackGXYLayerFactory)
			.addToView(getView());
      }
      
      @Override
      public void update(LiveDecodedModel aModel ) 
      {
    	  List<TLcdASTERIXTrack> asterix_tracks = new ArrayList<TLcdASTERIXTrack>();
    	  
    	  Enumeration<ILspLayer> layers = getView().layers();
    	  int count = 0;
    	  while(layers.hasMoreElements() ) 
    	  {
    		  ILspLayer l = layers.nextElement();
    		  boolean is_asterix = l.getModel().getModelDescriptor().toString().contains("TLcdASTERIXTrackModelDescriptor");
    		  int slayer_asterix = layer_asterix.hashCode();
    		  int sl = l.hashCode();
    		  
    		  if(is_asterix && slayer_asterix != sl )
    		  {
    			  count++;
    			  l.setVisible(false); 
    			  Enumeration<Object> e = l.getModel().elements();
    			  while(e.hasMoreElements()) 
    			  {
    				  Object o = e.nextElement();
    				  if(o instanceof TLcdASTERIXTrack)
    				  {
    					  TLcdASTERIXTrack t = (TLcdASTERIXTrack)o;
    					  asterix_tracks.add(t);
    				  }
    			  }
    	  	  }
    	  }
    	  //System.out.println("Asterix Layers: " + count);
    	  ILcdModel model = layer_asterix.getModel();
    	  ILcdModel model_polyline = layer_polyline_asterix.getModel();
    	  for (TLcdASTERIXTrack at : asterix_tracks) 
    	  {
    		  if( tracks_asterix_objects.containsKey( at.toString() ) )
    		  {
    			  TLcdASTERIXTrack original = tracks_asterix_objects.get(at.toString());
    			  //((ILcd3DEditablePoint) track.getValue(TrackDataTypes.LOCATION)).move3D(aX, aY, aZ);
    			  original.move3D(at.getLon(), at.getLat(), at.getZ());
    			  tracks_asterix_objects.replace(at.toString(), original);
    	          model.elementChanged(original, ILcdModel.FIRE_LATER);
    	          
    	          TLcd3DEditablePointList polyline  = tracks_asterix_polyline_objects.get(at.toString());
    	          polyline.insert3DPoint(polyline.getPointCount(), new TLcdXYZPoint( at.getLon(), at.getLat(), at.getZ() ));
    	          ILcd3DEditablePointList polyline_object;
    			  if (model_polyline.getModelReference() instanceof ILcdGeodeticReference) 
    			  {
    				  polyline_object = new TLcdLonLatHeightPolyline(polyline){
			            @Override
			            public String toString() {
			              return " ";
			            }
			          };
    		      } else {
    		    	  polyline_object = new TLcdXYZPolyline(polyline){
  			            @Override
  			            public String toString() {
  			              return " ";
  			            }
  			          };
    		      }
    	          tracks_asterix_polyline_objects.replace(at.toString(), polyline);
    	          model_polyline.elementChanged(polyline_object, ILcdModel.FIRE_LATER);
    		  }
    		  else
    		  {
    			  tracks_asterix_objects.put(at.toString(), at);
    			  model.addElement(at, ILcdModel.FIRE_LATER);
    			  
    			  TLcd3DEditablePointList polyline = new TLcd3DEditablePointList();
    			  polyline.insert3DPoint(0, new TLcdXYZPoint( at.getLon(), at.getLat(), at.getZ() ));
    			  tracks_asterix_polyline_objects.put(at.toString(), polyline);
    			  ILcd3DEditablePointList polyline_object;
    			  if (model_polyline.getModelReference() instanceof ILcdGeodeticReference) 
    			  {
    				  polyline_object = new TLcdLonLatHeightPolyline(polyline){
  			            @Override
  			            public String toString() {
  			              return " ";
  			            }
  			          };
    		      } else {
    		    	  polyline_object = new TLcdXYZPolyline(polyline){
  			            @Override
  			            public String toString() {
  			              return " ";
  			            }
  			          };
    		      }
    			  model_polyline.addElement( polyline_object, ILcdModel.FIRE_LATER);
    			  
    		  }
    	  }
    	  
    	  model.fireCollectedModelChanges();
    	  model_polyline.fireCollectedModelChanges();
      }
      
    }
   );
    
    try {
      fLiveDecodedModel.startLiveDecoder();
      timer.scheduleAtFixedRate(deleteAsterixLayers, 5000, 5000, TimeUnit.MILLISECONDS);
      return true;
    } catch (IOException e) {
    	 System.out.println("Error creando layer");
      TLcdUserDialog.message(
          e.getMessage(),
          ILcdDialogManager.ERROR_MESSAGE,
          getView().getOverlayComponent(), 
          getView().getOverlayComponent()
      );
      return false;
    }
  }

  //@Override
  public void tearDown() {
    fTimer.stop();
    fLiveDecodedModel.dispose();
    //super.tearDown();
  }
  
  private class InvalidateLabelsActionListener implements ActionListener {

	    /*@Override
	    public void actionPerformed(ActionEvent e) {
	      // Firing model changes because of track updates automatically refreshes the map, but the label decluttering
	      // algorithm (see TLcdGXYContinuousLabelingAlgorithm) needs a refresh from time to time, to make the
	      // labels move away gently if overlap is about to occur.
	      ILcdGXYLayer trackLayer = findTrackLayer(getView());
	      if (trackLayer != null && trackLayer.isLabeled()) {
	    	  ((ILcdGXYView)getView()).invalidateGXYLayer(trackLayer, true, this, "Invalidating labels of track layer");
	      }
	    }

	    private ILcdGXYLayer findTrackLayer(TLspExternalView tLspExternalView) {
	      Enumeration layers = tLspExternalView.layers();
	      while (layers.hasMoreElements()) {
	        ILcdGXYLayer layer = (ILcdGXYLayer) layers.nextElement();
	        //if (fLiveTrackGXYLayerFactory.canCreateLayers(layer.getModel())) {
	        if (fLiveTrackGXYLayerFactory.accept(layer.getModel())) {
	          return layer;
	        }
	      }
	      return null;
	    }*/
	  
	  @Override
	    public void actionPerformed(ActionEvent e) {
	      // Firing model changes because of track updates automatically refreshes the map, but the label decluttering
	      // algorithm (see TLcdGXYContinuousLabelingAlgorithm) needs a refresh from time to time, to make the
	      // labels move away gently if overlap is about to occur.
		  ILspLayer trackLayer = findTrackLayer(getView());
	      if (trackLayer != null ) {
	    	  getView().invalidate(true, trackLayer, "Invalidating labels of track layer");
	      }
	    }

	    private ILspLayer findTrackLayer(TLspExternalView tLspExternalView) {
	      Enumeration layers = tLspExternalView.layers();
	      while (layers.hasMoreElements()) {
	    	  ILspLayer layer =  (ILspLayer)layers.nextElement();
	        if (fLiveTrackGXYLayerFactory.canCreateLayers(layer.getModel())) {
	        //if (fLiveTrackGXYLayerFactory.accept(layer.getModel())) {
	          return layer;
	        }
	      }
	      return null;
	    }
	    
  }
  
}
