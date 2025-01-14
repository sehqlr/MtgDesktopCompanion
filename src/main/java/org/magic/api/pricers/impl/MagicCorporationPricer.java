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
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

public class MagicCorporationPricer extends AbstractPricesProvider {

	private static final String BASE_URL="http://www.magiccorporation.com/";
	
	
	@Override
	public String getName() {
		return "MagicCorporation";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		
	String url =BASE_URL+"mc.php";
	List<MagicPrice> ret = new ArrayList<>();
	
	Document content =RequestBuilder.build().url(url).setClient(URLTools.newClient()).method(METHOD.GET)
						.addContent("rub","cartes")
						.addContent("op","search")
						.addContent("search","2")
						.addContent("word",card.getName()).toHtml();
		
		
		Elements trs = content.select("tr.hover");
		String link=BASE_URL+trs.first().select("td").get(3).select("span a").attr("href");
		
		
			content = RequestBuilder.build().url(link)
											.setClient(URLTools.newClient())
											.method(METHOD.GET)
											.addHeader(URLTools.ACCEPT_LANGUAGE, "fr-FR,fr;q=0.9,en;q=0.8")
											.addHeader("Upgrade-Insecure-Requests", "1")
											.addHeader(URLTools.ACCEPT_ENCODING, "gzip, deflate")
											.toHtml();
			trs = content.select("table[width=100%]:has(form) tr");
			
			logger.debug(getName() +" getting prices at " + link);
			
			if(trs!=null)
			{	
				for(Element tr : trs)
				{
					var mp = new MagicPrice();
						mp.setMagicCard(card);
						mp.setCountry("FR");
						mp.setCurrency("EUR");
						mp.setQuality(tr.select("td").get(1).text());
						mp.setValue(UITools.parseDouble(tr.select("td").get(3).text().replace("\u0080", "")));
						mp.setSeller(tr.select("td").first().text());
						mp.setSite(getName());
						mp.setLanguage("EN");
						
						if(tr.select("td").get(2).childNodeSize()>0)
						{
							if(tr.select("td").get(2).getElementsByTag("img").first().attr("src").contains("de.gif"))
								mp.setLanguage("DE");
							else if(tr.select("td").get(2).getElementsByTag("img").first().attr("src").contains("vf.gif"))
								mp.setLanguage("FR");
							else
								mp.setLanguage("EN");
						}
						mp.setFoil(tr.select("td").get(2).childNodeSize()>1);
						mp.setUrl(tr.select("td").first().getElementsByTag("a").attr("href"));
						ret.add(mp);
				}
			}
			else
			{
				logger.debug(getName() +" found nothing");
			}
		

		return ret;
	}

}
