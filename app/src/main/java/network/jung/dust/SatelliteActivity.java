package network.jung.dust;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.InvocationTargetException;


public class SatelliteActivity extends AppCompatActivity {
    WebView webView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satellite);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("미세먼지 위성");

        webView = (WebView) findViewById(R.id.webview_test);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://earth.nullschool.net/#current/particulates/surface/level/overlay=pm10/orthographic=-233.85,35.59,1500/loc=127.304,36.832");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(webView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        cookieSyncManager.sync();
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            try {
                Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null).invoke(webView, (Object[]) null);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            } catch (NoSuchMethodException nsme) {
                nsme.printStackTrace();
            } catch (InvocationTargetException ite) {
                ite.printStackTrace();
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            }
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
