package Shopaholix.main;

import java.util.ArrayList;

import Shopaholix.database.Backend;
import Shopaholix.database.Item;
import Shopaholix.database.ItemRatings.Rating;
import Shopaholix.database.Tag;
import Shopaholix.database.User;
import Shopaholix.database.UserLog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
public class SearchActivity extends Activity {
    /** Called when the activity is first created. */
	Backend backend = Backend.backend;
	SearchView view;
	LinearLayout results;
	Button familyButton;
	Button scanButton;
	EditText searchBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        view = new SearchView(this);
        searchBar = view.EditText();
        searchBar.setInputType(524288); 
    	results = view.VerticalLayout();
    	familyButton = view.Button();
    	scanButton = view.Button();
        setContentView(view.render(searchBar, results, familyButton, scanButton));
        
        final Activity that = this;
        searchBar.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
				final TextWatcher thatt = this;
				ArrayList<Item> suggestedItems = backend.getSuggestedItems(searchBar.getText().toString());
				ArrayList<Tag> suggestedTags = backend.getSuggestedTags(searchBar.getText().toString());
				
				results.removeAllViews();
				
				for(int i = 0; i < 2; i++){
					if(i >= suggestedTags.size()) break;
					final Tag t = suggestedTags.get(i);
					View itemResult = view.SearchResult(t.name, t.ratings.get(new User("Personal")));
					
					results.addView(itemResult);
					
					itemResult.setOnClickListener(new OnClickListener(){
						public void onClick(View view){
							String[] tokens = searchBar.getText().toString().split(" ");
							String result = "";
							for(int i = 0; i < tokens.length - (searchBar.getText().toString().charAt(searchBar.getText().toString().length()-1) == ' ' ? 0 : 1); i++){
								result += tokens[i] + " "; 
							}
							
							searchBar.setText(result + t.name);
							thatt.afterTextChanged(null);
							searchBar.setSelection(searchBar.getText().length());
						}
					});
					results.addView(view.HR());
				}

				for(final Item i: suggestedItems){
					
					View itemResult = view.SearchResult(i.name, i.ratings.get(new User("Personal")));
					
					results.addView(itemResult);
					
					itemResult.setOnClickListener(new OnClickListener(){
						public void onClick(View view){
							Intent myIntent = new Intent(that, ItemActivity.class);
							myIntent.putExtra("upc", i.upc);
							UserLog.appendLog("Searched for item: " + i.name + ", upc: " + i.upc);
			                that.startActivityForResult(myIntent, 10);
						}
					});
					results.addView(view.HR());
					
				}
				
				
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        
        familyButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent myIntent = new Intent(that, FamilyActivity.class);
                that.startActivityForResult(myIntent, 10);
        	}
        });
        
        scanButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent myIntent = new Intent(that, ScanActivity.class);
                that.startActivityForResult(myIntent, 10);
        	}
        });
        
    }
   
}

class SearchView extends BaseView{
	public SearchView(Activity a){super(a);}
	
	public View render(EditText E, LinearLayout results, Button familyButton, Button scanButton){
		LinearLayout L = Shell();
			
			L.addView(E);
			L.addView(results);
			LinearLayout L1 = HorizontalLayout(); L.addView(L1);
				L1.addView(familyButton);
					familyButton.setText("Family");
				L1.addView(scanButton);
					scanButton.setText("Scan");
		return L;
	}
	
	
	public View SearchResult(String text, Rating myRating){
		LinearLayout L = LinearLayout();
		L.setOrientation(LinearLayout.HORIZONTAL);
				
			android.widget.TextView T = DefaultTextView(text); L.addView(T);
			
			T.setLayoutParams(new LinearLayout.LayoutParams(0, 50, 5));
			ImageView I = ImageView(); L.addView(I);
				Log.d("cow", "myRatingsmoooo " + myRating);
				I.setLayoutParams(new LinearLayout.LayoutParams(0, 50, 1));
				switch (myRating){
					case GOOD: I.setImageResource(R.drawable.green_up); break;
					case NEUTRAL: I.setImageResource(R.drawable.yellow_mid); break;
					case BAD: I.setImageResource(R.drawable.red_down); break;
				}
				
						
		return L;
	}
}

