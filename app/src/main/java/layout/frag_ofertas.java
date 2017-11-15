package layout;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_frag_ofertas, null, false);
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

            for (int i = 0; i < respuesta.length(); i++) {
                try {
                    JSONObject obj = respuesta.getJSONObject(i);

                    //assuming you have a friendsView object that is some sort of Layout.
                    Button yourButton = new Button(fo.getActivity());
                    yourButton.setLayoutParams(botonBase.getLayoutParams());

                    yourButton.setText(obj.getString("Nombre"));
                    //do stuff like add text and listeners.

                    buttonHolder.addView(yourButton);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
