package android.manutenza.com.manutenzaandroid;

import android.content.Intent;
import android.graphics.Point;
import android.manutenza.com.manutenzaandroid.qrcode.BarcodeCaptureActivity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Per manutenti vicini
    private FragmentActivity activity;

    //Per elenco proposte
    private ArrayList<AndroidInfo> elencoProposte = new ArrayList<>();

    //Per QRCODE e URL
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    protected static final String URL = "http://192.168.1.5:8080/";

    //Callback Facebook
    private CallbackManager callbackManager;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;

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
                                    //Log.e("MainAcitivity", "JSON: "+response);
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


    //Metodo che fa una richiesta JSON tramite libreria Volley, inviando la mail risultate e prelevando il codice JSON
    public void getElencoProposte(){

        //Toast.makeText(getApplicationContext(), "Entro fase 1", Toast.LENGTH_SHORT).show();

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"androidQuery",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                                intent.putExtra("email", email);
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
