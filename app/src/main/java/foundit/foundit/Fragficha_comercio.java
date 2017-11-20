package foundit.foundit;


import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.math.BigDecimal;
import java.sql.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Comentario.Comentario;

/**
 * A simple {@link Fragment} subclass.
 * Created by Albert Pastor 02/11/17
 */
public class Fragficha_comercio extends Fragment {

    Button bt_cerrar_ficha;
    ImageButton bt_like;
    RatingBar rbarTotal, rbarComercio;

    int IDUsuario = 22;
    int IDComercio = 2204;
    String comentario = "";
    Boolean yaValorado = false;
    private Marker marker;
    ListView listRatings;
    ArrayList<comentario> listComent = new ArrayList<comentario>();

    public Fragficha_comercio(){}

    @SuppressLint("ValidFragment")
    public Fragficha_comercio(Marker marker) {
        // Required empty public constructor
        this.marker = marker;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragficha_comercio, container, false);
        listRatings = (ListView) view.findViewById(R.id.list_valoraciones);
        onFichaOpen();

        bt_cerrar_ficha = (Button) view.findViewById(R.id.bt_cerrar_fichaCom);
        bt_cerrar_ficha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent Main = new Intent(getActivity(), MainFoundit.class);
                startActivity(Main);
            }
        });

        rbarComercio = (RatingBar) view.findViewById(R.id.rating_comercio);
        rbarComercio.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(!yaValorado){
                puntuar(rating);}
                else{
                    Toast.makeText(getActivity(), "Ya has valorado este comercio", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_like = (ImageButton) view.findViewById(R.id.bt_like);
        bt_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_like.setImageDrawable(getResources().getDrawable(R.drawable.likelleno));
            }
        });
        return view;
    }

    private void onFichaOpen() {
        //IDComercio = Integer.parseInt(marker.getId().replaceAll("[^0-9]", ""));
        String x = "http://185.137.93.170:8080/sql.php?sql=SELECT%20*%20FROM%20Comentarios%20WHERE%20IDComercio%20=%20"+IDComercio;
        RegisterTaskFicha t = new RegisterTaskFicha();
        t.faF = getActivity();
        try {
            JSONArray respuesta = t.execute(x).get();
            if (respuesta.length() > 0){
            mostrarValoraciones(respuesta);
            }
            if (respuesta.toString().contains(String.valueOf(IDUsuario))){
            yaValorado = true;
            }else{yaValorado = false;}
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void mostrarValoraciones(JSONArray respuesta) throws JSONException {
        int idUsuario, idComercio;
        float rating;

    for (int i = 0; i < respuesta.length(); i++ ){
        JSONObject obj = respuesta.getJSONObject(i);
          idUsuario = obj.getInt("IDUsuario");
        idComercio = obj.getInt("IDComercio");
        rating = BigDecimal.valueOf(obj.getDouble("Rate")).floatValue();
        comentario c = new comentario(idUsuario,idComercio,"",rating);

        listComent.add(c);
        }
        if (!listComent.isEmpty()){
        String[] ratings = new String[listComent.size()];

        int j = 0;
        for(comentario com : listComent){
         String username = recuperarUsuario(com.idUsuario);
        ratings[j] = String.valueOf(com.rating)+" - "+username;
                j++;

        }
            ArrayAdapter<String> adapter =(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ratings));
            listRatings.setAdapter(adapter);
        }
    }

    private String recuperarUsuario(int id) {
        String username = "";
        String x = "http://185.137.93.170:8080/sql.php?sql=SELECT%20Alias%20FROM%20Usuario%20WHERE%20ID%20=%20"+id;
        RegisterTaskFicha t = new RegisterTaskFicha();
        t.faF = getActivity();
        try {
            JSONArray respuesta = t.execute(x).get();
            if (respuesta.length() > 0){
                username = (respuesta.getJSONObject(0)).getString("Alias");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return username;
    }

    private void puntuar(float val ) {
        String x = "http://185.137.93.170:8080/sql.php?sql=INSERT%20INTO%20Comentarios(ID,%20IDUsuario,%20IDComercio,%20IDComentResponse,%20ComentText,%20Rate)" +
                "%20VALUES(null,"+IDUsuario+","+IDComercio+",null,"+comentario+","+val+")";

        RegisterTaskFicha t = new RegisterTaskFicha();
        t.faF = getActivity();
        //Toast.makeText(getActivity(), x, Toast.LENGTH_LONG).show();
        try {

            JSONArray respuesta = t.execute(x).get();

                Toast.makeText(getActivity(), "Comercio puntuado con un: "+val, Toast.LENGTH_SHORT).show();
              // refreshRating();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        

    }

    private void refreshRating() {

    }
}
class RegisterTaskFicha extends AsyncTask<String, String, JSONArray> {

    FragmentActivity faF;
    @Override
    protected JSONArray doInBackground(String... params) {
        String result = Util.GetWeb(Uri.parse(params[0]));

        try {
           return new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            return new JSONArray("{\"resultado\":\"error\",\"mensaje\":\"No se ha podido realizar la accion\"}");
        } catch (JSONException e) {
            return null; // Nunca ocurrirá
        }
    }

    @Override
    protected void onPostExecute(JSONArray respuesta) {
       // try {
          //  Toast.makeText(faF,respuesta.get(0).toString(),Toast.LENGTH_SHORT).show();
       // } catch (JSONException e) {
       //     e.printStackTrace();
       // }
    }
}
 class comentario {
     int idUsuario;
     int idComercio;
     String text;
     float rating;

     public comentario(int idUsu, int idCom, String text, float rating)
     {
         this.idUsuario = idUsu;
         this.idComercio = idCom;
         this.text = text;
         this.rating = rating;
     }

 }