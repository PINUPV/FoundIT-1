package layout;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import foundit.foundit.FragBusqueda;
import foundit.foundit.MainFoundit;
import foundit.foundit.R;
import foundit.foundit.Util;

import static android.util.Base64.DEFAULT;
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

    ArrayList<Ofertita> ofertas = new ArrayList<>();

    static final String[] filtro = new String[] {"Distancia ↓","Distancia ↑","Valoración ↓","Valoración ↑"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.inflater = inflater;
        this.container = container;
        view = inflater.inflate(R.layout.fragment_frag_ofertas, null, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerOfer);
        ArrayAdapter<String> adapter =(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, filtro));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                OrdenaBotones(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });



        LoadOfertas t = new LoadOfertas();
        t.fa = getActivity();
        t.fo = this;
        try {
            Uri uri = new Uri.Builder().scheme("http")
                    .encodedAuthority("185.137.93.170:8080")
                    .path("sql.php")
                    .appendQueryParameter("sql", "SELECT Ofertas.Nombre as oferta_nombre, Ofertas.*, ComercPremium.*, Comercio.*, User_Rates.* FROM Ofertas " +
                            "LEFT JOIN ComercPremium ON Ofertas.Comercio = ComercPremium.IDComer " +
                            "LEFT JOIN Comercio ON Ofertas.Comercio = Comercio.ID " +
                            "LEFT JOIN (SELECT IDComercio, AVG(Rate) FROM Comentarios GROUP BY IDComercio) AS User_Rates ON Ofertas.Comercio = User_Rates.IDComercio ")
                    .build();
            t.execute(uri);
        } catch (Exception e) { }

        return view;
    }

    void OrdenaBotones(final int modo) {
        LinearLayout buttonHolder = (LinearLayout) getView().findViewById(R.id.botonesOfertaContainer);

        // Quitar botones
        for (Ofertita o : ofertas) {
            buttonHolder.removeView(o.boton);
        }
        // Ordenar
        Collections.sort(ofertas, new Comparator<Ofertita>() {
            @Override
            public int compare(Ofertita o1, Ofertita o2) {
                switch (modo) {
                    case 0:
                        return Double.compare(o1.distancia, o2.distancia);
                    case 1:
                        return Double.compare(o2.distancia, o1.distancia);
                    case 2:
                        return Double.compare(o1.score, o2.score);
                    case 3:
                        return Double.compare(o2.score, o1.score);
                }
                return 0;
            }
        });

        // Reañadir botones
        for (Ofertita o : ofertas) {
            buttonHolder.addView(o.boton);
        }
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

            for (int i = 0; i < respuesta.length(); i++) {
                try {
                    final JSONObject obj = respuesta.getJSONObject(i);

                    final Ofertita o = new Ofertita();
                    o.hasta = dateFormatGet.parse(obj.getString("fechaValidez"));

                    // La oferta es antigua, pasar de largo
                    if (new Date().after(o.hasta)) continue;

                    o.nombreNegocio = obj.getString("Nombre");
                    o.nombreOferta = obj.getString("oferta_nombre");
                    o.posicion = new LatLng(obj.getDouble("Latitud"), obj.getDouble("Longitud"));
                    o.distancia = Util.distance(o.posicion.latitude, o.posicion.longitude,
                            FragBusqueda.lastMapPosition.latitude, FragBusqueda.lastMapPosition.longitude);
                    try {
                        o.score = Double.parseDouble(obj.getString("AVG(Rate)"));
                    } catch (Exception e) {
                        o.score = 0;
                    }
                    o.SetImage(obj.getString("imagenOferta"));

                    ofertas.add(o);

                    Button yourButton = new Button(fo.getActivity());
                    yourButton.setLayoutParams(botonBase.getLayoutParams());
                    yourButton.setText(o.nombreNegocio + "\nHasta el " + o.GetHasta());
                    yourButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initiatePopupWindow(v, o);
                        }
                    });

                    buttonHolder.addView(yourButton);
                    o.boton = yourButton;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            OrdenaBotones(0);
        }
    }

    private void initiatePopupWindow(View v, final Ofertita o) {
        try {
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.oferta_popup, null);
            // create a 300px width and 470px height PopupWindow
            final PopupWindow pw = new PopupWindow(layout,
                    (int) (view.getWidth() * 0.8),
                    600,
                    true);
            // display the popup in the center
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);

            ((TextView) layout.findViewById(R.id.oferta_nombre)).setText(o.nombreOferta);
            ((TextView) layout.findViewById(R.id.oferta_nombre_negocio)).setText(o.nombreNegocio);
            ((TextView) layout.findViewById(R.id.oferta_fecha)).setText(o.GetHasta());
            ((TextView) layout.findViewById(R.id.oferta_distancia)).setText(String.format ("%.3f", o.distancia / 1000) + " km");
            if (o.imagen != null) {
                ((ImageView) layout.findViewById(R.id.ofera_popup_imagen)).setImageBitmap(o.imagen);
            }
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

                    fb.QueuedMarkerTarget = o.posicion;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class Ofertita {
        final SimpleDateFormat dateFormatSet = new SimpleDateFormat("dd/MM/yyyy");

        public Date hasta;
        public String nombreNegocio;
        public String nombreOferta;
        public LatLng posicion;
        public double distancia;
        public double score;
        public Button boton;
        public Bitmap imagen = null;

        public String GetHasta() {
            return dateFormatSet.format(hasta);
        }

        public void SetImage(String b64Imagen) {
            if (b64Imagen != null && b64Imagen != "") {
                byte[] data = Base64.decode(b64Imagen, DEFAULT);
                imagen = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
        }
    }
}
