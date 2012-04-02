package Shopaholix.main;

import java.util.ArrayList;

import Shopaholix.database.Backend;
import Shopaholix.database.Item;
import Shopaholix.database.ItemRatings.Rating;
import Shopaholix.database.Tag;
import Shopaholix.database.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
public class SearchActivity extends Activity {
    /** Called when the activity is first created. */
	Backend backend = Backend.backend;
	SearchView view;
	LinearLayout results;
	EditText searchBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new SearchView(this);
        searchBar = view.EditText();
    	results = view.VerticalLayout();
        setContentView(view.render(searchBar, results));
        
        final Activity that = this;
        searchBar.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {

				ArrayList<Item> suggestedItems = backend.getSuggestedItems(searchBar.getText().toString());
				ArrayList<Tag> suggestedTags = backend.getSuggestedTags(searchBar.getText().toString());
				
				results.removeAllViews();
				for(final Item i: suggestedItems){
					
					View itemResult = view.SearchResult(i.name, i.ratings.get(new User("Personal")));
					
					results.addView(itemResult);
					
					itemResult.setOnClickListener(new OnClickListener(){
						public void onClick(View view){
							Intent myIntent = new Intent(that, ItemActivity.class);
							myIntent.putExtra("upc", i.upc);
			                that.startActivityForResult(myIntent, 10);
						}
					});
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
    	  super.onActivityResult(requestCode, resultCode, data); 
    	  ArrayList<Item> suggestedItems = backend.getSuggestedItems(searchBar.getText().toString());
			ArrayList<Tag> suggestedTags = backend.getSuggestedTags(searchBar.getText().toString());
			
			results.removeAllViews();
			for(final Item i: suggestedItems){
				
				View itemResult = view.SearchResult(i.name, i.ratings.get(new User("Personal")));
				
				results.addView(itemResult);
				final Activity that = this;
				itemResult.setOnClickListener(new OnClickListener(){
					public void onClick(View view){
						Intent myIntent = new Intent(that, ItemActivity.class);
						myIntent.putExtra("upc", i.upc);
						that.startActivityForResult(myIntent, 10);
					}
				});
			}
    }
}

class SearchView extends BaseView{
	public SearchView(Activity a){super(a);}
	
	public View render(EditText E, LinearLayout results){
		LinearLayout L = Shell();
						
			L.addView(E);
			L.addView(results);
			
		return L;
	}
	
	public View SearchResult(String text, Rating myRating){
		LinearLayout L = LinearLayout();
		L.setOrientation(LinearLayout.HORIZONTAL);
				
			android.widget.TextView T = DefaultTextView(text); L.addView(T);
			
				T.setLayoutParams(new LinearLayout.LayoutParams(0, 50, 5));
			ImageView I = ImageView();
				Log.d("cow", "myRatingsmoooo " + myRating);
				I.setLayoutParams(new LinearLayout.LayoutParams(0, 50, 1));
				switch (myRating){
					case GOOD: I.setImageResource(R.drawable.green_up); break;
					case NEUTRAL: I.setImageResource(R.drawable.yellow_mid); break;
					case BAD: I.setImageResource(R.drawable.red_down); break;
				}
				
			L.addView(I);			
		return L;
	}
}

