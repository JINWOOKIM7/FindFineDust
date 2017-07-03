package network.jung.dust;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout; //드로어
    private Context mContext;
    private TextView date_textView, location_textView, level_textView, notice_textView, pm10_textView, pm25_textView, no2_textView, o3_textView, co_textView, now_textView;
    private TextView pm10_textView2, pm25_textView2, no2_textView2, o3_textView2, co_textView2;
    private ImageView imageView, pm10_imageView, pm25_imageView, no2_imageView, o3_imageView, co_imageView;

    /////쓰레드 실행순서 정하려고 선언
    CountDownLatch latch = new CountDownLatch(1);
    CountDownLatch latch2 = new CountDownLatch(1);
    //////

    double latitude; //위도
    double longitude; //경도

    final String GPS_URL1 = "https://apis.daum.net/local/geo/transcoord?apikey=bcd4e6971fc78f3cbba4e7dd558b69c3&fromCoord=WGS84&y=";
    final String GPS_URL2 = "&x=";
    final String GPS_URL3 = "&toCoord=TM&output=xml";

    final String MEASURE_URL1 = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?tmX=";
    final String MEASURE_URL2 = "&tmY=";
    final String MEASURE_URL3 = "&pageNo=1&numOfRows=10&ServiceKey=kBvcS2tSyuOuQjiA5lQVFywun16CnkGRuS7BSx3D2S3SuX1WSLjf5Dm5zWCxUjHbNraPEC9Vj7E7%2FfeWgFr3tg%3D%3D";

    final String ATMOS_URL1 = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?stationName=";
    final String ATMOS_URL2 = "&dataTerm=month&pageNo=1&numOfRows=10&ServiceKey=kBvcS2tSyuOuQjiA5lQVFywun16CnkGRuS7BSx3D2S3SuX1WSLjf5Dm5zWCxUjHbNraPEC9Vj7E7%2FfeWgFr3tg%3D%3D&ver=1.3";

    String getX = null;
    String getY = null;
    String stationName = null;
    Dust dust = null;
    boolean flag2 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        ///////////////////////////////////
        location_textView = (TextView) findViewById(R.id.location_textView);
        date_textView = (TextView) findViewById(R.id.date_textView);
        level_textView = (TextView) findViewById(R.id.level_textView);
        notice_textView = (TextView) findViewById(R.id.notice_textview);
        imageView = (ImageView) findViewById(R.id.level_imageView);
        pm10_imageView = (ImageView) findViewById(R.id.pm10_imageView);
        pm25_imageView = (ImageView) findViewById(R.id.pm25_imageView);
        no2_imageView = (ImageView) findViewById(R.id.no2_imageView);
        o3_imageView = (ImageView) findViewById(R.id.o3_imageView);
        co_imageView = (ImageView) findViewById(R.id.co_imageView);

        pm10_textView = (TextView) findViewById(R.id.pm10_textView);
        pm25_textView = (TextView) findViewById(R.id.pm25_textView);
        no2_textView = (TextView) findViewById(R.id.no2_textView);
        o3_textView = (TextView) findViewById(R.id.o3_textView);
        co_textView = (TextView) findViewById(R.id.co_textView);

        pm10_textView2 = (TextView) findViewById(R.id.pm10_textView2);
        pm25_textView2 = (TextView) findViewById(R.id.pm25_textView2);
        no2_textView2 = (TextView) findViewById(R.id.no2_textView2);
        o3_textView2 = (TextView) findViewById(R.id.o3_textView2);
        co_textView2 = (TextView) findViewById(R.id.co_textView2);
        now_textView = (TextView) findViewById(R.id.now_textView);
        ///////////////////////////////////


        ///////////////////////////////////드로어
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); //메뉴아이콘
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        int colorRed = getResources().getColor(R.color.textColorPrimary);
        mDrawerLayout.setBackgroundColor(colorRed);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.navigation_item_img:
                        Intent intentImage = new Intent(MainActivity.this,ImageActivity.class); //인텐트
                        startActivity(intentImage);
                        break;

                    case R.id.navigation_item_satellite:
                        Intent intentSatellite = new Intent(MainActivity.this,SatelliteActivity.class);
                        startActivity(intentSatellite);
                        break;

                    case R.id.navigation_item_mask:
                        Intent intentMask = new Intent(MainActivity.this,MaskActivity.class);
                        startActivity(intentMask);
                        break;

                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        ///////////////////////////////////드로어

        /////권한
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//권한이 없을때
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //권한요청
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else { //권한있을때
            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location_textView.setText("GPS를 켜주세요");
                location_textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isClickable()) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 1);
                        }
                    }
                });
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener); ///GPS 받아오기
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener); ///

        }


    }//////////end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // 갤러리 사용권한에 대한 콜백을 받음
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ///////////////////////////////////경도위도
                    final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        location_textView.setText("GPS를 켜주세요");
                        location_textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.isClickable()) {
                                    Log.d("GPS", "test");
                                }
                            }

                        });
                    } else {
                        try {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);
                        } catch (SecurityException e) {
                        }
                    }
                    ///////////////////////////////////경도위도
                } else {
                    // 사용자가 권한 동의를 안함
                    // 권한 동의안함 버튼 선택
                    Log.d("GPS", "거절");
                    finish();
                }
                return;
            }
            // 예외케이스
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();  //위도
            longitude = location.getLongitude(); //경도
            Geocoder gCoder = new Geocoder(mContext); //Geocoder 클래스 이용해서 위도,경도로 현재 행정구역을 얻어온다
            try {
                List<Address> addr = gCoder.getFromLocation(latitude, longitude, 1);
                Address a = addr.get(0);
                String address = "(" + a.getLocality() + " " + a.getThoroughfare() + ")";
                location_textView.setText(address); //현재위치를 표시해줌
            } catch (IOException e) {
            }
            //
            if (flag2) {
                DustAsyncTaskGPS dustAsyncTask = new DustAsyncTaskGPS(); //WGS84를 TM좌표로 변환
                dustAsyncTask.execute(GPS_URL1 + latitude + GPS_URL2 + longitude + GPS_URL3);

                ///TM좌표로 변환완료
                DustAsyncTaskMeasure dustAsyncTaskMeasure = new DustAsyncTaskMeasure();
                dustAsyncTaskMeasure.execute(MEASURE_URL1);

                //TM좌표로 가장까운 측정소
                DustAsyncTaskAtmos dustAsyncTaskAtmos = new DustAsyncTaskAtmos();
                dustAsyncTaskAtmos.execute(ATMOS_URL1);
                flag2 = false;
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public class DustAsyncTaskGPS extends AsyncTask<String, Void, String> {

        public String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();    //이곳이 풀파서를 사용하게 하는곳
                factory.setNamespaceAware(true);                                    //이름에 공백도 인식
                XmlPullParser xpp = factory.newPullParser();                            //풀파서 xpp라는 객체 생성
                url = new URL(urls[0]);
                Log.d("GPS", url + "");
                InputStream is = url.openStream();
                xpp.setInput(is, "UTF-8");
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {    //문서의 끝이 아닐때
                    switch (eventType) {
                        case XmlPullParser.START_TAG:    //'<'시작태그를 만났을때
                            String tag = xpp.getName();
                            if (tag.compareTo("result") == 0) { //파서가 result  태그를 만나면 x의 y의 속성 값을 각각 getX,getY에 넣음.
                                getX = xpp.getAttributeValue(null, "x");
                                getY = xpp.getAttributeValue(null, "y");
                            }
                            break;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {

            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) { //첫번째 파싱이 끝나면 latch 감소시킴
            super.onPostExecute(s);
            latch.countDown();
        }
    }

    public class DustAsyncTaskMeasure extends AsyncTask<String, Void, String> {
        public String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            try {
                latch.await(); //여기서 기다리다가 latch가 countDown되면 진행
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();    //이곳이 풀파서를 사용하게 하는곳
                factory.setNamespaceAware(true);                                    //이름에 공백도 인식
                XmlPullParser xpp = factory.newPullParser();                            //풀파서 xpp라는 객체 생성
                url = new URL(urls[0] + getX + MEASURE_URL2 + getY + MEASURE_URL3);
                Log.d("GPS", url + "");
                InputStream is = url.openStream();
                xpp.setInput(is, "UTF-8");
                int eventType = xpp.getEventType();
                boolean flag = true;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:    //'<'시작태그를 만났을때
                            String tag = xpp.getName();
                            if (tag.equals("stationName") && flag) {
                                stationName = xpp.nextText();
                                flag = false;
                            }
                            break;
                    }
                    eventType = xpp.next();
                }

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();

            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            latch2.countDown();

        }

    }

    public class DustAsyncTaskAtmos extends AsyncTask<String, Void, String> {

        public String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            try {
                latch2.await();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();    //이곳이 풀파서를 사용하게 하는곳
                factory.setNamespaceAware(true);                                    //이름에 공백도 인식
                XmlPullParser xpp = factory.newPullParser();
                url = new URL(urls[0] + stationName + ATMOS_URL2);
                Log.d("GPS", url + "");
                InputStream is = url.openStream();
                xpp.setInput(is, "UTF-8");
                int eventType = xpp.getEventType();
                boolean flag = true;

                while (eventType != XmlPullParser.END_DOCUMENT) {    //문서의 끝이 아닐때
                    switch (eventType) {
                        case XmlPullParser.START_TAG:    //'<'시작태그를 만났을때
                            String tag = xpp.getName();
                            if (tag.equals("item") && flag) {
                                dust = new Dust();
                            }
                            if (tag.equals("dataTime") && flag) {
                                dust.setDataTime(xpp.nextText());
                            }
                            if (tag.equals("mangName") && flag) {
                                dust.setMangName(xpp.nextText());
                            }
                            if (tag.equals("so2Value") && flag) {
                                dust.setSo2Value(xpp.nextText());
                            }
                            if (tag.equals("coValue") && flag) {
                                dust.setCoValue(xpp.nextText());
                            }
                            if (tag.equals("o3Value") && flag) {
                                dust.setO3Value(xpp.nextText());
                            }
                            if (tag.equals("no2Value") && flag) {
                                dust.setNo2Value(xpp.nextText());
                            }
                            if (tag.equals("pm10Value") && flag) {
                                dust.setPm10Value(xpp.nextText());
                            }
                            if (tag.equals("pm10Value24") && flag) {
                                dust.setPm10Value24(xpp.nextText());
                            }
                            if (tag.equals("pm25Value") && flag) {
                                dust.setPm25Value(xpp.nextText());
                            }
                            if (tag.equals("pm25Value24") && flag) {
                                dust.setPm25Value24(xpp.nextText());
                            }
                            if (tag.equals("khaiValue") && flag) {
                                dust.setKhaiValue(xpp.nextText());
                            }
                            if (tag.equals("khaiGrade") && flag) {
                                dust.setKhaiGrade(xpp.nextText());
                            }
                            if (tag.equals("so2Grade") && flag) {
                                dust.setSo2Grade(xpp.nextText());
                            }
                            if (tag.equals("coGrade") && flag) {
                                dust.setCoGrade(xpp.nextText());
                            }
                            if (tag.equals("o3Grade") && flag) {
                                dust.setO3Grade(xpp.nextText());
                            }
                            if (tag.equals("no2Grade") && flag) {
                                dust.setNo2Grade(xpp.nextText());
                            }
                            if (tag.equals("pm10Grade") && flag) {
                                dust.setPm10Grade(xpp.nextText());
                            }
                            if (tag.equals("pm25Grade") && flag) {
                                dust.setPm25Grade(xpp.nextText());
                            }
                            if (tag.equals("pm10Grade1h") && flag) {
                                dust.setPm10Grade1h(xpp.nextText());
                            }
                            if (tag.equals("pm25Grade1h") && flag) {
                                dust.setPm25Grade1h(xpp.nextText());
                                flag = false;
                            }
                            break;
                    }
                    eventType = xpp.next(); //다음으로

                }

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }


            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            date_textView.setText(dust.dataTime);
            DrawerLayout mLayout;
            switch (dust.khaiGrade) {
                case "1":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.level1)); //이모티콘
                    level_textView.setText("좋음");
                    notice_textView.setText("나들이를 떠나세요~");
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.level1_NavigationBarColor));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.level1_StatusBarColor));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.level1_ActionBarColor)));
                    mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.level1_ActionBarColor));
                    now_textView.setBackgroundColor(getResources().getColor(R.color.level1_StatusBarColor));
                    break;

                case "2":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.level2));
                    level_textView.setText("보통");
                    notice_textView.setText("보통이지만 주의하세요~");
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.level2_NavigationBarColor));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.level2_StatusBarColor));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.level2_ActionBarColor)));
                    mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.level2_ActionBarColor));
                    now_textView.setBackgroundColor(getResources().getColor(R.color.level2_StatusBarColor));
                    break;

                case "3":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.level3));
                    level_textView.setText("나쁨");
                    notice_textView.setText("밖에서 숨쉬지 마세요~");
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.level3_NavigationBarColor));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.level3_StatusBarColor));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.level3_ActionBarColor)));
                    mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.level3_ActionBarColor));
                    now_textView.setBackgroundColor(getResources().getColor(R.color.level3_StatusBarColor));
                    break;

                case "4":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.level4));
                    level_textView.setText("매우 나쁨");
                    notice_textView.setText("숨쉬지 마세요~");
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.level4_NavigationBarColor));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.level4_StatusBarColor));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.level4_ActionBarColor)));
                    mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.level4_ActionBarColor));
                    now_textView.setBackgroundColor(getResources().getColor(R.color.level4_StatusBarColor));
                    break;
            }

            Log.d("GPS", "미세먼지" + dust.getPm10Value() + ", 등급:" + dust.getPm10Grade());
            switch (dust.getPm10Grade()) {
                case "1":
                    pm10_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level1));
                    pm10_textView.setText("좋음");
                    break;
                case "2":
                    pm10_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level2));
                    pm10_textView.setText("보통");
                    break;
                case "3":
                    pm10_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level3));
                    pm10_textView.setText("나쁨");
                    break;
                case "4":
                    pm10_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level4));
                    pm10_textView.setText("매우 나쁨");
                    break;
            }

            switch (dust.getPm25Grade()) {
                case "1":
                    pm25_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level1));
                    pm25_textView.setText("좋음");
                    break;
                case "2":
                    pm25_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level2));
                    pm25_textView.setText("보통");
                    break;
                case "3":
                    pm25_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level3));
                    pm25_textView.setText("나쁨");
                    break;
                case "4":
                    pm25_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level4));
                    pm25_textView.setText("매우 나쁨");
                    break;
            }


            switch (dust.getNo2Grade()) {
                case "1":
                    no2_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level1));
                    no2_textView.setText("좋음");
                    break;
                case "2":
                    no2_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level2));
                    no2_textView.setText("보통");
                    break;
                case "3":
                    no2_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level3));
                    no2_textView.setText("나쁨");
                    break;
                case "4":
                    no2_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level4));
                    no2_textView.setText("매우 나쁨");
                    break;
            }


            switch (dust.getO3Grade()) {
                case "1":
                    o3_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level1));
                    o3_textView.setText("좋음");
                    break;
                case "2":
                    o3_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level2));
                    o3_textView.setText("보통");
                    break;
                case "3":
                    o3_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level3));
                    o3_textView.setText("나쁨");
                    break;
                case "4":
                    o3_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level4));
                    o3_textView.setText("매우 나쁨");
                    break;
            }


            switch (dust.getCoGrade()) {
                case "1":
                    co_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level1));
                    co_textView.setText("좋음");
                    break;
                case "2":
                    co_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level2));
                    co_textView.setText("보통");
                    break;
                case "3":
                    co_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level3));
                    co_textView.setText("나쁨");
                    break;
                case "4":
                    co_imageView.setImageDrawable(getResources().getDrawable(R.drawable.level4));
                    co_textView.setText("매우 나쁨");
                    break;
            }

            pm10_textView2.setText(dust.getPm10Value() + "㎍/㎥");
            pm25_textView2.setText(dust.getPm25Value() + "㎍/㎥");
            no2_textView2.setText(dust.getNo2Value() + "ppm");
            o3_textView2.setText(dust.getO3Value() + "ppm");
            co_textView2.setText(dust.getCoValue() + "ppm");

        }
    }

}