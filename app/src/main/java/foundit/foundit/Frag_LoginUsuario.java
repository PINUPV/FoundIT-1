package foundit.foundit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import Usuario.Usuario;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_LoginUsuario extends Fragment {

    private static EditText Usuario;
    private static EditText Password;
    private static Button Login;
    private static TextView CreateUser;
    private static final String DEBUG="LOG_Frag_LoginUsuario";

    public Frag_LoginUsuario() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag__login_usuario, container, false);
        /*
        * Fragment que recoge la información de login de usuario, la envia a la db y espera una respuesta. Si devuelve la info del usuario
        * se crea un objeto tipo usuario con toda la información pertinente y se guarda en el la MainClass
        */
        Usuario = (EditText) view.findViewById(R.id.eTFragLoginUsuarioUserName);
        Password = (EditText) view.findViewById(R.id.eTFragmentLoginUsuarioPass);
        CreateUser=(TextView) view.findViewById(R.id.tVFragmentLoginUsuarioRegistrate);
        Login=(Button) view.findViewById(R.id.bTNFragmentLoginUsuarioLogin);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=Usuario.getText().toString();
                String pass=Password.getText().toString();
                if(user.isEmpty()){
                    Usuario.setHintTextColor(getResources().getColor(R.color.HintError));
                }else if(pass.isEmpty()){
                    Password.setHintTextColor(getResources().getColor(R.color.HintError));
                }else{
                    Log.v(DEBUG,"Usuario: "+user+" Password: "+pass);
                    Usuario objUsuario = new Usuario(user,"last1",user,pass,user+"@gmail.com","");
                    MainFoundit.setUsuario(objUsuario);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragBusqueda()).commit();
                }
            }
        });
        CreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragRegistro_Usuario()).commit();
            }
        });


        return view;
    }

}
