package transit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import transit.CardFragment.Direction;
import com.afollestad.cardsui.CardCenteredHeader;
import com.afollestad.cardsui.CardCompressed;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardTheme;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.OrderType;

import android.app.AlertDialog;
import android.app.Fragment
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class SettingsView extends Fragment {


	Dictionary providers =new Hashtable();

	private CardTheme mTheme;
	private Toast mToast;
	Storage mystorage;
	View thisView;
	Dictionary stopDict = new Hashtable();
	Dictionary mystops = new Hashtable();
	Dictionary mystopids = new Hashtable();
	String urlbegin = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions";

	public SettingsView() {
		providers.put(		"AC Transit", "actransit"   		);
		providers.put(	"Asheville Redefines Transit", "art"   			);
		providers.put(	"CMRT and Howard Transit", "howard"   			);
		providers.put(		"California University of Pennsylvania", "calu-pa"   		);
		providers.put(		"Camarillo Area (CAT)", "camarillo"   		);
		providers.put(	"Cape Cod Regional Transit Authority", "ccrta"   			);
		providers.put(			"Chapel Hill Transit", "chapel-hill"   	);
		providers.put(			"Charles River TMA - EZRide", "charles-river"   	);
		providers.put(		"Charm City Circulator", "charm-city"   		);
		providers.put(	"City College NYC", "ccny"   			);
		providers.put(		"City of Oxford", "oxford-ms"   		);
		providers.put(			"Collegetown Shuttle", "collegetown"   	);
		providers.put(	"CyRide", "cyride"   			);
		providers.put(			"DC Circulator", "dc-circulator"   	);
		providers.put("Downtown Connection", "da"   				);
		providers.put(		"Dumbarton Express", "dumbarton"   		);
		providers.put(	"East Carolina University", "ecu"   			);
		providers.put(	"Emery-Go-Round", "emery"   			);
		providers.put(		"Fairfax (CUE)", "fairfax"   		);
		providers.put(		"Foothill Transit", "foothill"   		);
		providers.put(	"George Mason University", "gmu"   			);
		providers.put(				"Georgia College", "georgia-college"   );
		providers.put(		"Glendale Beeline", "glendale"   		);
		providers.put(			"Gold Coast Transit", "south-coast"   	);
		providers.put(	"Lasell College", "lasell"   			);
		providers.put(		"Los Angeles Metro", "lametro"   		);
		providers.put(			"Los Angeles Rail", "lametro-rail"   	);
		providers.put(	"Loyola University Maryland", "loyola"   			);
		providers.put(	"MBTA", "mbta"   			);
		providers.put(	"Massachusetts Institute of Technology", "mit"   			);
		providers.put(		"Moorpark Transit", "moorpark"   		);
		providers.put(	"NYC MTA - Bronx", "bronx"   			);
		providers.put(		"NYC MTA - Brooklyn", "brooklyn"   		);
		providers.put(			"NYC MTA - Staten Island", "staten-island"   	);
		providers.put(	"North County Transit District", "nctd"   			);
		providers.put(		"Omnitrans", "omnitrans"   		);
		providers.put(	"Palos Verdes Transit", "pvpta"   			);
		providers.put(	"Pensacola Beach (SRIA)", "sria"   			);
		providers.put(			"Portland Streetcar", "portland-sc"   	);
		providers.put(	"Prince Georges County", "pgc"   			);
		providers.put(	"RTC RIDE, Reno", "reno"   			);
		providers.put(		"Radford Transit", "radford"   		);
		providers.put(		"Roosevelt Island", "roosevelt"   		);
		providers.put(			"Rutgers Univ. Newark College Town Shuttle", "rutgers-newark"   	);
		providers.put(		"Rutgers University", "rutgers"   		);
		providers.put(		"San Francisco Muni", "sf-muni"   		);
		providers.put(		"Seattle Streetcar", "seattle-sc"   		);
		providers.put(			"Simi Valley (SVT)", "simi-valley"   	);
		providers.put(	"Societe de transport de Laval", "stl"   			);
		providers.put(		"Societe de transport de Sherbrooke", "sherbrooke"   		);
		providers.put(	"Temple University", "temple"   			);
		providers.put(			"Thousand Oaks Transit (TOT)", "thousand-oaks"   	);
		providers.put(		"Thunder Bay", "thunderbay"   		);
		providers.put(	"Toronto Transit Commission", "ttc"   			);
		providers.put(		"Unitrans ASUCD/City of Davis", "unitrans"   		);
		providers.put(	"University of California San Francisco", "ucsf"   			);
		providers.put(	"University of Maryland", "umd"   			);
		providers.put(		"University of Minnesota", "umn-twin"   		);
		providers.put(	"Ventura Intercity (VISTA)", "vista"   			);
		providers.put(	"Western Kentucky University", "wku"   			);
		providers.put(		"York College", "york-pa"   		);
	}

	public static SettingsView newSettingsView(int index) {
		SettingsView frag = new SettingsView();
		Bundle b = new Bundle();
		b.putInt("index", index);
		frag.setArguments(b);
		return frag;


	}





	//    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.settings_view, container, false);



		final Spinner spinner = (Spinner) thisView.findViewById(R.id.spinner1);
		final Spinner routeSpinner = (Spinner) thisView.findViewById(R.id.spinner2);
		final Spinner directionSpinner = (Spinner) thisView.findViewById(R.id.spinner3);
		directionSpinner.setPrompt("Select direction");
		final Spinner stopSpinner = (Spinner) thisView.findViewById(R.id.spinner4);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.agencies, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		ArrayList dirx = new ArrayList();
		dirx.add("Select Direction");
		ArrayAdapter<CharSequence> adapterdir = new ArrayAdapter<CharSequence>(getActivity(),android.R.layout.simple_spinner_item,  dirx);
		directionSpinner.setAdapter(adapterdir);

		ArrayList stopx = new ArrayList();
		stopx.add("Select Stop");
		ArrayAdapter<CharSequence> adapterstop = new ArrayAdapter<CharSequence>(getActivity(),android.R.layout.simple_spinner_item,  stopx);
		stopSpinner.setAdapter(adapterstop);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

				if(isNetworkConnected()){
					routeSpinner.setEnabled(false);
					directionSpinner.setEnabled(false);
					stopSpinner.setEnabled(false);
					String agency = (String) spinner.getSelectedItem();
					String shortagency = (String) providers.get(agency);
					ArrayList routes = null;
					try {

						routes = new GetRoutesTask().execute("http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=" +shortagency ).get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					routes.add(0, "Select Route");
					ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(getActivity(),android.R.layout.simple_spinner_item, routes);
					adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					routeSpinner.setAdapter(adapter2);
					routeSpinner.setSelection(0);
					routeSpinner.setEnabled(true);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});


		routeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if(isNetworkConnected()){
					directionSpinner.setEnabled(false);
					stopSpinner.setEnabled(false);
					String agency = (String) spinner.getSelectedItem();
					String shortagency = (String) providers.get(agency);
					String route = (String) routeSpinner.getSelectedItem();

					if (route != "Select Route"){
						try {
							mystops = new GetStopsTask().execute("http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=" +shortagency + "&r="+ route).get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						ArrayList directions = new ArrayList();
						for(Enumeration e = mystops.keys(); e.hasMoreElements();){
							String dx = (String) e.nextElement();
							directions.add(dx);
						}
						ArrayAdapter<CharSequence> adapter3 = new ArrayAdapter<CharSequence>(getActivity(),android.R.layout.simple_spinner_item, directions);
						adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						directionSpinner.setAdapter(adapter3);
						directionSpinner.setEnabled(true);
						stopSpinner.setEnabled(true);
					}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});




		directionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if(isNetworkConnected()){
					try{
						stopSpinner.setEnabled(false);
						String route = (String) routeSpinner.getSelectedItem();
						String direction = (String) directionSpinner.getSelectedItem();
						if (route != "Select Route"){
							ArrayList stopslist = (ArrayList) mystops.get(direction);
							ArrayList stopsDisplay = new ArrayList();
							for(int i = 0; i< stopslist.size(); i++){
								stopsDisplay.add(stopDict.get(stopslist.get(i)));
							}
							ArrayAdapter<CharSequence> adapter4 = new ArrayAdapter<CharSequence>(getActivity(),android.R.layout.simple_spinner_item,stopsDisplay );
							adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							stopSpinner.setAdapter(adapter4);
							stopSpinner.setEnabled(true);
						}
					}catch(NullPointerException e){
						
					}finally {}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});


		Button mButtonDelete = (Button) thisView.findViewById(R.id.button2);
		Button mButton = (Button) thisView.findViewById(R.id.button1);
		Button mButtonCheck = (Button) thisView.findViewById(R.id.button3);
		
		mButton.setOnClickListener(
				new View.OnClickListener()
				{
					public void onClick(View view)
					{

						String agency = (String) providers.get(spinner.getSelectedItem());
						String route = (String) routeSpinner.getSelectedItem(); 

						if (route != "Select Route"){
							String direction = (String) directionSpinner.getSelectedItem();
							String stoptitle = (String) stopSpinner.getSelectedItem();
							String stopid = null;
							if (mystopids.get(stopSpinner.getSelectedItem()) instanceof ArrayList){
								ArrayList x = (ArrayList) mystopids.get(stoptitle);
								Log.d("wefewfwefes", x.toString());
								ArrayList stopslist = (ArrayList) mystops.get(direction);
								if (stopslist.contains(x.get(0))){
									stopid = (String) x.get(0);
								}else{
									stopid = (String) x.get(1);
								}
							}else{
								stopid = (String) mystopids.get(stoptitle);
							}



							
						}
					}
				});

		mButtonDelete.setOnClickListener(
				new View.OnClickListener()
				{
					public void onClick(View view)
					{


						String agency = (String) providers.get(spinner.getSelectedItem());
						String route = (String) routeSpinner.getSelectedItem(); 
						String direction = (String) directionSpinner.getSelectedItem();
						String stoptitle = (String) stopSpinner.getSelectedItem();
						String stopid = null;

						if (route != "Select Route"){
							if (mystopids.get(stopSpinner.getSelectedItem()) instanceof ArrayList){
								ArrayList x = (ArrayList) mystopids.get(stoptitle);
								ArrayList stopslist = (ArrayList) mystops.get(direction);
								if (stopslist.contains(x.get(0))){
									stopid = (String) x.get(0);
								}else{
									stopid = (String) x.get(1);
								}
							}else{
								stopid = (String) mystopids.get(stoptitle);
							}

							String fn = agency+"_"+route+"_"+direction+"_"+stopid;
							if (mystorage.isFileExist("MyDirName", fn)){
								mystorage.deleteFile("MyDirName", fn);
								showToast("Succesfully Deleted!");
							}else{
								showToast("Not Found!");
							}
							
							
						}
					}
				});
		
		mButtonCheck.setOnClickListener(
				new View.OnClickListener()
				{
					public void onClick(View view)
					{

						String agency = (String) providers.get(spinner.getSelectedItem());
						String route = (String) routeSpinner.getSelectedItem(); 

						if (route != "Select Route"){
							String direction = (String) directionSpinner.getSelectedItem();
							String stoptitle = (String) stopSpinner.getSelectedItem();
							String stopid = null;
							if (mystopids.get(stopSpinner.getSelectedItem()) instanceof ArrayList){
								ArrayList x = (ArrayList) mystopids.get(stoptitle);
								ArrayList stopslist = (ArrayList) mystops.get(direction);
								if (stopslist.contains(x.get(0))){
									stopid = (String) x.get(0);
								}else{
									stopid = (String) x.get(1);
								}
							}else{
								stopid = (String) mystopids.get(stoptitle);
							}



							String one = "&a=" + agency + "&r=" + route + "&s=" + stopid + "&useShortTitles=true";
							try {
								String display = new RefreshTask().execute(one).get();
				            	new AlertDialog.Builder(getActivity())
				                .setTitle("Predictions")
				                .setMessage(display)
				                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				                    public void onClick(DialogInterface dialog, int which) { 
				                        // continue with delete
				                    }
				                 })
				                 .show();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
		
		
		
		
		
		return thisView;
	}

	public void setStorage(Storage mStorage){
		mystorage = mStorage;
	}


	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 mTheme = CardTheme.Light;
	}



	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			((MainActivity) getActivity()).changeTheme(mTheme, getArguments().getInt("index"));
		}
	}


	private void showToast(String message) {
		if (mToast != null) mToast.cancel();
		mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		mToast.show();
	}


	public static void closeKeyboard(Context c, IBinder windowToken) {
		InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(windowToken, 0);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////

	// Implementation of AsyncTask used to download XML feed from stackoverflow.com.
	public class GetRoutesTask extends AsyncTask<String, Void, ArrayList> {
		@Override
		protected ArrayList doInBackground(String... urls) {

			ArrayList routes = getRoutes(urls[0]);
			return getRoutes(urls[0]);	

		}
	}

	private ArrayList getRoutes(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(100000 /* milliseconds */);
			conn.setConnectTimeout(150000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			try {
				return parse(conn.getInputStream());
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new ArrayList();
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ArrayList();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ArrayList();
		}
	}

	public ArrayList parse(InputStream in) throws XmlPullParserException, IOException {
		try {	
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private ArrayList readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList entries = new ArrayList();

		parser.require(XmlPullParser.START_TAG, null, "body");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("route")) {
				entries.add(parser.getAttributeValue(null, "tag"));
				parser.nextTag();
			} 
		}  
		return entries;
	}


	////////////////////////////////////////////////////////////////////////////////

	// Implementation of AsyncTask used to download XML feed from stackoverflow.com.
	public class GetStopsTask extends AsyncTask<String, Void, Dictionary> {
		@Override
		protected Dictionary doInBackground(String... urls) {

			return getStops(urls[0]);	
		}
	}

	private Dictionary getStops(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(100000 /* milliseconds */);
			conn.setConnectTimeout(150000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			try {
				return parseStops(conn.getInputStream());
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Hashtable();
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new Hashtable();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new Hashtable();
		}
	}

	public Dictionary parseStops(InputStream in) throws XmlPullParserException, IOException {
		try {	
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readStopsFeed(parser);
		} finally {
			in.close();
		}
	}

	private Dictionary readStopsFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		Dictionary entries = new Hashtable();

		parser.require(XmlPullParser.START_TAG, null, "body");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("route")) {
				parser.require(XmlPullParser.START_TAG, null, "route");
				entries = readStopTitles(parser);
				parser.require(XmlPullParser.END_TAG, null, "route");
			}else {
				skip(parser);
			}
		}  
		return entries;
	}

	private Dictionary readStopTitles(XmlPullParser parser) throws XmlPullParserException, IOException {
		Dictionary entries = new Hashtable();
		parser.require(XmlPullParser.START_TAG, null, "route");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			//			Log.d("wefwfwefwewww", name);
			if (name.equals("stop")){
				String mytag = parser.getAttributeValue(null, "tag");
				String mytitle = parser.getAttributeValue(null, "title");
				stopDict.put(mytag,mytitle);
				//mystopids.put(parser.getAttributeValue(null, "title"), parser.getAttributeValue(null, "stopId"));

				if (((Hashtable) mystopids).containsKey(mytitle)){
					if (mystopids.get(mytitle) instanceof ArrayList){}
					else{
						ArrayList x = new ArrayList();
						x.add(mystopids.get(mytitle));
						x.add(mytag);
						mystopids.put(mytitle, x );	
					}
				} else{
					mystopids.put(mytitle, mytag);
				}

				parser.nextTag();
			}else if (name.equals("direction")){
				parser.require(XmlPullParser.START_TAG, null, "direction");
				entries.put(parser.getAttributeValue(null, "title"), readStops(parser));
				parser.require(XmlPullParser.END_TAG, null, "direction");
			}else {
				skip(parser);
			}
		}
		return entries;  
	}

	private ArrayList readStops(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList x = new ArrayList();
		parser.require(XmlPullParser.START_TAG, null, "direction");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("stop")){
				x.add(parser.getAttributeValue(null, "tag"));
				parser.nextTag();
			}
			else {
				skip(parser);
			}
		} 
		return x;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG: 
				depth++;
				break;
			}
		}
	}
	
	
	
	
	///////////////////////////////////////////////////// For Check button response //////////
	
	
	public Dictionary checkParse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return checkReadFeed(parser);
		} finally {
			in.close();
		}
	}

	private Dictionary checkReadFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		Dictionary entries = new Hashtable();

		parser.require(XmlPullParser.START_TAG, null, "body");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("predictions")) {
				parser.require(XmlPullParser.START_TAG, null, "predictions");
				entries.put("routeTitle", parser.getAttributeValue(null, "routeTitle"));
				entries.put("stopTitle", (String) parser.getAttributeValue(null, "stopTitle"));
				entries.put("agencyTitle", (String) parser.getAttributeValue(null, "agencyTitle"));
			} else if (name.equals("direction")){
				parser.require(XmlPullParser.START_TAG, null, "direction");
				Direction dire = new Direction();
				dire.direction = parser.getAttributeValue(null, "title");
				dire.minutes = readEntry(parser);
				int k = 0;
				while (entries.get("directionObject"+Integer.toString(k)) != null){
					k++;
				}
				entries.put("directionObject"+ Integer.toString(k), dire);
				parser.require(XmlPullParser.END_TAG, null, "direction");
			}
			else {
				checkSkip(parser);
			}
		}  
		return entries;
	}

	private String readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
		String mins = "";

		parser.require(XmlPullParser.START_TAG, null, "direction");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("prediction")){
				if (mins != "") mins = mins + ", ";
				mins = mins + parser.getAttributeValue(null, "minutes");
				parser.nextTag();
			}
			else {
				checkSkip(parser);
			}
		}  
		return mins;
	}



	private void checkSkip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	public class Direction {
		public  String minutes;
		public  String direction;

	}

	private Dictionary getDict(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(100000 /* milliseconds */);
			conn.setConnectTimeout(150000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			try {
				return checkParse(conn.getInputStream());
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Hashtable();
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new Hashtable();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new Hashtable();
		}
	}

	
	
	private class RefreshTask extends AsyncTask<String,String,String> {

		protected String doInBackground(String...strings) {
			String content = strings[0];
			Dictionary mydict = getDict(urlbegin + content); //"&a=sf-muni&r=38&stopId=14260&useShortTitles=true"
			String display;
			try{
				display = "Arriving in:   " + ((Direction) mydict.get("directionObject"+Integer.toString(0))).minutes;
			} catch (Exception e){
				display = "No Current Prediction";
			}
			return display;
		}

	
	}

	
	////////////////////////////////////////////////////

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}

}

