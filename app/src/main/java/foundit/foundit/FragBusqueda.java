package foundit.foundit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;


public class FragBusqueda extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    public GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 14;
    private static final int LOCATION_REQUES_CODE = 1;
    private LatLng miPosicion = new LatLng(39.48,-0.34); // Posicion del politecnico
    private ImageButton botonFiltros;
    private ImageButton botonLupa;
    private List<String> listaActividades;

    public static LatLng lastMapPosition;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_frag_busqueda, null, false);
        // Inflate the layout for this fragment
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.fragMap);
        mapFragment.getMapAsync(this);

        botonFiltros = (ImageButton)view.findViewById(R.id.busquedaFiltros);

        botonFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recuperarListaActividades();
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //ScrollView ventanaFiltros = (ScrollView) v.findViewById(R.id.ventanaFiltros);
                //LayoutInflater inflater = LayoutInflater.from(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View actual = inflater.inflate(R.layout.layout_filtros,null);
                //actual.setVisibility(View.VISIBLE);

                //Cargar en el linear layout n CheckBox
                final List<String> Filtros=new ArrayList<>();

                LinearLayout layout_Filtros = (LinearLayout) actual.findViewById(R.id.linear_layout_filtros);
                recuperarListaActividades();
                for(String atributo:listaActividades){
                    CheckBox cB = new CheckBox(getActivity());
                    cB.setText(atributo);
                    cB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            String mostrar="";
                            if(isChecked){
                                Filtros.add(buttonView.getText().toString());
                            }else{
                                Filtros.remove(buttonView.getText().toString());
                            }
                            for(String filtro:Filtros){
                                mostrar+=filtro+" ";
                            }

                            //Toast.makeText(getActivity(), mostrar, Toast.LENGTH_SHORT).show();
                        }
                    });
                    layout_Filtros.addView(cB);
                }



                builder.setView(actual);
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        busquedaActual="";
                        int cantFiltros = Filtros.size()-1;
                        for(String filtro:Filtros){
                            /*busquedaActual+="%22"+filtro+"%22";
                            if(cantFiltros>0){
                                busquedaActual+=",";
                            }*/
                            busquedaActual+=filtro;
                        }
                        //Log.v("Prueba filtros",busquedaActual);
                        Util.CargarComerciosEnMapa(mMap,busquedaActual);
                        //Util.CargarComerciosConFiltroEnMapa(mMap, busquedaActual);
                    }
                });

                dialog=builder.create();
                dialog.show();

            }
        });

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

    public LatLng QueuedMarkerTarget = null;

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
        LocationManager mLocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            mMap.setMyLocationEnabled(true);
        }

         else {

            Toast.makeText(getActivity(), "No tienes los permisos necesarios para ejecutar la aplicación", Toast.LENGTH_LONG);
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
                        UpdateLastMapPosition();
                    } catch (Exception e) {
                        Log.e("ERROR1", e.toString());
                    }
                }
            }
        });

        if (QueuedMarkerTarget != null) {
            final CameraUpdate center = CameraUpdateFactory.newLatLng(QueuedMarkerTarget);
            final CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

            mMap.moveCamera(center);
            mMap.moveCamera(zoom);

            QueuedMarkerTarget = null;
        };

        UpdateLastMapPosition();
        //recuperarListaActividades();
    }

    void UpdateLastMapPosition() {
        lastMapPosition = mMap.getCameraPosition().target;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            miPosicion = new LatLng(location.getLatitude(),location.getLongitude());
            UpdateLastMapPosition();
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

    public void recuperarListaActividades(){
        try {
            listaActividades = Util.GetListadoCategorias();
        } catch (Exception e) {
            Log.e("ERROR2", e.toString());
        }
        Collections.sort(listaActividades, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

    Toast.makeText(getActivity(),listaActividades.get(0),Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        mMap.setOnInfoWindowClickListener(this);
        return false;

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        try {
            String nombre = ActualizaMapa.MarkersInfo.get(marker).getName(); // Nombre del negocio
            String calle = ActualizaMapa.MarkersInfo.get(marker).getAddress(); // Calle del negocio
            int idCom = ActualizaMapa.MarkersInfo.get(marker).getId();
            
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new Fragficha_comercio(idCom,nombre,calle)).commit();
        }catch (Exception ne){
            Toast.makeText(getActivity(),"Ficha de comercio no disponible",Toast.LENGTH_SHORT).show();
        }

    }
}
