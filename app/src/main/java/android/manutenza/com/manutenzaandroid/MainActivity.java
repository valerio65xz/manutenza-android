package android.manutenza.com.manutenzaandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.manutenza.com.manutenzaandroid.qrcode.BarcodeCaptureActivity;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Per manutenti vicini
    private Handler handler;
    private Locator locator;
    private Location location;
    private FragmentActivity activity;
    private ArrayList<DistanzaManutenti> manutenti = new ArrayList<>();

    //Per elenco proposte
    private ArrayList<AndroidInfo> elencoProposte = new ArrayList<>();

    //Per QRCODE e URL
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    protected static final String URL = "http://192.168.1.107:8080/";

    //Callback Facebook
    private CallbackManager callbackManager;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;

        //Handler per ricevere subito quando la locazione
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message inputMessage) {
                //Acquisisci posizione
                location = (Location) inputMessage.obj;

                //***TEMPORANEO: CREO 10 LOCAZIONI RANDOM
                for (int a=0; a<10; a++){
                    //Crea latitudine random
                    int i = new Random().nextInt(10000000)+450000000;
                    double lat = i/10000000.0;
                    //Toast.makeText(this,"Coordinata: "+lat, Toast.LENGTH_LONG).show();

                    //Crea longitudine random
                    int j = new Random().nextInt(10000000)+70000000;
                    double lng = j/10000000.0;
                    //Toast.makeText(this,"Coordinata: "+lng, Toast.LENGTH_LONG).show();

                    //Crea oggetto e aggiungi all'ArrayList.
                    Location temp = new Location("");
                    temp.setLatitude(lat);
                    temp.setLongitude(lng);
                    int dist = (int)temp.distanceTo(location);
                    manutenti.add(new DistanzaManutenti(temp, dist));

                }

                //Ordinamento secondo distanza minima
                Collections.sort(manutenti, new Comparator<DistanzaManutenti>() {
                    @Override public int compare(DistanzaManutenti d1, DistanzaManutenti d2) {
                        return d1.getDistance() - d2.getDistance();
                    }
                });

                //Lancio l'activity per i manutenti con il mio array ordinato di manutenti più vicini, passato tramite putExtra()
                //Per passare strutture composte all'intent, devo convertirle prima in JSON. Uso la libreria Google GSON
                Gson gson = new Gson();
                String manutentiJson = gson.toJson(manutenti);

                Intent intent = new Intent(activity, ManutentiActivity.class);
                intent.putExtra("manutenti", manutentiJson);
                startActivity(intent);
            }

        };

        //Gestione login facebook
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        //loginButton.setFragment(activity);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //Mi serve un Graph per prendere le info totali fra cui email
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (response.getError() != null) {
                                    Toast.makeText(activity, "Errore: "+response.getError(), Toast.LENGTH_LONG).show();
                                } else {
                                    email = response.getJSONObject().optString("email");
                                    //Toast.makeText(activity, "Email: "+email, Toast.LENGTH_LONG).show();
                                    //Richiamo il metodo che mi preleva le info dal Web Server Spring
                                    getElencoProposte();
                                }
                            }
                        });

                //Non so bene a che servono ma meglio lasciarle
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    //Avvio la localizzazione sul Thread principale
    public void near(View v){

        //Istanzio l'oggetto Locator e lo collego al Thread UI principale (si può fare anche a parte)
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                locator = new Locator(activity, handler);
            }
        });

        //Se ho i permessi di accesso alla posizione, cerco di avviare la localizzazione
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locator.startConnection();

            //Una volta accertatomi di avere i permessi, controllo che sia abilitato il GPS. Se no, lo chiedo all'utente
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            boolean is_gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!is_gps) locator.enableGPS();
        }

        //Se l'utente non ha i permessi, li richiedo.
        else ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 101);


    }

    //OLD!! LASCIARLO STARE PER ORA
    //Implementazione mediante libreria Google Play Services - Vision e libreria di NKDroid
    public void QRCode(View v){
        Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
    }

    //Risultato per il QRCode e per CallBack Facebook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;

                    //A questo punto si ritorna il valore ad una sub activity o altro. Io lo mostro nel log
                    Toast.makeText(this, "QRCode: "+barcode.displayValue, Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Risultato: "+barcode.displayValue);
                    //scanResult.setText(barcode.displayValue);
                } else {
                    Toast.makeText(this, "No result found", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "No result found");
                    //scanResult.setText("No Result Found");
                }
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Metodo che lancia Google Maps con la posizione di un manutente
    //PER ORA LO CHIAMO TEMPORANEAMENTE MANUALMENTE. POI METTERE UN PARAMETRO CHE PASSA L'OGGETTO LOCATION
    public void manutenteOnMaps(View v){

        //Costruzione URI per la locazione nella mappa
        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+manutenti.get(0).getLocation().getLatitude()+","+manutenti.get(0).getLocation().getLongitude()+"(NomeManutente)");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        //Se ho Maps installato, lancio. Altrimenti niente.
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }

    }

    //Metodo che mi consente di rispondere ad una richiesta di permessi. In questo caso, accesso alla posizione
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {

                // If I pressed yes, I can enable my location and start the location updates
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        //Replico il metodo di near() così da non ripremere il bottone
                        locator.startConnection();
                        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                        boolean is_gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    }
                }

                //Else, I say this message
                else{
                    Toast.makeText(this,"I can't acquire the location if you don't let me do it!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    //Metodo che fa una richiesta JSON tramite libreria Volley, inviando la mail risultate e prelevando il codice JSON
    public void getElencoProposte(){

        //Toast.makeText(getApplicationContext(), "Entro fase 1", Toast.LENGTH_SHORT).show();

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"androidQuery",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(getApplicationContext(), "Entro fase 2", Toast.LENGTH_SHORT).show();

                        try {
                            //getting the whole json object from the response
                            //JSONObject obj = new JSONObject(response);

                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            //JSONArray infoArray = obj.getJSONArray("");

                            //Mi prelevo l'array JSON dal response
                            JSONArray infoArray = new JSONArray(response);

                            //now looping through all the elements of the json array
                            for (int i = 0; i < infoArray.length(); i++) {

                                //getting the json object of the particular index inside the array
                                JSONObject infoObject = infoArray.getJSONObject(i);

                                //creating a hero object and giving them the values from json object
                                AndroidInfo androidInfo = new AndroidInfo(infoObject.getInt("id_richiesta"), infoObject.getString("titolo_richiesta"), infoObject.getString("categoria_richiesta"), infoObject.getString("foto_richiesta"), infoObject.getInt("id_proposta"), (float)infoObject.getDouble("prezzo_proposta"), infoObject.getString("nome_utente"));
                                elencoProposte.add(androidInfo);
                            }

                            //Se l'elenco proposte non è vuoto, inizializzo l'activity per il QR CODE, con lo stesso meccanismo
                            //Per convertire un arraylist in JSON per passarlo all'intent
                            if (elencoProposte.size()!=0){
                                Gson gson = new Gson();
                                String elencoProposteJson = gson.toJson(elencoProposte);

                                Intent intent = new Intent(activity, ElencoProposteActivity.class);
                                intent.putExtra("proposte", elencoProposteJson);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Primo exception", ""+e.toString());
                        }
                    }
                },
                //In caso di errori
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Log.e("Secondo exception", ""+error.toString());
                    }
                }
        ){
            //Per aggiungere i parametri al POST
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}
