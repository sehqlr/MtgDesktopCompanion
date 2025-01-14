package org.magic.services.providers;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Packaging;
import org.magic.api.beans.Packaging.EXTRA;
import org.magic.api.beans.Packaging.TYPE;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.ImageTools;
import org.magic.tools.URLTools;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class PackagesProvider {

	private static final String PACKAGING_DIR_NAME = "packaging";
	private Document document;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	public enum LOGO { ORANGE,BLUE,YELLOW,WHITE,NEW}
	private List<MagicEdition> list;
	private static PackagesProvider inst;
	
	private PackagesProvider() {
		try {
			logger.debug("Loading booster pics");
			document = URLTools.extractXML(MTGConstants.MTG_BOOSTERS_URI);
			logger.debug("Loading booster pics done");
			list = new ArrayList<>();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public static PackagesProvider inst()
	{
		if(inst==null)
			inst=new PackagesProvider();
		
		return inst;
	}
	
	public String getURLFor(Packaging p)
	{
		String eval = null;
		try {
			var xPath = XPathFactory.newInstance().newXPath();
			eval = "//edition[@id='"+p.getEdition().getId()+"']/"+p.getType().name().toLowerCase()+"[@lang='"+p.getLang()+"']";
			var nodeList = (NodeList) xPath.compile(eval).evaluate(document, XPathConstants.NODESET);
			var item = nodeList.item(0);
			return item.getAttributes().getNamedItem("url").getNodeValue(); 
		} catch (Exception e) {
			logger.error("Error loading url for " + eval + p);
			return null;
		}
	}
	
	
	public List<Packaging> getItemsFor(String me)
	{
		return getItemsFor(new MagicEdition(me));
	}
	
	
	public void caching(boolean force, MagicEdition s)
	{
		getItemsFor(s).forEach(p->caching(force, p));
	}
	
	public BufferedImage caching(boolean force, Packaging p) {
		
		if(p.getUrl()==null)
			return null;
		
		var f = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), PACKAGING_DIR_NAME,p.getEdition().getId().replace("CON", "CON_"),p.getType().name()).toFile();
		var pkgFile = new File(f,p.toString()+".png");
		
		try {
			FileUtils.forceMkdir(f);
			if(force||!pkgFile.exists())
			{
				BufferedImage im = URLTools.extractImage(p.getUrl());
				ImageTools.saveImage(im, pkgFile, "PNG");
				logger.debug("[" + p.getEdition().getId() +"] SAVED for " + p.getType()+"-"+p);
				return im;
			}
		} catch (Exception e) {
			logger.error("[" + p.getEdition().getId() +"] ERROR for " + p.getType()+"-"+p +" :" +e);
		}
		return null;
		
	}
	
	public BufferedImage getLogo(LOGO logo)
	{
		var url = "";
		try {
			var xPath = XPathFactory.newInstance().newXPath();
			var expression = "//logo[contains(@version,'" + logo.name().toLowerCase() + "')]";
			logger.trace(expression);
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			var item = nodeList.item(0);
			url = item.getAttributes().getNamedItem("url").getNodeValue();
			return URLTools.extractImage(url);
		} catch (IOException e) {
			logger.error(logo + " could not load : " + url,e);
			return null;
		} catch (XPathExpressionException e) {
			logger.error(logo + " is not found :" + e);
			return null;
		} catch (Exception e) {
			logger.error(logo + " error loading " + url,e);
			return null;
		}
	}


	public BufferedImage get(Packaging p)
	{
		try {
			var b=Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), PACKAGING_DIR_NAME,p.getEdition().getId().replace("CON", "CON_"),p.getType().name(),p.toString()+".png").toFile();
			
			if(b.exists())
				return ImageTools.read(b);
			else
				return caching(false, p);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}
	

	public void clear() {
		var f = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), PACKAGING_DIR_NAME).toFile();
		try {
			FileUtils.cleanDirectory(f);
		} catch (IOException e) {
			logger.error("error removing data in "+f,e);
		}
		
	}
	

	
	public void caching(boolean force)
	{
		listEditions().forEach(s->caching(force,s));
	}
	
	public synchronized List<Packaging> getItemsFor(MagicEdition me)
	{
		List<Packaging> ret = new ArrayList<>();
		
		if(me==null)
			return ret;
		
		NodeList n = null ;
		NodeList nodeList = null;
		try {
			var xPath = XPathFactory.newInstance().newXPath();
			nodeList = (NodeList) xPath.compile("//edition[@id='" + me.getId().toUpperCase() + "']").evaluate(document, XPathConstants.NODESET);
			n = nodeList.item(0).getChildNodes();
			
		} catch (Exception e) {
			logger.trace("Error retrieving IDs "+ me.getId() + "->" + me + " : " + e);
		}
		
		if(n==null)
			return ret;
		
		
		for (var i = 0; i < n.getLength(); i++)
		{
			if(n.item(i).getNodeType()==1)
			{
				var p = new Packaging();
						  p.setType(TYPE.valueOf(n.item(i).getNodeName().toUpperCase()));
						 
						  try {
							  p.setLang(n.item(i).getAttributes().getNamedItem("lang").getNodeValue());
						  }
						  catch(Exception e)
						  {
							  logger.error("no lang found for " + p + n.item(i),e);
						  }
						  
						  
						  try {
							  p.setExtra(EXTRA.valueOf(n.item(i).getAttributes().getNamedItem("extra").getNodeValue().toUpperCase()));
						  } 
						  catch (Exception e) {
								//do nothing
						  }
						  
						  
						  p.setUrl(n.item(i).getAttributes().getNamedItem("url").getNodeValue());
						  p.setEdition(me);
						 try {
						  p.setNum(Integer.parseInt(n.item(i).getAttributes().getNamedItem("num").getNodeValue()));
						 }
						 catch(Exception e)
						 {
							 p.setNum(1);
						 }
				
				ret.add(p);
			}
		}
		
		
		return ret;
	}


	public List<MagicEdition> listEditions() {

		if (!list.isEmpty())
			return list;

		try {
			var xPath = XPathFactory.newInstance().newXPath();
			var expression = "//edition/@id";
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			for (var i = 0; i < nodeList.getLength(); i++)
			{
				list.add(getEnabledPlugin(MTGCardsProvider.class).getSetById(nodeList.item(i).getNodeValue()));
			}
			
			Collections.sort(list);
		} catch (Exception e) {
			logger.error("Error retrieving IDs ", e);
		}
		return list;
	}

	public List<Packaging> get(MagicEdition me,TYPE t, String lang)
	{
		return getItemsFor(me).stream().filter(e->e.getType()==t && e.getLang().equalsIgnoreCase(lang)).collect(Collectors.toList());
	}
	

	public List<Packaging> get(MagicEdition me,TYPE t)
	{
		return getItemsFor(me).stream().filter(e->e.getType()==t).collect(Collectors.toList());
	}

	
	

}
