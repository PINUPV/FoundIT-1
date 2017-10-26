package foundit.foundit;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragRegistro_Comercio extends Fragment {

    EditText nombreDelComercioText,direccionText,codPostalText,telfText,emailText,webText, ciudaText;
    Spinner pais,categoria;
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

        nombreDelComercioText = (EditText) view.findViewById(R.id.nombreDelComercioText);
        direccionText = (EditText) view.findViewById(R.id.direccionText);
        codPostalText = (EditText) view.findViewById(R.id.codPostalText);
        telfText = (EditText) view.findViewById(R.id.telfText);
        emailText = (EditText) view.findViewById(R.id.emailText);
        webText = (EditText) view.findViewById(R.id.webText);
        pais = (Spinner)  view.findViewById(R.id.PaisSpinner);
        ciudaText = (EditText) view.findViewById(R.id.ciudadText);

        categoria = (Spinner) view.findViewById(R.id.categoriaSpinner);
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
                p = pais.getSelectedItem().toString();
                cat = categoria.getSelectedItem().toString();
                if(comprobar_nombre(nombreDelComercioText.getText().toString())
                        &&comprobar_categoria(cat)
                        &&comprobar_direccion(direccionText.getText().toString())&&comprobar_pais(p)
                        &&comprobar_telefono(telfText.getText().toString())) {
                    registrarComercio();
                    Intent Main = new Intent(getActivity(), MainFoundit.class);
                    startActivity(Main);
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
        String x = "http://185.137.93.170:8080/comercio.php?Nombre=" + nombreDelComercioText.getText() +
                "&Poblacion=" + ciudaText.getText() + "&Pais=" + p + "&IDCategoria1=" + cat +
                "&provincia=Valencia &Calle="+direccionText.getText()+"&Latitud=39.4657952&Longitud=-0.3315638";

        RegisterTask t = new RegisterTask();
        t.fa = getActivity();
        //Toast.makeText(getActivity(), x, Toast.LENGTH_LONG).show();
        t.execute(x);
        //Intent Main = new Intent(getActivity(), MainFoundit.class);
        // startActivity(Main);
        Toast.makeText(getActivity(), "Comercio registrado correctamente", Toast.LENGTH_LONG).show();
        }
}

class RegisterTaskComercio extends AsyncTask<String, String, JSONObject> {

    FragmentActivity fa;
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
            Toast.makeText(fa,respuesta.getString("mensaje"),Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
