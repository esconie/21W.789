package Shopaholix.database;

import java.util.ArrayList;

public class Item {
	public long upc;
	public String name;
	public HashMap<String> tags;
	public ItemRatings ratings;
	public Item(long upc, String name, ItemRatings ratings){
		this.upc=upc;
		this.name=name;
		this.ratings=ratings;
		
	}
	public boolean satisfies(ArrayList<Tag> tags2, Tag tag) {
		// TODO Auto-generated method stub
		return false;
	}

}
