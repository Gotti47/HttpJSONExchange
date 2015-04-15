package com.gottibujiku.httpjsonexchange.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.attribute.AclEntry.Builder;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * This class manages the transfer of text based data over the network
 * by using HTTP protocol and JSON format.
 * 
 * Exposes methods to handle POST and GET requests.
 * 
 * @author Newton Bujiku
 * @since April,2015
 * 
 */
public class HttpJSONExchange {

	private static final char PARAM_SEPARATOR ='&';//used to separate the parameters in the query string
	private static final char QUERY_SEPARATOR ='?';//used to separate the domain and the query string
	private static final String UTF_CHARSET = "UTF-8";

	public HttpJSONExchange(){

	}


	public JSONObject sendGet(String fullDomainName, HashMap<String, String> queryParams, HashMap<String, String> headers){
		JSONObject jsonResponse = null;
		URL url = null;//a url object to hold a complete URL

		StringBuilder stringBuilder = new StringBuilder();//avoid strings,they are immutable

		//Loop around the map to form a complete query string
		//Do encoding to escape special characters
		Set<String> keys = queryParams.keySet();//holds all keys in the map
		String value = null;
		for(String key : keys){
			value = queryParams.get(key);//retrieve parameter value

			try {
				//encode the  key and value from the map to get name&value pairs and append them to the queryBuilder
				stringBuilder.append(
						URLEncoder.encode(key,UTF_CHARSET) + PARAM_SEPARATOR +
						URLEncoder.encode(value,UTF_CHARSET)
						);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();

			}

		}
		
		try {
			url = new URL(fullDomainName + QUERY_SEPARATOR + stringBuilder.toString());//a complete URL 
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if(headers != null){//check if there were any additional headers
				keys = headers.keySet();//get headers' keys
				//set the headers as request properties
				for(String key : keys){
					connection.setRequestProperty(key, headers.get(key));
				}
			}
			connection.setRequestProperty("Accept-Charset", UTF_CHARSET);//accept the given encoding
			//a call to connection.connect() is superfluous since connect will be called
			//implicitly when the stream is opened
			connection.setRequestMethod("GET");
			
			String line = null;
			stringBuilder = new StringBuilder();
			try(BufferedReader reader =new BufferedReader(new InputStreamReader(connection.getInputStream()))){//open with resources
				//open a stream to read the response) if all was OK
				if( connection.getResponseCode() == HttpURLConnection.HTTP_OK){
					while((line = reader.readLine()) != null){
						//if  the response from the server is not null
						stringBuilder.append(line);
					}
				}
				
			}
			connection.disconnect();
			jsonResponse = new JSONObject(stringBuilder);//change the response into a json object		
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;//return if the URL is malformed
		} catch (IOException e) {
			e.printStackTrace();
			return null;//if failed to open a connection
		}
			
		return jsonResponse;
		
	}






}