/*
 *
 * Copyright (c) 1999-2017 Luciad All Rights Reserved.
 *
 * Luciad grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Luciad.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. LUCIAD AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL LUCIAD OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF LUCIAD HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 */
package asterix;

import static com.luciad.view.lightspeed.label.algorithm.TLspLabelLocationProvider.Location.NORTH;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Set;

import com.luciad.datamodel.TLcdDataProperty;
import com.luciad.datamodel.TLcdDataType;
import com.luciad.format.asterix.TLcdASTERIXTrackModelDescriptor;
import com.luciad.model.ILcdDataModelDescriptor;
import com.luciad.model.ILcdModel;
import com.luciad.util.TLcdInterval;
import com.luciad.view.lightspeed.label.TLspLabelPlacer;
import com.luciad.view.lightspeed.label.algorithm.ILspLabelingAlgorithm;
import com.luciad.view.lightspeed.label.algorithm.TLspLabelLocationProvider;
import com.luciad.view.lightspeed.label.algorithm.discrete.TLspLabelingAlgorithm;
import com.luciad.view.lightspeed.layer.ALspSingleLayerFactory;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.layer.TLspPaintState;
import com.luciad.view.lightspeed.layer.shape.TLspShapeLayerBuilder;
import com.luciad.view.lightspeed.painter.label.ILspLabelPainter;
import com.luciad.view.lightspeed.painter.label.TLspLabelPainter;
import com.luciad.view.lightspeed.painter.label.style.TLspDataObjectLabelTextProviderStyle;
import com.luciad.view.lightspeed.style.TLspPinLineStyle;
import com.luciad.view.lightspeed.style.TLspTextStyle;
import com.luciad.view.lightspeed.style.styler.TLspLabelStyler;

/**
 * ILcdGXYLayerFactory that can create ILcdGXYLayers for the real-time track models.
 */
public class SimulatorGXYLayerFactory extends ALspSingleLayerFactory {//implements ILspLayerFactory, ILcdFilter {

  /*@Override
  public boolean accept(Object aObject) {
    if (aObject instanceof ILcdModel) {
      ILcdModel model = (ILcdModel) aObject;
      return model.getModelDescriptor() instanceof TrackSimulationModelDescriptor ||
             model.getModelDescriptor() instanceof TLcdASTERIXTrackModelDescriptor;
    }
    return false;
  }*/
  
  @Override
  public boolean canCreateLayers(ILcdModel aModel) {
      return aModel.getModelDescriptor() instanceof TrackSimulationModelDescriptor ||
    		  aModel.getModelDescriptor() instanceof TLcdASTERIXTrackModelDescriptor;
  }

  @Override
  public ILspLayer createLayer(ILcdModel aModel) {
    if (canCreateLayers(aModel)) {
      return createTrackLayer(aModel);
    }
    return null;
  }

  private ILspLayer createTrackLayer(ILcdModel aModel) {
	  
	/*ILspLayer layer;// = new ILspLayer();
    layer.setModel(aModel);
    layer.setLabel(aModel.getModelDescriptor().getDisplayName());
    layer.setLabeled(true);
    layer.setLabelsEditable(true);
    layer.setGXYPen(MapSupport.createPen(aModel.getModelReference()));

    layer.setGXYPainterProvider(new TrackGXYPainter());

    //Don't paint the labels when zoomed out (small scale)
    layer.setLabelScaleRange(new TLcdInterval(
        GXYScaleSupport.mapScale2InternalScale(1.0 / 5000000.0, -1, null), Double.MAX_VALUE));*/

	/*  ILspLabelPainter labelPainter = new ILspLabelPainter();
    labelPainter.setWithPin(true);
    labelPainter.setFrame(true);
    labelPainter.setFilled(true);
    labelPainter.setBackground(new Color(255, 255, 255, 128));
    labelPainter.setExpressions(findLabelProperty(aModel));
    /*layer.setGXYLabelPainterProvider(labelPainter);
    layer.setGXYLabelEditorProvider(labelPainter);*/

    //return layer;
    
    if(aModel != null)
    {
	    return TLspShapeLayerBuilder.newBuilder()
	    		.model(aModel)
	            .label((aModel.getModelDescriptor().getDisplayName()))
	            .labelEditable(true)
	            .labelPainter(createTrackLabelPainter(aModel))
	            .labelScaleRange(new TLcdInterval( GXYScaleSupport.mapScale2InternalScale(1.0 / 5000000.0, -1, null), Double.MAX_VALUE))
	            .build();
	    }
    return null;
  }

  private ILspLabelPainter createTrackLabelPainter(ILcdModel aModel) {
	    TLspLabelPainter painter = new TLspLabelPainter();
	    painter.setOverlayLabels(true);

	    TLspPinLineStyle pinStyle = TLspPinLineStyle.newBuilder()
	                                                .color(new Color(232, 119, 34))
	                                                .width(1.5f)
	                                                .build();

	    TLspTextStyle textStyle = TLspTextStyle.newBuilder()
	                                           .font(Font.decode("Default-BOLD-10"))
	                                           .textColor(new Color(255, 255, 255))
	                                           .haloColor(new Color(0, 0, 0))
	                                           .haloThickness(1)
	                                           .build();

	    TLspTextStyle selectedStyle = textStyle.asBuilder()
	                                           .haloColor(new Color(0, 0, 0))
	                                           .build();

	    ILspLabelingAlgorithm labelingAlgorithm = new TLspLabelingAlgorithm(new TLspLabelLocationProvider(20, NORTH));

	    painter.setStyler(TLspPaintState.REGULAR,
	                      TLspLabelStyler.newBuilder()
	                                     .group(TLspLabelPlacer.DEFAULT_DECLUTTER_GROUP)
	                                     .algorithm(labelingAlgorithm)
	                                     .styles(
	                                    		 textStyle, 
	                                    		 pinStyle, 
	                                    		 TLspDataObjectLabelTextProviderStyle.newBuilder()
	                                             .expressions(findLabelProperty(aModel))
	                                             .build()
	                                      )
	                                     .build()
	    );
	    painter.setStyler(TLspPaintState.SELECTED,
	                      TLspLabelStyler.newBuilder()
	                                     .group(TLspLabelPlacer.DEFAULT_DECLUTTER_GROUP)
	                                     .algorithm(labelingAlgorithm)
	                                     .styles(
	                                    		 selectedStyle, 
	                                    		 pinStyle, 
	                                    		 TLspDataObjectLabelTextProviderStyle.newBuilder()
	                                             .expressions(findLabelProperty(aModel))
	                                             .build()
	                                      )
	                                     .build()
	    );
	    

	    return painter;
	  }
  
  public static String[] findLabelProperty(ILcdModel aModel) 
  {
    ILcdDataModelDescriptor modelDescriptor = (ILcdDataModelDescriptor) aModel.getModelDescriptor();
    Set<TLcdDataType> dataTypeSet = modelDescriptor.getModelElementTypes();
    // Find a property that has 'track' and 'number' in its name.
    for (TLcdDataType dataType : dataTypeSet) 
    {
      List<TLcdDataProperty> dataProperties = dataType.getProperties();
      for (TLcdDataProperty dataProperty : dataProperties) 
      {
        if (dataProperty.getName().contains("Track") && dataProperty.getName().contains("Number")) 
        {
        	return new String[]{dataProperty.getName()};
        }
      }

    }
    //If no property could be found, return the first property of the first type
    if (modelDescriptor.getModelElementTypes().size() > 0) 
    {
      return new String[]{modelDescriptor.getModelElementTypes().iterator().next().getProperties().get(0).getName()};
    } 
    else 
    {
      return null;
    }
  }
  
}
