// Margaret Lanphere
// Nov 7 2023
// CSS 534 
// Program 5 Map Reduce 

import java.io.IOException;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class MapReduceKDTree {
	private static int numberOfCores = 4;
	
	private static Tuple2D parseLineToTuple(String line) {
        // remove parentheses and split by comma
        String[] parts = line.split(":");
        if(parts.length > 1){
        	parts = parts[1].replaceAll("[()]", "").split(",\\s*");
        	if (parts.length == 2) {
            	try {
                	// parse the x and y coordinates from the string parts
                	int x = Integer.parseInt(parts[0].trim());
                	int y = Integer.parseInt(parts[1].trim());
                	// return a new Tuple2D object with these coordinates
                	return new Tuple2D(x, y);
            	} catch (NumberFormatException e) {
                // handle any number format exceptions during parsing
                	System.err.println("Invalid number format in line: " + line);
            	}
        	}
        	// return null if the line cannot be parsed into a Tuple2D
        	return null; 
        }
        else{
        	return null;
        }
    }

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        JobConf conf;
        private Text word = new Text();
        //private final static IntWritable one = new IntWritable(1);
        
        public void configure( JobConf job ) {
            this.conf = job; 
        }
    	
    	
    	// reads in points and splits to different parts of the cluster based on X coord
        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            
            // get command line arguments 
            int clusterSize = Integer.parseInt( conf.get("clusterSize"));

			// convert line to Tuple2D point
            String line = value.toString();
	        Tuple2D point = parseLineToTuple(line);
	        int thisX = point.getX();
	        
	        // assign key based on total cores available 
		    int cluster = thisX % (clusterSize * numberOfCores); 
		    output.collect(new Text(Integer.toString(cluster)), new Text(point.toString()));   
	    }
    }
        
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> { 
	    
	    JobConf conf;
	    public void configure( JobConf job ) {
            this.conf = job; 
        }
	    
	    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException { 
	        
	        List<Tuple2D> points = new ArrayList<>();
	        
	        // collect all the points assigned to this node as Tuple2D objects 
	        while (values.hasNext()) {
                String pointInput = values.next().toString();
				String[] parts = pointInput.replaceAll("[()]", "").split(",\\s*");
            	Tuple2D next = new Tuple2D(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            	points.add(next);
            }
                        
            // transform ArrayList into array for input to the KDTree build function            
            Tuple2D[] pointsArray = new Tuple2D[points.size()];
            points.toArray(pointsArray);
	    	
	    	// build the KD-Tree using the list of points 
        	KDNode root = KDTree.buildKDTree(pointsArray, 0);
        	
        	// get number of searches to be performed
        	int numSearches = Integer.parseInt(conf.get("numSearches"));
        	
        	// loop through search parameter pairs to collect found points
        	for(int i = 0; i < numSearches; i++){
        		// parsing command line arguments for x and y search range
            	String xrange = conf.get("search" + i + "x"); 
            	String yrange = conf.get("search" + i + "y");
            	
            	String[] xRange = xrange.split(":");
            	String[] yRange = yrange.split(":");
            
            	// make Range object to query the KD Tree
	        	Range queryRange ;
            	queryRange = new Range(new Tuple2D(Integer.parseInt(xRange[0]), 
            	Integer.parseInt(yRange[0])), new Tuple2D(Integer.parseInt(xRange[1]), 
            	Integer.parseInt(yRange[1])));
	       
	       		// perform query and capture results for this node as a list
            	Tuple2D[] results = KDTree.rangeQuery(queryRange, root, 0).toArray(new Tuple2D[0]);

            	// collect output as points 
            	for (Tuple2D point : results) {
                	output.collect(new Text("search" + i), new Text("(" + point.getX() + "," + point.getY() + ")"));  
            	}      
        	
        	
        	}
        	
	    }
	}
	 
	 
    public static void main(String[] args) throws Exception { // input format:
		
		// job configuration statements 
		JobConf conf = new JobConf(MapReduceKDTree.class); // AAAAA is this program’s file name
		conf.setJobName("MapReduceKDTree"); 
		conf.setOutputKeyClass(Text.class);
	
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(Map.class); 
		conf.setCombinerClass(Reduce.class); 
		conf.setReducerClass(Reduce.class);
		
		conf.setInputFormat(TextInputFormat.class); 
		conf.setOutputFormat(TextOutputFormat.class);
		
		// command line arguments 
	    // input output numPoints clusterSize xRange yRange xRange yRange...
		
		FileInputFormat.setInputPaths(conf, new Path(args[0])); // input directory name 
		FileOutputFormat.setOutputPath(conf, new Path(args[1])); // output directory name
    	
 		conf.set("numPoints", String.valueOf(args[2]));
 		conf.set("clusterSize", String.valueOf(args[3]));
 		//conf.set("xRanges", String.valueOf(args[4]));
 		//conf.set("yRanges", String.valueOf(args[5]));   
 		
 		conf.set( "argc", String.valueOf( args.length - 4 ) ); // argc maintains #keywords
 		int numSearches = (args.length - 4)/2; 
 		conf.set("numSearches", String.valueOf(numSearches)); 
 		int offset = 0;
 	   	for ( int i = 0; i < numSearches; i++){
			conf.set( "search" + i + "x", args[i + 4 + offset] ); // keyword1, keyword2, ...
			conf.set( "search" + i + "y", args[i + 5 + offset] );
			offset++;
    	}
 	   	
 	   	// Time the execution
    	long startTime = System.currentTimeMillis();
 	   	JobClient.runJob(conf);
    	long endTime = System.currentTimeMillis();
    	
    	long elapsedTime = endTime - startTime;
    	
    	// Print execution time 
    	System.out.println("Elapsed time = " + elapsedTime);
    	
    }
}
