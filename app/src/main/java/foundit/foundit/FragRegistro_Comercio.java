package foundit.foundit;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentController;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragRegistro_Comercio extends Fragment {

    EditText nombreDelComercio,direccionComercio,codPostalComercio,telfComercio,emailcomercio,webComercio, ciudadComercio;
    Spinner paisComercio,categoriaComercio;
    Button bRegistraCom, bCancelar;
    String p, cat;

    public FragRegistro_Comercio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_registro_comercio, container, false);

        nombreDelComercio = (EditText) view.findViewById(R.id.nombreDelComercioText);
        direccionComercio = (EditText) view.findViewById(R.id.direccionText);
        codPostalComercio = (EditText) view.findViewById(R.id.codPostalText);
        telfComercio = (EditText) view.findViewById(R.id.telfText);
        emailcomercio = (EditText) view.findViewById(R.id.emailText);
        webComercio = (EditText) view.findViewById(R.id.webText);
        paisComercio = (Spinner)  view.findViewById(R.id.PaisSpinner);
        ciudadComercio = (EditText) view.findViewById(R.id.ciudadText);

        categoriaComercio = (Spinner) view.findViewById(R.id.categoriaSpinner);
        bCancelar = (Button) view.findViewById(R.id.cancelar);
        bCancelar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent Main = new Intent(getActivity(), MainFoundit.class);
        startActivity(Main);
         }
        });


        bRegistraCom = (Button) view.findViewById(R.id.registro);
        bRegistraCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = paisComercio.getSelectedItem().toString();
                cat = categoriaComercio.getSelectedItem().toString();
                if(comprobar_nombre(nombreDelComercio.getText().toString())
                        &&comprobar_categoria(cat)
                        &&comprobar_direccion(direccionComercio.getText().toString())&&comprobar_pais(p)
                        &&comprobar_telefono(telfComercio.getText().toString())) {
                    registrarComercio();
                }

        }});
        return view;
    }


    public boolean comprobar_nombre(String nombre) {
        if (nombre.length() == 0) {
            Toast.makeText(getActivity(), "Rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    public boolean comprobar_direccion(String dir) {
        if (dir.length() == 0) {
            Toast.makeText(getActivity(), "Rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    public boolean comprobar_telefono(String telf) {
        if (telf.length() == 0) {
            Toast.makeText(getActivity(), "Rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    public boolean comprobar_pais(String pais) {
        if (pais.length() == 0) {
            Toast.makeText(getActivity(), "Rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    public boolean comprobar_categoria(String cat) {
        if (cat.length() == 0) {
            Toast.makeText(getActivity(), "Rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }

    public void registrarComercio(){
        //Comprobar si la insercción del comercio ha funcionado correctamente
        //Toast.makeText(getActivity(), "No ha sido posible registrar el comercio", Toast.LENGTH_LONG).show();
        String x = "http://185.137.93.170:8080/registro-comercio.php?Nombre=" + nombreDelComercio.getText() +
                "&Poblacion=" + ciudadComercio.getText() + "&Pais=" + p + "&IDCategoria1=" + 2 +
                "&Provincia=Valencia &Calle="+direccionComercio.getText()+"&Latitud=39.4657952&Longitud=-0.3315638";

        RegisterTaskComercio t = new RegisterTaskComercio();
        t.faC = getActivity();
        //t.execute(x);
        try {
            JSONObject resp = t.execute(x).get();
            if (resp.toString().contains("ok")){
                Toast.makeText(getActivity(), "Comercio registrado", Toast.LENGTH_LONG).show();
                Intent Main = new Intent(getActivity(), MainFoundit.class);
                startActivity(Main);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        }
}

class RegisterTaskComercio extends AsyncTask<String, String, JSONObject> {

    FragmentActivity faC;
    @Override
    protected JSONObject doInBackground(String... params) {
        String result = Util.GetWeb(Uri.parse(params[0]));
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            return new JSONObject("{\"resultado\":\"error\",\"mensaje\":\"No se ha podido realizar la acción\"}");
        } catch (JSONException e) {
            return null; // Nunca ocurrirá
        }
    }

    @Override
    protected void onPostExecute(JSONObject respuesta) {
        try {
            Toast.makeText(faC,respuesta.getString("mensaje"),Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
