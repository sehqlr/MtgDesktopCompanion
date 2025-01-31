package org.magic.api.pricers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.JaccardDistance;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;
import org.magic.tools.InstallCert;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

public class CardKingdomPricer extends AbstractPricesProvider {
	
	private static final String API_URI="https://api.cardkingdom.com/api/pricelist";
	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";
	private Document doc;
	private List<String> eds;

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	public void cachingPriceList()
	{
		try {
			FileTools.saveFile(new File(MTGConstants.DATA_DIR,"mtgkingdom.json"), URLTools.extractJson(API_URI).toString());
		} catch (IOException e) {
			logger.error("error getting cache priceguide",e);
		}
	}
	

	public CardKingdomPricer() {
		super();
		if(getBoolean(LOAD_CERTIFICATE))
		{
			try {
				InstallCert.installCert("cardkingdom.com");
				setProperty(LOAD_CERTIFICATE, "false");
			} catch (Exception e1) {
				logger.error(e1);
			}
		}

		eds = new ArrayList<>();
	}
	
	private void initEds()
	{
		try {
			doc = URLTools.extractHtml(getString("WEBSITE")+"/catalog/magic_the_gathering/by_az");
			Elements e = doc.select(".anchorList a[href]");
			for (Element ed : e)
			{
				eds.add(ed.html());
			}
		} catch (IOException e) {
			logger.error("Could not init list eds", e);
		}

	}

	private String findGoodEds(String set) {
		
		if(set.startsWith("Revised"))
			return "3rd Edition";
		
		if(set.contains("Sixth Edition"))
			return "6th Edition";
		
		double leven = 100;
		var name = "";
		EditDistance<Double> d = new JaccardDistance();
		for (String s : eds) 
		{
			double dist = d.apply(set.toLowerCase(), s.toLowerCase());
			if (dist < leven) {
				leven = dist;
				name = s;
			}
		}
		return name;
	}

	private String format(String s) {
		return s.replace("'s", "s").replace(",", "").replace(" ", "-").replace("'","").toLowerCase();
	}

	public List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {

		if(eds.isEmpty())
			initEds();
		
		
		List<MagicPrice> list = new ArrayList<>();
		var html = getString("URL");


		String url = html + format(findGoodEds(card.getCurrentSet().getSet())) + "/" + format(card.getName());
		Elements prices = null;
		Elements qualities = null;

		logger.info(getName() + " looking for prices " + url);
		try {
			doc = URLTools.extractHtml(url);
			qualities = doc.select(".cardTypeList li");
			prices = doc.select(".stylePrice");

		} catch (Exception e) {
			logger.info(getName() + " no item : " + e.getMessage());
			return list;
		}

		List<MagicPrice> lstPrices = new ArrayList<>();
		for (var i = 0; i < qualities.size(); i++) {
			var mp = new MagicPrice();

			String price = prices.get(i).html().replaceAll("\\$", "");
			mp.setMagicCard(card);
			mp.setValue(UITools.parseDouble(price));
			mp.setCurrency("USD");
			mp.setSeller(getName());
			mp.setSite(getName());
			mp.setUrl(url+"?partner=Mtgdesktopcompanion&utm_source=Mtgdesktopcompanion&utm_medium=affiliate&utm_campaign=condition");
			mp.setQuality(qualities.get(i).html());
			mp.setLanguage("English");

			if (!qualities.get(i).hasClass("disabled"))
				lstPrices.add(mp);
		}
		logger.info(getName() + " found " + lstPrices.size() + " item(s)");
		return lstPrices;
	}

	@Override
	public String getName() {
		return "Card Kingdom";
	}

	@Override
	public void initDefault() {
		setProperty("URL", "https://www.cardkingdom.com/mtg/");
		setProperty("WEBSITE", "https://www.cardkingdom.com/");
		
		setProperty(LOAD_CERTIFICATE, "true");
	}


	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}

	@Override
	public boolean isPartner() {
		return true;
	}
	
}
