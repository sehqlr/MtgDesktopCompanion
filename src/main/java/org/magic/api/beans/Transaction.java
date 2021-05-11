package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction {
	
	public enum STAT {NEW,ACCEPTED,REFUSED} 
	
	private int id=-1;
	private Date dateProposition;
	private List<MagicCardStock> items;
	private Contact contact;
	private String message;
	private STAT statut;
	
	
	public Transaction() {
		dateProposition = new Date();
		items = new ArrayList<>();
		contact=new Contact();
		statut = STAT.NEW;
	}
	
	public void setStatut(STAT statut) {
		this.statut = statut;
	}
	public STAT getStatut() {
		return statut;
	}
	
	
	public double total()
	{
		return getItems().stream().mapToDouble(e->e.getQte()*e.getPrice()).sum();
	}
	
	
	@Override
	public String toString() {
		return String.valueOf(getId());
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateProposition() {
		return dateProposition;
	}
	public void setDateProposition(Date dateProposition) {
		this.dateProposition = dateProposition;
	}
	public List<MagicCardStock> getItems() {
		return items;
	}
	public void setItems(List<MagicCardStock> proposition) {
		this.items = proposition;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}

	
}