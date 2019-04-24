package org.magic.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class URLToolsClient {

	private HttpClient httpclient;
	private HttpClientContext httpContext;
	private BasicCookieStore cookieStore;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private HttpResponse response;
	
	public HttpResponse getResponse() {
		return response;
	}
	
	public HttpClient getHttpclient() {
		return httpclient;
	}
	
	public HttpClientContext getHttpContext() {
		return httpContext;
	}
	
	
	public URLToolsClient() {
		httpclient = HttpClients.custom().setUserAgent(MTGConstants.USER_AGENT).setRedirectStrategy(new LaxRedirectStrategy()).build();
		httpContext = new HttpClientContext();
		cookieStore = new BasicCookieStore();
		httpContext.setCookieStore(cookieStore);
	}
	
	public List<NameValuePair> add(String k, String v)
	{
		return new ArrayList<>();
	}
	
	public Builder<String, String> build()
	{
		
		return new ImmutableMap.Builder<>();
	}
	
	public String doPost(String url, Map<String,String> entities, Map<String,String> headers) throws IOException
	{
		return doPost(url,new UrlEncodedFormEntity(entities.entrySet().stream().map(e-> new BasicNameValuePair(e.getKey(), e.getValue())).collect(Collectors.toList())),headers);
	}
	
	
	@Deprecated
	public String doPost(String url, List<NameValuePair> entities, Map<String,String> headers) throws IOException
	{
			return doPost(url,new UrlEncodedFormEntity(entities),headers);
		
	}
	
	
	public String doGet(String url, Map<String,String> headers) throws IOException
	{
		logger.debug("GET " + url);
		HttpGet getReq = new HttpGet(url);
		init(headers,getReq);
		response  = httpclient.execute(getReq,httpContext);
		return extractAndClose(response);
	}
	
	public HttpResponse execute(HttpRequestBase req) throws IOException
	{
		logger.trace("executing" + req);
		return httpclient.execute(req,httpContext);
	}
	
	
	public String extractAndClose(HttpResponse response) throws IOException
	{
		logger.trace("return " + response.getStatusLine().getStatusCode());
		String ret = EntityUtils.toString(response.getEntity());
		EntityUtils.consume(response.getEntity());
		return ret;
	}
	
	private void init(Map<String,String> headers,HttpRequestBase req)
	{
		if(headers!=null)
			headers.entrySet().forEach(e->req.addHeader(e.getKey(), e.getValue()));
	}
	
	
	public String doPost(String url, HttpEntity entities, Map<String,String> headers) throws IOException
	{
			logger.debug("POST " + url);
		
			HttpPost postReq = new HttpPost(url);
			try {
				if(entities!=null)
					postReq.setEntity(entities);
				
				init(headers,postReq);
				
				response  = httpclient.execute(postReq,httpContext);
				return extractAndClose(response);
			} catch (UnsupportedEncodingException e1) {
				throw new IOException(e1);
			}

	}
	
	public String doGet(URI url) throws IOException
	{
		return doGet(url.toString(),null);
	}
	
	
	public String doGet(String url) throws IOException
	{
		return doGet(url,null);
	}

	public String getCookieValue(String cookieName) {
		String value = null;
		for (Cookie cookie : cookieStore.getCookies()) {
			if (cookie.getName().equals(cookieName)) {
				value = cookie.getValue();
				break;
			}
		}
		return value;
		
	}

	public List<Cookie> getCookies() {
		return cookieStore.getCookies();
	}
}
