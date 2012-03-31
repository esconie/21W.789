package Shopaholix.database;

import java.util.ArrayList;
import java.util.HashMap;
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
		PriorityQueue<Tuple<Item, Integer>> bestItems = new PriorityQueue<Tuple<Item, Integer>>();
		for (Item item : items) {
			int score = scoreItem(item);
			bestItems.add(new Tuple<Item,Integer>(item, score));
		}
		ArrayList<Item> suggestedItems = new ArrayList<Item>();
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
		return score;

	}

	public ArrayList<Tag> getSuggestedTags(ArrayList<Tag> tags) {
		return getSuggestedTags(tags, 10);
	}

	private ArrayList<Tag> getSuggestedTags(ArrayList<Tag> tags,
			int numberOfResults) {
		ArrayList<Tag> allTags = db.getAllTags();
		ArrayList<Item> items = db.getAllItems();
		PriorityQueue<Tuple<Tag, Integer>> bestTags = new PriorityQueue<Tuple<Tag, Integer>>();
		for (Tag tag : allTags) {
			int score = scoreTag(tag, tags, items);
			bestTags.add(new Tuple<Tag,Integer>(tag, score));
		}
		ArrayList<Tag> suggestedTags = new ArrayList<Tag>();
		while (numberOfResults > 0 && !bestTags.isEmpty()) {
			suggestedTags.add(bestTags.poll().a);
		}
		return suggestedTags;
	}

	private int scoreTag(Tag tag, ArrayList<Tag> tags, ArrayList<Item> items) {
		ArrayList<User> users = db.getUsers();
		HashMap<User, HashMap<Rating, Integer>> aggregate = new HashMap<User, HashMap<Rating, Integer>>();
		for (User user : users) {
			HashMap<Rating, Integer> temp = new HashMap<Rating, Integer>();
			for (Rating rating : Rating.values()) {
				temp.put(rating, 0);
			}
			aggregate.put(user, temp);
		}
		for (Item item : items) {
			if (item.satisfies(tags, tag)) {
				for (User user : item.ratings.keySet()) {
					HashMap<Rating, Integer> temp = aggregate.get(user);
					Rating rating = item.ratings.get(user);
					temp.put(rating, temp.get(rating));
				}
			}
		}
		int score=0;
		TagRatings tagRating = new TagRatings();
		for (User user : users) {
			int weight = 1;
			if (user.equals(db.getMe())) {
				weight = 5;
			}
			HashMap<Rating, Integer> temp = aggregate.get(user);
			int numberOfRatings = temp.get(Rating.GOOD)
					+ temp.get(Rating.NEUTRAL) + temp.get(Rating.BAD);
			if (temp.get(Rating.GOOD) / numberOfRatings > .5) {
				tagRating.put(user, Rating.GOOD);
			} else if (temp.get(Rating.BAD) / numberOfRatings > .5) {
				tagRating.put(user, Rating.BAD);
			} else if (numberOfRatings == 0) {
				tagRating.put(user, Rating.UNRATED);
			} else {
				tagRating.put(user, Rating.NEUTRAL);
			}
			score+=weight*temp.get(Rating.GOOD)-weight*temp.get(Rating.BAD);
		}

		tag.ratings = tagRating;
		return score;
	}

	public Item getItem(long upc) {
		if (db.contains(upc))
			return db.getItem(upc);
		else {
			Item item = UPCDatabase.lookupByUPC(upc);
			db.putItem(item);
			return item;
		}
	}

	public void addFamilyMember(User user) {
		db.addFamilyMember(user);
	}

	public void removeFamilyMember(User user) {
		db.removeFamilyMember(user);
	}
	public ArrayList<User> getFamilyMembers(){
		return db.getUsers();
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
