package it.unito.geosummly;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class DiscoveryOperator {
	public static Logger logger = Logger.getLogger(SamplingOperator.class.toString());
	
	public DiscoveryOperator() {}
	
	public void execute(String in, String out, int comb, int rnum) throws IOException {
		
		//Read csv file and create the matrix without coordinate values
		ArrayList<String> features=new ArrayList<String>();
		ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
		FileReader reader =new FileReader(in);
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> list = parser.getRecords();
		parser.close();
		int index=1;
		if(list.get(0).get(1).contains("Latitude") || list.get(0).get(1).contains("Longitude"))
			index=3;
		
		for(int k=index;k<list.get(0).size();k++) {
			features.add(list.get(0).get(k));
		}
		
		for(int k=1;k<list.size();k++) {
			ArrayList<Double> rec=new ArrayList<Double>();
			for(int j=index;j<list.get(k).size(); j++)
				rec.add(Double.parseDouble(list.get(k).get(j)));
			matrix.add(rec);
		}
		
		//Get rnum random cells
		if(rnum>0) {
			ArrayList<ArrayList<Double>> matrixRnd = new ArrayList<ArrayList<Double>>();
			Random r=new Random();
			int rnd=0;
			for(int i=0;i<rnum;i++) {
				rnd=r.nextInt(matrix.size());
				matrixRnd.add(matrix.get(rnd));
			}
			matrix=new ArrayList<ArrayList<Double>>(matrixRnd); //put the random cells in the matrix
		}
		
		DiscoveryTools dt=new DiscoveryTools();
		
		/* ***********************5% PROCESS*********************** */
		//Get standard deviation values
		/*ArrayList<Double> meanDensities=new ArrayList<Double>();
		ArrayList<Double> stdSingles=new ArrayList<Double>();
		
		//get the 5%
		int perc= (int) Math.floor((matrix.size()*5)/100);
		
		//for each category
		for(int j=0;j<matrix.get(0).size();j++) {
			ArrayList<Double> rec=new ArrayList<Double>();
			//get all the elements of the category
			for(int i=0;i<matrix.size();i++) {
				rec.add(matrix.get(i).get(j));
			}
			Collections.sort(rec);
			//remove the last 'perc' outliers
			for(int i=matrix.size()-1;i>=(matrix.size()-perc);i--) {
				rec.remove(i);
			}
			//remove the first 'perc' outliers
			for(int i=0;i<perc;i++)
				rec.remove(0);
			//get the mean value of the remaining densities
			double mean=dt.getMean(rec);
			meanDensities.add(mean);
			//get the variance
			double variance=dt.getVariance(rec, mean);
			//get the std
			double std=dt.getStdDev(variance);
			stdSingles.add(std);
		}
		//Create the std matrix
		ArrayList<ArrayList<Double>> stdMatrix=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<matrix.size();i++)
			stdMatrix.add(stdSingles);*/
		
		ArrayList<Double> meanDensities=dt.getMeanArray(matrix);
		ArrayList<ArrayList<Double>> stdMatrix=dt.getStdMatrix(matrix);
		ArrayList<Double> stdSingles=new ArrayList<Double>(stdMatrix.get(0));
		double n=matrix.size();
		
		//The option combination have to be less or equal than the number of features
		if(comb > features.size())
			comb=features.size();
		
		//Deltad values of single features
		ArrayList<Double> deltadValues=new ArrayList<Double>();
		deltadValues.addAll(dt.getSingleDensities(meanDensities, stdSingles, n)); //add deltad values of singles
		
		//Deltad values of feature combinations
		for(int j=2;j<=comb;j++) {
			int[] combinations=new int[j]; //array which will contain the indices of the values to combine
			Arrays.fill(combinations, -1);
			deltadValues.addAll(dt.getCombinations(matrix, new ArrayList<Double>(), meanDensities, combinations, 0, 0, n));
			logger.log(Level.INFO, "All combinations of "+j+" values executed");
		}
		
		//Label of single features
		ArrayList<String >feat=dt.changeFeaturesLabel("density", "", features);
		ArrayList<String> featuresDeltad=new ArrayList<String>();
		featuresDeltad.addAll(dt.getFeaturesLabel("deltad", feat));
		
		//Label of feature combinations
		for(int j=2;j<=comb;j++) {
			int[] combinations=new int[j]; //array which will contain the indices of the features to combine
			Arrays.fill(combinations, -1);
			featuresDeltad.addAll(dt.getFeaturesForCombinations(new ArrayList<String>(), feat, combinations, 0, 0));
			logger.log(Level.INFO, "All combinations of "+j+" features executed");
		}
		
		//Write down the matrices to file
		DataPrinter dp=new DataPrinter();
		dp.printResultHorizontal(null, stdMatrix, dt.getFeaturesLabel("std", feat), out+ "/std-values.csv");
		dp.printResultVertical(deltadValues, featuresDeltad, out+ "/deltad-values.csv");
	}
}