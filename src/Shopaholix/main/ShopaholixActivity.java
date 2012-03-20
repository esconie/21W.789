package Shopaholix.main;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ShopaholixActivity extends Activity {
   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    }
    public void myButtonPressed(View v) throws IOException{
    	TextView t = (TextView)this.findViewById(R.id.textView1);
    	t.setText("Running");
    	new DownloadFile((Context)this).execute();
    	
    }
    public void onProgessUpdate(String... args) throws IOException{
        TextView t = (TextView)this.findViewById(R.id.textView1);
        t.setText("Done");
        
    }
}