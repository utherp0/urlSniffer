package org.uth.urlSniffer.utils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * URLSniffer - takes a list of target URLs, establishes a connections, polls the
 * http response headers to see source Server, returns count of provided target server
 * @author Ian 'Uther' Lawson
 */
public class URLSniffer 
{
  public static void main( String[] args )
  {
    if( args.length != 2 )
    {
      System.out.println( "Usage java URLSniffer (comma seperated target URLs) (comma separated sub-search server target)");
      System.exit(0);
    }
    
    new URLSniffer( args[0], args[1] );
  }
  
  public URLSniffer( String urlWhiteList, String serverSubWhiteList )
  {
    URLSniffer.log( "Starting...");
    
    // Extract the list of target URLs
    String[] targetURLs = urlWhiteList.split(",");
    
    // Extract the substring match for target server determination
    String[] serverSubSearch = serverSubWhiteList.split( "," );
    
    // Loop through the target URLs
    for( String target : targetURLs )
    {      
      //URLSniffer.log( "Sniffing " + target );
      
      try
      {
        URL targetURL = new URL( target );
        
        HttpURLConnection connection = (HttpURLConnection)targetURL.openConnection();
        
        this.pollResponseHeader(connection, serverSubSearch);
      }
      catch( Exception exc )
      {
        exc.printStackTrace();
        URLSniffer.log( "Invalid URL format for target " + target );
      }
    }
    
    URLSniffer.log( "Complete...");
  }
  
  private void pollResponseHeader( HttpURLConnection target, String[] subSearch )
  {
    String server = target.getHeaderField("Server");
    URLSniffer.log( "FOUND " + server + " " + target.getURL().toString());
    
    // Simple substring search search
    for( String subSearchComponent : subSearch )
    {      
      if( server != null )
      {        
        if( server.indexOf(subSearchComponent) != -1 )
        {
          URLSniffer.log( "  --> MATCH " + subSearchComponent + " " + target.getURL().toString() );
        }
      }
    }
  }
  
  private static void log( String message )
  {
    System.out.println( "[URLSniffer] " + message );
  }
}
