package foundit.foundit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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

import com.gordonwong.materialsheetfab.MaterialSheetFab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import foundit.foundit.UtilClasses.Fab;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragGestion_Comercio extends Fragment {

    private static final String DEBUG="LogFragGestion_Comercio";
    private MaterialSheetFab materialSheetFab;
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

        ///////////////////
        /*//cargar las ofertas
        LoadMyOfertas t = new LoadMyOfertas();
        t.myFragGestComer=myFrag;
        t.myFragGestComerView=view;
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
        }*/

        Fab fab = view.findViewById(R.id.fab);
        View sheetView = view.findViewById(R.id.fab_sheet);
        View overlay = view.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.background_card);
        int fabColor = getResources().getColor(R.color.colorPrimaryDark);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        TextView tVVerOfertas = view.findViewById(R.id.tVGestion_Comercio_Fav_VerOfertas);
        tVVerOfertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new frag_mis_ofertas()).commit();
            }
        });
        TextView tVAddOferta = view.findViewById(R.id.tVGestion_Comercio_Fav_AnyadirOferta);
        tVAddOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new registro_oferta()).commit();
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
    /*class LoadMyOfertas extends AsyncTask<Uri, String, JSONArray> {
        FragGestion_Comercio myFragGestComer;
        View myFragGestComerView;

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

            //AlertDialog.Builder builder = new AlertDialog.Builder(myFragGestComer.getActivity());
            LayoutInflater inflater = myFragGestComer.getActivity().getLayoutInflater();
            //View actual = inflater.inflate(R.layout.alertdialog_ofertas_activas,null);
            LinearLayout lLAlertDialogOfertasActivas = (LinearLayout) myFragGestComerView.findViewById(R.id.lLAlertDialogOfertasActivas);
            lLAlertDialogOfertasActivas.removeAllViews();


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
                            //dialog.cancel();
                        }
                    });

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            //dialog.show();

        }
    }*/

}
