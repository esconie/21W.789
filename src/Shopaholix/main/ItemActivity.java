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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class ItemActivity extends Activity {
    /** Called when the activity is first created. */
	 
	String upc;
	Item I;
	Backend backend;
	
	RadioButton greenUp;
	RadioButton yellowMid;
	RadioButton redDown;
	ImageView itempic;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backend = Backend.backend;
        
        upc = this.getIntent().getExtras().getString("upc");
        I = backend.getItem(upc);
        UserLog.appendLog("Viewing Item Screen for item: " + I.name + ", upc: " + I.upc);    
        Rating myRating = I.ratings.get(new User("Personal"));
        ItemView view = new ItemView(this);
        itempic = view.ImageView();
        greenUp = view.RadioButton();
        greenUp.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		backend.rateItem(upc, Rating.GOOD);
        		UserLog.appendLog("Giving a GOOD rating to item: " + I.name + ", upc: " + I.upc);
        	}
        });
        
        yellowMid = view.RadioButton();
        yellowMid.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		backend.rateItem(upc, Rating.NEUTRAL);
        		UserLog.appendLog("Giving a NEUTRAL rating to item: " + I.name + ", upc: " + I.upc);
        	}
        });
        
        redDown = view.RadioButton();
        redDown.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		backend.rateItem(upc, Rating.BAD);
        		UserLog.appendLog("Giving a BAD rating to item: " + I.name + ", upc: " + I.upc);
        	}
        });
        
        
        setContentView(view.render(itempic, greenUp, yellowMid, redDown, I.name, myRating));
        new DownloadWebPageTask().execute(I.url);
    }

    
    private class DownloadWebPageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            String response = "";
            Bitmap bitmap = null;
            for (String url : urls) {
                try {
                    Log.d("viewer",url);
                    URL url1 = new URL(url);
                    URLConnection connection = url1.openConnection();
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bitmap = BitmapFactory.decodeStream(bis);
                    bis.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //textView.setText(result);
            if (result == null) return;
            itempic.setImageBitmap(result);
        }
    }

}

class ItemView extends BaseView{
	public ItemView(Activity a){super(a);}
	
	public View render(ImageView itempic, RadioButton greenUp, RadioButton yellowMid, RadioButton redDown, String name, Rating myRating){
		LinearLayout L = Shell();
			L.addView(BigTextView(name));
			
			L.addView(RateSection(itempic, greenUp, yellowMid, redDown, myRating));
			L.addView(HR());
			
			LinearLayout L1 = VerticalLayout(); L.addView(L1);
				L1.addView(UserResult("Aakanksha"));
				L1.addView(UserResult("Danny"));
				
			L.addView(HR());
			
			L.addView(BigTextView("Public Opinion 74%"));
		return L;
	}
	
	public View RateSection(ImageView itempic, RadioButton greenUp, RadioButton yellowMid, RadioButton redDown, Rating myRating){
		LinearLayout V = HorizontalLayout(); 
			V.addView(itempic);
				itempic.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			
			RadioGroup RG = RadioGroup(); V.addView(RG);
				RG.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
				
				RG.addView(greenUp);
					greenUp.setButtonDrawable(R.layout.green_up);
					greenUp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
					greenUp.setPadding(150, 0, 0, 0);
					RG.addView(yellowMid);
					yellowMid.setButtonDrawable(R.layout.yellow_mid);
					yellowMid.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
					yellowMid.setPadding(150, 0, 0, 0);
					RG.addView(redDown);
					redDown.setButtonDrawable(R.layout.red_down);
					redDown.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
					redDown.setPadding(150, 0, 0, 0);
					
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
