package transit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.afollestad.cardsui.*;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.silk.fragments.list.SilkListFragment;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.OrderType;




public class CardFragment extends SilkListFragment<CardBase> implements CardHeader.ActionListener, Card.CardMenuListener<CardBase> {

	String urlbegin = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions";
	Storage mystorage;
	ArrayList added = new ArrayList();
	ArrayList addedDirections = new ArrayList();
	CardFragment c = this;
	//	Dictionary dictn = null;
	static CardAdapter myadapter;

	public CardFragment() {
	}

	public static CardFragment newCardFragment(int index) {
		CardFragment frag = new CardFragment();
		Bundle b = new Bundle();
		b.putInt("index", index);
		frag.setArguments(b);
		return frag;
	}

	private CardTheme mTheme;
	private Toast mToast;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			((MainActivity) getActivity()).changeTheme(mTheme, getArguments().getInt("index"));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTheme = CardTheme.Light;
	}

	@Override
	protected String getTitle() {
		return null;
	}

	@Override
	protected ListView createListView() {
		CardListView list = new CardListView(getActivity());
		list.setCardTheme(mTheme);
		return list;
	}

	public void setStorage(Storage mStorage){
		mystorage = mStorage;
	}

	@Override
	protected SilkAdapter initializeAdapter() {

		showToast("Initializing");
		CardAdapter adapter = new CardAdapter(getActivity(), android.R.color.holo_blue_dark).setPopupMenu(R.menu.card_menu, this);


		if(getArguments().getInt("index") == 1){

		}
		else{

			/*
			 * Dict contains:
			 * routeTitle, stopTitle, agencyTitle
			 * directionObject which has  d.direction, d.minutes
			 * 
			 */
			if (!isNetworkConnected()){
				adapter.add(new CardCompressed("No internet connection detected.",""));
			}
		}
		return adapter;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setClipToPadding(false);
		myadapter = (CardAdapter) getAdapter();	
		MainActivity.setInsets(this);

	}

	@Override
	protected void onItemTapped(int index, CardBase item, View view) {
//		        showToast("Card clicked: " + item.getTitle());
	}

	@Override
	public void onHeaderActionClick(CardHeader header) {
//		        showToast(header.getTitle() + " action clicked: " + header.getActionTitle());
	}

	@Override
	public void onMenuItemClick(CardBase card, MenuItem item) {
//		        showToast("Menu item clicked for " + card.getTitle() + ": " + item.getTitle());
	}

	private void showToast(String message) {
		if (mToast != null) mToast.cancel();
		mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		mToast.show();
	}

	@Override
	public int getEmptyText() {
		return 0;
	}


	public Dictionary parse(InputStream in) throws XmlPullParserException, IOException {
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

	private Dictionary readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
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
				skip(parser);
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
				skip(parser);
			}
		}  
		return mins;
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
				return parse(conn.getInputStream());
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
	

	// Implementation of AsyncTask used to download XML feed from stackoverflow.com.
	public class DownloadXmlTask extends AsyncTask<String, Void, Dictionary> {

		Dictionary mydict;
		@Override
		protected Dictionary doInBackground(String... urls) {
			return getDict(urls[0]);

		}

		@Override
		protected void onPostExecute(Dictionary result) {  
			mydict = result;
		}
	}

	private class RefreshTask extends AsyncTask<Void,Dictionary,Integer> {

		@Override
		protected void onPreExecute(){
			int count = myadapter.getCount();
			Log.d("wwefwefwewwwww", Integer.toString(count));
			for(int j = count-1; j >= 0; j--){
				myadapter.remove(j);
			}
			added = new ArrayList();
			addedDirections = new ArrayList();
		}

		protected Integer doInBackground(Void...voids) {
			List<File> files = mystorage.getFiles("MyDirName", OrderType.DATE);
			Collections.sort(files);
			if (((MainActivity) getActivity()).reverseOrder){
				Collections.reverse(files);
			}
			for (int i = 0; i < files.size(); i++){
				Log.d("myfiles", files.get(i).getName());
				String content = mystorage.readTextFile("MyDirName", files.get(i).getName());
				Log.d("myurl", content);
				publishProgress(getDict(urlbegin + content)); //"&a=sf-muni&r=38&stopId=14260&useShortTitles=true"

			}

			return files.size();
		}

		protected void onProgressUpdate(Dictionary...dict) {

			Dictionary mydict = dict[0];
			String rt = (String) mydict.get("routeTitle");
			String at = (String) mydict.get("agencyTitle");
			CardHeader header = new CardHeader(rt);

			header.setAction(at,new CardHeader.ActionListener() {
				@Override
				public void onHeaderActionClick(CardHeader header) {
				}});
			if (!(added.contains(rt+at))){
				myadapter.add(header);
				added.add(rt+at);
			}
			int m = 0;
			while (mydict.get("directionObject"+Integer.toString(m)) != null){
				
				String dt = (String) ((Direction)mydict.get("directionObject"+Integer.toString(m))).direction;
				Log.d("mydetails", rt+"  "+at+"  "+dt);
				if (!(addedDirections.contains(rt+at+dt))){
					myadapter.add(new CardCenteredHeader(dt));
					addedDirections.add(rt+at+dt);
				}
				myadapter.add(new CardCompressed((String) mydict.get("stopTitle") , (String) ((Direction)mydict.get("directionObject"+Integer.toString(m))).minutes));
				m++;
			}
		}
		
		@Override
		protected void onPostExecute(Integer result) {  
				
			if(result==0){
				myadapter.add(new CardCompressed("Add most used stops from the Settings menu", ""));
			}
			
		}
	}
	
	
	  
	  @Override
	   public void onResume() {
	    super.onResume();
		if (isNetworkConnected()){
			showToast("Refreshing");
			new RefreshTask().execute();
		}else{
			showToast("Please check your internet connection");
		}
	  }
	
    private boolean isNetworkConnected() {
  	  ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
  	 return cm.getActiveNetworkInfo() != null;
    }
}
