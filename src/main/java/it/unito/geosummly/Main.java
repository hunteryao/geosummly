package it.unito.geosummly;


import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import fi.foyt.foursquare.api.FoursquareApiException;


public class Main {
    public static Logger logger = Logger.getLogger(Main.class.toString());
    
	public static void main(String[] args) throws FoursquareApiException, UnknownHostException{
		
		/******************************CREATE THE BOUNDING BOX**********************************/
		double north=45.08200587145192; //north coordinate of the bounding box
		double south=45.05218065994234;
		double west=7.661247253417969;
		double east=7.70416259765625;
		int cells_number=20; //Number N of cells
		
		BoundingBox bbox=new BoundingBox(north, south, west, east); //Initialize the bounding box
		ArrayList<BoundingBox> data=new ArrayList<BoundingBox>(); //Data structure
		
		//Create a N*N grid based on the bounding box
		Grid grid=new Grid();
		grid.setCellsNumber(cells_number);
		grid.setBbox(bbox);
		grid.setStructure(data);
		grid.createCells();
		
		/*******************************COLLECT ALL THE GEOPOINTS********************************/
		//Initialize a MongoDB instance
    	MongoClient mongoClient=new MongoClient("localhost");
    	DB db=mongoClient.getDB("VenueDB");
    	DBCollection coll=db.getCollection("ResultVenues");
    	
    	//Initialize a Gson instance and declare the document which will contain the JSON results for MongoDB 
    	Gson gson=new Gson();
		BasicDBObject doc;
		
		//Get the tools class and its support variables
		TransformationTools tools=new TransformationTools();
		ArrayList<ArrayList<Double>> supportMatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> bboxArea=new ArrayList<Double>();
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> cellVenue;
		InformationType infoType=InformationType.CELL;
		
		//Download venues informations
		for(BoundingBox b: data){
		    logger.log(Level.INFO, "Fetching 4square metadata of the cell: " + b.toString());
			cellVenue=fsv.searchVenues(b.getRow(), b.getColumn(), b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			//Copy to cache
			for(FoursquareDataObject fdo: cellVenue){
				String obj=gson.toJson(fdo); //Serialize with Gson
				doc=(BasicDBObject) JSON.parse(obj); //initialize the document with the JSON result parsed for MongoDB
				coll.insert(doc); //insert the document into MongoDB collection
			}
			//Generate a matrix with geopoints informations
			supportMatrix=tools.getInformations(infoType, b.getCenterLat(), b.getCenterLng(), supportMatrix, cellVenue);
			bboxArea.add(b.getArea());
		}
		supportMatrix=tools.fixRowsLength(tools.getTotal(), supportMatrix); //update rows length for consistency
		
		/****************CREATE THE TRANSFORMATION MATRIX AND SERIALIZE IT TO FILE******************/
		//Build the transformation matrix
		TransformationMatrix tm=new TransformationMatrix();
		ArrayList<ArrayList<Double>> frequencyMatrix=tools.sortMatrix(supportMatrix, tools.getMap());
		tm.setFrequencyMatrix(frequencyMatrix);
		if(infoType.equals(InformationType.CELL)) {
			ArrayList<ArrayList<Double>> densityMatrix=tools.buildDensityMatrix(frequencyMatrix, bboxArea);
			tm.setDensityMatrix(densityMatrix);
			ArrayList<ArrayList<Double>> normalizedMatrix=tools.buildNormalizedMatrix(CoordinatesNormalizationType.NORM, densityMatrix);
			tm.setNormalizedMatrix(normalizedMatrix);
		}
		tm.setHeader(tools.sortFeatures(tools.getMap()));
		
		//write down the transformation matrix to file
		DataPrinter dp=new DataPrinter();
		dp.printResult(tm.getFrequencyMatrix(), tools.getFeaturesLabel("f", tm.getHeader()), "output/frequency-transformation-matrix.csv");
		if(infoType.equals(InformationType.CELL)) {
			dp.printResult(tm.getDensityMatrix(), tools.getFeaturesLabel("density", tm.getHeader()), "output/density-transformation-matrix.csv");
			dp.printResult(tm.getNormalizedMatrix(), tools.getFeaturesLabel("normalized_density", tm.getHeader()), "output/normalized-transformation-matrix.csv");
		}
	}
}