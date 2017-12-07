package foundit.foundit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import Usuario.Usuario;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_LoginUsuario extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static EditText Usuario;
    private static EditText Password;
    private static Button Login;
    private static SignInButton btnLoginGoogle;
    private static TextView CreateUser;
    private static final String DEBUG = "LOG_Frag_LoginUsuario";
    private static final int RC_SIGN_IN = 9001;
    private Frag_LoginUsuario myFrag=this;
    public Frag_LoginUsuario() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (MainFoundit.getUsuario() != null) {
            Toast.makeText(getContext(), "Ya has iniciado sesión", Toast.LENGTH_SHORT).show();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragBusqueda()).commit();
            return null;
        }


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag__login_usuario, container, false);
        /*
        * Fragment que recoge la información de login de usuario, la envia a la db y espera una respuesta. Si devuelve la info del usuario
        * se crea un objeto tipo usuario con toda la información pertinente y se guarda en el la MainClass
        */
        Usuario = view.findViewById(R.id.eTFragLoginUsuarioUserName);
        Password = view.findViewById(R.id.eTFragmentLoginUsuarioPass);
        CreateUser = view.findViewById(R.id.tVFragmentLoginUsuarioRegistrate);
        Login = view.findViewById(R.id.bTNFragmentLoginUsuarioLogin);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = Usuario.getText().toString();
                String pass=Password.getText().toString();
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                byte[] messageDigest = md.digest(pass.getBytes());
                BigInteger number = new BigInteger(1, messageDigest);
                String passMD5 = number.toString(16);

                if (user.isEmpty()) {
                    Usuario.setHintTextColor(getResources().getColor(R.color.HintError));
                } else if (pass.isEmpty()) {
                    Password.setHintTextColor(getResources().getColor(R.color.HintError));
                } else {
                    //consulta a la db preguntando por la información.
                    ComprobarUsuario c = new ComprobarUsuario();
                    c.myFrag_LoginUsuario=myFrag;

                    try{
                        Uri uri = new Uri.Builder().scheme("http")
                                .encodedAuthority("185.137.93.170:8080")
                                .path("sql.php")
                                .appendQueryParameter("sql", "SELECT ID,Alias,Email, Poblacion FROM `Usuario` WHERE Alias Like '"+user+"' AND passwordMD5 LIKE '"+passMD5+"'")
                                .build();
                        Log.v(DEBUG,uri.toString());
                        c.execute(uri);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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

        com.google.android.gms.common.SignInButton googleLogin = view.findViewById(R.id.login_with_google);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.login_with_google) {
                    signIn();
                }
            }
        });
        configureSignIn();

        return view;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class ComprobarUsuario extends AsyncTask<Uri, String, JSONArray> {
        Frag_LoginUsuario myFrag_LoginUsuario;

        @Override
        protected JSONArray doInBackground(Uri... params) {
            String result = Util.GetWeb(params[0]);
            try {
                return new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                return new JSONArray("[]");
            } catch (JSONException e) {
                return null; // Nunca ocurrirá
            }
        }

        @Override
        protected void onPostExecute(JSONArray Respuesta) {
            try {
                if(Respuesta.length()>0){
                    JSONObject RespComer = Respuesta.getJSONObject(0);
                    Log.v(DEBUG,Respuesta.toString());
                    String id = RespComer.getString("ID");
                    String Alias = RespComer.getString("Alias");
                    String Email = RespComer.getString("Email");
                    String Poblacion =RespComer.getString("Poblacion");

                    Usuario objUsuario = new Usuario(id, null, null, Alias, null, Email, Poblacion);
                    MainFoundit.setUsuario(objUsuario);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragBusqueda()).commit();

                }else{
                    //popup que indique que no existe el usuario
                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("El usuario no existe o alguno de los datos no son correctos.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    // Create the AlertDialog object and return it
                    dialog = builder.create();
                    dialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    GoogleApiClient mGoogleApiClient;
    public void configureSignIn() {
    //Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        //Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // This IS the method where the result of clicking the signIn button will be handled
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(…);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
// Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                String idToken = account.getIdToken();
                String name = account.getDisplayName();
                String email = account.getEmail();
                //Object photoUri = account.getPhotoUrl();
                //String photo = photoUri.toString();
                
                Usuario objUsuario = new Usuario(idToken, null, null, name, null, email, null);
                MainFoundit.setUsuario(objUsuario);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragBusqueda()).commit();

            } else {
// Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful. ");
                Toast.makeText(getActivity(), "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


}

