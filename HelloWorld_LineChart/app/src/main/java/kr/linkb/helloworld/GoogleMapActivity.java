package kr.linkb.helloworld;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GoogleMapActivity extends AppCompatActivity {

    SupportMapFragment mapFragment;
    GoogleMap map;
    class Loc {
        String title; double latitude, longtitude;
        Loc(String title, double latitude, double longtitude){
            this.title = title; this.latitude = latitude; this.longtitude = longtitude;
        }
    }
    ArrayList<Loc> locList = new ArrayList<Loc>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        locList.add(new Loc("광교테크노",37.293472, 127.044252 ) );
//        locList.add(new Loc("버클리대", 37.871942, -122.258540 ) );
//        locList.add(new Loc("뉴욕", 42.690552, -73.777470 ) );
        locList.add(new Loc("중국심천", 22.532056, 114.117302 ) );
//        locList.add(new Loc("상해외국어대",31.277872, 121.482943 ) );

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                for(int i =0; i< locList.size();i++){
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(new LatLng(locList.get(i).latitude, locList.get(i).longtitude));
                    marker.title(locList.get(i).title);
                    map.addMarker(marker);
                }

                startLocationService();
            }
        });
        try{
            MapsInitializer.initialize(this);
        } catch(Exception e) { e.printStackTrace(); }

    }
    class GPSListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            String message = "Current location - lat:"+location.getLatitude()+
                    ", long:"+location.getLongitude();

            Toast.makeText(GoogleMapActivity.this, message, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    }

    private void startLocationService(){
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try{
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                String message = "Last known location - lat:" + location.getLatitude()+
                        " .long:"+ location.getLongitude();
                Toast.makeText(GoogleMapActivity.this, message, Toast.LENGTH_SHORT).show();

//haha add 3 lines
                Log.d("test", "haha location.getLatitude()="+location.getLatitude());
                Log.d("test", "haha location.getLongitude()="+location.getLongitude());
                LatLng address = new LatLng(22.532056, 114.117302 );
//                LatLng address = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d("test", "haha address="+address);
//                CameraPosition cp = new CameraPosition.Builder().target((address )).zoom(15).build();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 5));

                Log.d("test", "haha location.getLongitude()= pass ?"+location.getLongitude());
            }
        }catch(SecurityException e){e.printStackTrace();}

//        startService();
        
        GPSListener gpsListener = new GPSListener();
//        manager.requestLocationUpdates();
        long time = 10000; float dist = 0;
        try{
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, dist, gpsListener);
        }catch(SecurityException e){e.printStackTrace();};

    }
}
