package foundit.foundit;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class FragRegistro_Usuario extends Fragment {


    public FragRegistro_Usuario(){

    }

    EditText contraseña, nombre, apellidos, usuario, confircontra, correo, poblacion;
    Button bTNRegUserAlta;



    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_registro__usuario, container, false);

        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_frag_registro__usuario);
        contraseña = (EditText) view.findViewById(R.id.text_contraseña);
        nombre = (EditText) view.findViewById(R.id.text_nombre);
        apellidos = (EditText) view.findViewById(R.id.text_apellidos);
        usuario = (EditText) view.findViewById(R.id.text_usuario);
        confircontra = (EditText) view.findViewById(R.id.text_confircontra);
        correo = (EditText) view.findViewById(R.id.text_correo);
        poblacion = (EditText) view.findViewById(R.id.text_poblacion);
        bTNRegUserAlta=(Button) view.findViewById(R.id.bTNRegUserAlta);
        bTNRegUserAlta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comprobar_nombre(nombre.getText().toString()) && comprobar_apellidos(apellidos.getText().toString()) &&
                        comprobar_usuario(usuario.getText().toString()) &&
                        comprobar_contraseña(contraseña.getText().toString()) &&
                        comprobar_confircontra(contraseña.getText().toString(), confircontra.getText().toString()) &&
                        comprobar_correo(correo.getText().toString())) {
                   String x = "http://185.137.93.170:8080/registro-usuario.php?alias=" + usuario.getText() +
                           "&email=" + correo.getText() + "&pass=" + contraseña.getText() + "&poblacion=" + poblacion.getText() +
                           "&provincia=Valencia&pais=españistan&cp=45065&telefono=908765433";

                    RegisterTask t = new RegisterTask();
                    t.fa = getActivity();
                    //Toast.makeText(getActivity(), x, Toast.LENGTH_LONG).show();
                    try {
                        JSONObject respuesta = t.execute(x).get();
                        if (respuesta.toString().contains("ok")){
                            Toast.makeText(getActivity(), "Usuario registrado", Toast.LENGTH_LONG).show();
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
        });


        

        return view;
    }
    public boolean comprobar_contraseña(String contra){
        if(contra.length() < 6) {
            Toast.makeText(getActivity(), "La contraseña debe tener 6 dígitos", Toast.LENGTH_LONG).show();
            return false;
        }
        else return true;
    }
    public boolean comprobar_nombre(String nombre) {
        if (nombre.length() == 0) {
            Toast.makeText(getActivity(), "Rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    public boolean comprobar_apellidos(String apellidos) {
        if (apellidos.length() == 0) {
            Toast.makeText(getActivity(), "Rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    public boolean comprobar_usuario(String usuario) {
        if (usuario.length() == 0) {
            Toast.makeText(getActivity(), "Rellenar todos los campos obligatorios", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    public boolean comprobar_confircontra(String contra1, String contra2) {
        if (!contra1.equals(contra2)) {
            Toast.makeText(getActivity(), "Las contraseñas deben ser iguales", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    public boolean comprobar_correo(String correo) {
        if (!TextUtils.isEmpty(correo) && android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Introduce una dirección de correo válida", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}

class RegisterTask extends AsyncTask<String, String, JSONObject> {

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