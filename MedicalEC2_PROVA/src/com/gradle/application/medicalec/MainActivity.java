package com.gradle.application.medicalec;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivityDrools";
//	private static final String PACKAGE = "com.gradle.application.medicalec";
    private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MainActivity.context = getApplicationContext();

		final EditText edittext = (EditText) findViewById(R.id.editText1);
		final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	
	        	//try {
	        		Builder builder = new Builder();
	     	 		Session session = builder.build();
	     	 		session.start();
	     	 		
	     	 		MyEvent evento = new MyEvent("eventName");
//		       		session.notify("MyEvent", new Integer(5), new HashMap<String, Object>());
	     	 		session.getMachinery().insert(evento);
	     	 		
	     	 		session.dump();
	     	 		session.getMachinery().dispose();

	     	 		Log.i(TAG, " DONE...");

	
//	            } 
	        	/*catch (Throwable t) {
	             	 edittext.setText("");
	                  edittext.setText(t.toString());
	                  Log.e(TAG, t.toString());
	            }*/
	            
	        }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public static Context getAppContext() {
        return MainActivity.context;
    }
	
	
	public static class MyEvent {
    	
    	public MyEvent(String name) {
			super();
			this.setName(name);
		}

		public String name;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof MyEvent)) {
				return false;
			}
			MyEvent other = (MyEvent) obj;
			if (getName() == null) {
				if (other.getName() != null) {
					return false;
				}
			} else if (!getName().equals(other.getName())) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "MyEvent [name=" + getName() + "]";
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
    }
}
