package Shopaholix.main;

import Shopaholix.database.Backend;
import Shopaholix.database.Item;
import Shopaholix.database.ItemRatings;
import Shopaholix.database.ItemRatings.Rating;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.location.*;


public class ItemActivity extends Activity {
    /** Called when the activity is first created. */
	 
	String upc;
	Item I;
	Backend backend;
	
	RadioButton greenUp;
	RadioButton yellowMid;
	RadioButton redDown;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backend = new Backend((Context)this);
        
        upc = savedInstanceState.getString("upc");
        I = backend.getItem(upc);
        Rating myRating = I.ratings.get("Personal");
        ItemView view = new ItemView(this);
        greenUp = view.RadioButton();
        greenUp.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		backend.rateItem(upc, Rating.GOOD);
        	}
        });
        
        yellowMid = view.RadioButton();
        yellowMid.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		backend.rateItem(upc, Rating.NEUTRAL);
        	}
        });
        
        redDown = view.RadioButton();
        redDown.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		backend.rateItem(upc, Rating.BAD);
        	}
        });
        
        
        setContentView(view.render(greenUp, yellowMid, redDown, I.name, myRating));
    }
  
}

class ItemView extends BaseView{
	public ItemView(Activity a){super(a);}
	
	public View render(RadioButton greenUp, RadioButton yellowMid, RadioButton redDown, String name, Rating myRating){
		LinearLayout L = Shell();
			L.addView(BigTextView(name));
			
			L.addView(RateSection(greenUp, yellowMid, redDown, myRating));
			L.addView(HR());
			
			LinearLayout L1 = VerticalLayout(); L.addView(L1);
				L1.addView(UserResult("Aakanksha"));
				L1.addView(UserResult("Danny"));
				
			L.addView(HR());
			
			L.addView(BigTextView("Public Opinion 74%"));
		return L;
	}
	
	public View RateSection(RadioButton greenUp, RadioButton yellowMid, RadioButton redDown, Rating myRating){
		LinearLayout V = HorizontalLayout(); 
			ImageView I = ImageView(); V.addView(I);
				I.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			
			RadioGroup RG = RadioGroup(); V.addView(RG);
				RG.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
				
				RG.addView(greenUp);
					greenUp.setButtonDrawable(R.layout.green_up);
					greenUp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
					RG.addView(yellowMid);
					yellowMid.setButtonDrawable(R.layout.yellow_mid);
					yellowMid.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
					RG.addView(redDown);
					redDown.setButtonDrawable(R.layout.red_down);
					redDown.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
					
				switch(myRating){
					case GOOD: greenUp.setChecked(true); break;
					case NEUTRAL: yellowMid.setChecked(true); break;
					case BAD: redDown.setChecked(true); break;
					case UNRATED:break;
				}
		return V;
	}
	
	public View UserResult(String name){
		LinearLayout L = HorizontalLayout();
			TextView T = DefaultTextView(name); L.addView(T);
			T.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			
			
			ImageView I = ImageView(); L.addView(I);
				I.setLayoutParams(new LinearLayout.LayoutParams(0, 50, 1));
				I.setImageResource(R.drawable.green_up);
			
		return L;
	}
	
}
