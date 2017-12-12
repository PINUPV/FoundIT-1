package foundit.foundit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;


/**
 *
 * Use the {@link registro_oferta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registro_oferta extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters
    public static final int SELECT_FILE = 1 ;
    private  String ID = "";
    EditText nombreOferta, fecha,descripcion;
    ImageView fotoOferta;
    Button subir_foto, aceptar, eliminar;
    DatePickerDialog.OnDateSetListener DateSetListener;
    String elNombre, fechaOf, descr, fotoOf;

    FragmentActivity fa;


    public registro_oferta() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment registro_oferta.
     */
    // TODO: Rename and change types and number of parameters
    public static registro_oferta newInstance() {
        registro_oferta fragment = new registro_oferta();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_registro_oferta, container, false);
        nombreOferta = (EditText) view.findViewById(R.id.nombre_oferta);

        fa = getActivity();

        fotoOferta = (ImageView) view.findViewById(R.id.oferta_image);

        aceptar = (Button) view.findViewById(R.id.publicar);
        eliminar = (Button) view.findViewById(R.id.Eliminar);
        subir_foto = (Button) view.findViewById(R.id.subir_imagen);
        fecha = (EditText) view.findViewById(R.id.fecha);
        descripcion = (EditText) view.findViewById(R.id.descripcion);

        if(getID()){
            rellenar(ID);
            nombreOferta.setText(elNombre);
            fecha.setText(fechaOf);
            descripcion.setText(descr);
            if(!fotoOf.isEmpty()){
                byte [] encodeByte=Base64.decode(fotoOf,Base64.DEFAULT);

                InputStream inputStream  = new ByteArrayInputStream(encodeByte);
                Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
                fotoOferta.setImageBitmap(bitmap);
            }
        }



            aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (comprobar_nombre_oferta(nombreOferta.getText().toString())&& comprobar_fecha(fecha.getText().toString())) {
                        String nombre = nombreOferta.getText().toString();
                        String fechaOf = fecha.getText().toString();
                        String image_str = "";
                        if(!hasNullOrEmptyDrawable(fotoOferta)) {
                            Bitmap picture = ((BitmapDrawable) fotoOferta.getDrawable()).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            picture.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
                            byte[] byte_arr = stream.toByteArray();
                            image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                        }
                        String descr = descripcion.getText().toString();
                        publicarenDB(nombre, fechaOf, image_str, descr);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragGestion_Comercio()).commit();
                    }

                }
            });


            subir_foto.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent.createChooser(galleryIntent, "Select File"), SELECT_FILE);
                }
            });
            eliminar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                             if(ID.length()>0){
                                                 eliminarOferta(ID);
                                             }else{
                                                 Toast.makeText(getActivity(),"Oferta no creada, no se puede eliminar",Toast.LENGTH_LONG);
                                             }
                                                FragmentManager fragmentManager = getFragmentManager();
                                                fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragGestion_Comercio()).commit();
                                            }
                                        });

                    fecha.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Calendar cal = Calendar.getInstance();
                            int y = cal.get(Calendar.YEAR);
                            int month = cal.get(Calendar.MONTH);
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, DateSetListener, y, month, day);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        }
                    });
            DateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    Log.d(TAG, "onDateSet: dd/mm/yyyy" + dayOfMonth + "-" + month + "-" + year);
                    String date = year+ "-" + month + "-" + dayOfMonth;
                    fecha.setText(date);
                }
            };

        return view;
    }

    private void eliminarOferta(String id) {
    String x = "http://185.137.93.170:8080/sql.php?sql=DELETE%20FROM%20Ofertas%20WHERE%20ID%20=%20"+ID;
        RegisterTaskOferta t = new RegisterTaskOferta();
        t.faOf = getActivity();
        try {

            JSONArray respuesta = t.execute(x).get();

            Toast.makeText(getActivity(), "Oferta eliminada: "+ID, Toast.LENGTH_SHORT).show();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void publicarenDB(final String n, final String f, final String im, final String des ) {
        String x = "";
        if (im.length() > 0) {
            // Subir imagen al servidor
            Uri uri = new Uri.Builder().scheme("http")
                    .encodedAuthority("185.137.93.170:8080")
                    .path("uploadimg.php")
                    .appendQueryParameter("ImageData", im)
                    .build();
            AsyncTask<Uri, String, String> t = new AsyncTask<Uri, String, String>() {
                @Override
                protected String doInBackground(Uri... uris) {
                    try {
                        URL url = new URL("http://185.137.93.170:8080/uploadimg.php");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                            wr.write( ("ImageData=" + im).getBytes(StandardCharsets.UTF_8) );
                        }
                        StringBuilder out = new StringBuilder();
                        InputStream in = conn.getInputStream();
                        String line;
                        BufferedReader dis = new BufferedReader(new InputStreamReader(in));
                        while ((line = dis.readLine()) != null) {
                            out.append(line);
                        }
                        return out.toString();
                    } catch (Exception e){}
                    return "";
                }

                @Override
                protected void onPostExecute(String Respuesta) {
                    publicarenDB2(n, f, Respuesta, des);
                }
            };
            t.execute(uri);
        } else {
            publicarenDB2(n, f, "", des);
        }
    }

    private void publicarenDB2(String n,String f, String imURL, String des ) {
        if(des.length()<= 0) des = "null";
        else des = "'"+des+"'";
        String x;
        if(ID.length()<=0) {
            x = "http://185.137.93.170:8080/sql.php?sql=INSERT%20INTO%20Ofertas(Comercio,Nombre,FechaInicio,fechaValidez,imagenOferta,Descripcion,publicado)" +
                    "%20VALUES(22,'" + n + "','2017-01-01','" + f + "','" + imURL + "'," + des + ",'1')";


        } else {
            x = "http://185.137.93.170:8080/sql.php?sql=UPDATE%20INTO%20Ofertas(Comercio,Nombre,FechaInicio,fechaValidez,imagenOferta,Descripcion,publicado)" +
                    "%20VALUES(22,'" + n + "','2017-01-01','" + f + "','" + imURL + "'," + des + ",'1')";
        }
        RegisterTaskOferta t = new RegisterTaskOferta();
        t.faOf = getActivity();
        try {
            JSONArray respuesta = t.execute(x).get();
            Toast.makeText(fa, "Oferta publicada: "+n, Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public boolean comprobar_nombre_oferta(String nombre)
    {
        if (nombre.length() == 0) {
            Toast.makeText(getActivity(), "El campo nombre de la oferta debe rellenarse", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    public  boolean comprobar_fecha(String fecha){
        if (fecha.length() == 0) {
            Toast.makeText(getActivity(), "El campo fecha de vencimiento de la oferta debe rellenarse", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }

    public boolean getID(){
        try{
            ID = getArguments().getString("ID");
            return ID.length()>0;
        }catch(Exception e){
            return false;
        }
    }
    public static boolean hasNullOrEmptyDrawable(ImageView iv)
    {
        Drawable drawable = iv.getDrawable();
        BitmapDrawable bitmapDrawable = drawable instanceof BitmapDrawable ? (BitmapDrawable)drawable : null;

        return bitmapDrawable == null || bitmapDrawable.getBitmap() == null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == SELECT_FILE){
            Uri selectedImage = data.getData();

            fotoOferta.setImageURI(selectedImage);
        }
    }
    public void rellenar(String id) {

        String x = "http://185.137.93.170:8080/sql.php?sql=SELECT%20Nombre,%20fechaValidez,%20imagenOferta,%20Descripcion%20FROM%20Ofertas%20WHERE%20ID%20=%20" + id;
        RegisterTaskOferta t = new RegisterTaskOferta();
        t.faOf = getActivity();
        try {
            JSONArray respuesta = t.execute(x).get();
            if (respuesta.length() > 0) {
                JSONObject oferta = respuesta.getJSONObject(0);
                elNombre = oferta.getString("Nombre");
                fechaOf = oferta.getString("fechaValidez");
                fotoOf = oferta.getString("imagenOferta");
                descr = oferta.getString("Descripcion");
                if(descr.equals("null")){
                    descr= "";
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    class RegisterTaskOferta extends AsyncTask<String, String, JSONArray> {
        FragmentActivity faOf;
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
    }
}

