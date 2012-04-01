package Shopaholix.main;

import Shopaholix.database.BackendTest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BackendTestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new BackendTest((Context)this).execute();        
    }
    public void myButtonPressed(View v){
        TextView t = (TextView)this.findViewById(R.id.textView1);
        t.setText("Testing commitqewrger re");
    }
}
