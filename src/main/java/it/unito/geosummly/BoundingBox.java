package it.unito.geosummly;

import java.util.logging.Logger;

/**
 * @author Giacomo Falcone
 *
 * This class represents the concept of bounding box
 */

public class BoundingBox {
	private double north;
	private double south;
	private double west;
	private double east;
	private double centerLat; //Latitude of central point
	private double centerLng; //Longitude of central point
	private double area; //area of the bbox
	private int row; //row of the cell (position)
	private int column; //column of the cell (position)
	
	public static Logger logger = Logger.getLogger(BoundingBox.class.toString());

	
	public BoundingBox(){}
	
	public BoundingBox(double n, double s, double w, double e){
		this.north=n;
		this.south=s;
		this.west=w;
		this.east=e;
		this.centerLat=(n+s)/2;
		this.centerLng=(e+w)/2;
		this.area=(getDistance(s, w, n, w) * getDistance(s, w, s, e));
	}

	public void setNorth(double north){
		this.north=north;
	}

	public double getNorth(){
		return north;
	}

	public void setSouth(double south){
		this.south=south;
	}

	public double getSouth(){
		return south;
	}

	public void setWest(double west){
		this.west=west;
	}

	public double getWest(){
		return west;
	}

	public void setEast(double east){
		this.east=east;
	}

	public double getEast(){
		return east;
	}
	
	public void setCenterLat(double centerLat){
		this.centerLat=centerLat;
	}

	public double getCenterLat(){
		return centerLat;
	}
	
	public void setCenterLng(double centerLng){
		this.centerLng=centerLng;
	}

	public double getCenterLng(){
		return centerLng;
	}
	
	
	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String toString(){
		return "Row: "+row+" Column:"+column+" N:"+north+" S:"+south+" W:"+west+" E:"+east+" C_Lat:"+centerLat+" C_Lng:"+centerLng+" Area:"+area;
	}
	
	//Haversine formula implementation. It returns the distance between two points given latitude and longitude values 
	public double getDistance(double lat1, double lng1, double lat2, double lng2){
		double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    double meterConversion = 1609.00;

	    return dist*meterConversion;
	}
}
