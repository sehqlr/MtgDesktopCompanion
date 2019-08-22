package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.URLTools;
import org.magic.tools.XMLTools;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TCGPlayerPricer extends AbstractMagicPricesProvider {

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}


	@Override
	public List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {
		List<MagicPrice> list = new ArrayList<>();
		String url = getString("URL");
		url = url.replace("%API_KEY%", getString("API_KEY"));

		String set = "";

		if (me == null)
			set = URLTools.encode(card.getCurrentSet().getSet());
		else
			set = URLTools.encode(me.getSet());

		if (set.contains("Edition"))
			set = set.replace("Edition", "");

		String name = card.getName();
		name = name.replaceAll(" \\(.*$", "");
		name = name.replace("'", "%27");
		name = name.replace(" ", "+");

		setProperty("KEYWORD", "s=" + set + "p=" + name);

		String link = url.replace("%SET%", set);
		link = link.replace("%CARTE%", name);

		logger.info(getName() + " looking " + " for " + link);

		DocumentBuilderFactory dbFactory = XMLTools.createSecureXMLFactory();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			throw new IOException(e1);
		}

		Document doc = null;

		try {
			doc = dBuilder.parse(URLTools.openConnection(link).getInputStream());
			logger.trace(doc);

			doc.getDocumentElement().normalize();

			NodeList nodes = doc.getElementsByTagName("product");

			MagicPrice mp = new MagicPrice();
			mp.setCurrency("USD");
			mp.setSite(getName());
			mp.setUrl(nodes.item(0).getChildNodes().item(11).getTextContent());
			mp.setSeller(getName());
			mp.setValue(Double.parseDouble(nodes.item(0).getChildNodes().item(7).getTextContent()));

			list.add(mp);
			logger.info(getName() + " found " + list.size() + " item(s)");
			if (list.size() > getInt("MAX") && getInt("MAX") > -1)
				return list.subList(0, getInt("MAX"));

		} catch (Exception e) {
			logger.error(e);
			return list;
		}

		return list;

	}

	@Override
	public String getName() {
		return "TCGPlayer";
	}


	@Override
	public void initDefault() {
		setProperty("MAX", "-1");
		setProperty("API_KEY", "MGCASSTNT");
		setProperty("URL", "http://partner.tcgplayer.com/x3/phl.asmx/p?v=3&pk=%API_KEY%&s=%SET%&p=%CARTE%");
		setProperty("WEBSITE", "http://www.tcgplayer.com/");
		
		setProperty("KEYWORD", "");

	}


}
