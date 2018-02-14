package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
*
* @author RARS
*/

public class Utilidades 
{
  public static String PATH = "";
  public static String CONFIGURACIONES_XML = "config.xml";
  
  public static String ARCHIVO_CONFIGURACION;  
  //Config tags
  private static Map<String,Object> tags_values = new HashMap<String,Object>() 
  {
	{
		put( 
			"RADAR_SERVER_IP", 
			new HashMap<String,Object>() 
			{
				{
				    put("tag_name", "radar_ip");
				    put("tag_type", "String");
				    put("tag_value", "");
			  	}
			}
		);
		put( 
			"RADAR_SERVER_PUERTO", 
			new HashMap<String,Object>() 
			{
				{
				    put("tag_name", "radar_puerto");
				    put("tag_type", "int");
				    put("tag_value", "");
			  	}
			}
		);
		put( 
			"DATABASE_HOST", 
			new HashMap<String,Object>() 
			{
				{
				    put("tag_name", "database_host");
				    put("tag_type", "String");
				    put("tag_value", "");
			  	}
			}
		);
		put( 
			"DATABASE_NAME", 
			new HashMap<String,Object>() 
			{
				{
				    put("tag_name", "database_name");
				    put("tag_type", "String");
				    put("tag_value", "");
			  	}
			}
		);
		put( 
			"DATABASE_USER", 
			new HashMap<String,Object>() 
			{
				{
				    put("tag_name", "database_user");
				    put("tag_type", "String");
				    put("tag_value", "");
			  	}
			}
		);
		put( 
			"DATABASE_PASS", 
			new HashMap<String,Object>() 
			{
				{
				    put("tag_name", "database_pass");
				    put("tag_type", "String");
				    put("tag_value", "");
			  	}
			}
		);
	}
  };

  public void Utilidades(String path)
  {
    this.PATH = path;
  }
  
  public void setPath(String path)
  {
	  PATH = path;
	  ARCHIVO_CONFIGURACION = PATH + "/" + CONFIGURACIONES_XML;
  }
  
  public Map<String, Object> getTags()
  {
	  return tags_values;
  }
  
  public void setTagValue(String tag, Object value) 
  {
	  ((Map<String, Object>) tags_values.get(tag)).replace("tag_value", value);
  }
  
  public Object getTagValue(String tag)
  {
	  String type = ((Map<String, Object>) tags_values.get(tag)).get("tag_type").toString();
	  switch (type) {
		case "int":
			return Integer.parseInt(((Map<String, Object>) tags_values.get(tag)).get("tag_value").toString());
		case "String":
			return ((Map<String, Object>) tags_values.get(tag)).get("tag_value").toString();
		case "double":
			return Double.parseDouble(((Map<String, Object>) tags_values.get(tag)).get("tag_value").toString());
		default:
			return ((Map<String, Object>) tags_values.get(tag)).get("tag_value");
	  }
	  
  }
  
  public String getTagName(String tag)
  {
	  return ((Map<String, Object>) tags_values.get(tag)).get("tag_name").toString();
  }
  
  public int getTagsCount() 
  {
	  return tags_values.size(); 
  }
  
  int num_tabs = 0;
  public String getStringTabs(int num_t)
  {
      String tabs = "";
      for (int tab = 1; tab <= num_t; tab++)
      {
          tabs = tabs + "\t";
      }
      return tabs;
  }
  
  public void printTagsValues()
  {
	  printMap(tags_values);
  }
  
  public void printMap(Map<String,?> ts)
  {
	
	for (Map.Entry<String, ?> t : ts.entrySet()) 
  	{
		boolean is_dictionary = t.getValue() instanceof Map;
		boolean is_list = t.getValue() instanceof List || t.getValue() instanceof ArrayList<?>; 
		System.out.println(getStringTabs(num_tabs) + t.getKey() + ":" + (is_dictionary || is_list ? "" : t.getValue().toString()));
		if(is_dictionary)
		{
			num_tabs++;
	        System.out.println(getStringTabs(num_tabs - 1) + "{");
	        printMap((Map<String, ?>) t.getValue());
	        System.out.println(getStringTabs(num_tabs - 1) + "}");
	        num_tabs--;
		}
		if (is_list)
		{
	      for( Map<String,?> tt : (List<Map<String,?>>)t.getValue() )
	      {
	          num_tabs++;
	          System.out.println(getStringTabs(num_tabs - 1) + "[");
	          printMap(tt);
	          System.out.println(getStringTabs(num_tabs - 1) + "]");
			  num_tabs--;
	      }
		}
  	}
  }
  
  public void printList(List<?> l)
  {
	for (Object object : l) 
  	{
		if(object instanceof Map)
		{
			printMap((Map<String,?>)object);
		}
		else if(object instanceof JSONObject)
		{
			printMap(toMap((JSONObject)object));
		}
  	}
  }
  
  public static Map<String, Object> toMap(JSONObject jsonobj)
  {
      Map<String, Object> map = new HashMap<String, Object>();
      Iterator<String> keys = jsonobj.keySet().iterator();
      while(keys.hasNext()) {
          String key = keys.next();
          Object value = jsonobj.get(key);
          if (value instanceof JSONArray) {
              value = toList((JSONArray) value);
          } else if (value instanceof JSONObject) {
              value = toMap((JSONObject) value);
          }   
          map.put(key, value);
      }   return map;
  }
  
  public static List<Object> toList(JSONArray array) {
      List<Object> list = new ArrayList<Object>();
      for(int i = 0; i < array.size(); i++) {
          Object value = array.get(i);
          if (value instanceof JSONArray) {
              value = toList((JSONArray) value);
          }
          else if (value instanceof JSONObject) {
              value = toMap((JSONObject) value);
          }
          list.add(value);
      }
      return list;
  }

}