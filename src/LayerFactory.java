import static com.luciad.view.lightspeed.label.algorithm.TLspLabelLocationProvider.Location.SOUTH;
import static com.luciad.view.lightspeed.label.algorithm.TLspLabelLocationProvider.Location.WEST;
import static com.luciad.view.lightspeed.label.algorithm.TLspLabelLocationProvider.Location.EAST;
import static com.luciad.view.lightspeed.label.algorithm.TLspLabelLocationProvider.Location.NORTH;
import static com.luciad.view.lightspeed.style.complexstroke.ALspComplexStroke.compose;
import static com.luciad.view.lightspeed.style.complexstroke.ALspComplexStroke.parallelLine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.luciad.earth.model.TLcdEarthModelDescriptor;
import com.luciad.format.shp.TLcdSHPModelDescriptor;
import com.luciad.gui.TLcdImageIcon;
import com.luciad.model.ILcdModel;
import com.luciad.util.logging.ILcdLogger;
import com.luciad.util.logging.TLcdLoggerFactory;
import com.luciad.view.lightspeed.label.TLspLabelPlacer;
import com.luciad.view.lightspeed.label.algorithm.ILspLabelingAlgorithm;
import com.luciad.view.lightspeed.label.algorithm.TLspLabelLocationProvider;
import com.luciad.view.lightspeed.label.algorithm.discrete.TLspLabelingAlgorithm;
import com.luciad.view.lightspeed.layer.ALspSingleLayerFactory;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.layer.TLspPaintState;
import com.luciad.view.lightspeed.layer.raster.TLspRasterLayerBuilder;
import com.luciad.view.lightspeed.layer.shape.TLspShapeLayerBuilder;
import com.luciad.view.lightspeed.layer.style.TLspLayerStyle;
import com.luciad.view.lightspeed.painter.label.style.TLspDataObjectLabelTextProviderStyle;
import com.luciad.view.lightspeed.style.ALspStyle;
import com.luciad.view.lightspeed.style.ILspTexturedStyle;
import com.luciad.view.lightspeed.style.ILspWorldElevationStyle;
import com.luciad.view.lightspeed.style.TLspComplexStrokedLineStyle;
import com.luciad.view.lightspeed.style.TLspFillStyle;
import com.luciad.view.lightspeed.style.TLspIconStyle;
import com.luciad.view.lightspeed.style.TLspLineStyle;
import com.luciad.view.lightspeed.style.TLspTextStyle;
import com.luciad.view.lightspeed.style.TLspVerticalLineStyle;
import com.luciad.view.lightspeed.style.TLspIconStyle.ScalingMode;
import com.luciad.view.lightspeed.style.complexstroke.ALspComplexStroke;
import com.luciad.view.lightspeed.style.styler.TLspLabelStyler;
import com.luciad.view.lightspeed.style.styler.TLspStyler;

public class LayerFactory extends ALspSingleLayerFactory {

  private static ILcdLogger sLogger = TLcdLoggerFactory.getLogger(LayerFactory.class.getName());

  private String default_selection_fill_color = "#41f471";
  private String default_fill_color = "#4143f4";
  private String default_line_color = "#ffffff";
  private String default_text_color = "#ffffff";
  private String default_halo_color = "#000000";
  
  private String selection_fill_color = default_selection_fill_color;
  private String fill_color = default_fill_color;
  private String line_color = default_line_color;
  private String text_color = default_text_color;
  private String halo_color = default_halo_color;
  
  private String icon_path = "";
  private String label_text = "";
  
  public String getIconPath() {
	  return icon_path;
  }
  
  public void setIconPath(String new_icon_path) {
	  icon_path = new_icon_path;
  }
  
  public String getLabelText() {
	  return label_text;
  }
  
  public void setLabelText(String new_label_text) {
	  label_text = new_label_text;
  }
  
  public String getSelectionFillColor() {
	  return selection_fill_color;
  }
  
  public void setSelectionFillColor(String new_color) {
	  selection_fill_color = new_color;
  }
  
  public String getFillColor() {
	  return fill_color;
  }
  
  public void setFillColor(String new_color) {
	  fill_color = new_color;
  }
  
  public String getLineColor() {
	  return line_color;
  }
  
  public void setLineColor(String new_color) {
	  line_color = new_color;
  }
  
  public String getTextColor() {
	  return text_color;
  }
  
  public void setTextColor(String new_color) {
	  text_color = new_color;
  }
  
  public String getHaloColor() {
	  return halo_color;
  }
  
  public void setHaloColor(String new_color) {
	  halo_color = new_color;
  }
  
  public void setDefautlsColor() {
	  selection_fill_color = default_selection_fill_color;
	  fill_color = default_fill_color;
	  line_color = default_line_color;
	  text_color = default_text_color;
	  halo_color = default_halo_color;
  }
  
  public Color hex2Rgb(String color_hex) {
	    return new Color(
	            Integer.valueOf( color_hex.substring( 1, 3 ), 16 ),
	            Integer.valueOf( color_hex.substring( 3, 5 ), 16 ),
	            Integer.valueOf( color_hex.substring( 5, 7 ), 16 ) );
  }
  
  public String generateColorRandom() {
	// create random object - reuse this as often as possible
      Random random = new Random();
      // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
      int nextInt = random.nextInt(256*256*256);
      // format it as hexadecimal string (with hashtag and leading zeros)
      String color_hex = String.format("#%06x", nextInt);
      return color_hex;
  }
  
  @Override
  public boolean canCreateLayers(ILcdModel aModel) {
    return aModel.getModelDescriptor().getDisplayName().equals("Textured shapes") ||
           aModel.getModelDescriptor().getDisplayName().equals("Extruded textured shapes") ||
           aModel.getModelDescriptor().getDisplayName().equals("Stipple pattern shapes") ||
           aModel.getModelDescriptor().getDisplayName().equals("Extruded stipple shapes") ||
           aModel.getModelDescriptor().getDisplayName().equals("Solid fill shapes") ||
           aModel.getModelDescriptor().getDisplayName().equals("Solid fill shapes no select") ||
           aModel.getModelDescriptor().getDisplayName().equals("Extruded solid shapes") ||
           aModel.getModelDescriptor().getDisplayName().equals("Points with icon") ||
           aModel.getModelDescriptor().getDisplayName().equals("Polyline") ||
           aModel.getModelDescriptor().getDisplayName().equals("PolylineNoSelect") ||
           aModel.getModelDescriptor().getDisplayName().equals("Line") ||
           aModel.getModelDescriptor().getDisplayName().equals("Predicted"); 
  }

  @Override
  public ILspLayer createLayer(ILcdModel aModel) {
    if (aModel.getModelDescriptor().getDisplayName().equals("Textured shapes")) {
      return createTexturedLayer(aModel, false);
    } else if (aModel.getModelDescriptor().getDisplayName().equals("Extruded textured shapes")) {
      return createTexturedLayer(aModel, true);
    } else if (aModel.getModelDescriptor().getDisplayName().equals("Stipple pattern shapes")) {
      return createStipplePatternLayer(aModel, false);
    } else if (aModel.getModelDescriptor().getDisplayName().equals("Extruded stipple shapes")) {
      return createStipplePatternLayer(aModel, true);
    } else if (aModel.getModelDescriptor().getDisplayName().equals("Solid fill shapes")) {
      return createSolidFillLayer(aModel, false, true);
    }else if (aModel.getModelDescriptor().getDisplayName().equals("Solid fill shapes no select")) {
        return createSolidFillLayer(aModel, false, false);
    }else if (aModel.getModelDescriptor().getDisplayName().equals("Extruded solid shapes")) {
      return createSolidFillLayer(aModel, true, true);
    } else if (aModel.getModelDescriptor().getDisplayName().equals("Points with icon")){
      ILspLayer layer = createPointWithIconLayer(aModel);
      setIconPath("");
      return layer;
    } else if (aModel.getModelDescriptor().getDisplayName().equals("Polyline")){
    	return createPolylineLayer(aModel, true);
    } else if (aModel.getModelDescriptor().getDisplayName().equals("PolylineNoSelect")){
    	return createPolylineLayer(aModel, false);
    } else if (aModel.getModelDescriptor().getDisplayName().equals("Line")){
    	return createLineLayer(aModel);
    } else if (aModel.getModelDescriptor().getDisplayName().equals("Predicted")){
    	return createPredictedLayer(aModel);
    }
    //return null;
    return createSimpleLayer(aModel);
  }

  /**
   * Returns a layer using a texture for filling the shapes of the given model
   *
   * @param aModel the model for which to create the layer
   * @param aExtruded
   * @return a layer using a texture for filling the shapes of the given model
   */
  private ILspLayer createTexturedLayer(ILcdModel aModel, boolean aExtruded) {

    //Read in an image to be used as a texture
    BufferedImage image = null;
    try {
      image = ImageIO.read(getClass().getResourceAsStream("/images/luciad_logo.png"));
    } catch (IOException e) {
      sLogger.error("Unable to read texture image file for textured layer creation");
    }

    //Create the fill style with the loaded image as a texture
    TLspFillStyle.Builder fillStyleBuilder = TLspFillStyle.newBuilder()
                                                          .texture(image)
                                                          .textureCoordinatesMode(ILspTexturedStyle.TextureCoordinatesMode.OBJECT_RELATIVE)
                                                          .elevationMode(aExtruded ? ILspWorldElevationStyle.ElevationMode.ABOVE_ELLIPSOID :
                                                                         ILspWorldElevationStyle.ElevationMode.ON_TERRAIN);
    TLspFillStyle fillStyle = fillStyleBuilder.build();
    TLspFillStyle selectedStyle = fillStyleBuilder.color(hex2Rgb(selection_fill_color)).build();

    //Return the layer on which we set the created fill styles
    return TLspShapeLayerBuilder.newBuilder().model(aModel)
                                .bodyEditable(true)
                                .bodyStyler(TLspPaintState.REGULAR, fillStyle)
                                .bodyStyler(TLspPaintState.SELECTED, selectedStyle)
                                .bodyStyler(TLspPaintState.EDITED, selectedStyle)
                                .build();
  }

  /**
   * Returns a layer using a custom stipple pattern for filling the shapes of the given model
   *
   * @param aModel the model for which to create the layer
   * @param aExtruded
   * @return a layer using a custom stipple pattern for filling the shapes of the given model
   */
  private ILspLayer createStipplePatternLayer(ILcdModel aModel, boolean aExtruded) {
    //Create a custom stipple pattern
    TLspFillStyle.StipplePattern stipplePattern =
        TLspFillStyle.StipplePattern.newBuilder()
                                    .fillRect(1, 1, 13, 13)
                                    .fillRect(18, 18, 13, 13)
                                    .fillPolygon(new int[]{18, 30, 30}, new int[]{1, 1, 13}, 3)
                                    .fillPolygon(new int[]{1, 1, 13}, new int[]{30, 18, 30}, 3)
                                    .build();

    //Create the fill styles
    TLspFillStyle.Builder fillStyleBuilder = TLspFillStyle.newBuilder()
                                                          .stipplePattern(stipplePattern)
                                                          .elevationMode(aExtruded ? ILspWorldElevationStyle.ElevationMode.ABOVE_ELLIPSOID :
                                                                         ILspWorldElevationStyle.ElevationMode.ON_TERRAIN);
    TLspFillStyle fillStyle = fillStyleBuilder.color(hex2Rgb(fill_color)).build();
    TLspFillStyle selectedStyle = fillStyleBuilder.color(hex2Rgb(selection_fill_color)).build();

    //Create a line style for improving the visibility of the shapes' outlines
    TLspLineStyle lineStyle = TLspLineStyle.newBuilder().color(hex2Rgb(line_color))
                                           .width(2.0)
                                           .elevationMode(aExtruded ? ILspWorldElevationStyle.ElevationMode.ABOVE_ELLIPSOID :
                                                          ILspWorldElevationStyle.ElevationMode.ON_TERRAIN)
                                           .build();
    //Return the layer on which we set the created fill styles
    return TLspShapeLayerBuilder.newBuilder().model(aModel)
                                .selectable(true)
                                .bodyEditable(true)
                                .bodyStyler(TLspPaintState.REGULAR, new TLspStyler(fillStyle,
                                                                                   lineStyle))
                                .bodyStyler(TLspPaintState.SELECTED, new TLspStyler(selectedStyle,
                                                                                    lineStyle))
                                .bodyStyler(TLspPaintState.EDITED, new TLspStyler(selectedStyle,
                                                                                  lineStyle))
                                .build();
  }

  /**
   * Returns a layer using a solid color for filling the shapes of the given model
   *
   * @param aModel the model for which to create the layer
   * @param aExtruded
   * @return a layer using a solid color for filling the shapes of the given model
   */
  private ILspLayer createSolidFillLayer(ILcdModel aModel, boolean aExtruded, boolean select) {

    //Create the fill styles
    TLspFillStyle.Builder fillStylebuilder = TLspFillStyle.newBuilder()
                                                          .elevationMode(aExtruded ? ILspWorldElevationStyle.ElevationMode.ABOVE_ELLIPSOID :
                                                                         ILspWorldElevationStyle.ElevationMode.ON_TERRAIN);
    TLspFillStyle fillStyle = fillStylebuilder.color(hex2Rgb(fill_color)).opacity(0.7f).build();
    TLspFillStyle selectedStyle = fillStylebuilder.color(hex2Rgb(selection_fill_color)).opacity(0.7f).build();
    TLspLineStyle lineStyle = TLspLineStyle.newBuilder().color(hex2Rgb(line_color))
                                           .width(2.0)
                                           .elevationMode(aExtruded ? ILspWorldElevationStyle.ElevationMode.ABOVE_ELLIPSOID :
                                                          ILspWorldElevationStyle.ElevationMode.ON_TERRAIN)
                                           .build();

    
    
    //Return the layer on which we set the created fill styles
    TLspShapeLayerBuilder layerBuilder = TLspShapeLayerBuilder.newBuilder().model(aModel)
                .selectable(select)
                .bodyEditable(true)
                .bodyStyler(TLspPaintState.REGULAR, new TLspStyler(fillStyle,lineStyle))
                .bodyStyler(TLspPaintState.SELECTED, new TLspStyler(selectedStyle, lineStyle))
                .bodyStyler(TLspPaintState.EDITED, new TLspStyler(selectedStyle, lineStyle))
                .labelStyles(
                		TLspPaintState.REGULAR, 
                		TLspTextStyle.newBuilder().haloColor(hex2Rgb(halo_color)).textColor(hex2Rgb(text_color)).build(), 
                		TLspDataObjectLabelTextProviderStyle.newBuilder()
                            .expressions(PolygonDataTypes.NAME)
                            .build()
                );
    return layerBuilder.build();
                                
  }
  
  public TLspLayerStyle getSolidFillLayer(ILcdModel aModel, boolean aExtruded) {

	    //Create the fill styles
	    TLspFillStyle.Builder fillStylebuilder = TLspFillStyle.newBuilder()
	                                                          .elevationMode(aExtruded ? ILspWorldElevationStyle.ElevationMode.ABOVE_ELLIPSOID :
	                                                                         ILspWorldElevationStyle.ElevationMode.ON_TERRAIN);
	    TLspFillStyle fillStyle = fillStylebuilder.color(hex2Rgb(fill_color)).opacity(0.7f).build();
	    TLspFillStyle selectedStyle = fillStylebuilder.color(hex2Rgb(selection_fill_color)).opacity(0.7f).build();
	    TLspLineStyle lineStyle = TLspLineStyle.newBuilder().color(hex2Rgb(line_color))
	                                           .width(2.0)
	                                           .elevationMode(aExtruded ? ILspWorldElevationStyle.ElevationMode.ABOVE_ELLIPSOID :
	                                                          ILspWorldElevationStyle.ElevationMode.ON_TERRAIN)
	                                           .build();
	   
	    //Return the layer on which we set the created fill styles
		return TLspShapeLayerBuilder.newBuilder().model(aModel)
	                                .selectable(true)
	                                .bodyEditable(true)
	                                .bodyStyler(TLspPaintState.REGULAR, new TLspStyler(fillStyle,
	                                                                                   lineStyle))
	                                .bodyStyler(TLspPaintState.SELECTED, new TLspStyler(selectedStyle,
	                                                                                    lineStyle))
	                                .bodyStyler(TLspPaintState.EDITED, new TLspStyler(selectedStyle,
	                                                                                  lineStyle))
	                                .build().getLayerStyle();
	  }
  
  private ILspLayer createSimpleLayer(ILcdModel aModel) {
    // Create a layer depending on the type of model.
    if (aModel.getModelDescriptor() instanceof TLcdSHPModelDescriptor) {
      // Create a layer with the given model.
      // Create the fill and line styles using builders. The elevation mode for both
      // styles is set to ON_TERRAIN, so that the data will be draped over the 3D terrain
      // when the view is set to 3D.
      TLspFillStyle fill = TLspFillStyle.newBuilder()
                                        .color(hex2Rgb(fill_color))
                                        .elevationMode(ILspWorldElevationStyle.ElevationMode.ON_TERRAIN)
                                        .stipplePattern(TLspFillStyle.StipplePattern.HALF_TONE_2x2)
                                        .build();
      TLspLineStyle line = TLspLineStyle.newBuilder()
                                        .elevationMode(ILspWorldElevationStyle.ElevationMode.ON_TERRAIN)
                                        .build();

      TLspShapeLayerBuilder layerBuilder = TLspShapeLayerBuilder.newBuilder();
      layerBuilder.model(aModel);
      layerBuilder.layerType(ILspLayer.LayerType.BACKGROUND);

      // Assign the line and fill styles to the bodies in their regular object state
      layerBuilder.bodyStyles(TLspPaintState.REGULAR, fill, line);

      // Derive the selection styling from the regular stying, but change the color
      layerBuilder.selectable(true);
      layerBuilder.bodyStyles(TLspPaintState.SELECTED,
                              fill.asBuilder().color(hex2Rgb(selection_fill_color)).build(),
                              line.asBuilder().color(hex2Rgb(line_color)).width(3).build());
      return layerBuilder.build();
    } else if (aModel.getModelDescriptor() instanceof TLcdEarthModelDescriptor) {
      // Create a raster layer using its builder, using all default settings.
	      return TLspRasterLayerBuilder.newBuilder().model(aModel).build();
	    } else {
	      return null;
	    }
   }
  
  private ILspLayer createPointWithIconLayer(ILcdModel aModel) {
	    //Return the layer on which we set the created fill styles
	    if(!getIconPath().equals("") && new File(getIconPath()).exists()) {
	    	TLcdImageIcon icon = new TLcdImageIcon(new TLcdImageIcon(getIconPath()));
	        TLspIconStyle.Builder iconStyleBuilder = TLspIconStyle.newBuilder()
	            .icon(icon)
	             //Set icons to have a fixed world size
	            .scalingMode(ScalingMode.WORLD_SCALING_CLAMPED)
	            .worldSize(50000)
	            .scale(1)
	            //Set the icons' opacity value
	            .opacity(1.0f);
	        TLspVerticalLineStyle.Builder<?> lineBuilder = TLspVerticalLineStyle.newBuilder()
	                .color(hex2Rgb(line_color))
	                .width(1.0f);
	        TLspShapeLayerBuilder layerBuilder = TLspShapeLayerBuilder.newBuilder().model(aModel)
	                .selectable(true)
	                .bodyEditable(true)
	                .bodyStyler(TLspPaintState.REGULAR,
	                        new TLspStyler(
	                        		iconStyleBuilder.build(),
	                                lineBuilder.build()
	                        )
	                 )
	                .labelStyles(
	                		TLspPaintState.REGULAR, 
	                		TLspTextStyle.newBuilder().alignment(TLspTextStyle.Alignment.CENTER).haloColor(hex2Rgb(halo_color)).textColor(hex2Rgb(text_color)).build(), 
	                		TLspDataObjectLabelTextProviderStyle.newBuilder()
	                            .expressions(TrackDataTypes.NAME, TrackDataTypes.LABEL)
	                            .build()
	                );
	    	 return layerBuilder.build();
	    }
	    else
	    {
	    	//Create the fill styles
		    TLspFillStyle.Builder fillStylebuilder = TLspFillStyle.newBuilder()
		                                                          .elevationMode(ILspWorldElevationStyle.ElevationMode.ON_TERRAIN);
	    	TLspFillStyle fillStyle = fillStylebuilder.color(hex2Rgb(fill_color)).opacity(0.7f).build();
		    TLspFillStyle selectedStyle = fillStylebuilder.color(hex2Rgb(selection_fill_color)).opacity(0.7f).build();
		    TLspLineStyle lineStyle = TLspLineStyle.newBuilder().color(hex2Rgb(line_color))
		                                           .width(2.0)
		                                           .elevationMode(ILspWorldElevationStyle.ElevationMode.ON_TERRAIN)
		                                           .build();
	    	TLspShapeLayerBuilder layerBuilder = TLspShapeLayerBuilder.newBuilder().model(aModel)
	                .selectable(true)
	                .bodyEditable(true)
	                .bodyStyler(TLspPaintState.REGULAR, new TLspStyler(fillStyle,lineStyle))
	                .bodyStyler(TLspPaintState.SELECTED, new TLspStyler(selectedStyle, lineStyle))
	                .bodyStyler(TLspPaintState.EDITED, new TLspStyler(selectedStyle, lineStyle));
	    	return layerBuilder.build();
	    }                           
	  }
  
  	private ILspLayer createPolylineLayer(ILcdModel aModel, boolean select) {
	    return TLspShapeLayerBuilder.newBuilder().model(aModel)
                .selectable(select)
                .bodyEditable(true)
                .bodyStyles(TLspPaintState.REGULAR, TLspLineStyle.newBuilder().color(hex2Rgb(line_color)).width(2).build())
                .bodyStyles(TLspPaintState.SELECTED, TLspLineStyle.newBuilder().color(hex2Rgb(line_color)).width(2).build())
                .labelStyles(
                		TLspPaintState.REGULAR, 
                		TLspTextStyle.newBuilder().haloColor(hex2Rgb(halo_color)).textColor(hex2Rgb(text_color)).build(), 
                		TLspDataObjectLabelTextProviderStyle.newBuilder()
                            .expressions(PolygonDataTypes.NAME)
                            .build()
                )
                .build();
	                                
	  }
  	
  	private ILspLayer createLineLayer(ILcdModel aModel) {
  		
		ALspComplexStroke thickLine = parallelLine().lineWidth(1 * 4).lineColor(hex2Rgb(halo_color)).build();
		ALspComplexStroke thinLine = parallelLine().lineWidth(1 * 2).lineColor(hex2Rgb(line_color)).build();
		ALspComplexStroke baseLine = compose(thickLine, thinLine);
  		ALspStyle style = TLspComplexStrokedLineStyle.newBuilder()
                 .fallback(baseLine)
                 .build();
  		ILspLabelingAlgorithm labelingAlgorithm = new TLspLabelingAlgorithm(new TLspLabelLocationProvider(SOUTH));
  		TLspLabelStyler fLabelStyler = TLspLabelStyler.newBuilder()
                .group(TLspLabelPlacer.DEFAULT_DECLUTTER_GROUP)
                .algorithm(labelingAlgorithm)
                .styles(TLspTextStyle.newBuilder().haloColor(hex2Rgb(halo_color)).textColor(hex2Rgb(text_color)).build(), style, TLspDataObjectLabelTextProviderStyle.newBuilder()
                        .expressions(LineDataTypes.LABEL)
                        .build())
                .build();
  		
	    return TLspShapeLayerBuilder.newBuilder().model(aModel)
                .selectable(false)
                .bodyEditable(false)
                .bodyStyles(TLspPaintState.REGULAR, style)
                .bodyStyles(TLspPaintState.SELECTED, style)
//                .bodyStyles(TLspPaintState.REGULAR, TLspLineStyle.newBuilder().color(hex2Rgb(line_color)).width(2).build())
//                .bodyStyles(TLspPaintState.SELECTED, TLspLineStyle.newBuilder().color(hex2Rgb(line_color)).width(2).build())
                .labelStyler(
                		TLspPaintState.REGULAR, 
                		fLabelStyler
                )
                .build();
	                                
	  }
  	
  	private ILspLayer createPredictedLayer(ILcdModel aModel) {
  		
		ALspComplexStroke thickLine = parallelLine().lineWidth(1 * 4).lineColor(hex2Rgb(halo_color)).build();
		ALspComplexStroke thinLine = parallelLine().lineWidth(1 * 2).lineColor(hex2Rgb(line_color)).build();
		ALspComplexStroke baseLine = compose(thickLine, thinLine);
  		ALspStyle style = TLspComplexStrokedLineStyle.newBuilder()
                 .fallback(baseLine)
                 .build();
  		ILspLabelingAlgorithm labelingAlgorithm = new TLspLabelingAlgorithm(new TLspLabelLocationProvider(SOUTH));
  		TLspLabelStyler fLabelStyler = TLspLabelStyler.newBuilder()
                .group(TLspLabelPlacer.DEFAULT_DECLUTTER_GROUP)
                .algorithm(labelingAlgorithm)
                .styles(TLspTextStyle.newBuilder().haloColor(hex2Rgb(halo_color)).textColor(hex2Rgb(text_color)).build(), style, TLspDataObjectLabelTextProviderStyle.newBuilder()
                        .expressions(LineDataTypes.LABEL)
                        .build())
                .build();
  		
  		
  		if(!getIconPath().equals("") && new File(getIconPath()).exists()) 
  		{
  			TLcdImageIcon icon = new TLcdImageIcon(new TLcdImageIcon(getIconPath()));
	        TLspIconStyle.Builder iconStyleBuilder = TLspIconStyle.newBuilder()
	            .icon(icon)
	             //Set icons to have a fixed world size
	            .scalingMode(ScalingMode.WORLD_SCALING_CLAMPED)
	            .worldSize(50000)
	            .scale(1)
	            //Set the icons' opacity value
	            .opacity(1.0f);
	        TLspShapeLayerBuilder layerBuilder = TLspShapeLayerBuilder.newBuilder().model(aModel)
	                .selectable(false)
	                .bodyEditable(false)
	                .bodyStyler(TLspPaintState.REGULAR,
	                        new TLspStyler(
	                        		iconStyleBuilder.build(),
	                        		style
	                        )
	                 )
	                .labelStyler(
	                		TLspPaintState.REGULAR, 
	                		fLabelStyler
	                )
	                ;
	    	 return layerBuilder.build();

  		}
  		else
  		{
  		
  			return TLspShapeLayerBuilder.newBuilder().model(aModel)
                .selectable(false)
                .bodyEditable(false)
                .bodyStyles(TLspPaintState.REGULAR, style)
                .bodyStyles(TLspPaintState.SELECTED, style)
//                .bodyStyles(TLspPaintState.REGULAR, TLspLineStyle.newBuilder().color(hex2Rgb(line_color)).width(2).build())
//                .bodyStyles(TLspPaintState.SELECTED, TLspLineStyle.newBuilder().color(hex2Rgb(line_color)).width(2).build())
                .labelStyler(
                		TLspPaintState.REGULAR, 
                		fLabelStyler
                )
                .build();
  		}                           
	  }
  
}
