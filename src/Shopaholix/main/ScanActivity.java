package Shopaholix.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.location.*;


public class ScanActivity extends Activity {
    /** Called when the activity is first created. */
	ImageView greenUp;
	ImageView yellowMid;
	ImageView redDown;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ScanView(this).render());
    }
    public void clickUp(View v){
    	
    }
    public void clickMid(View v){
    	
    }
	public void clickDown(View v){
		
	}
    
}

class ScanView extends BaseView{
	public ScanView(Activity a){super(a);}
	
	public View render(){
		LinearLayout L = Shell();
			L.addView(BigTextView("Scan Item"));
			
			
			//Button B = Button(); L.addView(B);
				
				//B.setText("Scan Item");
				
		return L;
	}
	
	
}
