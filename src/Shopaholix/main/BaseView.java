package Shopaholix.main;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;


public class BaseView {
	public Activity a;
	public BaseView(Activity a){
		this.a = a;
	}
	
	public LinearLayout Shell(){
		LinearLayout L = VerticalLayout();
		L.setBackgroundColor(android.graphics.Color.WHITE);
		L.setPadding(15, 15, 15, 15);
		return L;
		
	}
    
	
	public View HR(){
		LinearLayout L = LinearLayout();
		L.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2));
		L.setBackgroundColor(android.graphics.Color.LTGRAY);
		return L;
	}
	
	
    
	public TextView DefaultTextView(String text){
		TextView T = TextView();
		T.setTextColor(android.graphics.Color.DKGRAY);
		T.setGravity(Gravity.CENTER_VERTICAL);
		T.setLayoutParams(new LinearLayout.LayoutParams(-1 /*match_parent*/, -2 /*wrap_content*/));
		
		T.setText(text);
		return T;
	}
	public TextView BigTextView(String text){
		TextView T = TextView();
		T.setTextColor(android.graphics.Color.DKGRAY);
		T.setTextSize(20);
		T.setLayoutParams(new LinearLayout.LayoutParams(-1 /*match_parent*/, -2 /*wrap_content*/));
		
		T.setText(text);
		return T;
		
	}
    
    
    
	
	public LinearLayout VerticalLayout(){
		LinearLayout L = LinearLayout();
		L.setOrientation(LinearLayout.VERTICAL);
		return L;
	}
	public LinearLayout HorizontalLayout(){
		LinearLayout L = LinearLayout();
		L.setOrientation(LinearLayout.HORIZONTAL);
		return L;
	}
	
	

    public Button Button(){
    	return new Button(a);
    }
    public RadioGroup RadioGroup(){
    	return new RadioGroup(a);
    }
    public RadioButton RadioButton(){
    	return new RadioButton(a);
    }
	public LinearLayout LinearLayout(){
    	return new LinearLayout(a);
    }
	public EditText EditText(){
    	return new EditText(a);
    }
	public TextView TextView(){
    	return new TextView(a);
    }
	public ImageView ImageView(){
    	return new ImageView(a);
    }
	
}
