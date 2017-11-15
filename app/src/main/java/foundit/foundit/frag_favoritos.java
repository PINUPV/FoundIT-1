package foundit.foundit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Spinner;
import android.widget.ArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class frag_favoritos extends Fragment {


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


        return view;

    }



}
