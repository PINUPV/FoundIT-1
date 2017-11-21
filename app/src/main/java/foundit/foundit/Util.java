package foundit.foundit;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
            Log.v("debug", url.toString());
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

    public static void OnAppStarted() {
        Uri uri = new Uri.Builder().scheme("http")
                .encodedAuthority("185.137.93.170:8080")
                .path("sql.php")
                .appendQueryParameter("sql", "SELECT nombre FROM Categoria_comercio")
                .build();
        new PrecargarCagetorias().execute(uri);
    }

    private static class PrecargarCagetorias extends AsyncTask<Uri, String, Void> {
        protected Void doInBackground(Uri... urls) {
            String received = Util.GetWeb(urls[0]);
            listadoCategoriasCacheadas.clear();
            try {
                JSONArray obj = new JSONArray(received);
                for (int i = 0; i < obj.length(); i++) {
                    listadoCategoriasCacheadas.add(obj.getJSONObject(i).getString("nombre"));
                }
            } catch (Exception e) {
                Log.e("ERROR general", e.toString());
            }
            return null;
        }
    }

    private static List<String> listadoCategoriasCacheadas = new ArrayList<String>();
    public static List<String> GetListadoCategorias() {
        return listadoCategoriasCacheadas;
    }

    static double radius = 0;
    public static void CargarComerciosEnMapa(GoogleMap mMap, String busqueda) {
        String lat = Double.toString(mMap.getCameraPosition().target.latitude);
        String lon = Double.toString(mMap.getCameraPosition().target.longitude);
        int maxZoomLevel = 21;
        radius = (10 * Math.pow(2, maxZoomLevel - mMap.getCameraPosition().zoom)/*"500"*/);
        Log.e("Zoom", ""+mMap.getCameraPosition().zoom);
        Log.e("Radius", ""+radius);
        Uri uri = new Uri.Builder().scheme("http")
                .encodedAuthority("185.137.93.170:8080")
                .path("busqueda.php")
                .appendQueryParameter("distancia", "" + radius)
                .appendQueryParameter("gpslat", lat)
                .appendQueryParameter("gpslong", lon)
                .appendQueryParameter("busqueda", busqueda)
                .appendQueryParameter("filtro", "[]")
                .build();
        ActualizaMapa am = new ActualizaMapa();
        am.mMap = mMap;
        am.execute(uri);
    }
    public static void CargarComerciosConFiltroEnMapa(GoogleMap mMap, String filtro) {
        String lat = Double.toString(mMap.getCameraPosition().target.latitude);
        String lon = Double.toString(mMap.getCameraPosition().target.longitude);
        int maxZoomLevel = 21;
        radius = (10 * Math.pow(2, maxZoomLevel - mMap.getCameraPosition().zoom)/*"500"*/);
        Log.e("Zoom", ""+mMap.getCameraPosition().zoom);
        Log.e("Radius", ""+radius);
        String filt="["+filtro+"]";
        Uri uri = new Uri.Builder().scheme("http")
                .encodedAuthority("185.137.93.170:8080")
                .path("busqueda.php")
                .appendQueryParameter("distancia", "" + radius)
                .appendQueryParameter("gpslat", lat)
                .appendQueryParameter("gpslong", lon)
                .appendQueryParameter("filtro", filt)
                .build();
        ActualizaMapa am = new ActualizaMapa();
        am.mMap = mMap;
        am.execute(uri);
    }

    // https://stackoverflow.com/questions/8832071/how-can-i-get-the-distance-between-two-point-by-latlng
    public static float distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
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

    public static HashMap<Marker, Comercio.Comercio> MarkersInfo = new HashMap<>();

    class MarkerCache {
        Marker marker;
        String ID;
        public MarkerCache(String ID, Marker marker) {
            this.marker = marker;
            this.ID = ID;
        }
    }

    protected void onPostExecute(JSONObject respuesta) {
        synchronized (oldMarkers) {
            int antes = oldMarkers.size();
            int borrados = 0;
            int nuevos = 0;
            int oldRecycled = 0;
            ArrayList<MarkerCache> newMarkers = new ArrayList<MarkerCache>();
            try {
                if (respuesta.get("resultado").equals("ok")) {
                    JSONArray array = respuesta.getJSONArray("mensaje");
                    Log.e("Cantidad de marcadores", "" + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        MarkerCache oldMarker = idEnAnteriores(obj.getString("ID"));
                        if (oldMarker == null) {
                            LatLng latlong = new LatLng(Double.parseDouble(obj.getString("Latitud")), Double.parseDouble(obj.getString("Longitud")));
                            Marker m = mMap.addMarker(
                                    new MarkerOptions()
                                    .position(latlong)
                                    .title(obj.getString("Nombre"))
                                    .snippet(obj.getString("Calle")));
                            newMarkers.add(new MarkerCache(obj.getString("ID"), m));
                            Comercio.Comercio comInfo = new Comercio.Comercio();
                            comInfo.setAddress(obj.getString("Calle"));
                            comInfo.setCountry(obj.getString("Pais"));
                            comInfo.setCity(obj.getString("Provincia"));
                            comInfo.setName(obj.getString("Nombre"));
                            comInfo.setId(obj.getInt("ID"));
                           // MarkersInfo.put(m, new Comercio.Comercio());
                            MarkersInfo.put(m, comInfo);
                            nuevos++;
                        } else {
                            newMarkers.add(oldMarker);
                            oldMarkers.remove(oldMarker);
                            oldRecycled++;
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e("Error JSON", e.toString());
                e.printStackTrace();
            }

            for (int i = 0; i < oldMarkers.size(); i++){
                oldMarkers.get(i).marker.remove();
                borrados++;
            }
            oldMarkers.clear();
            oldMarkers.addAll(newMarkers);
            // Radio capturado
            //mMap.addCircle(new CircleOptions().center(mMap.getCameraPosition().target).radius(Util.radius));
        }
    }

    private MarkerCache idEnAnteriores(String id) throws JSONException {
        for (int i = 0; i < oldMarkers.size(); i++) {
            if (oldMarkers.get(i).ID.equals(id)) {
                return oldMarkers.get(i);
            }
        }
        return null;
    }
}