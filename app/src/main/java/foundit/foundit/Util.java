package foundit.foundit;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yop-Portatil on 25/10/2017.
 */

public class Util {
    public static String GetWeb(Uri uri) {
        StringBuilder out = new StringBuilder("");
        String line;
        InputStream in = null;
        try {
            URL url = new URL(uri.toString());
            in = url.openConnection().getInputStream();

            BufferedReader dis = new BufferedReader(new InputStreamReader(in));
            while ((line = dis.readLine()) != null) {
                out.append(line);
            }
            return out.toString();
        } catch (IOException e) {
            Log.e("ERROR2", e.toString());
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) { }
        }
        return "";
    }

    public static void CargarComerciosEnMapa(GoogleMap mMap, String busqueda) {
        String lat = Double.toString(mMap.getCameraPosition().target.latitude);
        String lon = Double.toString(mMap.getCameraPosition().target.longitude);
        Uri uri = new Uri.Builder().scheme("http")
                .encodedAuthority("185.137.93.170:8080")
                .path("busqueda.php")
                .appendQueryParameter("distancia", "1000")
                .appendQueryParameter("gpslat", lat)
                .appendQueryParameter("gpslong", lon)
                .appendQueryParameter("busqueda", busqueda)
                .appendQueryParameter("filtro", "[]")
                .build();
        ActualizaMapa am = new ActualizaMapa();
        am.mMap = mMap;
        am.execute(uri);
    }
}

class ActualizaMapa extends AsyncTask<Uri, Void, JSONObject> {
    public GoogleMap mMap;
    public JSONArray resultadosAnteriores = new JSONArray();
    private Exception exception;
    static ArrayList<MarkerCache> oldMarkers = new ArrayList<MarkerCache>();

    protected JSONObject doInBackground(Uri... urls) {
        Uri uri = urls[0];
        String received = Util.GetWeb(uri);
        Log.e("Recibido", received);
        JSONObject obj = null;
        try {
            obj = new JSONObject(received);
        } catch (JSONException e) {
            Log.e("ERROR JSONException", e.toString());
        } catch (Exception e) {
            Log.e("ERROR general", e.toString());
        }
        return obj;
    }

    class MarkerCache {
        Marker marker;
        String ID;
        boolean check = false;
        public MarkerCache(String ID, Marker marker) {
            this.marker = marker;
            this.ID = ID;
        }
    }

    protected void onPostExecute(JSONObject respuesta) {
        synchronized (oldMarkers) {
            ArrayList<MarkerCache> newMarkers = new ArrayList<MarkerCache>();
            try {
                if (respuesta.get("resultado").equals("ok")) {

                    for (int i = 0; i < oldMarkers.size(); i++){
                        oldMarkers.get(i).check = false;
                    }

                    JSONArray array = respuesta.getJSONArray("mensaje");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        MarkerCache oldMarker = idEnAnteriores(obj.getString("ID"));
                        if (oldMarker == null) {
                            MarkerOptions mo = new MarkerOptions();
                            LatLng latlong = new LatLng(Double.parseDouble(obj.getString("Latitud")), Double.parseDouble(obj.getString("Longitud")));
                            mo.position(latlong);
                            //mo.snippet("Hola");
                            mo.title(obj.getString("Nombre"));
                            newMarkers.add(new MarkerCache(obj.getString("ID"), mMap.addMarker(mo)));
                        } else {
                            oldMarker.check = true;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < oldMarkers.size(); i++){
                if (!oldMarkers.get(i).check) {
                    oldMarkers.get(i).marker.remove();
                }
            }
            oldMarkers.clear();
            oldMarkers.addAll(newMarkers);
        }
        //mMap
    }

    private MarkerCache idEnAnteriores(String id) throws JSONException {
        for (int i = 0; i < oldMarkers.size(); i++) {
            if (oldMarkers.get(i).ID.equals((id))) {
                return oldMarkers.get(i);
            }
        }
        return null;
    }
}