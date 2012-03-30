package Shopaholix.database;

import java.util.ArrayList;

public class Item {
	public long upc;
	public String name;
	public ArrayList<String> tags;
	public ItemRatings ratings;
	public Item(long upc, String name, ItemRatings ratings){
		this.upc=upc;
		this.name=name;
		this.ratings=ratings;
	}

}
