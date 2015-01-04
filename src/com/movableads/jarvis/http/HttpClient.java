package com.movableads.jarvis.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.movableads.jarvis.Global;


public class HttpClient {
	
	public HttpClient() {

	}

	public JSONObject getHttpJson(String URL ) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpRequest = new HttpGet(URL);
		httpRequest.setHeader("Accept", "application/json");
		HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			InputStream instream = entity.getContent();
			Header contentEncoding = response.getFirstHeader("Content-Encoding");
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
			}

			String resultString = convertStreamToString(instream);
			instream.close();
			JSONObject jsonObjRecv = new JSONObject(resultString);

			return jsonObjRecv;
		}
		return null;
	}
	
	
	public JSONObject getHttpJson2(String URL ) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpRequest = new HttpGet(URL);
		httpRequest.setHeader("Accept", "application/json");
		httpRequest.setHeader("Appkey", Global.APPKEY);
		httpRequest.setHeader("Authtoken", Global.authToken);
		httpRequest.setHeader("Requesttoken", Global.reqToken);
		
		HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			InputStream instream = entity.getContent();
			Header contentEncoding = response.getFirstHeader("Content-Encoding");
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
			}

			String resultString = convertStreamToString(instream);
			instream.close();
			JSONObject jsonObjRecv = new JSONObject(resultString);

			return jsonObjRecv;
		}
		return null;
	}
	
	private String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}

		return sb.toString();
	}
	
	
	
	public JSONObject postData(String url, Vector<NameValuePair> postInfo) throws Exception {
		HttpPost request = new HttpPost(url);
		request.setEntity(makeEntity(postInfo));
		
    	DefaultHttpClient client = new DefaultHttpClient();
    	ResponseHandler<String> reshandler = new BasicResponseHandler();
    	String ret = client.execute(request, reshandler);
    	JSONObject jsonObjRecv = new JSONObject(ret);
		return jsonObjRecv;
	}
	
	
	public JSONObject postData2(String url, Vector<NameValuePair> postInfo) throws Exception {
		HttpPost request = new HttpPost(url);
		
		request.setHeader("Appkey", Global.APPKEY);
		request.setHeader("Authtoken", Global.authToken);
		request.setHeader("Requesttoken", Global.reqToken);
		
		if(postInfo != null)
			request.setEntity(makeEntity(postInfo));
		
    	DefaultHttpClient client = new DefaultHttpClient();
    	ResponseHandler<String> reshandler = new BasicResponseHandler();
    	String ret = client.execute(request, reshandler);
    	JSONObject jsonObjRecv = new JSONObject(ret);
		return jsonObjRecv;
	}
	
	private HttpEntity makeEntity(Vector<NameValuePair> nameValue) throws Exception {
    	HttpEntity result = null;
    	result = new UrlEncodedFormEntity(nameValue, HTTP.UTF_8);
    	return result;
    }
	
}
