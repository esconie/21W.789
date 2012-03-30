package Shopaholix.database;

import java.util.ArrayList;

public class Backend {
	private DBAdapter db;
	public ArrayList<Item> getSuggestedItems(ArrayList<Tag> tags){
		return getSuggestedItems(tags,10);		
	}
	
	private ArrayList<Item> getSuggestedItems(ArrayList<Tag> tags, int numberOfResults) {
		ArrayList<Item> items=db.getAllItems();
		
		for(Item item:items)
		{
			int score=scoreItem(item)
		}
		return 
	}

	public ArrayList<Tag> getSuggestedTags(ArrayList<Tag> tags){
		return getSuggestedTags(tags,10);
	}
	private ArrayList<Tag> getSuggestedTags(ArrayList<Tag> tags, int numberOfResults) {
		// TODO Auto-generated method stub
		return null;
	}

	public Item getItem(long upc){
		
		
	}
	

}
