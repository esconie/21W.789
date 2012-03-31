package Shopaholix.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import android.widget.TextView;
import android.location.*;
public class SearchActivity extends Activity {
    /** Called when the activity is first created. */
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);
        //greenUp = (ImageView) findViewById(R.id.greenUp);
        //yellowMid = (ImageView) findViewById(R.id.yellowMid);
        //redDown = (ImageView) findViewById(R.id.redDown);
        final LinearLayout results = (LinearLayout)findViewById(R.id.results);
        LayoutInflater lInflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        EditText editText = (EditText)findViewById(R.id.editText);
        final LinearLayout searchLine = (LinearLayout)lInflator.inflate(R.layout.search_result, null);
        editText.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				results.removeAllViews();
				((TextView)searchLine.getChildAt(0)).setText(s.toString(), BufferType.NORMAL);
				results.addView(searchLine);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        
    }
    
}