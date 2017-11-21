package foundit.foundit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragGestion_Comercio extends Fragment {

    private static Button bTNOfertas;
    private static final String DEBUG="LogFragGestion_Comercio";
    private FragGestion_Comercio myFrag=this;
    public FragGestion_Comercio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_gestion__comercio, container, false);
        LoadMyComercio c = new LoadMyComercio();
        c.myFragGestComer=myFrag;
        try{
            Uri uri = new Uri.Builder().scheme("http")
                    .encodedAuthority("185.137.93.170:8080")
                    .path("sql.php")
                    .appendQueryParameter("sql", "SELECT Nombre, Poblacion FROM Comercio WHERE ID = 22")
                    .build();
            Log.v(DEBUG,uri.toString());
            c.execute(uri);
        }catch (Exception e){
            e.printStackTrace();
        }

        bTNOfertas=(Button) view.findViewById(R.id.bTNGestion_Comercio_VerOfertas);
        bTNOfertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cargar las ofertas
                LoadMyOfertas t = new LoadMyOfertas();
                t.myFragGestComer=myFrag;
                try {
                    Uri uri = new Uri.Builder().scheme("http")
                            .encodedAuthority("185.137.93.170:8080")
                            .path("sql.php")
                            .appendQueryParameter("sql", "SELECT ID, Nombre, fechaValidez FROM Ofertas WHERE Comercio=22")
                            .build();
                    Log.v(DEBUG,uri.toString());
                    t.execute(uri);
                } catch (Exception e) { }


            }
        });





        return view;
    }

    class LoadMyComercio extends AsyncTask<Uri, String, JSONArray> {
        FragGestion_Comercio myFragGestComer;

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
            EditText NombreComer=(EditText) myFragGestComer.getView().findViewById(R.id.eTGestion_Comercio_NombreComer);
            EditText PoblacionComer=(EditText) myFragGestComer.getView().findViewById(R.id.eTGestion_Comercio_PoblacionComer);
            try {
                JSONObject RespComer = Respuesta.getJSONObject(0);
                NombreComer.setText(RespComer.getString("Nombre"));
                PoblacionComer.setText(RespComer.getString("Poblacion"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v(DEBUG,Respuesta.toString());
        }
    }
    class LoadMyOfertas extends AsyncTask<Uri, String, JSONArray> {
        FragGestion_Comercio myFragGestComer;
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
        protected void onPostExecute(JSONArray Respuesta){
            Log.v(DEBUG,Respuesta.toString());

            AlertDialog.Builder builder = new AlertDialog.Builder(myFragGestComer.getActivity());
            LayoutInflater inflater = myFragGestComer.getActivity().getLayoutInflater();
            View actual = inflater.inflate(R.layout.alertdialog_ofertas_activas,null);
            LinearLayout lLAlertDialogOfertasActivas = (LinearLayout) actual.findViewById(R.id.lLAlertDialogOfertasActivas);
            lLAlertDialogOfertasActivas.removeAllViews();


            builder.setView(actual)
                    .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentManager fragmentManager = myFragGestComer.getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new registro_oferta()).commit();
                        }
                    });

            final AlertDialog dialog=builder.create();
            try{
                for(int i = 0; i<Respuesta.length();i++){
                    View viewOfertas = inflater.inflate(R.layout.view_oferta,null);
                    LinearLayout LinearOferta = (LinearLayout) viewOfertas.findViewById(R.id.lLview_Oferta_MuestraOferta);

                    TextView nombreOferta = (TextView) LinearOferta.findViewById(R.id.tVView_Oferta_NombreOferta);
                    TextView idOferta = (TextView) LinearOferta.findViewById(R.id.tVView_Oferta_ID);
                    TextView fechaOferta = (TextView) LinearOferta.findViewById(R.id.tVView_Oferta_FechaOferta);
                    JSONObject oferta = Respuesta.getJSONObject(i);
                    nombreOferta.setText(oferta.getString("Nombre"));
                    idOferta.setText("ID: "+oferta.getString("ID"));
                    fechaOferta.setText(oferta.getString("fechaValidez"));
                    lLAlertDialogOfertasActivas.addView(LinearOferta);
                    LinearOferta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager fragmentManager = myFragGestComer.getActivity().getSupportFragmentManager();
                            registro_oferta view = new registro_oferta();
                            Bundle args = new Bundle();
                            args.putString("ID", ((TextView) v.findViewById(R.id.tVView_Oferta_ID)).getText().toString().split(" ")[1]);
                            view.setArguments(args);
                            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, view).commit();
                            dialog.cancel();
                        }
                    });

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            dialog.show();

        }
    }

}
