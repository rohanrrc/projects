package transit;

import com.afollestad.cardsui.CardTheme;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

 
public class MyWebView extends Fragment {
     

    WebView webview;
    private CardTheme mTheme;
    private Toast mToast;
    private String webviewtempURL = "http://www.nextbus.com/webkit/";
    
    public WebView getWebView(){
    	return this.webview;
    }
     
    
    public MyWebView() {
    }

    public static MyWebView newMyWebView(int index) {
    	MyWebView frag = new MyWebView();
        Bundle b = new Bundle();
        b.putInt("index", index);
        frag.setArguments(b);
        return frag;
    }
    
    public class GeoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // When user clicks a hyperlink, load in the existing WebView
            view.loadUrl(url);
            return true;
        }
        
    }
    
    /**
     * WebChromeClient subclass handles UI-related calls
     * Note: think chrome as in decoration, not the Chrome browser
     */
    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }
     
    }
    

    
//    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View thisView = inflater.inflate(R.layout.web_view, container, false);
        
        webview = (WebView) thisView.findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new GeoWebViewClient());   
        webview.getSettings().setGeolocationEnabled(true);
        webview.setWebChromeClient(new GeoWebChromeClient());
        if(isNetworkConnected()){
        webview.loadUrl("http://www.nextbus.com/webkit/");
        }
    	return thisView;
    }
    

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

  @Override
  public void onPause() {
  	super.onPause();

  	 webviewtempURL = webview.getUrl();
  	webview.loadUrl("about:blank");
  }
  
  @Override
   public void onResume() {
    super.onResume();
    if (!webviewtempURL.equals("") && webviewtempURL != null) {
    	
    	if(isNetworkConnected()){
      webview.loadUrl(webviewtempURL);
    	}
    }
  }
  
  private boolean isNetworkConnected() {
  	  ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
  	 return cm.getActiveNetworkInfo() != null;
    }

        
}

