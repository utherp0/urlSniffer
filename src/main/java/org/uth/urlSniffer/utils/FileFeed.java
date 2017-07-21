package org.uth.urlSniffer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Scanner;

/**
 * Wrapper around the URLSniffer that takes a file of target URLs and pushes them
 * through the sniffer, then generates a report as to findings.
 * @author Ian 'Uther' Lawson
 */
public class FileFeed 
{
  public static void main( String args[] )
  {
    if( args.length != 3 )
    {
      System.out.println( "Usage: java FileFeed inputFile outputReport comma_separated_list_of_target_server_names" );
      System.exit(0);
    }
    
    new FileFeed( args[0], args[1], args[2] );
  }
  
  public FileFeed( String inputFile, String outputReport, String serversToMatch )
  {
    // Read the file of target URLs
    try
    {
      File targetLocation = new File( inputFile );
      FileInputStream inputStream = new FileInputStream(targetLocation);

      Scanner scanner = new Scanner(inputStream);

      StringBuilder buffer = null;
      
      //first use a Scanner to get each line
      while( scanner.hasNextLine() )
      {
        String dataComponent = scanner.nextLine();
        
        if( buffer == null )
        {
          buffer = new StringBuilder(dataComponent);
        }
        else
        {
          buffer.append( "," + dataComponent );
        }
      }
      
      inputStream.close();
      
      // Run the analytic pass
      long start = System.currentTimeMillis();      
      URLSniffer sniffer = new URLSniffer( buffer.toString(), serversToMatch );
      long end = System.currentTimeMillis();
      
      long elapsed = end - start;
      String reportDate = DateFormatter.longDateFormat(start);
      
      // Grab the caches
      List<String> discoveredServers = sniffer.getDiscoveredServers();
      List<String> matches = sniffer.getMatches();
      
      // Generate the output report
      FileWriter writer = new FileWriter( outputReport, true);
      BufferedWriter bufferedWriter = new BufferedWriter(writer);
 
      bufferedWriter.write("Report created " + reportDate );
      bufferedWriter.newLine();
      bufferedWriter.write("Time to generate report " + elapsed + "ms." );
      bufferedWriter.newLine();
      bufferedWriter.write("Report sought: " + serversToMatch );
      bufferedWriter.newLine();
      bufferedWriter.newLine();
      bufferedWriter.write("Discovered Servers:" );
      bufferedWriter.newLine();
      
      for( String discovered : discoveredServers )
      {
        bufferedWriter.write( discovered );
        bufferedWriter.newLine();
      }
      
      bufferedWriter.newLine();
      bufferedWriter.write( "Matches:");
      bufferedWriter.newLine();
      
      for( String match : matches )
      {
        bufferedWriter.write( match );
        bufferedWriter.newLine();        
      }
      
      bufferedWriter.newLine();
      bufferedWriter.write( "Total Matches Discovered: " + matches.size());
 
      bufferedWriter.close();      
    }
    catch( Exception exc )
    {
      System.out.println( "FileFeed failed due to " + exc.toString());
      exc.printStackTrace();
    }
  }
}
