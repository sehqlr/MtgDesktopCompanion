package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;


public class UndergroundSeaPricer extends AbstractPricesProvider {

	private static final String BASE_URL="http://www.usmtgproxy.com/";
	private String priceToken="price = ";
	private String editionToken="cardVersion =";

	private MagicPrice mp = null;
	
	@Override
	public String getName() {
		return "UnderGroundSea";
	}

	
	private String getUrl()
	{
		var month = String.format("%02d",Calendar.getInstance().get(Calendar.MONTH)+1);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String url = BASE_URL + "wp-content/uploads/"+year+"/"+month+"/proxycardslist.html";

		if(!URLTools.isCorrectConnection(url))
		{
			month=String.format("%02d",Calendar.getInstance().get(Calendar.MONTH));
			url = BASE_URL + "wp-content/uploads/"+year+"/"+month+"/proxycardslist.html";
		}
		
		return url;
	}
	
	
	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		
		Document d = URLTools.extractHtml(getUrl());
		AstNode root = new Parser().parse(d.select("script").get(1).html(), "", 1);
		List<MagicPrice> ret = new ArrayList<>();
		
		String url = getUrl();
		
		root.visit(visitedNode -> {
			if(visitedNode.getType()==Token.NEW)
			{
				mp = new MagicPrice();
				mp.setMagicCard(card);
				mp.setSite(getName());
				mp.setUrl(url);
				mp.setQuality(EnumCondition.PROXY.name());
				mp.setCurrency(Currency.getInstance("USD"));
				mp.setLanguage("EN");
				mp.setCountry("China");
			}
			
			if(visitedNode.getType()==Token.EXPR_RESULT)
			{
				String value = visitedNode.toSource();
				
				if(value.startsWith("cardslist") || value.startsWith("createTable") || value.startsWith("window.onload") || value.startsWith("confirmBtn") || value.startsWith("infos"))
					return false;
				
				String content = visitedNode.toSource().substring(visitedNode.toSource().indexOf('.')+1).trim();
			
				if(content.toLowerCase().contains(card.getName().toLowerCase()) && mp !=null)
				{
					ret.add(mp);
					mp.setFoil(content.contains("(foil)"));
				}
				
				if(content.startsWith(priceToken) && mp !=null)
				{
					content = content.substring(content.indexOf(priceToken)+priceToken.length()).replace(";", "").trim();
					mp.setValue(UITools.parseDouble(content));
				}
				
				if(content.startsWith(editionToken) && mp !=null)
				{
					content = content.substring(content.indexOf(editionToken)+editionToken.length()).replace(";", "").replace("\"", "").trim();
					mp.setSeller(content);
				}
				
				if(content.startsWith("cardID") && mp !=null)
				{
					content = content.substring(content.indexOf(editionToken)+editionToken.length()).replace(";", "").replace("\"", "").trim();
					mp.setShopItem(content);
				}
				
			}
			
			return true;
		});
		return ret;
	}

}
