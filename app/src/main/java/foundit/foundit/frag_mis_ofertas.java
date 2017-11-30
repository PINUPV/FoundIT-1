package foundit.foundit;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class frag_mis_ofertas extends Fragment {

    private static final String DEBUG="Logfrag_mis_ofertas";
    private frag_mis_ofertas myFrag=this;
    public frag_mis_ofertas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_mis_ofertas, container, false);
        LoadMyOfertas t = new LoadMyOfertas();
        t.myFragMisOfertas=myFrag;
        t.myFragMisOfertasView=view;
        try {
            Uri uri = new Uri.Builder().scheme("http")
                    .encodedAuthority("185.137.93.170:8080")
                    .path("sql.php")
                    .appendQueryParameter("sql", "SELECT ID, Nombre, fechaValidez FROM Ofertas WHERE Comercio=22")
                    .build();
            Log.v(DEBUG,uri.toString());
            t.execute(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }
    class LoadMyOfertas extends AsyncTask<Uri, String, JSONArray> {
        frag_mis_ofertas myFragMisOfertas;
        View myFragMisOfertasView;

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
        protected void onPostExecute(JSONArray Respuesta) {
            Log.v(DEBUG, Respuesta.toString());

            LayoutInflater inflater = myFragMisOfertas.getActivity().getLayoutInflater();
            LinearLayout lLAlertDialogOfertasActivas =  myFragMisOfertasView.findViewById(R.id.lLAlertDialogOfertasActivas);
            lLAlertDialogOfertasActivas.removeAllViews();

            try {
                for (int i = 0; i < Respuesta.length(); i++) {
                    View viewOfertas = inflater.inflate(R.layout.view_oferta, null);
                    LinearLayout LinearOferta = viewOfertas.findViewById(R.id.lLview_Oferta_MuestraOferta);

                    TextView nombreOferta = LinearOferta.findViewById(R.id.tVView_Oferta_NombreOferta);
                    TextView idOferta = LinearOferta.findViewById(R.id.tVView_Oferta_ID);
                    TextView fechaOferta = LinearOferta.findViewById(R.id.tVView_Oferta_FechaOferta);
                    JSONObject oferta = Respuesta.getJSONObject(i);
                    nombreOferta.setText(oferta.getString("Nombre"));
                    idOferta.setText("ID: " + oferta.getString("ID"));
                    fechaOferta.setText(oferta.getString("fechaValidez"));
                    lLAlertDialogOfertasActivas.addView(LinearOferta);
                    LinearOferta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager fragmentManager = myFragMisOfertas.getActivity().getSupportFragmentManager();
                            registro_oferta view = new registro_oferta();
                            Bundle args = new Bundle();
                            args.putString("ID", ((TextView) v.findViewById(R.id.tVView_Oferta_ID)).getText().toString().split(" ")[1]);
                            view.setArguments(args);
                            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, view).commit();
                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
