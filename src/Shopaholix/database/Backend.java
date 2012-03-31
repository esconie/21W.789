package Shopaholix.database;

import java.util.ArrayList;
import java.util.PriorityQueue;

import Shopaholix.database.ItemRatings.Rating;

public class Backend {
	private DBAdapter db;

	public ArrayList<Item> getSuggestedItems(ArrayList<Tag> tags) {
		return getSuggestedItems(tags, 10);
	}

	private ArrayList<Item> getSuggestedItems(ArrayList<Tag> tags,
			int numberOfResults) {
		ArrayList<Item> items = db.getAllItems();
		PriorityQueue<Tuple<Item, Integer>> bestItems = new PriorityQueue<>();
		for (Item item : items) {
			int score = scoreItem(item);
			bestItems.add(new Tuple(item, score));
		}
		ArrayList<Item> suggestedItems;
		while (numberOfResults > 0 && !bestItems.isEmpty()) {
			suggestedItems.add(bestItems.poll().a);
		}
		return suggestedItems;
	}

	private int scoreItem(Item item) {
		int score = 0;
		for (User user : item.ratings.keySet()) {
			int weight = 1;
			if (user.equals(db.getMe())) {
				weight = 5;
			}
			Rating rating = item.ratings.get(user);
			if (rating == Rating.BAD) {
				score -= weight;
			} else if (rating == Rating.GOOD) {
				score += weight;
			}
		}

	}

	public ArrayList<Tag> getSuggestedTags(ArrayList<Tag> tags) {
		return getSuggestedTags(tags, 10);
	}

	private ArrayList<Tag> getSuggestedTags(ArrayList<Tag> tags,
			int numberOfResults) {
		ArrayList<Tag> allTags = db.getAllTags();
		ArrayList<Item> items = db.getAllItems();
		PriorityQueue<Tuple<Tag, Integer>> bestTags = new PriorityQueue<>();
		for (Tag tag : allTags) {
			int score = scoreTag(tag, tags, items);
			bestTags.add(new Tuple(tag, score));
		}
		ArrayList<Tag> suggestedTags;
		while (numberOfResults > 0 && !bestTags.isEmpty()) {
			suggestedTags.add(bestTags.poll().a);
		}
		return suggestedTags;
	}

	private int scoreTag(Tag tag, ArrayList<Tag> tags, ArrayList<Item> items) {
		int score = 0;
		for (Item item : items) {
			if (item.satisfies(tags, tag)) {
				for (User user : item.ratings.keySet()) {
					int weight = 1;
					if (user.equals(me)) {
						weight = 5;
					}
					Rating rating = item.ratings.get(user);
					if (rating == Rating.BAD) {
						score -= weight;
					} else if (rating == Rating.GOOD) {
						score += weight;
					}
				}
			}
		}
		
		tag.ratings=new TagRatings();//change this later
		return score;
	}

	public Item getItem(long upc) {
		if(db.contains(upc))
			return db.getItem(upc);
		else{
			Item item=UPCDatabase.lookupByUPC(upc);
			db.putItem(item);
			return item;
		}
	}
	public void addFamilyMember(User user){
		db.addFamilyMember(user);
	}
	public void removeFamilyMember(User user){
		db.removeFamilyMember(user);
	}

	public class Tuple<A, B extends Comparable<B>> implements
			Comparable<Tuple<A, B>> {
		A a;
		B b;

		public Tuple(A a, B b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public int compareTo(Tuple<A, B> arg0) {
			return b.compareTo(arg0.b);
		}

	}
}
