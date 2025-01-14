package org.magic.api.beans;

import java.io.Serializable;
import java.util.Currency;
import java.util.Date;

public class CardShake implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String link;
	private Double price;
	private double priceDayChange;
	private double percentDayChange;
	private double priceWeekChange;
	private double percentWeekChange;
	private MagicCard card;
	private String ed;
	private Date dateUpdate;
	private Currency currency;
	private String providerName;
	private boolean foil;
	private boolean borderless;
	private boolean extendedArt;
	private boolean showcase;
	private boolean fullArt;
	private boolean etched;
	
	public CardShake() {
		price = 0.0;
		priceDayChange = 0;
		percentDayChange = 0;
		priceWeekChange = 0;
		percentWeekChange = 0;
		currency=Currency.getInstance("USD");
		dateUpdate=new Date();
		foil = false;
		etched = false;
	}
	
	public CardShake(boolean foil) {
		price = 0.0;
		priceDayChange = 0;
		percentDayChange = 0;
		priceWeekChange = 0;
		percentWeekChange = 0;
		currency=Currency.getInstance("USD");
		dateUpdate=new Date();
		this.foil=foil;
	}
	
	public void init(double price, double lastDayPrice,double lastWeekPrice) {
		this.price=price;
		priceDayChange = price-lastDayPrice;
		
		
		if(lastDayPrice==0)
			lastDayPrice=1;
		
		if(lastWeekPrice==0)
			lastWeekPrice=1;
		
		
		percentDayChange = ((price-lastDayPrice)/lastDayPrice);
		
		priceWeekChange = price-lastWeekPrice;
		percentWeekChange = ((price-lastWeekPrice)/lastWeekPrice);
		
		currency=Currency.getInstance("USD");
		
		dateUpdate=new Date();
	}
	
	public boolean isEtched() {
		return etched;
	}
	
	public void setEtched(boolean etched) {
		this.etched = etched;
	}

	
	public boolean isBorderless() {
		return borderless;
	}

	public void setBorderless(boolean borderless) {
		this.borderless = borderless;
	}

	public boolean isExtendedArt() {
		return extendedArt;
	}

	public void setExtendedArt(boolean extendedArt) {
		this.extendedArt = extendedArt;
	}

	public boolean isShowcase() {
		return showcase;
	}

	public void setShowcase(boolean showcase) {
		this.showcase = showcase;
	}

	public boolean isFullArt() {
		return fullArt;
	}

	public void setFullArt(boolean fullArt) {
		this.fullArt = fullArt;
	}

	public boolean isFoil() {
		return foil;
	}
	
	public void setFoil(boolean foil) {
		this.foil = foil;
	}
	
	

	public Currency getCurrency() {
		return currency;
	}




	public void setCurrency(Currency currency) {
		this.currency = currency;
	}




	public Date getDateUpdate() {
		return dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link=link;
	}

	public String getEd() {
		return ed;
	}

	public void setEd(String ed) {
		this.ed = ed;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public double getPriceDayChange() {
		return priceDayChange;
	}

	public void setPriceDayChange(double priceDayChange) {
		this.priceDayChange = priceDayChange;
	}

	public double getPercentDayChange() {
		return percentDayChange;
	}

	public void setPercentDayChange(double percentDChange) {
		this.percentDayChange = percentDChange;
	}

	public double getPriceWeekChange() {
		return priceWeekChange;
	}

	public void setPriceWeekChange(double priceWeekChange) {
		this.priceWeekChange = priceWeekChange;
	}

	public double getPercentWeekChange() {
		return percentWeekChange;
	}

	public void setPercentWeekChange(double percentWeekChange) {
		this.percentWeekChange = percentWeekChange;
	}

	public MagicCard getCard() {
		return card;
	}

	public void setCard(MagicCard card) {
		this.card = card;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String name2) {
		this.providerName=name2;
		
	}

}
