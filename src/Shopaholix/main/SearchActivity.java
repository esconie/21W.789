package Shopaholix.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import android.widget.TextView;
import android.location.*;
public class SearchActivity extends Activity {
    /** Called when the activity is first created. */
	
	SearchView view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new SearchView(this);
        final EditText searchBar = view.EditText();
    	final LinearLayout results = view.VerticalLayout();
        setContentView(view.render(searchBar, results));
        
<<<<<<< HEAD
        EditText editText = (EditText)findViewById(R.id.editText);
        final LinearLayout searchLine = (LinearLayout)lInflator.inflate(R.layout.search_result, null);
        editText.addTextChangedListener(new TextWatcher(){
=======
        searchBar.addTextChangedListener(new TextWatcher(){
			@Override
>>>>>>> 261b16c14179faa5c5a881fe6f885521823c742d
			public void afterTextChanged(Editable s) {
				results.removeAllViews();
				
				results.addView(view.SearchResult(s.toString()));
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        
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
	
	public View SearchResult(String text){
		LinearLayout L = LinearLayout();
		L.setOrientation(LinearLayout.HORIZONTAL);
				
			L.addView(DefaultTextView(text));
			
			ImageView I = ImageView();
				I.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
				I.setImageResource(R.drawable.green_up);
			L.addView(I);			
		return L;
	}
}

