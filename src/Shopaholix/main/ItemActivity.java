package Shopaholix.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.location.*;


public class ItemActivity extends Activity {
    /** Called when the activity is first created. */
	ImageView greenUp;
	ImageView yellowMid;
	ImageView redDown;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ItemView(this).render());
    }
    public void clickUp(View v){
    	
    }
    public void clickMid(View v){
    	
    }
	public void clickDown(View v){
		
	}
    
}

class ItemView extends BaseView{
	public ItemView(Activity a){super(a);}
	
	public View render(){
		LinearLayout L = Shell();
			L.addView(BigTextView("Chobani Yoghurt"));
			LinearLayout V = HorizontalLayout(); L.addView(V);
				ImageView I = ImageView(); V.addView(I);
					I.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
				
				
				
				RadioGroup RG = RadioGroup(); V.addView(RG);
					RG.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
					
					RadioButton greenUp = RadioButton(); RG.addView(greenUp);
						greenUp.setButtonDrawable(R.layout.green_up);
						greenUp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
					RadioButton yellowMid = RadioButton(); RG.addView(yellowMid);
						yellowMid.setButtonDrawable(R.layout.yellow_mid);
						yellowMid.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
					RadioButton redDown = RadioButton(); RG.addView(redDown);
						redDown.setButtonDrawable(R.layout.red_down);
						redDown.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
						
					
				
			L.addView(HR());
			
			LinearLayout L1 = VerticalLayout(); L.addView(L1);
				L1.addView(UserResult("Aakanksha"));
				L1.addView(UserResult("Danny"));
				
			L.addView(HR());
			
			L.addView(BigTextView("Public Opinion 74%"));
		return L;
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
