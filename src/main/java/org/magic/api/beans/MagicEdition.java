package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class MagicEdition implements Serializable, Comparable<MagicEdition> {
	public static final long serialVersionUID = 1L;

	private String set;
	private String artist;
	@SerializedName(alternate = "multiverse_id", value = "multiverseId") private String multiverseId;
	private String flavor;
	private String number;
	private String layout;
	private String url;
	private String id;
	private String releaseDate;
	private String type;
	private int cardCount;
	private int cardCountOfficial;
	private String block;
	private transient List<Object> booster;
	private Map<String, String> translations;
	private boolean onlineOnly;
	private Integer mkmid;
	private String mkmname;
	private String gathererCode;
	private boolean foilOnly;
	private String keyRuneCode;
	private int tcgplayerGroupId;
	private String scryfallId;
	private boolean preview;
	private boolean foreignOnly;
	
	
	
	public boolean isForeignOnly() {
		return foreignOnly;
	}

	public void setForeignOnly(boolean foreignOnly) {
		this.foreignOnly = foreignOnly;
	}

	public boolean isPreview() {
		return preview;
	}

	public void setPreview(boolean preview) {
		this.preview = preview;
	}

	public int getCardCountOfficial() {
		return cardCountOfficial;
	}

	public void setCardCountOfficial(int cardCountOfficial) {
		this.cardCountOfficial = cardCountOfficial;
	}

	public boolean isFoilOnly() {
		return foilOnly;
	}

	public void setFoilOnly(boolean foilOnly) {
		this.foilOnly = foilOnly;
	}

	public String getMultiverseid() {
		return multiverseId;
	}
	
	public void setMultiverseid(String multiverseid) {
		this.multiverseId = multiverseid;
	}
	
	public Integer getMkmid() {
		return mkmid;
	}

	public void setMkmid(Integer mkmid) {
		this.mkmid = mkmid;
	}

	public String getMkmName() {
		return mkmname;
	}

	public void setMkmName(String mkmname) {
		this.mkmname = mkmname;
	}


	public boolean isOnlineOnly() {
		return onlineOnly;
	}

	public void setOnlineOnly(boolean onlineOnly) {
		this.onlineOnly = onlineOnly;
	}

	public Map<String, String> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<String, String> translations) {
		this.translations = translations;
	}
	
	public MagicEdition(String idMe)
	{
		setId(idMe);
		booster = new ArrayList<>();
		translations = new HashMap<>();
	}
	
	public MagicEdition(String idMe,String name)
	{
		setId(idMe);
		setSet(name);
		booster = new ArrayList<>();
		translations = new HashMap<>();
	}

	public MagicEdition() {
		booster = new ArrayList<>();
		translations = new HashMap<>();
	}

	public List<Object> getBooster() {
		return booster;
	}

	public void setBooster(List<Object> booster) {
		this.booster = booster;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCardCount() {
		return cardCount;
	}

	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (this.getClass() != obj.getClass())
			return false;
		
		if(getId()==null)
			return false;
	
		return getId().equals(((MagicEdition) obj).getId());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return getSet();
	}

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}


	public String getFlavor() {
		return flavor;
	}

	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int compare(MagicEdition o1, MagicEdition o2) {
		
		if(o1==null || o2==null)
			return -1;
		
		if(o1.getSet()==null || o2.getSet()==null)
			return -1;
		
		return o1.getSet().compareTo(o2.getSet());
	}

	@Override
	public int hashCode() {
		if (set != null)
			return set.hashCode();

		return -1;
	}

	@Override
	public int compareTo(MagicEdition o) {
		return compare(this, o);
	}

	public String getGathererCode() {
		return gathererCode;
	}

	public void setGathererCode(String gathererCode) {
		this.gathererCode = gathererCode;
	}

	public void setKeyRuneCode(String r) {
		keyRuneCode=r;
		
	}
	
	public String getKeyRuneCode() {
		return keyRuneCode;
	}

	public int getTcgplayerGroupId() {
		return tcgplayerGroupId;
	}

	public void setTcgplayerGroupId(int tcgplayerGroupId) {
		this.tcgplayerGroupId = tcgplayerGroupId;
	}

	public void setScryfallId(String scryfallId) {
		this.scryfallId=scryfallId;
		
	}

	public String getScryfallId() {
		return scryfallId;
	}

	
}
