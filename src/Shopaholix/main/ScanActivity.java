package Shopaholix.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.*;
import Shopaholix.scanning.*;

public class ScanActivity extends Activity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScanView view = new ScanView(this);
		Button B = view.Button();
		B.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
				intent.putExtra("SCAN_WIDTH", 400);
				intent.putExtra("SCAN_HEIGHT", 400);
				intent.putExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
				intent.putExtra("PROMPT_MESSAGE", "Scanning for UPC Code");
				startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
			}
		});
		EditText E = view.EditText();
		final Activity that = this;
		E.setOnFocusChangeListener(new OnFocusChangeListener(){
			public void onFocusChange(View view, boolean b){
				Intent myIntent = new Intent(view.getContext(), SearchActivity.class);
                that.startActivity(myIntent);
			}
		});
		setContentView(view.render(E, B));
	}
	
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	    if (result != null) {
	      String contents = result.getContents();
	      if (contents != null) {
	        showDialog(R.string.app_name, result.toString());
	      } else {
	        showDialog(R.string.app_name, "Failure");
	      }
	    }
	  }
	
	private void showDialog(int title, CharSequence message) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(title);
	    builder.setMessage(message);
	    builder.setPositiveButton("OK", null);
	    builder.show();
	  }
}

class ScanView extends BaseView {
	public ScanView(Activity a) {
		super(a);
	}

	public View render(EditText E, Button B) {
		LinearLayout L = Shell();
			L.addView(E);
			L.addView(BigTextView("Scan Item"));
			
			L.addView(B);
				B.setText("Scan Item");
			
		return L;
	}

}
