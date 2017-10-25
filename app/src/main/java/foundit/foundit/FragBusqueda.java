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
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class FragBusqueda extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener{

    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 14;
    private static final int LOCATION_REQUES_CODE = 1;
    private LatLng miPosicion = new LatLng(39.48,-0.34); // Posicion del politecnico
    private Button botonPrueba;
    private ImageButton botonLupa;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_frag_busqueda, null, false);
        // Inflate the layout for this fragment
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.fragMap);
        mapFragment.getMapAsync(this);

        /*botonPrueba = (Button)view.findViewById(R.id.botonPrueba);

        botonPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Debug","Hola");
            }
        });*/

        botonLupa = (ImageButton)view.findViewById(R.id.imageButton2);

        botonLupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTex;
                editTex = (EditText) getActivity().findViewById(R.id.eTFragBusquedaeT);
                String categoriaBusqueda = editTex.getText().toString();
                if(categoriaBusqueda.length()< 100 && esAlfaNumerica(categoriaBusqueda)){
                    Toast.makeText(getActivity(), categoriaBusqueda, Toast.LENGTH_SHORT).show();
                    busquedaActual = categoriaBusqueda;
                    Util.CargarComerciosEnMapa(mMap, busquedaActual);
                }
                else
                    Toast.makeText(getActivity(), "la palabra clave introducida no es valida", Toast.LENGTH_SHORT).show();
            }
        });

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

    boolean esperandoAMapaIdle = true;
    JSONArray ultimaBusqueda;

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

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                esperandoAMapaIdle = true;
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (esperandoAMapaIdle) {
                    Log.w("STATE", "mapa desplazado terminado");

                    try {
                        Util.CargarComerciosEnMapa(mMap, busquedaActual);
                    } catch (Exception e) {
                        Log.e("ERROR1", e.toString());
                    }
                }
            }
        });
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

    String busquedaActual = "";
    public void ButtonOnClic(View v){
        EditText editTex;
        editTex = (EditText)v.findViewById(R.id.eTFragBusquedaeT);
        String categoriaBusqueda = editTex.getText().toString();
    }

    public boolean esAlfaNumerica(final String cadena) {
        for(int i = 0; i < cadena.length(); ++i) {
            char caracter = cadena.charAt(i);

            if(!Character.isLetterOrDigit(caracter)) {
                return false;
            }
        }
        return true;
    }

    public void actualizarComercios(JSONArray info){

        //for(int i = 0, i < info.length(); i++){
        //  mMap.addMarker(new MarkerOptions().posi)


    }
}
