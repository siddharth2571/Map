package gallery.sd.map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //    @InjectView(R.id.distance)
    public static TextView distance;
    //    @InjectView(R.id.duration)
    public static TextView duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        ButterKnife.inject(this);
        distance = (TextView) findViewById(R.id.distance);
        duration = (TextView) findViewById(R.id.duration);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(23.0225, 72.5714);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Ahmedabad"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 9.0f));

        if (mMap != null) {
            CustomMapPathDraw drawPoli = new CustomMapPathDraw(mMap, PointActivity.startingLatlng, PointActivity.destinationLatlng);
            drawPoli.drawMarkerAtPoint(true);
        } else {
            Toast.makeText(getApplicationContext(), "Please Wait", Toast.LENGTH_SHORT).show();
        }
    }
}
