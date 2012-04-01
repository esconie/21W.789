package Shopaholix.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import Shopaholix.database.ItemRatings.Rating;
import android.content.Context;

public class Backend {
	HashMap<String, Item> items = new HashMap<String, Item>();
	private Context context;
	private User me;
	private HashSet<User> users;
	private HashSet<Tag> allTags;

	public Backend(Context context) {
		this.context = context;
		me = new User("Personal");
		users = new HashSet<User>();
		allTags = new HashSet<Tag>();
		users.add(me);
	}

	public ArrayList<Item> getSuggestedItems(ArrayList<Tag> tags) {
		return getSuggestedItems(tags, 5);
	}

	private ArrayList<Item> getSuggestedItems(ArrayList<Tag> tags,
			int numberOfResults) {
		Collection<Item> items = this.items.values();
		PriorityQueue<Tuple<Item, Integer>> bestItems = new PriorityQueue<Tuple<Item, Integer>>();
		for (Item item : items) {
			if (item.satisfies(tags)) {
				int score = scoreItem(item);
				bestItems.add(new Tuple<Item, Integer>(item, score));
			}
		}
		ArrayList<Item> suggestedItems = new ArrayList<Item>();
		while (numberOfResults > 0 && !bestItems.isEmpty()) {
			suggestedItems.add(bestItems.poll().a);
			numberOfResults--;
		}
		return suggestedItems;
	}

	private int scoreItem(Item item) {
		int score = 0;
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
		return -score;

	}

	public ArrayList<Tag> getSuggestedTags(ArrayList<Tag> requiredTags) {
		return getSuggestedTags(requiredTags, 5);
	}

	private ArrayList<Tag> getSuggestedTags(ArrayList<Tag> requiredTags,
			int numberOfResults) {
		PriorityQueue<Tuple<Tag, Integer>> bestTags = new PriorityQueue<Tuple<Tag, Integer>>();
		for (Tag tag : allTags) {
			int score = scoreTag(tag, requiredTags);
			bestTags.add(new Tuple<Tag, Integer>(tag, score));
		}
		ArrayList<Tag> suggestedTags = new ArrayList<Tag>();
		while (numberOfResults > 0 && !bestTags.isEmpty()) {
			suggestedTags.add(bestTags.poll().a);
			numberOfResults--;
		}
		return suggestedTags;
	}

	private int scoreTag(Tag tag, ArrayList<Tag> tags) {
		HashMap<User, HashMap<Rating, Integer>> aggregate = new HashMap<User, HashMap<Rating, Integer>>();
		for (User user : users) {
			HashMap<Rating, Integer> temp = new HashMap<Rating, Integer>();
			for (Rating rating : Rating.values()) {
				temp.put(rating, 0);
			}
			aggregate.put(user, temp);
		}
		for (Item item : items.values()) {
			if (item.satisfies(tags, tag)) {
				for (User user : item.ratings.keySet()) {
					HashMap<Rating, Integer> temp = aggregate.get(user);
					Rating rating = item.ratings.get(user);
					temp.put(rating, temp.get(rating) + 1);
				}
			}
		}
		int score = 0;
		TagRatings tagRating = new TagRatings();
		for (User user : users) {
			int weight = 1;
			if (user.equals(me)) {
				weight = 5;
			}
			HashMap<Rating, Integer> temp = aggregate.get(user);
			int numberOfRatings = temp.get(Rating.GOOD)
					+ temp.get(Rating.NEUTRAL) + temp.get(Rating.BAD);

			if (temp.get(Rating.GOOD) > .5 * numberOfRatings) {
				tagRating.put(user, Rating.GOOD);
			} else if (temp.get(Rating.BAD) > numberOfRatings * .5) {
				tagRating.put(user, Rating.BAD);
			} else if (numberOfRatings == 0) {
				tagRating.put(user, Rating.UNRATED);
			} else {
				tagRating.put(user, Rating.NEUTRAL);
			}
			score += weight * temp.get(Rating.GOOD) - weight
					* temp.get(Rating.BAD);
		}

		tag.ratings = tagRating;
		return -score;
	}

	public Item getItem(String upc) {
		if (items.containsKey(upc)) {
			return items.get(upc);
		} else {
			Item item = UPCDatabase.lookupByUPC(upc);

			items.put(upc, item);
			for (Tag tag : item.tags) {
				allTags.add(tag);
			}
			return item;
		}
	}

	public void rateItem(String UPC, Rating rating) {
		items.get(UPC).ratings.put(me, rating);
	}

	public void rateFamilyItem(String UPC, User user, Rating rating) {
		items.get(UPC).ratings.put(user, rating);
	}

	public void addFamilyMember(User user) {
		users.add(user);
	}

	public void removeFamilyMember(User user) {
		users.remove(user);
	}

	public HashSet<User> getFamilyMembers() {
		return users;
	}

	public class Tuple<A, B extends Comparable<B>> implements
			Comparable<Tuple<A, B>> {
		A a;
		B b;

		public Tuple(A a, B b) {
			this.a = a;
			this.b = b;
		}

		public int compareTo(Tuple<A, B> arg0) {
			return b.compareTo(arg0.b);
		}

	}
}
