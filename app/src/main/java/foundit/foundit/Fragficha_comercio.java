package foundit.foundit;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
    private Marker marker;

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
                    Toast.makeText(getActivity(), "Ya has valorado este comercio", Toast.LENGTH_LONG);
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
        String x = "http://185.137.93.170:8080/sql.php?sql=SELECT%20*%20FROM%20Comentarios%20WHERE%20IDUsuario%20=%2022%20AND%20IDComercio%20=%202204";
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
        String x = "http://185.137.93.170:8080/sql.php?sql=INSERT INTO Comentarios(ID, IDUsuario, IDComercio, IDComentResponse, ComentText, Rate) " +
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
