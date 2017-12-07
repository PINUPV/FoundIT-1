package foundit.foundit;


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
public class Frag_LoginUsuario extends Fragment {

    private static EditText Usuario;
    private static EditText Password;
    private static Button Login;
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

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
// Build a GoogleSignInClient with the options specified by gso.
        //Object vmGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        // apiClient = new GoogleApiClient.Builder(this)
       //Object apiClient = new GoogleApiClient.Builder(getActivity())
                //.enableAutoManage(getActivity(), (GoogleApiClient.OnConnectionFailedListener) getActivity())
                //.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                //.build();*/

        // This method configures Google SignIn

        /*mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();
        //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
        // User is signed in
                    createUserInFirebaseHelper();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
        // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };*/

        return view;
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
                JSONObject RespComer = Respuesta.getJSONObject(0);
                Log.v(DEBUG, RespComer.toString());
                if(RespComer.length()>1){


                    //Usuario objUsuario = new Usuario("22", user, "last1", user, pass, user + "@gmail.com", "Valencia");
                    //MainFoundit.setUsuario(objUsuario);


                    //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    //fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragBusqueda()).commit();

                }else{
                    //no existe el usuario o los datos son incorrectos
                }
                //NombreComer.setText(RespComer.getString("Nombre"));
                //PoblacionComer.setText(RespComer.getString("Poblacion"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v(DEBUG,Respuesta.toString());
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
                .enableAutoManage(getActivity(), (GoogleApiClient.OnConnectionFailedListener) getActivity())
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
                Object photoUri = account.getPhotoUrl();
                String photo = photoUri.toString();
                /*
// Save Data to SharedPreference
                SharedPrefManager sharedPrefManager = new SharedPrefManager(mContext);
                sharedPrefManager.saveIsLoggedIn(mContext, true);
                sharedPrefManager.saveEmail(mContext, email);
                sharedPrefManager.saveName(mContext, name);
                sharedPrefManager.savePhoto(mContext, photo);
                sharedPrefManager.saveToken(mContext, idToken);
//sharedPrefManager.saveIsLoggedIn(mContext, true);
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
                */
            } else {
// Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful. ");
                Toast.makeText(getActivity(), "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


}

