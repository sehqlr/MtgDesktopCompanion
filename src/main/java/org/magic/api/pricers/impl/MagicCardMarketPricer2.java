package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Article.ARTICLES_ATT;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Product.PRODUCT_ATTS;
import org.api.mkm.services.ArticleService;
import org.api.mkm.services.CartServices;
import org.api.mkm.services.ProductServices;
import org.api.mkm.tools.MkmAPIConfig;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.InstallCert;

public class MagicCardMarketPricer2 extends AbstractPricesProvider {

	private static final String IS_EXACT = "IS_EXACT";
	private static final String FALSE = "false";
	private static final String LANGUAGE_ID = "LANGUAGE_ID";
	private static final String FILTER_COUNTRY = "FILTER_COUNTRY";
	private static final String MIN_CONDITION = "MIN_CONDITION";
	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";
	
	private List<MagicPrice> lists;
	private boolean initied=false;
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	public MagicCardMarketPricer2() {
		super();
		
		if(getBoolean(LOAD_CERTIFICATE))
		{
			try {
				InstallCert.installCert("cardmarket.com");
				setProperty(LOAD_CERTIFICATE, FALSE);
			} catch (Exception e1) {
				logger.error(e1);
			}
		}
	}
	
	private void init()
	{
		
		
		try {
			MkmAPIConfig.getInstance().init(getString("APP_ACCESS_TOKEN_SECRET"), getString("APP_ACCESS_TOKEN"),getString("APP_SECRET"), getString("APP_TOKEN"));
			initied=true;
		} catch (MkmException e) {
			logger.error(e);
		}
	}
	

	public static Product getProductFromCard(MagicCard mc, List<Product> list) {
		
		if(list.size()==1)
			return list.get(0);
		
		String edName = mc.getCurrentSet().getSet();
		Product resultat = null;
	
		if (mc.getCurrentSet().getMkmName() != null)
			edName = mc.getCurrentSet().getMkmName();
	
		
		if(mc.isExtraCard())
			edName +=": Extras";
	
	
		
		Integer mkmId=0;
		
		if(mc.getMkmId()!=null)
			mkmId=mc.getMkmId();
		
		for (Product p : list) 
		{
			
			if (p.getCategoryName().equalsIgnoreCase("Magic Single") && (p.getExpansionName().equalsIgnoreCase(edName) || (p.getIdProduct()==mkmId && !mc.isExtraCard()))) {
				resultat = p;
				break;
			}
		}
		return resultat;
	}
	
	
	@Override
	public List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		if(!initied)
			init();
		
		try {
			lists = new ArrayList<>();

			logger.info(getName() + " looking for " + card + " " + card.getCurrentSet());

			if (card.getRarity() != null && !getBoolean("COMMONCHECK") && card.getRarity()==MTGRarity.COMMON) {
				var mp = new MagicPrice();
					mp.setMagicCard(card);
					mp.setCurrency("EUR");
					mp.setValue(0.01);
					mp.setSite(getName());
					mp.setSeller("Not checked common");
					lists.add(mp);
					return lists;
				}

			var pService = new ProductServices();
			EnumMap<PRODUCT_ATTS, String> atts = new EnumMap<>(PRODUCT_ATTS.class);
			atts.put(PRODUCT_ATTS.idGame, "1");
			
			
			if(!getString(IS_EXACT).isEmpty())
				atts.put(PRODUCT_ATTS.exact, getString(IS_EXACT));

			if (!getString(LANGUAGE_ID).equals(""))
				atts.put(PRODUCT_ATTS.idLanguage, getString(LANGUAGE_ID));

			if (!getBoolean("USER_ARTICLE")) {
				var p = getProductFromCard(card, pService.findProduct(card.getName(), atts));

				if (p != null) {
					p = pService.getProductById(p.getIdProduct());
					var mp = new MagicPrice();
					mp.setSeller(String.valueOf(p.getExpansionName()));
					mp.setValue(p.getPriceGuide().getLOW());
					mp.setQuality("");
					mp.setUrl(MkmConstants.MKM_SITE_URL  + p.getWebsite());
					mp.setSellerUrl(MkmConstants.MKM_SITE_URL+"/fr/Magic/Users/"+mp.getSeller()+"/Offers/Singles");
					mp.setSite(getName());
					mp.setFoil(false);
					mp.setCurrency("EUR");
					mp.setLanguage(String.valueOf(p.getLocalization()));
					lists.add(mp);
				}

			} 
			else {
				List<Product> list = pService.findProduct(card.getName(), atts);
				var resultat = getProductFromCard(card, list);
				if (resultat == null) {
					logger.info(getName() + " found no product for " + card);
					return lists;
				}

				var aServ = new ArticleService();
				EnumMap<ARTICLES_ATT, String> aatts = new EnumMap<>(ARTICLES_ATT.class);
				aatts.put(ARTICLES_ATT.start, "0");
				aatts.put(ARTICLES_ATT.maxResults, getString("MAX"));

				if (!getString(LANGUAGE_ID).equals(""))
					aatts.put(ARTICLES_ATT.idLanguage, getString(LANGUAGE_ID));

				if (!getString(MIN_CONDITION).equals(""))
					aatts.put(ARTICLES_ATT.minCondition, getString(MIN_CONDITION));

				List<Article> articles = aServ.find(resultat, aatts);
			
				logger.debug(getName() + " found " + articles.size() + " items");

				for (Article a : articles) 
				{
					var mp = new MagicPrice();
							mp.setSeller(String.valueOf(a.getSeller()));
							mp.setSellerUrl(MkmConstants.MKM_SITE_URL+"/fr/Magic/Users/"+mp.getSeller()+"/Offers/Singles");
							
							mp.setCountry(String.valueOf(a.getSeller().getAddress().getCountry()));
							mp.setValue(a.getPrice());
							mp.setQuality(a.getCondition());
							mp.setUrl(MkmConstants.MKM_SITE_URL + resultat.getWebsite());
							mp.setSite(getName());
							mp.setFoil(a.isFoil());
							mp.setCurrency("EUR");
							mp.setLanguage(a.getLanguage().toString());
							mp.setShopItem(a);
					
					if(StringUtils.isEmpty(getString(FILTER_COUNTRY)) || ArrayUtils.contains(getArray(FILTER_COUNTRY),mp.getCountry().toUpperCase()))
						lists.add(mp);
						
				}
			}

		} catch (Exception e) {
			logger.error("Error retrieving prices for " + card, e);
			logger.error(e);

		}

		return lists;
	}

	public String getName() {
		return "MagicCardMarket";
	}

	@Override
	public void alertDetected(final List<MagicPrice> p) {
		if(!initied)
			init();
	
		ThreadManager.getInstance().executeThread(() -> {
			if (!p.isEmpty() && getBoolean("AUTOMATIC_ADD_CARD_ALERT")) {
				var cart = new CartServices();
				try {
					List<Article> list = new ArrayList<>();

					for (MagicPrice mp : p) {
						Article a = (Article) mp.getShopItem();
						a.setCount(1);
						list.add(a);
					}
					boolean res = cart.addArticles(list);
					logger.info("add " + list + " to card :" + res);
				} catch (Exception e) {
					logger.error("Could not add " + p + " to cart", e);
				}
			}
		}, "addCart");

	}

	@Override
	public void initDefault() {
		setProperty("APP_TOKEN", "");
		setProperty("APP_SECRET", "");
		setProperty("APP_ACCESS_TOKEN", "");
		setProperty("APP_ACCESS_TOKEN_SECRET", "");
		setProperty(LANGUAGE_ID, "1");
		setProperty(IS_EXACT, "");
		setProperty(MIN_CONDITION, "");
		setProperty("COMMONCHECK", FALSE);
		setProperty("MAX", "10");
		setProperty("USER_ARTICLE", FALSE);
		setProperty("AUTOMATIC_ADD_CARD_ALERT", FALSE);
		setProperty(FILTER_COUNTRY, "EN,"+Locale.getDefault().getCountry());
		setProperty(LOAD_CERTIFICATE,"true");

	}

	@Override
	public String getVersion() {
		return MkmConstants.MKM_API_VERSION;
	}


}