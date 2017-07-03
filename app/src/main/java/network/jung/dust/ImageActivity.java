package network.jung.dust;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class ImageActivity extends AppCompatActivity {
    WebView pm10_webview, pm25_webview, o3_webview, no2_webview, so2_webview, temp_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("미세먼지 예보 이미지");

        pm10_webview = (WebView) findViewById(R.id.pm10_webview);
        pm10_webview.setWebViewClient(new WebViewClient());
        pm10_webview.loadUrl("http://www.webairwatch.com/kaq/modelimg_case4/PM10.27KM.Animation.gif");
        pm10_webview.getSettings().setLoadWithOverviewMode(true);
        pm10_webview.getSettings().setUseWideViewPort(true);

        pm25_webview = (WebView) findViewById(R.id.pm25_webview);
        pm25_webview.setWebViewClient(new WebViewClient());
        pm25_webview.loadUrl("http://www.webairwatch.com/kaq/modelimg_CASE4/PM2_5.27km.animation.gif");
        pm25_webview.getSettings().setLoadWithOverviewMode(true);
        pm25_webview.getSettings().setUseWideViewPort(true);


        o3_webview = (WebView) findViewById(R.id.o3_webview);
        o3_webview.setWebViewClient(new WebViewClient());
        o3_webview.loadUrl("http://www.webairwatch.com/kaq/modelimg_CASE4/O3.27km.animation.gif");
        o3_webview.getSettings().setLoadWithOverviewMode(true);
        o3_webview.getSettings().setUseWideViewPort(true);

        no2_webview = (WebView) findViewById(R.id.no2_webview);
        no2_webview.setWebViewClient(new WebViewClient());
        no2_webview.loadUrl("http://www.webairwatch.com/kaq/modelimg_CASE4/NO2.27km.animation.gif");
        no2_webview.getSettings().setLoadWithOverviewMode(true);
        no2_webview.getSettings().setUseWideViewPort(true);

        so2_webview = (WebView) findViewById(R.id.so2_webview);
        so2_webview.setWebViewClient(new WebViewClient());
        so2_webview.loadUrl("http://www.webairwatch.com/kaq/modelimg_CASE4/SO2.27km.animation.gif");
        so2_webview.getSettings().setLoadWithOverviewMode(true);
        so2_webview.getSettings().setUseWideViewPort(true);

        temp_webview = (WebView) findViewById(R.id.temp_webview);
        temp_webview.setWebViewClient(new WebViewClient());
        temp_webview.loadUrl("http://www.webairwatch.com/kaq/modelimg_CASE4/VECTOR.27km.animation.gif");
        temp_webview.getSettings().setLoadWithOverviewMode(true);
        temp_webview.getSettings().setUseWideViewPort(true);
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
