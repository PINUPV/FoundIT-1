package foundit.foundit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.JsonReader;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Busqueda extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener
{

    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 14;
    private static final int LOCATION_REQUES_CODE = 1;
    private LatLng miPosicion = new LatLng(39.48,-0.34); // Posicion del politecnico



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //PEDIMOS LOS PERMISOS NECESARIOS
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUES_CODE);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUES_CODE);
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
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        mMap.setOnMyLocationButtonClickListener(this);
        LocationManager mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);


        } else {

           // Si no tenemos permiso de localizacion mostrar un mensaje
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion,DEFAULT_ZOOM));
        uiSettings.setMyLocationButtonEnabled(true);

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this,"MyLocationButtonCliked",Toast.LENGTH_SHORT).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion,DEFAULT_ZOOM));
        return false;
    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            miPosicion = new LatLng(location.getLatitude(),location.getLongitude());

            try {
                URL url = new URL("http://185.137.93.170:8080/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("distancia", "500");
                urlConnection.addRequestProperty("gpslat", Double.toString(location.getLatitude()));
                urlConnection.addRequestProperty("gpslong", Double.toString(location.getLongitude()));
                urlConnection.addRequestProperty("busqueda", "");
                urlConnection.addRequestProperty("filtro", "[]");
                String received = Util.GetWeb(urlConnection);
                Toast.makeText(getApplicationContext(), received, Toast.LENGTH_LONG);
                try {
                    JSONObject obj = new JSONObject(received);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException ex) {

            } catch (IOException ex) {

            }
        }



        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(this,"GPS desactivado",Toast.LENGTH_SHORT);

        }
    };

    public void ButtonOnClic(View v){
        Toast.makeText(this, "Boton Pulsado", Toast.LENGTH_SHORT).show();

    }

}


