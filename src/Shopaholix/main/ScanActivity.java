package Shopaholix.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
        ScanView view = new ScanView(this);
        Button B = view.Button();
        B.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		
        	}
        });
        setContentView(view.render(B));
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
	
	public View render(Button B){
		LinearLayout L = Shell();
			L.addView(BigTextView("Scan Item"));
			
			
			L.addView(B);
				B.setText("Scan Item");
				
		return L;
	}
	
	
}
