package Shopaholix.main;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import Shopaholix.database.Backend;
import Shopaholix.database.Item;
import Shopaholix.database.ItemRatings.Rating;
import Shopaholix.database.User;
import Shopaholix.database.UserLog;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class FamilyActivity extends Activity {
    /** Called when the activity is first created. */
	 
	String upc;
	Item I;
	Backend backend;
	EditText searchBar;
	LinearLayout familyList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	UserLog.appendLog("Viewing Family Screen");
        super.onCreate(savedInstanceState);
        backend = Backend.getBackend(this);
        
        final FamilyView view = new FamilyView(this);
      
        searchBar = view.EditText();
        familyList = view.VerticalLayout();
        setContentView(view.render(searchBar, familyList));
        
        searchBar.setOnEditorActionListener(new OnEditorActionListener(){
        	public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
        		familyList.removeAllViews();
        		
        		backend.addFamilyMember(new User(v.getText().toString()), v.getText().toString());
        		for(User u : backend.users.values()){
        			ImageView I = view.ImageView();
        			I.setOnClickListener(new OnClickListener(){
    					public void onClick(View arg0) {
							//backend.removeFamilyMember(userID)
    					}
        			});
        			familyList.addView(view.FamilyResult(I, v.getText().toString()));
        		}
    			
    			
    			return true;
        	}
        });
    }

    
}

class FamilyView extends BaseView{
	public FamilyView(Activity a){super(a);}
	
	public View render(EditText searchBar, LinearLayout familyList){
		LinearLayout L = Shell();
			L.addView(BigTextView("Family View"));
			L.addView(searchBar);
			
			L.addView(HR());
			
			
			L.addView(familyList);
				familyList.addView(FamilyResult(ImageView(), "Aakanksha"));
				familyList.addView(FamilyResult(ImageView(), "Danny"));
				
			L.addView(HR());
			
			
		return L;
	}
	
	public View FamilyResult(ImageView I, String name){
		LinearLayout L = HorizontalLayout();
			TextView T = DefaultTextView(name); L.addView(T);
			T.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			
			
			L.addView(I);
				I.setLayoutParams(new LinearLayout.LayoutParams(0, 50, 1));
				I.setImageResource(R.drawable.cross);
			
		return L;
	}
	
}
