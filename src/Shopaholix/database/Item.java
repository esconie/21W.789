package Shopaholix.database;

import java.util.ArrayList;
import java.util.HashSet;

public class Item {
	public String upc;
	public String name;
	public HashSet<Tag> tags;
	public ItemRatings ratings;
	public Item(String upc, String name, ItemRatings ratings){
		this.upc=upc;
		this.name=name;
		this.ratings=ratings;
		tags=new HashSet<Tag>();
		for(String tag:name.split(" ")){
			tags.add(new Tag(tag));
		}
		
	}
	public boolean satisfies(ArrayList<Tag> requiredTags) {
		for(Tag tag:requiredTags){
			if(!tags.contains(tag))
				return false;
		}
		return true;
	}
	public boolean satisfies(ArrayList<Tag> requiredTags, Tag requiredTag) {
		 return satisfies(requiredTags)&&tags.contains(requiredTag);
	}

	public String toString(){
		return upc+", "+name+", "+ratings.toString();
	}
}
