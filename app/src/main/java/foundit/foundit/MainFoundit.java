package foundit.foundit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import Usuario.Usuario;
import layout.frag_ofertas;

public class MainFoundit extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Usuario userActivo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_foundit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragBusqueda()).commit();

        Util.OnAppStarted();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_foundit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Aqui se configura el menu diseñado en res>menu>main_foundit.xml
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static FragmentManager fragmentManager;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fragmentManager = getSupportFragmentManager();


        if (id == R.id.nav_Mapa) {
            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragBusqueda()).commit();
        } else if (id == R.id.nav_RegUser) {
            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragRegistro_Usuario()).commit();
        } else if (id == R.id.nav_RegComer) {
            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new FragRegistro_Comercio()).commit();
        } else if (id == R.id.nav_fav) {
            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new frag_favoritos()).commit();
        } else if (id == R.id.nav_ofertas) {
            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new frag_ofertas()).commit();
        } else if (id == R.id.nav_login) {
            fragmentManager.beginTransaction().replace(R.id.ContainFoundit, new Frag_LoginUsuario()).commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Usuario getUsuario(){
        return userActivo;
    }
    public static void setUsuario(Usuario user){
        userActivo=user;
        Log.v("DEBUG","Username: "+userActivo.getName());
    }
}
