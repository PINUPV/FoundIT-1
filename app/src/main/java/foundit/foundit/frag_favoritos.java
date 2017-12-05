package foundit.foundit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class frag_favoritos extends Fragment {

    ImageButton bt_like2;
    boolean lik = true;
    ListView lista;
    int IDUsuario = 22;
    int IDComercio = 0;
    public frag_favoritos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_favoritos, null, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.FavSpinner);
        String[] filtro = new String[] {"Proximidad","Fecha"};
        ArrayAdapter<String> adapter =(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, filtro));
        spinner.setAdapter(adapter);




        /*lista = (ListView) view.findViewById(R.id.listviewfav);
        adaptador = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);
        */

        //bt_like2 = (ImageButton) view.findViewById(R.id.bt_like2);
        //bt_like2.setOnClickListener(new View.OnClickListener() {

        return view;

    }



}