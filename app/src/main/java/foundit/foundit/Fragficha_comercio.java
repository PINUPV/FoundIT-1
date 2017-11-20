package foundit.foundit;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import Usuario.Usuario;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Created by Albert Pastor 02/11/17
 */
public class Fragficha_comercio extends Fragment {

    Button bt_cerrar_ficha;
    ImageButton bt_like;
    RatingBar rbarTotal, rbarComercio;
    Float valoracion;
    int IDUsuario = 22;
    int IDComercio = 2204;
    String comentario = "";
    Boolean yaValorado = false;
    boolean lik = false;
    //private Usuario usu;


    public Fragficha_comercio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragficha_comercio, container, false);

        onFichaOpen();

        bt_cerrar_ficha = (Button) view.findViewById(R.id.bt_cerrar_fichaCom);
        bt_cerrar_ficha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(getActivity(), MainFoundit.class);
                startActivity(Main);
            }
        });
        (ngBar ratingBar, float rating, boolean fromUser) {
            if(!yaValorado){

        rbarComercio = (RatingBar) view.findViewById(R.layout.fragment_fragficha_comercio);
        rbarComercio.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(Rati
                puntuar(rating);}
                else{
                    Toast.makeText(getActivity(), "Ya has valorado este comercio", Toast.LENGTH_LONG);
                }
            }
        });
        bt_like = (ImageButton) view.findViewById(R.id.bt_like);
        bt_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lik == false){
                    bt_like.setImageDrawable(getResources().getDrawable(R.drawable.likelleno));
                    lik = true;
                    String x = "http://185.137.93.170:8080/lista-fav.php?iduser=" + IDUsuario +
                            "&idcomer=" + IDComercio;
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

                } else{
                    bt_like.setImageDrawable(getResources().getDrawable(R.drawable.likevacio));
                    lik = false;
                }
            }
        });
        return view;
    }

    private void onFichaOpen() {
        String x = "http://185.137.93.170:8080/sql.php?sql=185.137.93.170:8080/sql.php?sql=SELECT * FROM Comentarios WHERE IDUsuario = "+IDUsuario+"AND IDComercio = "+IDComercio;
        RegisterTaskFicha t = new RegisterTaskFicha();
        t.faF = getActivity();
        try {
            JSONObject respuesta = t.execute(x).get();
            if (respuesta.toString().contains("ok")){
            yaValorado = true;
            }else{yaValorado = false;}
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
       }

    private void puntuar(float val ) {
        String x = "http://185.137.93.170:8080/sql.php?sql=185.137.93.170:8080/sql.php?sql=INSERT INTO Comentarios(ID, IDUsuario, IDComercio, IDComentResponse, ComentText, Rate) " +
                "VALUES (0,"+IDUsuario+","+IDComercio+",null,"+comentario+","+val+")";

        RegisterTaskFicha t = new RegisterTaskFicha();
        t.faF = getActivity();
        //Toast.makeText(getActivity(), x, Toast.LENGTH_LONG).show();
        try {
            JSONObject respuesta = t.execute(x).get();
            if (respuesta.toString().contains("ok")){
                Toast.makeText(getActivity(), "Comercio puntuado con un: "+val, Toast.LENGTH_LONG).show();
              // refreshRating();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }

    private void refreshRating() {

    }
}
class RegisterTaskFicha extends AsyncTask<String, String, JSONObject> {

    FragmentActivity faF;
    @Override
    protected JSONObject doInBackground(String... params) {
        String result = Util.GetWeb(Uri.parse(params[0]));
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            return new JSONObject("{\"resultado\":\"error\",\"mensaje\":\"No se ha podido realizar la accion\"}");
        } catch (JSONException e) {
            return null; // Nunca ocurrirá
        }
    }

    @Override
    protected void onPostExecute(JSONObject respuesta) {
        try {
            Toast.makeText(faF,respuesta.getString("mensaje"),Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
