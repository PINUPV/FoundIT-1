package foundit.foundit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;


public class FragBusqueda extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener{

    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 14;
    private static final int LOCATION_REQUES_CODE = 1;
    private LatLng miPosicion = new LatLng(39.48,-0.34); // Posicion del politecnico


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_frag_busqueda, null, false);

        // Inflate the layout for this fragment
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.fragMap);
        mapFragment.getMapAsync(this);

        //PEDIMOS LOS PERMISOS NECESARIOS
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUES_CODE);
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUES_CODE);
        return view;
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(),"MyLocationButtonCliked",Toast.LENGTH_SHORT).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion,DEFAULT_ZOOM));
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        mMap.setOnMyLocationButtonClickListener(this);
        LocationManager mLocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener );

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);


        } else {

            // Si no tenemos permiso de localizacion mostrar un mensaje
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion,DEFAULT_ZOOM));
        uiSettings.setMyLocationButtonEnabled(true);


    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            miPosicion = new LatLng(location.getLatitude(),location.getLongitude());
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
}
