package gallery.sd.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PointActivity extends AppCompatActivity {
    private static final String TAG = "LOG";
    @InjectView(R.id.start)
    EditText starting;
    @InjectView(R.id.destination)
    EditText destination;
    @InjectView(R.id.rout)
    Button rout;

    @InjectView(R.id.card)
    CardView card;

    @InjectView(R.id.starttxt)
    TextView startTxt;
    @InjectView(R.id.endtxt)
    TextView endTxt;

    public static LatLng startingLatlng, destinationLatlng;


    protected GoogleApiClient mGoogleApiClient;

    /*private static final LatLngBounds BOUNDS_JAMAICA = new LatLngBounds(new LatLng(23.0225, 72.5714),
            new LatLng(21.1702, 72.8311));
*/
    private static final LatLngBounds BOUNDS_JAMAICA = new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
            new LatLng(72.77492067739843, -9.998857788741589));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        ButterKnife.inject(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.startFragment);
        PlaceAutocompleteFragment endFrag = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.destinationFragment);

        setAutoComplete(autocompleteFragment, startTxt);
        setAutoComplete(endFrag, endTxt);

        rout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PointActivity.this, MapsActivity.class));
            }
        });

        starting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setVisibility(View.VISIBLE);
//                setAutoComplete(starting);
            }
        });

        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setVisibility(View.VISIBLE);
//                setAutoComplete(destination);
            }
        });
    }

    public void setAutoComplete(PlaceAutocompleteFragment autocompleteFragment, final TextView edt) {
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                edt.setText(place.getName());
                if (edt == startTxt) {
                    startingLatlng = place.getLatLng();
                } else {
                    destinationLatlng = place.getLatLng();
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }


}


    /* mAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_JAMAICA, null);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.v("Connection Result", connectionResult.toString());
                    }
                })
                .build();

        starting.setAdapter(mAdapter);
        destination.setAdapter(mAdapter);
*/

