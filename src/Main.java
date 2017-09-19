import java.awt.Point;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
	    	  LuciadBDConnection conection = new LuciadBDConnection("localhost", "cmca", "root", "root");
	          List<JSONObject> tracks_list = conection.getTracks();
	          for(int t=0; t < tracks_list.size(); t++ ) 
	          {
	        	  float point_x = (float) tracks_list.get(t).get("x_geoposicion");
	        	  float point_y = (float) tracks_list.get(t).get("y_geoposicion");

	        	  System.out.println("-->"+point_x+","+point_y);
	          }
	      }
	      catch(Exception e) {
	    	  System.out.println("----- DATA BASE Exception -----");
	    	  System.out.println(e.getMessage());
	      }

	}

}
