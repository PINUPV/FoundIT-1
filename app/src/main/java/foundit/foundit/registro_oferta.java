package foundit.foundit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link registro_oferta.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link registro_oferta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registro_oferta extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters
    public static final int SELECT_FILE = 1 ;
    EditText nombreOferta, fecha;
    ImageView fotoOferta;
    Button subir_foto, aceptar, eliminar;
    DatePickerDialog.OnDateSetListener DateSetListener;




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

        fotoOferta = (ImageView) view.findViewById(R.id.oferta_image);

        aceptar = (Button) view.findViewById(R.id.publicar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre =  nombreOferta.getText().toString();
                String fechaOf = fecha.getText().toString();
                Bitmap picture = ((BitmapDrawable)fotoOferta.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
                byte [] byte_arr = stream.toByteArray();
                String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

            }
        });

        eliminar = (Button) view.findViewById(R.id.Eliminar);
        subir_foto = (Button) view.findViewById(R.id.subir_imagen);
        subir_foto.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent.createChooser(galleryIntent, "Select File"), SELECT_FILE);
            }
        });

        fecha = (EditText) view.findViewById(R.id.fecha);
        fecha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog,DateSetListener,y,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        DateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                Log.d(TAG, "onDateSet: dd/mm/yyyy"+ dayOfMonth + "/" + month + "/"+ year);
                String date = dayOfMonth+ "/" + month + "/"+ year;
                fecha.setText(date);
            }
        };
        return view;
    }


@Override
public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == SELECT_FILE){
            Uri selectedImage = data.getData();

            fotoOferta.setImageURI(selectedImage);
        }

}


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

