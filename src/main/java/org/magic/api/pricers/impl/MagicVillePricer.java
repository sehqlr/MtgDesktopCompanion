package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class MagicVillePricer extends AbstractPricesProvider {
	
	private static final String MAX = "MAX";
	private static final String WEBSITE = "WEBSITE";
	private URLToolsClient httpclient;
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	public MagicVillePricer() {
		super();
		httpclient = URLTools.newClient();

	}

	public List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		List<MagicPrice> list = new ArrayList<>();
			
		String res = httpclient.doPost(getString(WEBSITE)+"/fr/resultats.php?zbob=1", httpclient.buildMap().put("recherche_titre", card.getName()).build(), null);
		if(res.length()>100)
		{
			logger.error("too much result");
			return list;
		}
		
		var key = "ref=";
		var code = res.substring(res.indexOf(key), res.indexOf("\";"));
		String url = getString(WEBSITE)+"/fr/register/show_card_sale?"+code;
		
		logger.info(getName() + " looking for prices " + url);

		
		Document doc =URLTools.extractHtml(url);
		
		Element table = null;
		try {
			table = doc.select("table[width=98%]").get(2); // select the first table.
		} catch (IndexOutOfBoundsException e) {
			logger.info(getName() + " no sellers");
			return list;
		}

		Elements rows = table.select(MTGConstants.HTML_TAG_TR);

		for (var i = 3; i < rows.size(); i = i + 2) {
			var ligne = rows.get(i);
			var cols = ligne.getElementsByTag(MTGConstants.HTML_TAG_TD);
			var mp = new MagicPrice();

			String price = cols.get(4).text();
			price = price.substring(0, price.length() - 1);
			mp.setValue(Double.parseDouble(price));
			mp.setMagicCard(card);
			mp.setCurrency("EUR");
			mp.setSeller(cols.get(0).text());
			mp.setSellerUrl(getString(WEBSITE)+"/fr/register/cards_to_sell?user="+mp.getSeller());
			mp.setSite(getName());
			mp.setUrl(url);
			mp.setQuality(cols.get(2).text());
			mp.setLanguage(cols.get(1).getElementsByTag("span").text());
			mp.setCountry("France");
			mp.setFoil(mp.getLanguage().toLowerCase().contains("foil"));

			list.add(mp);

		}

		logger.info(getName() + " found " + list.size() + " item(s) return " + getString(MAX) + " items");

		if (list.size() > getInt(MAX) && getInt(MAX) > -1)
			return list.subList(0, getInt(MAX));

		return list;
	}

	@Override
	public String getName() {
		return "Magic-Ville";
	}


	@Override
	public void initDefault() {
		setProperty(MAX, "5");
		setProperty(WEBSITE, "https://www.magic-ville.com/");
		

	}

	@Override
	public String getVersion() {
		return "2.0";
	}


}
