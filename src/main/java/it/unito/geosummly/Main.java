package it.unito.geosummly;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

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
		
		/***************************************************************************************/
		/******************************CREATE THE BOUNDING BOX**********************************/
		/***************************************************************************************/
		double north=45.057; //north coordinate of the central cell
		double south=45.0561;
		double west=7.6600;
		double east=7.6613;
		int cells_number=20; //Number N of cells
		
		BoundingBox bbox=new BoundingBox(); //Bounding box
		BoundingBox cell=new BoundingBox(north, south, west, east); //Central cell
		ArrayList<BoundingBox> data=new ArrayList<BoundingBox>(); //Data structure
		
		//Create a N*N bounding box
		Grid box=new Grid();
		box.setCellsNumber(cells_number);
		box.setStructure(data);
		box.setCell(cell);
		box.setBbox(bbox);
		box.createCells();
		
		/***************************************************************************************/
		/****************************COLLECT ALL THE GEOPOINTS AND******************************/
		/****************************CREATE THE TRANSFORMATION MATRIX***************************/
		/***************************************************************************************/
		//Initialize a MongoDB instance
    	MongoClient mongoClient=new MongoClient("localhost");
    	DB db=mongoClient.getDB("VenueDB");
    	DBCollection coll=db.getCollection("ResultVenues");
    	
    	//Initialize a Gson instance and declare the document which will contain the JSON results for MongoDB 
    	Gson gson=new Gson();
		BasicDBObject doc;
		
		//Initialize the transformation matrix and its parameters
		ArrayList<ArrayList<Double>> matrix=new ArrayList<ArrayList<Double>>();
		TransformationMatrix t_matrix=new TransformationMatrix();
		t_matrix.setMatrix(matrix);
		ArrayList<Double> row_of_matrix; //row of the transformation matrix (one for each cell);
		
		//Download venues informations
		FoursquareSearchVenues fsv=new FoursquareSearchVenues();
		ArrayList<FoursquareDataObject> venueInfo;
		for(BoundingBox b: data){
		    logger.log(Level.INFO, "Fetching 4square metadata of the cell: " + b.toString());

			//Venues of a single cell
			venueInfo=fsv.searchVenues(b.getRow(), b.getColumn(), b.getNorth(), b.getSouth(), b.getWest(), b.getEast());
			
			for(FoursquareDataObject fdo: venueInfo){
				//Serialize with Gson
				String obj=gson.toJson(fdo);
				//Initialize the document which will contain the JSON result parsed for MongoDB and insert this document into MongoDB collection
				doc= (BasicDBObject) JSON.parse(obj);
				coll.insert(doc);
			}
			
			//Transformation matrix task
			int cat_num=fsv.getCategoriesNumber(venueInfo);//total number of categories of a single cell
			ArrayList<String> distinct_list=fsv.createCategoryList(venueInfo); //list of all the distinct categories
			ArrayList<Integer> occurrences_list=fsv.getCategoryOccurences(venueInfo, distinct_list);
			row_of_matrix=new ArrayList<Double>();
			for(Integer i: occurrences_list){
				row_of_matrix.add((double)(i/cat_num));
			}
			row_of_matrix.add(b.getCenterLat());
			row_of_matrix.add(b.getCenterLng());
			t_matrix.addRow(row_of_matrix);
		}
		
		
		// write down the transformation matrix to a file		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(bout);
        try {
            CSVPrinter csv = new CSVPrinter(osw, CSVFormat.DEFAULT);
		
            //write the header of the matrix
            csv.printRecord("headerColumn1,headerColumn2,...");
            // iterate per each row of the matrix
            //for(row : rows) {
                //csv.print("colum1");
                //csv.print("colum2");
                //...
                //csv.println();
            //}
            csv.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		
		OutputStream outputStream;
        try {
            outputStream = new FileOutputStream ("matrix.csv");
            bout.writeTo(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 

		
		//Print JSON files (just for debug)
    	/*DBCursor cursorDocJSON = coll.find();
    	while (cursorDocJSON.hasNext()) {
    		System.out.println(cursorDocJSON.next());
    	}*/
	}
}