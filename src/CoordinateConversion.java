/*
 * Author: Sami Salkosuo, sami.salkosuo@fi.ibm.com
 *
 * (c) Copyright IBM Corp. 2007
 */
//package com.ibm.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class CoordinateConversion
{

  public CoordinateConversion()
  {

  }

  	double kmxH_Knot = 0.539957;
  
  	public double kmxHToKnot(double kmxH) 
  	{
  		return kmxH * kmxH_Knot;
  	}
  	
  	double knot_kmxH = 1.852;
  	
  	public double knotToKmxH(double knot)
  	{
  		return knot * knot_kmxH;
  	}
  
  	public String decimalLatitudeToDMS(double value) {
		return decimalToDMS(value, true, false);
	}
	
	public String decimalLongitudeToDMS(double value) {
		return decimalToDMS(value, false, true);
	}
	
	//Input a double latitude or longitude in the decimal format
	//e.g. -79.982195
  	public String decimalToDMS(double coord, boolean latitud, boolean longitude) 
  	{
		String output, degrees, minutes, seconds;
		
		// gets the modulus the coordinate divided by one (MOD1).
		// in other words gets all the numbers after the decimal point.
		// e.g. mod := -79.982195 % 1 == 0.982195 
		//
		// next get the integer part of the coord. On other words the whole number part.
		// e.g. intPart := -79
		
		double mod = coord % 1;
		int intPart = (int)coord;
		
		//set degrees to the value of intPart
		//e.g. degrees := "-79"
		
		degrees = String.valueOf(Math.abs(intPart));
		
		// next times the MOD1 of degrees by 60 so we can find the integer part for minutes.
		// get the MOD1 of the new coord to find the numbers after the decimal point.
		// e.g. coord :=  0.982195 * 60 == 58.9317
		//	mod   := 58.9317 % 1    == 0.9317
		//
		// next get the value of the integer part of the coord.
		// e.g. intPart := 58
		
		coord = mod * 60;
		mod = coord % 1;
		intPart = (int)coord;
		   if (intPart < 0) {
		      // Convert number to positive if it's negative.
		      intPart *= -1;
		   }
		
		// set minutes to the value of intPart.
		// e.g. minutes = "58"
		minutes = String.valueOf(intPart);
		
		//do the same again for minutes
		//e.g. coord := 0.9317 * 60 == 55.902
		//e.g. intPart := 55
		coord = mod * 60;
		intPart = (int)coord;
		   if (intPart < 0) {
		      // Convert number to positive if it's negative.
		      intPart *= -1;
		   }
		
		// set seconds to the value of intPart.
		// e.g. seconds = "55"
		seconds = String.valueOf(intPart);
		
		// I used this format for android but you can change it 
		// to return in whatever format you like
		// e.g. output = "-79/1,58/1,56/1"
		/*output = (
				degrees + "/1," + 
				minutes + "/1," + 
				seconds + "/1" +
				" " +
				( latitud ? (coord > 0 ? "N" : "S") : "" ) + 
			    ( longitude ? (coord > 0 ? "E" : "W") : "" )
		);*/
		
		//Standard output of D°M′S″
		output = (
			degrees + "°" + 
			minutes + "'" + 
			seconds + "\"" +
			" " +
			( latitud ? (coord > 0 ? "N" : "S") : "" ) + 
		    ( longitude ? (coord > 0 ? "E" : "W") : "" )
		);
	
		return output;
	}
  	
  	public Map<String,Object> decimalToDMSSeparate(double coord, boolean latitud, boolean longitude) 
  	{
  		double bk_coord = coord;
		String degrees, minutes, seconds; //,output
		
		// gets the modulus the coordinate divided by one (MOD1).
		// in other words gets all the numbers after the decimal point.
		// e.g. mod := -79.982195 % 1 == 0.982195 
		//
		// next get the integer part of the coord. On other words the whole number part.
		// e.g. intPart := -79
		
		double mod = coord % 1;
		int intPart = (int)coord;
		
		//set degrees to the value of intPart
		//e.g. degrees := "-79"
		
		degrees = String.valueOf(Math.abs(intPart));
		
		// next times the MOD1 of degrees by 60 so we can find the integer part for minutes.
		// get the MOD1 of the new coord to find the numbers after the decimal point.
		// e.g. coord :=  0.982195 * 60 == 58.9317
		//	mod   := 58.9317 % 1    == 0.9317
		//
		// next get the value of the integer part of the coord.
		// e.g. intPart := 58
		
		coord = mod * 60;
		mod = coord % 1;
		intPart = (int)coord;
		   if (intPart < 0) {
		      // Convert number to positive if it's negative.
		      intPart *= -1;
		   }
		
		// set minutes to the value of intPart.
		// e.g. minutes = "58"
		minutes = String.valueOf(intPart);
		
		//do the same again for minutes
		//e.g. coord := 0.9317 * 60 == 55.902
		//e.g. intPart := 55
		coord = mod * 60;
		intPart = (int)coord;
		   if (intPart < 0) {
		      // Convert number to positive if it's negative.
		      intPart *= -1;
		   }
		
		// set seconds to the value of intPart.
		// e.g. seconds = "55"
		seconds = String.valueOf(intPart);
		
		// I used this format for android but you can change it 
		// to return in whatever format you like
		// e.g. output = "-79/1,58/1,56/1"
		/*output = (
				degrees + "/1," + 
				minutes + "/1," + 
				seconds + "/1" +
				" " +
				( latitud ? (coord > 0 ? "N" : "S") : "" ) + 
			    ( longitude ? (coord > 0 ? "E" : "W") : "" )
		);*/
		
		//Standard output of D°M′S″
		/*output = (
			degrees + "°" + 
			minutes + "'" + 
			seconds + "\"" +
			" " +
			( latitud ? (coord > 0 ? "N" : "S") : "" ) + 
		    ( longitude ? (coord > 0 ? "E" : "W") : "" )
		);*/
		
		Map<String, Object> output = new HashMap<>();
		output.put("degrees", Double.parseDouble(degrees));
		output.put("minutes", Double.parseDouble(minutes));
		output.put("seconds", Double.parseDouble(seconds));
		output.put(
			"NSEW",
			( latitud ? (bk_coord < 0 ? "S" : "N") : "" ) + 
		    ( longitude ? (bk_coord < 0 ? "W" : "E") : "" )
		);
		/*for (Map.Entry<String, Object> entry : output.entrySet()) {
		    System.out.println("clave=" + entry.getKey() + ", valor=" + entry.getValue());
		}
		System.out.println("");*/
		return output;
	}

  	public double DMSToDecimal(String format_coord) {
  	  //94°24'35.64'' W
  	  String separate_caracter = ";";
  	  String coord = format_coord.replaceAll("°", separate_caracter);
  	  coord = coord.replaceAll("\"", separate_caracter);
  	  coord = coord.replaceAll("'", separate_caracter);
  	  coord = coord.replaceAll(" ", "");
  	  coord = coord.startsWith(separate_caracter) ? coord.replaceFirst(separate_caracter, ""): coord;
  	  String split_coord[] = coord.split(separate_caracter);
  	  double grades = Float.parseFloat(split_coord[0]);
  	  double minutes = Float.parseFloat(split_coord[1]);
  	  double seconds = Float.parseFloat(split_coord[2]);
  	  String NSEW = split_coord[3];
  	  return DMSToDecimal(NSEW, grades, minutes, seconds );
    }
  	
      /*
	* Conversion DMS to decimal 
	*
	* Input: latitude or longitude in the DMS format ( example: W 79° 58' 55.903")
	* Return: latitude or longitude in decimal format   
	* hemisphereOUmeridien => {W,E,S,N}
	*
	*/
	public double DMSToDecimal(String hemisphereOUmeridien,double degres,double minutes,double secondes)
	{
		double LatOrLon=0;
		double signe=1.0;
				
		if((hemisphereOUmeridien.equals("W"))||(hemisphereOUmeridien.equals("S"))) {signe=-1.0;}		
		LatOrLon = signe*(Math.floor(degres) + Math.floor(minutes)/60.0 + secondes/3600.0);
		
		return(LatOrLon);		
	}
  
  public double[] utm2LatLon(String UTM)
  {
    UTM2LatLon c = new UTM2LatLon();
    return c.convertUTMToLatLong(UTM);
  }

  public String latLon2UTM(double latitude, double longitude)
  {
    LatLon2UTM c = new LatLon2UTM();
    return c.convertLatLonToUTM(latitude, longitude);

  }

  private void validate(double latitude, double longitude)
  {
    if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0
        || longitude >= 180.0)
    {
      throw new IllegalArgumentException(
          "Legal ranges: latitude [-90,90], longitude [-180,180).");
    }

  }

  public String latLon2MGRUTM(double latitude, double longitude)
  {
    LatLon2MGRUTM c = new LatLon2MGRUTM();
    return c.convertLatLonToMGRUTM(latitude, longitude);

  }

  public double[] mgrutm2LatLon(String MGRUTM)
  {
    MGRUTM2LatLon c = new MGRUTM2LatLon();
    return c.convertMGRUTMToLatLong(MGRUTM);
  }

  public double degreeToRadian(double degree)
  {
    return degree * Math.PI / 180;
  }

  public double radianToDegree(double radian)
  {
    return radian * 180 / Math.PI;
  }

  private double POW(double a, double b)
  {
    return Math.pow(a, b);
  }

  private double SIN(double value)
  {
    return Math.sin(value);
  }

  private double COS(double value)
  {
    return Math.cos(value);
  }

  private double TAN(double value)
  {
    return Math.tan(value);
  }

  private class LatLon2UTM
  {
    public String convertLatLonToUTM(double latitude, double longitude)
    {
      validate(latitude, longitude);
      String UTM = "";

      setVariables(latitude, longitude);

      String longZone = getLongZone(longitude);
      LatZones latZones = new LatZones();
      String latZone = latZones.getLatZone(latitude);

      double _easting = getEasting();
      double _northing = getNorthing(latitude);

      UTM = longZone + " " + latZone + " " + ((int) _easting) + " "
          + ((int) _northing);
      // UTM = longZone + " " + latZone + " " + decimalFormat.format(_easting) +
      // " "+ decimalFormat.format(_northing);

      return UTM;

    }

    protected void setVariables(double latitude, double longitude)
    {
      latitude = degreeToRadian(latitude);
      rho = equatorialRadius * (1 - e * e)
          / POW(1 - POW(e * SIN(latitude), 2), 3 / 2.0);

      nu = equatorialRadius / POW(1 - POW(e * SIN(latitude), 2), (1 / 2.0));

      double var1;
      if (longitude < 0.0)
      {
        var1 = ((int) ((180 + longitude) / 6.0)) + 1;
      }
      else
      {
        var1 = ((int) (longitude / 6)) + 31;
      }
      double var2 = (6 * var1) - 183;
      double var3 = longitude - var2;
      p = var3 * 3600 / 10000;

      S = A0 * latitude - B0 * SIN(2 * latitude) + C0 * SIN(4 * latitude) - D0
          * SIN(6 * latitude) + E0 * SIN(8 * latitude);

      K1 = S * k0;
      K2 = nu * SIN(latitude) * COS(latitude) * POW(sin1, 2) * k0 * (100000000)
          / 2;
      K3 = ((POW(sin1, 4) * nu * SIN(latitude) * Math.pow(COS(latitude), 3)) / 24)
          * (5 - POW(TAN(latitude), 2) + 9 * e1sq * POW(COS(latitude), 2) + 4
              * POW(e1sq, 2) * POW(COS(latitude), 4))
          * k0
          * (10000000000000000L);

      K4 = nu * COS(latitude) * sin1 * k0 * 10000;

      K5 = POW(sin1 * COS(latitude), 3) * (nu / 6)
          * (1 - POW(TAN(latitude), 2) + e1sq * POW(COS(latitude), 2)) * k0
          * 1000000000000L;

      A6 = (POW(p * sin1, 6) * nu * SIN(latitude) * POW(COS(latitude), 5) / 720)
          * (61 - 58 * POW(TAN(latitude), 2) + POW(TAN(latitude), 4) + 270
              * e1sq * POW(COS(latitude), 2) - 330 * e1sq
              * POW(SIN(latitude), 2)) * k0 * (1E+24);

    }

    protected String getLongZone(double longitude)
    {
      double longZone = 0;
      if (longitude < 0.0)
      {
        longZone = ((180.0 + longitude) / 6) + 1;
      }
      else
      {
        longZone = (longitude / 6) + 31;
      }
      String val = String.valueOf((int) longZone);
      if (val.length() == 1)
      {
        val = "0" + val;
      }
      return val;
    }

    protected double getNorthing(double latitude)
    {
      double northing = K1 + K2 * p * p + K3 * POW(p, 4);
      if (latitude < 0.0)
      {
        northing = 10000000 + northing;
      }
      return northing;
    }

    protected double getEasting()
    {
      return 500000 + (K4 * p + K5 * POW(p, 3));
    }

    // Lat Lon to UTM variables

    // equatorial radius
    double equatorialRadius = 6378137;

    // polar radius
    double polarRadius = 6356752.314;

    // flattening
    double flattening = 0.00335281066474748;// (equatorialRadius-polarRadius)/equatorialRadius;

    // inverse flattening 1/flattening
    double inverseFlattening = 298.257223563;// 1/flattening;

    // Mean radius
    double rm = POW(equatorialRadius * polarRadius, 1 / 2.0);

    // scale factor
    double k0 = 0.9996;

    // eccentricity
    double e = Math.sqrt(1 - POW(polarRadius / equatorialRadius, 2));

    double e1sq = e * e / (1 - e * e);

    double n = (equatorialRadius - polarRadius)
        / (equatorialRadius + polarRadius);

    // r curv 1
    double rho = 6368573.744;

    // r curv 2
    double nu = 6389236.914;

    // Calculate Meridional Arc Length
    // Meridional Arc
    double S = 5103266.421;

    double A0 = 6367449.146;

    double B0 = 16038.42955;

    double C0 = 16.83261333;

    double D0 = 0.021984404;

    double E0 = 0.000312705;

    // Calculation Constants
    // Delta Long
    double p = -0.483084;

    double sin1 = 4.84814E-06;

    // Coefficients for UTM Coordinates
    double K1 = 5101225.115;

    double K2 = 3750.291596;

    double K3 = 1.397608151;

    double K4 = 214839.3105;

    double K5 = -2.995382942;

    double A6 = -1.00541E-07;

  }

  private boolean checkNumber(double input, double min, double max, String msg)
	{
	//  Funcion para comprobar que los datos introduciodos estan dentro del margen valido.
	    msg = msg + " campo con datos no válidos: " + input;
	    String str = input + "";
	    for (int i = 0; i < str.length(); i++) 
	    {
	        String cha = str.substring(i, i + 1);
	        if(!cha.equals("."))
	        {
		        int ch = Integer.parseInt(cha);
		        if ((ch < 0 || 9 < ch)) 
		        {
		            //alert(msg);
		        	System.out.println(msg);
		            return false;
		        }
	        }
	    }
	    double num = Double.parseDouble(str);
	    if (num < min || max < num) 
	    {
	        //alert(msg + " fuera de rango válido [" + min + ".." + max + "]");
	        System.out.println(msg + " fuera de rango válido [" + min + ".." + max + "]");
	        return false;
	    }
	    input = Double.parseDouble(str);
	    return true;
	}

  	private double mp(double indegree, double inminute){
		//  Esta funcion calcula la latitud aumentada (parte meridional) a una latitud dada (indegree, inminute) 
		double result1 = 0;
		// Comprobamos que los valores estan dentro de los limites aceptables.
	
		if (!checkNumber(indegree, 0, 89, "Latitud grados ") || !checkNumber(inminute, 0, 59.9999999, "Latitud minutos ") )
		{
			return result1;
		}
	
		double degree =indegree;
		double minute = inminute;
		double mmm = minute / 60;
		degree = degree + mmm;
		double degree2 = 45 + (degree/2);
		double radian2 = degree2*Math.PI/180;
	
		result1 = 3437.746 * Math.log(Math.tan(radian2)) - 23 * Math.sin(degree*Math.PI/180);
		result1 = Math.round(result1 * 1000)/1000;
	
		return result1;
	}

  	public Map<String,Object> estimaDirectaByKmXH(double latitud_inicial, double longitud_inicial, double tiempo_minutos, double rumbo, double velocidad){
  		
		Map<String,Object> latitud_separate = decimalToDMSSeparate(latitud_inicial, true, false);
		Map<String,Object> longitud_separate = decimalToDMSSeparate(longitud_inicial, false, true);
		return estimaDirecta(
				(double)latitud_separate.get("degrees"),
				(double)latitud_separate.get("minutes"),
				(String)latitud_separate.get("NSEW"),
				(double)longitud_separate.get("degrees"),
				(double)longitud_separate.get("minutes"),
				(String)longitud_separate.get("NSEW"),
				tiempo_minutos,
				rumbo,
				kmxHToKnot(velocidad)
		);
	}
  	
	public Map<String,Object> estimaDirectaByKnot(double latitud_inicial, double longitud_inicial, double tiempo_minutos, double rumbo, double velocidad){
	
		Map<String,Object> latitud_separate = decimalToDMSSeparate(latitud_inicial, true, false);
		Map<String,Object> longitud_separate = decimalToDMSSeparate(longitud_inicial, false, true);
		return estimaDirecta(
				(double)latitud_separate.get("degrees"),
				(double)latitud_separate.get("minutes"),
				(String)latitud_separate.get("NSEW"),
				(double)longitud_separate.get("degrees"),
				(double)longitud_separate.get("minutes"),
				(String)longitud_separate.get("NSEW"),
				tiempo_minutos,
				rumbo,
				velocidad
		);
	}

	public Map<String,Object> estimaDirecta(
			double ilatdeg, 
			double ilatmin, 
			String ilatside,
			double ilondeg, 
			double ilonmin, 
			String ilonside,
			double tiempo_minutos, 
			double rumbo, 
			double velocidad //nudos
	){
	
		//   Esta es la funcion principal del programa: Estima directa utilizando latitudes aumentadas.
		//   Pasamos situacion inicial a grados asignando signo segun caso:
		//double inlat = dm2deg(ilatdeg, ilatmin, ilatside);
		double inlat = DMSToDecimal(ilatside, ilatdeg, ilatmin, 0.0);
		//double inlon = dm2deg(ilondeg, ilonmin, ilonside);
		double inlon = DMSToDecimal(ilonside, ilondeg, ilonmin, 0.0);
		
		double tiempo = tiempo_minutos / 60;
	
		double distancia = tiempo * velocidad;
		
		//  Calculamos latidud final:
		double dlat = distancia * Math.cos(rumbo*Math.PI/180);
		double fnlat = inlat * 60 + dlat;
		String flatside = "";
		if(fnlat > 0) {
			flatside = "N";
		}
		if(fnlat < 0) {
			flatside = "S";
		}
		double f1 = Math.abs(fnlat); 
		double f2 = f1 / 60;
		double flatgrados = Math.floor(f2);
		double flatminutos = f1 - 60 * flatgrados;
		//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
		//Latitud Final
		double flatdeg = Math.floor(flatgrados);
		double flatmin = Math.round(flatminutos * 100)/100;
		//flatside
		//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
		
		// Calculo de la longitud final
		// Calculamos las latitudes aumentadas correspondientes a las  latitudes inicial y final.
		double inmp = mp(ilatdeg, ilatmin);
		if (inlat != 0)
		{
			inmp=inmp*inlat/Math.abs(inlat);
		}
		double fnmp = mp(flatdeg, flatmin);
		if(fnlat != 0) {
			fnmp=fnmp*fnlat/Math.abs(fnlat);
		}
		double dla = fnmp - inmp;
		double rumborad = rumbo*Math.PI/180;
		double dlo = 0;
	
		if(rumbo == 90 || rumbo == 270)
		{
			double apartamiento = distancia;
		    dlo = apartamiento / Math.cos(Math.abs(inlat)*Math.PI/180); 
		    if(rumbo==270) {
		    	dlo = -dlo;
		    }
		}
		else
		{
		    dlo = dla *  Math.tan(rumborad);
		}
		double fnlon = inlon * 60 + dlo;
		String flonside = "";
		if(fnlon > 0) {
			flonside = "E";
		}
		if(fnlon < 0) {
			flonside = "W";
		}
		double e1 = Math.abs(fnlon);
		double e2 = e1 / 60;
		double flongrados = Math.floor(e2);
		double flonminutos = e1 - 60 * flongrados;
	
		//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
		//Longitud Final
		double flondeg = Math.floor(flongrados);
		double flonmin = Math.round(flonminutos * 100)/100;
		//flonside
		//*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
		
		double latitud_final = DMSToDecimal(flatside, flatdeg, flatmin, 0.0);
		double longitud_final = DMSToDecimal(flonside, flondeg, flonmin, 0.0);
		Map<String, Object> new_coords = new HashMap<>();
		new_coords.put("latidude", latitud_final);
		new_coords.put("longitude", longitud_final);
		
		return new_coords;
	}
  
  private class LatLon2MGRUTM extends LatLon2UTM
  {
    public String convertLatLonToMGRUTM(double latitude, double longitude)
    {
      validate(latitude, longitude);
      String mgrUTM = "";

      setVariables(latitude, longitude);

      String longZone = getLongZone(longitude);
      LatZones latZones = new LatZones();
      String latZone = latZones.getLatZone(latitude);

      double _easting = getEasting();
      double _northing = getNorthing(latitude);
      Digraphs digraphs = new Digraphs();
      String digraph1 = digraphs.getDigraph1(Integer.parseInt(longZone),
          _easting);
      String digraph2 = digraphs.getDigraph2(Integer.parseInt(longZone),
          _northing);

      String easting = String.valueOf((int) _easting);
      if (easting.length() < 5)
      {
        easting = "00000" + easting;
      }
      easting = easting.substring(easting.length() - 5);

      String northing;
      northing = String.valueOf((int) _northing);
      if (northing.length() < 5)
      {
        northing = "0000" + northing;
      }
      northing = northing.substring(northing.length() - 5);

      mgrUTM = longZone + latZone + digraph1 + digraph2 + easting + northing;
      return mgrUTM;
    }
  }

  private class MGRUTM2LatLon extends UTM2LatLon
  {
    public double[] convertMGRUTMToLatLong(String mgrutm)
    {
      double[] latlon = { 0.0, 0.0 };
      // 02CNR0634657742
      int zone = Integer.parseInt(mgrutm.substring(0, 2));
      String latZone = mgrutm.substring(2, 3);

      String digraph1 = mgrutm.substring(3, 4);
      String digraph2 = mgrutm.substring(4, 5);
      easting = Double.parseDouble(mgrutm.substring(5, 10));
      northing = Double.parseDouble(mgrutm.substring(10, 15));

      LatZones lz = new LatZones();
      double latZoneDegree = lz.getLatZoneDegree(latZone);

      double a1 = latZoneDegree * 40000000 / 360.0;
      double a2 = 2000000 * Math.floor(a1 / 2000000.0);

      Digraphs digraphs = new Digraphs();

      double digraph2Index = digraphs.getDigraph2Index(digraph2);

      double startindexEquator = 1;
      if ((1 + zone % 2) == 1)
      {
        startindexEquator = 6;
      }

      double a3 = a2 + (digraph2Index - startindexEquator) * 100000;
      if (a3 <= 0)
      {
        a3 = 10000000 + a3;
      }
      northing = a3 + northing;

      zoneCM = -183 + 6 * zone;
      double digraph1Index = digraphs.getDigraph1Index(digraph1);
      int a5 = 1 + zone % 3;
      double[] a6 = { 16, 0, 8 };
      double a7 = 100000 * (digraph1Index - a6[a5 - 1]);
      easting = easting + a7;

      setVariables();

      double latitude = 0;
      latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

      if (latZoneDegree < 0)
      {
        latitude = 90 - latitude;
      }

      double d = _a2 * 180 / Math.PI;
      double longitude = zoneCM - d;

      if (getHemisphere(latZone).equals("S"))
      {
        latitude = -latitude;
      }

      latlon[0] = latitude;
      latlon[1] = longitude;
      return latlon;
    }
  }

  private class UTM2LatLon
  {
    double easting;

    double northing;

    int zone;

    String southernHemisphere = "ACDEFGHJKLM";

    protected String getHemisphere(String latZone)
    {
      String hemisphere = "N";
      if (southernHemisphere.indexOf(latZone) > -1)
      {
        hemisphere = "S";
      }
      return hemisphere;
    }

    public double[] convertUTMToLatLong(String UTM)
    {
      double[] latlon = { 0.0, 0.0 };
      String[] utm = UTM.split(" ");
      zone = Integer.parseInt(utm[0]);
      String latZone = utm[1];
      easting = Double.parseDouble(utm[2]);
      northing = Double.parseDouble(utm[3]);
      String hemisphere = getHemisphere(latZone);
      double latitude = 0.0;
      double longitude = 0.0;

      if (hemisphere.equals("S"))
      {
        northing = 10000000 - northing;
      }
      setVariables();
      latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

      if (zone > 0)
      {
        zoneCM = 6 * zone - 183.0;
      }
      else
      {
        zoneCM = 3.0;

      }

      longitude = zoneCM - _a3;
      if (hemisphere.equals("S"))
      {
        latitude = -latitude;
      }

      latlon[0] = latitude;
      latlon[1] = longitude;
      return latlon;

    }

    protected void setVariables()
    {
      arc = northing / k0;
      mu = arc
          / (a * (1 - POW(e, 2) / 4.0 - 3 * POW(e, 4) / 64.0 - 5 * POW(e, 6) / 256.0));

      ei = (1 - POW((1 - e * e), (1 / 2.0)))
          / (1 + POW((1 - e * e), (1 / 2.0)));

      ca = 3 * ei / 2 - 27 * POW(ei, 3) / 32.0;

      cb = 21 * POW(ei, 2) / 16 - 55 * POW(ei, 4) / 32;
      cc = 151 * POW(ei, 3) / 96;
      cd = 1097 * POW(ei, 4) / 512;
      phi1 = mu + ca * SIN(2 * mu) + cb * SIN(4 * mu) + cc * SIN(6 * mu) + cd
          * SIN(8 * mu);

      n0 = a / POW((1 - POW((e * SIN(phi1)), 2)), (1 / 2.0));

      r0 = a * (1 - e * e) / POW((1 - POW((e * SIN(phi1)), 2)), (3 / 2.0));
      fact1 = n0 * TAN(phi1) / r0;

      _a1 = 500000 - easting;
      dd0 = _a1 / (n0 * k0);
      fact2 = dd0 * dd0 / 2;

      t0 = POW(TAN(phi1), 2);
      Q0 = e1sq * POW(COS(phi1), 2);
      fact3 = (5 + 3 * t0 + 10 * Q0 - 4 * Q0 * Q0 - 9 * e1sq) * POW(dd0, 4)
          / 24;

      fact4 = (61 + 90 * t0 + 298 * Q0 + 45 * t0 * t0 - 252 * e1sq - 3 * Q0
          * Q0)
          * POW(dd0, 6) / 720;

      //
      lof1 = _a1 / (n0 * k0);
      lof2 = (1 + 2 * t0 + Q0) * POW(dd0, 3) / 6.0;
      lof3 = (5 - 2 * Q0 + 28 * t0 - 3 * POW(Q0, 2) + 8 * e1sq + 24 * POW(t0, 2))
          * POW(dd0, 5) / 120;
      _a2 = (lof1 - lof2 + lof3) / COS(phi1);
      _a3 = _a2 * 180 / Math.PI;

    }

    double arc;

    double mu;

    double ei;

    double ca;

    double cb;

    double cc;

    double cd;

    double n0;

    double r0;

    double _a1;

    double dd0;

    double t0;

    double Q0;

    double lof1;

    double lof2;

    double lof3;

    double _a2;

    double phi1;

    double fact1;

    double fact2;

    double fact3;

    double fact4;

    double zoneCM;

    double _a3;

    double b = 6356752.314;

    double a = 6378137;

    double e = 0.081819191;

    double e1sq = 0.006739497;

    double k0 = 0.9996;

  }

  private class Digraphs
  {
    private Map digraph1 = new Hashtable();

    private Map digraph2 = new Hashtable();

    private String[] digraph1Array = { "A", "B", "C", "D", "E", "F", "G", "H",
        "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
        "Y", "Z" };

    private String[] digraph2Array = { "V", "A", "B", "C", "D", "E", "F", "G",
        "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V" };

    public Digraphs()
    {
      digraph1.put(new Integer(1), "A");
      digraph1.put(new Integer(2), "B");
      digraph1.put(new Integer(3), "C");
      digraph1.put(new Integer(4), "D");
      digraph1.put(new Integer(5), "E");
      digraph1.put(new Integer(6), "F");
      digraph1.put(new Integer(7), "G");
      digraph1.put(new Integer(8), "H");
      digraph1.put(new Integer(9), "J");
      digraph1.put(new Integer(10), "K");
      digraph1.put(new Integer(11), "L");
      digraph1.put(new Integer(12), "M");
      digraph1.put(new Integer(13), "N");
      digraph1.put(new Integer(14), "P");
      digraph1.put(new Integer(15), "Q");
      digraph1.put(new Integer(16), "R");
      digraph1.put(new Integer(17), "S");
      digraph1.put(new Integer(18), "T");
      digraph1.put(new Integer(19), "U");
      digraph1.put(new Integer(20), "V");
      digraph1.put(new Integer(21), "W");
      digraph1.put(new Integer(22), "X");
      digraph1.put(new Integer(23), "Y");
      digraph1.put(new Integer(24), "Z");

      digraph2.put(new Integer(0), "V");
      digraph2.put(new Integer(1), "A");
      digraph2.put(new Integer(2), "B");
      digraph2.put(new Integer(3), "C");
      digraph2.put(new Integer(4), "D");
      digraph2.put(new Integer(5), "E");
      digraph2.put(new Integer(6), "F");
      digraph2.put(new Integer(7), "G");
      digraph2.put(new Integer(8), "H");
      digraph2.put(new Integer(9), "J");
      digraph2.put(new Integer(10), "K");
      digraph2.put(new Integer(11), "L");
      digraph2.put(new Integer(12), "M");
      digraph2.put(new Integer(13), "N");
      digraph2.put(new Integer(14), "P");
      digraph2.put(new Integer(15), "Q");
      digraph2.put(new Integer(16), "R");
      digraph2.put(new Integer(17), "S");
      digraph2.put(new Integer(18), "T");
      digraph2.put(new Integer(19), "U");
      digraph2.put(new Integer(20), "V");

    }

    public int getDigraph1Index(String letter)
    {
      for (int i = 0; i < digraph1Array.length; i++)
      {
        if (digraph1Array[i].equals(letter))
        {
          return i + 1;
        }
      }

      return -1;
    }

    public int getDigraph2Index(String letter)
    {
      for (int i = 0; i < digraph2Array.length; i++)
      {
        if (digraph2Array[i].equals(letter))
        {
          return i;
        }
      }

      return -1;
    }

    public String getDigraph1(int longZone, double easting)
    {
      int a1 = longZone;
      double a2 = 8 * ((a1 - 1) % 3) + 1;

      double a3 = easting;
      double a4 = a2 + ((int) (a3 / 100000)) - 1;
      return (String) digraph1.get(new Integer((int) Math.floor(a4)));
    }

    public String getDigraph2(int longZone, double northing)
    {
      int a1 = longZone;
      double a2 = 1 + 5 * ((a1 - 1) % 2);
      double a3 = northing;
      double a4 = (a2 + ((int) (a3 / 100000)));
      a4 = (a2 + ((int) (a3 / 100000.0))) % 20;
      a4 = Math.floor(a4);
      if (a4 < 0)
      {
        a4 = a4 + 19;
      }
      return (String) digraph2.get(new Integer((int) Math.floor(a4)));

    }

  }

  private class LatZones
  {
    private char[] letters = { 'A', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
        'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Z' };

    private int[] degrees = { -90, -84, -72, -64, -56, -48, -40, -32, -24, -16,
        -8, 0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 84 };

    private char[] negLetters = { 'A', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
        'L', 'M' };

    private int[] negDegrees = { -90, -84, -72, -64, -56, -48, -40, -32, -24,
        -16, -8 };

    private char[] posLetters = { 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
        'X', 'Z' };

    private int[] posDegrees = { 0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 84 };

    private int arrayLength = 22;

    public LatZones()
    {
    }

    public int getLatZoneDegree(String letter)
    {
      char ltr = letter.charAt(0);
      for (int i = 0; i < arrayLength; i++)
      {
        if (letters[i] == ltr)
        {
          return degrees[i];
        }
      }
      return -100;
    }

    public String getLatZone(double latitude)
    {
      int latIndex = -2;
      int lat = (int) latitude;

      if (lat >= 0)
      {
        int len = posLetters.length;
        for (int i = 0; i < len; i++)
        {
          if (lat == posDegrees[i])
          {
            latIndex = i;
            break;
          }

          if (lat > posDegrees[i])
          {
            continue;
          }
          else
          {
            latIndex = i - 1;
            break;
          }
        }
      }
      else
      {
        int len = negLetters.length;
        for (int i = 0; i < len; i++)
        {
          if (lat == negDegrees[i])
          {
            latIndex = i;
            break;
          }

          if (lat < negDegrees[i])
          {
            latIndex = i - 1;
            break;
          }
          else
          {
            continue;
          }

        }

      }

      if (latIndex == -1)
      {
        latIndex = 0;
      }
      if (lat >= 0)
      {
        if (latIndex == -2)
        {
          latIndex = posLetters.length - 1;
        }
        return String.valueOf(posLetters[latIndex]);
      }
      else
      {
        if (latIndex == -2)
        {
          latIndex = negLetters.length - 1;
        }
        return String.valueOf(negLetters[latIndex]);

      }
    }

  }

}