import com.luciad.datamodel.TLcdCoreDataTypes;
import com.luciad.datamodel.TLcdDataModel;
import com.luciad.datamodel.TLcdDataModelBuilder;
import com.luciad.datamodel.TLcdDataType;
import com.luciad.datamodel.TLcdDataTypeBuilder;
import com.luciad.format.database.TLcdPrimaryKeyAnnotation;
import com.luciad.shape.shape3D.ILcd3DEditablePoint;
import com.luciad.util.TLcdHasGeometryAnnotation;

/**
   * This class builds the structural description of the track model, and providesread(Points)
   * static access to it. The method getDataModel() provides the full track data model.
   * The public constant TRACK_PLAN_DATA_TYPE refers to the only defined type of this model:
   * tracks.
   */
  public class LineDataTypes {

    // The data model for the tracks, fully describing the structure of the data.
    private static final TLcdDataModel TRACK_PLAN_DATA_MODEL;

    // The data model contains a single data type - the track data type.
    static final TLcdDataType TRACK_PLAN_DATA_TYPE;

    static final String ID = "id";
    static final String LOCATION = "Localización";
    /*static final String LON = "Longitud";
    static final String LAT = "Latitud";
    static final String HEIGHT = "Altitud";*/
    //static final String TIMESTAMP = "timestamp";
    static final String CALLSIGN = "callSign";
    static final String LABEL = "Distancia";

    static final String LINE_TYPE = "Línea"; //Starts with capital, same as Java class

    static {
      // Assign the constants
      TRACK_PLAN_DATA_MODEL = createDataModel();
      TRACK_PLAN_DATA_TYPE = TRACK_PLAN_DATA_MODEL.getDeclaredType(LINE_TYPE);
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
      TLcdDataTypeBuilder trackBuilder = builder.typeBuilder(LINE_TYPE);
      trackBuilder.addProperty(ID, TLcdCoreDataTypes.INTEGER_TYPE);
      trackBuilder.addProperty(LOCATION, geometryType);
      /*trackBuilder.addProperty(LON, TLcdCoreDataTypes.DOUBLE_TYPE);
      trackBuilder.addProperty(LAT, TLcdCoreDataTypes.DOUBLE_TYPE);
      trackBuilder.addProperty(HEIGHT, TLcdCoreDataTypes.DOUBLE_TYPE);*/
      //trackBuilder.addProperty(TIMESTAMP, TLcdCoreDataTypes.LONG_TYPE);
      trackBuilder.addProperty(CALLSIGN, TLcdCoreDataTypes.STRING_TYPE);
      trackBuilder.addProperty(LABEL, TLcdCoreDataTypes.STRING_TYPE);


      // Finalize the creation
      TLcdDataModel dataModel = builder.createDataModel();

      TLcdDataType type = dataModel.getDeclaredType(LINE_TYPE);
      // make sure LuciadLightspeed finds the geometry
      type.addAnnotation(new TLcdHasGeometryAnnotation(type.getProperty(LOCATION)));

      // Annotation indicating which property should be used as primary key
      type.addAnnotation(new TLcdPrimaryKeyAnnotation(type.getProperty(ID)));

      return dataModel;
    }
  }