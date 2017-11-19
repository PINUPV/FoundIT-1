package layout;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import foundit.foundit.FragBusqueda;
import foundit.foundit.MainFoundit;
import foundit.foundit.R;
import foundit.foundit.Util;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class frag_ofertas extends Fragment {


    public frag_ofertas() {
        // Required empty public constructor
    }

    LayoutInflater inflater;
    ViewGroup container;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.inflater = inflater;
        this.container = container;
        view = inflater.inflate(R.layout.fragment_frag_ofertas, null, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerOfer);
        String[] filtro = new String[] {"Distancia ↓","Distancia ↑","Valoración ↓","Valoración ↑"};
        ArrayAdapter<String> adapter =(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, filtro));
        spinner.setAdapter(adapter);


        LoadOfertas t = new LoadOfertas();
        t.fa = getActivity();
        t.fo = this;
        try {
            Uri uri = new Uri.Builder().scheme("http")
                    .encodedAuthority("185.137.93.170:8080")
                    .path("sql.php")
                    .appendQueryParameter("sql", "SELECT * FROM Ofertas " +
                            "LEFT JOIN ComercPremium ON Ofertas.Comercio = ComercPremium.IDComer " +
                            "LEFT JOIN Comercio ON Ofertas.Comercio = Comercio.ID")
                    .build();
            t.execute(uri);
        } catch (Exception e) { }

        return view;
    }

    class LoadOfertas extends AsyncTask<Uri, String, JSONArray> {

        FragmentActivity fa;
        frag_ofertas fo;

        @Override
        protected JSONArray doInBackground(Uri... params) {
            String result = Util.GetWeb(params[0]);
            try {
                return new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                return new JSONArray("[]");
            } catch (JSONException e) {
                return null; // Nunca ocurrirá
            }
        }

        @Override
        protected void onPostExecute(JSONArray respuesta) {
            Button botonBase = (Button) fo.getView().findViewById(R.id.ofertaBotonBase);
            LinearLayout buttonHolder = (LinearLayout) fo.getView().findViewById(R.id.botonesOfertaContainer);
            buttonHolder.removeView(botonBase);

            SimpleDateFormat dateFormatGet = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormatSet = new SimpleDateFormat("dd/MM/yyyy");

            for (int i = 0; i < respuesta.length(); i++) {
                try {
                    final JSONObject obj = respuesta.getJSONObject(i);

                    //assuming you have a friendsView object that is some sort of Layout.
                    Button yourButton = new Button(fo.getActivity());
                    yourButton.setLayoutParams(botonBase.getLayoutParams());

                    Date d;
                    try {
                        d = dateFormatGet.parse(obj.getString("fechaValidez"));

                        final double distancia = distance(obj.getDouble("Latitud"), obj.getDouble("Longitud"),
                                FragBusqueda.lastMapPosition.latitude, FragBusqueda.lastMapPosition.longitude);

                        // La oferta es antigua, pasar de largo
                        if (new Date().after(d)) continue;

                        final String hastaEl = dateFormatSet.format(d);

                        String text = obj.getString("Nombre") + "\n" +
                                "Hasta el " + hastaEl;
                        yourButton.setText(text);
                        //do stuff like add text and listeners.

                        yourButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initiatePopupWindow(v, obj, hastaEl, distancia);
                            }
                        });

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }




                    buttonHolder.addView(yourButton);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initiatePopupWindow(View v, final JSONObject obj, String hastaEl, double distanciaMetros) {
        try {
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.oferta_popup, null);
            // create a 300px width and 470px height PopupWindow
            final PopupWindow pw = new PopupWindow(layout,
                    (int) (view.getWidth() * 0.8),
                    450,
                    true);
            // display the popup in the center
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);

            ((TextView) layout.findViewById(R.id.oferta_nombre_comercio)).setText(obj.getString("Nombre"));
            ((TextView) layout.findViewById(R.id.oferta_fecha)).setText(hastaEl);
            ((TextView) layout.findViewById(R.id.oferta_distancia)).setText(String.format ("%.3f", distanciaMetros / 1000) + " km");
            ((Button) layout.findViewById(R.id.oferta_popup_cerrar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });
            ((Button) layout.findViewById(R.id.oferta_popup_irMapa)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                    FragmentManager fragmentManager = MainFoundit.fragmentManager;
                    final FragBusqueda fb = new FragBusqueda();
                    fragmentManager.beginTransaction().replace(R.id.ContainFoundit, fb).commit();

                    try {
                        fb.QueuedMarkerTarget = new LatLng(obj.getDouble("Latitud"), obj.getDouble("Longitud"));
                        //Util.CargarComerciosEnMapa(fb.mMap, "");
                        //marker.showInfoWindow();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // https://stackoverflow.com/questions/8832071/how-can-i-get-the-distance-between-two-point-by-latlng
    public float distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }
}
