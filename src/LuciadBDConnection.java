
/**
 *
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Roberto Alonso De la Garza Mendoza
 */
public class LuciadBDConnection {

    private String url = "";
    private String user = "";
    private String password = "";
    private String id_mision = "";
    private Connection connection = null;

    /**
     *
     * @param host the host location (locahost:port)
     * @param db_name the name of the database
     * @param user the user that can make the conection
     * @param password the password´s user
     * @throws SQLException if the conection can´t be made or one of more
     * arguments are wrong
     * @throws java.lang.ClassNotFoundException
     */
    public LuciadBDConnection(
            String host,
            String db_name,
            String user,
            String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        this.user = user;
        this.password = password;
        this.url = String.format("jdbc:mysql://%s/%s", host, db_name);
        this.connection = getConnection();
    }
    
        /**
     *
     * @param host the host location (locahost:port)
     * @param db_name the name of the database
     * @param user the user that can make the conection
     * @param password the password´s user
     * @param id_mision
     * @throws SQLException if the conection can´t be made or one of more
     * arguments are wrong
     * @throws java.lang.ClassNotFoundException
     */
    public LuciadBDConnection(
            String host,
            String db_name,
            String user,
            String password,
            String id_mision) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        this.user = user;
        this.password = password;
        this.url = String.format("jdbc:mysql://%s/%s", host, db_name);
        this.id_mision = id_mision;
        this.connection = getConnection();
    }
    
    /**
     * Make the connection to the database
     *
     * @return Connection Object used for sql queries
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Close the actual connection to the datbase
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        this.connection.close();
    }

    //--------------------------------------------------------------------------
    /**
     * Basic CRUD
     */
    /**
     * Insert a track
     *
     * @param track thte track to insert
     * @return
     * @throws SQLException
     */
    public boolean insertTrack(JSONObject track) throws SQLException {

        return insertTrack(
                (int) track.get("id_mision"),
                (int) track.get("id_usuario"),
                (int) track.get("tipo_dato"),
                (int) track.get("tipo_dato"),
                (float) track.get("x_geoposicion"),
                (float) track.get("y_geoposicion"),
                (float) track.get("elevacion"),
                (String) track.get("nombre"),
                (String) track.get("descripcion"),
                track.get("datos_json").toString(),
                (String) track.get("visibilidad"));
    }

    /**
     * Insert a track
     *
     * @param id_mision
     * @param id_usuario
     * @param tipo_dato
     * @param origen
     * @param x_geoposicion
     * @param y_geoposicion
     * @param elevacion
     * @param nombre
     * @param descripcion
     * @param datos_json
     * @param visibilidad
     * @return
     * @throws SQLException
     */
    public boolean insertTrack(
            int id_mision,
            int id_usuario,
            int tipo_dato,
            int origen,
            float x_geoposicion,
            float y_geoposicion,
            float elevacion,
            String nombre,
            String descripcion,
            String datos_json,
            String visibilidad) throws SQLException {
        PreparedStatement statement
                = connection.prepareStatement("INSERT INTO dato_tactico "
                        + "(id_mision,"
                        + "id_usuario,"
                        + "tipo_dato,"
                        + "origen,"
                        + "geoposicion,"
                        + "elevacion,"
                        + "fecha_creacion,"
                        + "nombre,"
                        + "descripcion,"
                        + "datos_json,"
                        + "visibilidad)"
                        + " VALUES (?,?,?,?,POINT(?,?),?,NOW(),?,?,?,?)");
        statement.setInt(1, id_mision);
        statement.setInt(2, id_usuario);
        statement.setInt(3, tipo_dato);
        statement.setInt(4, origen);
        statement.setFloat(5, x_geoposicion);
        statement.setFloat(6, y_geoposicion);
        statement.setFloat(7, elevacion);
        statement.setString(8, nombre);
        statement.setString(9, descripcion);
        statement.setString(10, datos_json);
        statement.setString(11, visibilidad);
        int rows_added = statement.executeUpdate();
        statement.close();
        return rows_added > 0;
    }

    /**
     * Get a list of Tracks from the database
     *
     * @return a track´s list from database
     * @throws SQLException
     */
    public List<JSONObject> getTracks() throws SQLException {
        List<JSONObject> tracks_list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        PreparedStatement statement = connection.prepareStatement("SELECT "
                + "ID,"
                + "id_mision,"
                + "id_usuario,"
                + "tipo_dato,"
                + "origen,"
                + "X(geoposicion),"
                + "Y(geoposicion),"
                + "elevacion,"
                + "fecha_creacion,"
                + "nombre,"
                + "descripcion,"
                + "datos_json,"
                + "visibilidad"
                + " FROM dato_tactico "
                + "WHERE id_mision = ? AND "
                + "eliminado = 0");
        statement.setString(1, this.id_mision);
        ResultSet queryresult = statement.executeQuery();
        while (queryresult.next()) {
            Map<String, Object> track = new HashMap<>();
            track.put("ID", (Object) queryresult.getInt("ID"));
            track.put("id_mision", (Object) queryresult.getInt("id_mision"));
            track.put("id_usuario", (Object) queryresult.getInt("id_usuario"));
            track.put("tipo_dato", (Object) queryresult.getInt("tipo_dato"));
            track.put("origen", (Object) queryresult.getInt("origen"));
            track.put("x_geoposicion", queryresult.getFloat("X(geoposicion)"));
            track.put("y_geoposicion", queryresult.getFloat("Y(geoposicion)"));
            track.put("elevacion", (Object) queryresult.getFloat("elevacion"));
            track.put("fecha_creacion", (Object) queryresult.getTimestamp("fecha_creacion"));
            track.put("nombre", (Object) queryresult.getString("nombre"));
            track.put("descripcion", (Object) queryresult.getString("descripcion"));
            try {
                track.put("datos_json", (Object) parser.parse(queryresult.getString("datos_json")));
            } catch (ParseException ex) {
                track.put("datos_json", initilizeDatosJSON(null));
            }
            track.put("visibilidad", (Object) queryresult.getString("visibilidad"));
            tracks_list.add(new JSONObject(track));
        }

        statement.close();
        queryresult.close();

        return tracks_list;
    }

    /**
     * Get the Track that match with the ID
     *
     * @param ID the ID
     * @return JSONObject with the information of the track or null if there no
     * track that match with the ID
     * @throws java.sql.SQLException
     */
    public JSONObject getTrackbyID(int ID) throws SQLException {
        List<JSONObject> tracks_list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        PreparedStatement statement = connection.prepareStatement("SELECT "
                + "ID,"
                + "id_mision,"
                + "id_usuario,"
                + "tipo_dato,"
                + "origen,"
                + "X(geoposicion),"
                + "Y(geoposicion),"
                + "elevacion,"
                + "fecha_creacion,"
                + "nombre,"
                + "descripcion,"
                + "datos_json,"
                + "visibilidad"
                + " FROM dato_tactico "
                + "WHERE ID = ? AND "
                + "id_mision = ? AND "
                + "eliminado = 0");
        statement.setInt(1, ID);
        statement.setInt(2, Integer.parseInt(this.id_mision));
        ResultSet queryresult = statement.executeQuery();
        while (queryresult.next()) {
            Map<String, Object> track = new HashMap<>();
            track.put("ID", (Object) queryresult.getInt("ID"));
            track.put("id_mision", (Object) queryresult.getInt("id_mision"));
            track.put("id_usuario", (Object) queryresult.getInt("id_usuario"));
            track.put("tipo_dato", (Object) queryresult.getInt("tipo_dato"));
            track.put("origen", (Object) queryresult.getInt("origen"));
            track.put("x_geoposicion", queryresult.getFloat("X(geoposicion)"));
            track.put("y_geoposicion", queryresult.getFloat("Y(geoposicion)"));
            track.put("elevacion", (Object) queryresult.getFloat("elevacion"));
            track.put("fecha_creacion", (Object) queryresult.getTimestamp("fecha_creacion"));
            track.put("nombre", (Object) queryresult.getString("nombre"));
            track.put("descripcion", (Object) queryresult.getString("descripcion"));
            try {
                track.put("datos_json", (Object) parser.parse(queryresult.getString("datos_json")));
            } catch (ParseException ex) {
                track.put("datos_json", initilizeDatosJSON(null));
            }
            track.put("visibilidad", (Object) queryresult.getString("visibilidad"));
            tracks_list.add(new JSONObject(track));
        }

        statement.close();
        queryresult.close();

        if (tracks_list.size() == 1) {
            return tracks_list.get(0);
        }

        return null;
    }

    /**
     * Update the track that have the same ID
     *
     * @param track JSON Object that containt the information track;
     * @return true of false if the track was succesfuly updated
     * @throws java.sql.SQLException
     */
    public boolean updateTrack(JSONObject track) throws SQLException {

        return updateTrack(
                (int) track.get("ID"),
                (int) track.get("id_mision"),
                (int) track.get("id_usuario"),
                (int) track.get("tipo_dato"),
                (int) track.get("origen"),
                (float) track.get("x_geoposicion"),
                (float) track.get("y_geoposicion"),
                (float) track.get("elevacion"),
                (String) track.get("nombre"),
                (String) track.get("descripcion"),
                track.get("datos_json").toString(),
                (String) track.get("visibilidad"));
    }

    /**
     * Update the track that have the same ID
     *
     * @param ID
     * @param id_mision
     * @param id_usuario
     * @param tipo_dato
     * @param origen
     * @param x_geoposicion
     * @param y_geoposicion
     * @param elevacion
     * @param nombre
     * @param descripcion
     * @param datos_json
     * @param visibilidad
     * @return
     * @throws SQLException
     */
    public boolean updateTrack(
            int ID,
            int id_mision,
            int id_usuario,
            int tipo_dato,
            int origen,
            float x_geoposicion,
            float y_geoposicion,
            float elevacion,
            String nombre,
            String descripcion,
            String datos_json,
            String visibilidad) throws SQLException {

        PreparedStatement statement
                = connection.prepareStatement("UPDATE dato_tactico SET "
                        + "id_mision = ?,"
                        + "id_usuario = ?,"
                        + "tipo_dato = ?,"
                        + "origen = ?,"
                        + "geoposicion = POINT(?,?),"
                        + "elevacion = ?,"
                        + "nombre = ?,"
                        + "descripcion = ?,"
                        + "datos_json = ?,"
                        + "visibilidad = ? "
                        + "WHERE ID = ?");
        statement.setInt(1, id_mision);
        statement.setInt(2, id_usuario);
        statement.setInt(3, tipo_dato);
        statement.setInt(4, origen);
        statement.setFloat(5, x_geoposicion);
        statement.setFloat(6, y_geoposicion);
        statement.setFloat(7, elevacion);
        statement.setString(8, nombre);
        statement.setString(9, descripcion);
        statement.setString(10, datos_json);
        statement.setString(11, visibilidad);
        statement.setInt(12, ID);

        int rows_updated = statement.executeUpdate();
        statement.close();
        return rows_updated > 0;
    }

    /**
     * Delete a track that match with the ID
     *
     * @param track
     * @return
     * @throws SQLException
     */
    public boolean deleteTrack(JSONObject track) throws SQLException {
        return deleteTrack((int) track.get("ID"));
    }

    /**
     * Delete a track that match with the ID
     *
     * @param ID
     * @return
     * @throws SQLException
     */
    public boolean deleteTrack(int ID) throws SQLException {
        PreparedStatement statement
                = connection.prepareStatement("UPDATE dato_tactico set eliminado WHERE ID = ?");
        statement.setInt(1, ID);
        int rows_deleted = statement.executeUpdate();
        statement.close();
        return rows_deleted > 0;
    }

    //-------------------------------END-CRUD-----------------------------------
    /**
     * Miscellanies methods
     */
    /**
     * Make a simple json structure to fill the track column
     *
     * @param datos_json A json object to fill with the extra points structure
     * or null<br>
     * if want to create a total new json object
     * @return the JSONObject to fill the track column
     */
    public JSONObject initilizeDatosJSON(JSONObject datos_json) {
        return datos_json != null ? createJSON(datos_json) : createJSON(null);
    }

    /**
     * Make a simple json structure to fill the track column
     *
     * @param json A json object to fill with the extra points structure or
     * null<br>
     * if want to create a total new json object
     * @return the JSONObject to fill the track column
     */
    private JSONObject createJSON(JSONObject json) {
        JSONObject datos_json = json != null ? json : new JSONObject();
        datos_json.put("puntos_extras", createExtraPoints());
        return datos_json;
    }

    /**
     * Create the structure for extra points
     *
     * @return the JSONObject with the structure for extra points
     */
    private JSONObject createExtraPoints() {
        //crear la estrucutura
        JSONObject puntos_extras = new JSONObject();
        //Agregar el campo total
        puntos_extras.put("total", "0");
        //Crear arreglo de puntos
        JSONArray puntos = new JSONArray();
        //Agregar arreglo a la estructura
        puntos_extras.put("puntos", puntos);
        return puntos_extras;
    }

    /**
     * Get the number of extra points of track
     *
     * @param track
     * @return the number of extra points
     */
    public int getTotalExtraPoints(JSONObject track) {
        JSONObject datos_json = (JSONObject) track.get("datos_json");
        JSONObject puntos_extras = (JSONObject) datos_json.get("puntos_extras");
        return Integer.parseInt(puntos_extras.get("total").toString());
    }

    /**
     * Set the number of extra points of a track
     *
     * @param track the track with the points
     * @param total the total of points
     * @return true of false if the track can be saved to database
     * @throws java.sql.SQLException
     */
    public boolean setTotalExtraPoints(JSONObject track, int total) throws SQLException {
        JSONObject datos_json = (JSONObject) track.get("datos_json");
        JSONObject puntos_extras = (JSONObject) datos_json.get("puntos_extras");
        puntos_extras.put("total", total);
        return updateTrack(track);
    }

    /**
     * Insert a point in the track gived
     *
     * @param track the track to insert the point
     * @param extra_point the point to insert
     * @throws SQLException
     */
    public void insertExtraPoint(JSONObject track, JSONObject extra_point) throws SQLException {
        JSONObject datos_json = (JSONObject) track.get("datos_json");
        JSONObject puntos_extras = (JSONObject) datos_json.get("puntos_extras");
        JSONArray puntos = (JSONArray) puntos_extras.get("puntos");
        puntos.add(extra_point);
        setTotalExtraPoints(track, getTotalExtraPoints(track) + 1);

    }

    /**
     * Insert a point in the track gived
     *
     * @param track
     * @param Latitud
     * @param Longitud
     * @throws SQLException
     */
    public void insertExtraPoint(JSONObject track, float Latitud, float Longitud) throws SQLException {
        JSONObject datos_json = (JSONObject) track.get("datos_json");
        JSONObject puntos_extras = (JSONObject) datos_json.get("puntos_extras");
        JSONArray puntos = (JSONArray) puntos_extras.get("puntos");
        JSONObject extra_point = new JSONObject();
        extra_point.put("index", (int) getTotalExtraPoints(track));
        extra_point.put("longitud", Longitud);
        extra_point.put("latitud", Latitud);
        puntos.add(extra_point);
        setTotalExtraPoints(track, getTotalExtraPoints(track) + 1);
    }

    /**
     * Insert a point in the track gived
     *
     * @param track
     * @param index
     * @param Latitud
     * @param Longitud
     * @throws SQLException
     */
    public void insertExtraPoint(JSONObject track, int index, float Latitud, float Longitud) throws SQLException {
        JSONObject datos_json = (JSONObject) track.get("datos_json");
        JSONObject puntos_extras = (JSONObject) datos_json.get("puntos_extras");
        JSONArray puntos = (JSONArray) puntos_extras.get("puntos");
        JSONObject extra_point = new JSONObject();
        extra_point.put("index", index);
        extra_point.put("longitud", Longitud);
        extra_point.put("latitud", Latitud);
        puntos.add(extra_point);
        setTotalExtraPoints(track, getTotalExtraPoints(track) + 1);
    }

    /**
     * Insert an array of points in the track gived
     *
     * @param track the track to insert the points
     * @param extra_points the points to insert
     * @return true of false if the track can be saved to database
     * @throws SQLException
     */
    public boolean insertExtraPoints(JSONObject track, JSONArray extra_points) throws SQLException {
        for (int i = 0; i < extra_points.size(); i++) {
            insertExtraPoint(track, (JSONObject) extra_points.get(i));
        }
        return setTotalExtraPoints(track, extra_points.size());
    }

    /**
     * Get the list of extra points from a track
     *
     * @param track the track with the extra points
     * @return JSONArray
     */
    public List<JSONObject> getExtraPoints(JSONObject track) {
        JSONObject datos_json = (JSONObject) track.get("datos_json");
        JSONObject puntos_extras = (JSONObject) datos_json.get("puntos_extras");
        List<JSONObject> puntos = new ArrayList<>();
        JSONArray points = (JSONArray) puntos_extras.get("puntos");
        for (int i = 0; i < points.size(); i++) {
            JSONObject point = (JSONObject) points.get(i);
            int index = Integer.parseInt(point.get("index").toString());
            puntos.add(index, point);
        }
        return puntos;
    }

    public void cleanAllExtraPoints(JSONObject track) {
        JSONObject datos_json = (JSONObject) track.get("datos_json");
        JSONObject puntos_extras = (JSONObject) datos_json.get("puntos_extras");
        puntos_extras.put("puntos", new JSONArray());
        puntos_extras.put("total", 0);
    }

    /**
     * Insert a new value into the json datos_json
     *
     * @param datos_json
     * @param key
     * @param value
     */
    public void insertValuetoJSON(JSONObject datos_json, Object key, Object value) {
        datos_json.put(key, value);
    }

    /**
     * Obtiene el id de la mision actual
     * @return el id de la mision actual
     */
    public String getId_mision() {
        return id_mision;
    }

    /**
     * Setea el id de la mision
     * @param id_mision 
     */
    public void setId_mision(String id_mision) {
        this.id_mision = id_mision;
    }
}