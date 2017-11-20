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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Created by Albert Pastor 02/11/17
 */
public class Fragficha_comercio extends Fragment {

    Button bt_cerrar_ficha, bt_u1, bt_u22, bt_u5;
    ImageButton bt_like;
    RatingBar rbarTotal, rbarComercio;

    int idU1 = 1, idU5 = 5, idU22 = 22;
    int IDUsuario = 22;
    int IDComercio = 0;
    String comentario = "_";
    Boolean yaValorado = false;
    String nombreComercio, calleComercio;
    ListView listRatings;
    boolean lik;
    ArrayList<comentario> listComent = new ArrayList<comentario>();
    float valoracionTotal = 0;
    TextView nomComercio;

    public Fragficha_comercio(){}

    @SuppressLint("ValidFragment")
    public Fragficha_comercio(int idCom, String nombre, String calle) {
        this.IDComercio = idCom;
       this.nombreComercio = nombre;
       this.calleComercio = calle;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragficha_comercio, container, false);
        listRatings = (ListView) view.findViewById(R.id.list_valoraciones);
        onFichaOpen();
        nomComercio = (TextView) view.findViewById(R.id.text_nombre_comercio);
        nomComercio.setText(nombreComercio);
        bt_cerrar_ficha = (Button) view.findViewById(R.id.bt_cerrar_fichaCom);
        bt_cerrar_ficha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent Main = new Intent(getActivity(), MainFoundit.class);
                startActivity(Main);
            }
        });
        bt_u1 = (Button) view.findViewById(R.id.bt_u1);
        bt_u1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                IDUsuario = idU1;
                refreshUsuario();
            }
        });
        bt_u5 = (Button) view.findViewById(R.id.bt_u5);
        bt_u5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                IDUsuario = idU5;
                refreshUsuario();
            }
        });
        bt_u22 = (Button) view.findViewById(R.id.bt_u22);
        bt_u22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                IDUsuario = idU22;
                refreshUsuario();
            }
        });
        rbarTotal = (RatingBar) view.findViewById(R.id.rating_total);
        rbarTotal.setRating(valoracionTotal);
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
                            Toast.makeText(getActivity(), "Favorito agregado", Toast.LENGTH_LONG).show();
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

    private void refreshUsuario() {
        String x = "http://185.137.93.170:8080/sql.php?sql=SELECT%20*%20FROM%20Comentarios%20WHERE%20IDComercio%20=%20"+IDComercio+"%20AND%20IDUsuario%20=%20"+IDUsuario;
        RegisterTaskFicha t = new RegisterTaskFicha();
        t.faF = getActivity();
        try {
            JSONArray respuesta = t.execute(x).get();
                if (respuesta.length() > 0) {
                    yaValorado = true;
                } else {
                    yaValorado = false;
                }
            }catch (ExecutionException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void onFichaOpen() {

        String x = "http://185.137.93.170:8080/sql.php?sql=SELECT%20*%20FROM%20Comentarios%20WHERE%20IDComercio%20=%20"+IDComercio;
        String z = "http://185.137.93.170:8080/sql.php?sql=SELECT%20*%20FROM%20Comentarios%20WHERE%20IDComercio%20=%20"+IDComercio+"%20AND%20IDUsuario%20=%20"+IDUsuario;
        RegisterTaskFicha t = new RegisterTaskFicha();
        t.faF = getActivity();
        try {
            JSONArray respuesta = t.execute(x).get();
            if (respuesta.length() > 0) {
                mostrarValoraciones(respuesta);
            }
            RegisterTaskFicha p = new RegisterTaskFicha();
            p.faF = getActivity();
            try {
                JSONArray respUsuario = p.execute(z).get();

                if (respUsuario.length() > 0) {
                    yaValorado = true;
                } else {
                    yaValorado = false;
                }
            }catch (ExecutionException e){

            }
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
        listComent.clear();
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
        valoracionTotal = 0;
        for(comentario com : listComent){
            valoracionTotal = valoracionTotal + com.rating;
         String username = recuperarUsuario(com.idUsuario);
        ratings[j] = "Valoración: "+String.valueOf(com.rating)+" - Usuario: "+username;
                j++;

        }
            valoracionTotal = valoracionTotal/listComent.size();
            ArrayAdapter<String> adapter =(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ratings));
            listRatings.setAdapter(null);
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

    private void puntuar(float val) {
        String x = "http://185.137.93.170:8080/sql.php?sql=INSERT%20INTO%20Comentarios(ID,%20IDUsuario,%20IDComercio,%20IDComentResponse,%20ComentText,%20Rate)" +
                "%20VALUES(null,"+IDUsuario+","+IDComercio+",null,'',"+val+")";

        RegisterTaskFicha t = new RegisterTaskFicha();
        t.faF = getActivity();
        try {

            JSONArray respuesta = t.execute(x).get();

                Toast.makeText(getActivity(), "Comercio puntuado con un: "+val, Toast.LENGTH_SHORT).show();
                refreshRating();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        

    }

    private void refreshRating() {
        onFichaOpen();
        rbarTotal.setRating(valoracionTotal);
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