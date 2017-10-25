package foundit.foundit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
                Toast.makeText(getActivity(), categoriaBusqueda, Toast.LENGTH_SHORT).show();
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

    int contador = 0;

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
                /*
                Toast.makeText(getActivity(),"mapa desplazado",Toast.LENGTH_SHORT).show();

                try {
                    URL url = new URL("http://185.137.93.170:8080/");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.addRequestProperty("distancia", "500");
                    urlConnection.addRequestProperty("gpslat", Double.toString(mMap.getCameraPosition().target.latitude));
                    urlConnection.addRequestProperty("gpslong", Double.toString(mMap.getCameraPosition().target.longitude));
                    urlConnection.addRequestProperty("busqueda", "");
                    urlConnection.addRequestProperty("filtro", "[]");
                    String received = Util.GetWeb(urlConnection);
                    Toast.makeText(getActivity(),"mapa desplazado (" + (contador++) + ")",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), received, Toast.LENGTH_LONG);
                    try {
                        JSONObject obj = new JSONObject(received);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException ex) {

                } catch (IOException ex) {

                } catch (Exception e) {

                }
                */
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


    public void ButtonOnClic(View v){
        EditText editTex;
        editTex = (EditText)v.findViewById(R.id.eTFragBusquedaeT);
        String categoriaBusqueda = editTex.getText().toString();
        Toast.makeText(getActivity(), categoriaBusqueda, Toast.LENGTH_SHORT).show();

    }

    public void actualizarComercios(JSONArray info){

        //for(int i = 0, i < info.length(); i++){
        //  mMap.addMarker(new MarkerOptions().posi)


    }
}
