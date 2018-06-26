package android.manutenza.com.manutenzaandroid;

import android.content.Intent;
import android.graphics.Point;
import android.manutenza.com.manutenzaandroid.qrcode.BarcodeCaptureActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.manutenza.com.manutenzaandroid.MainActivity.URL;

public class ElencoProposteActivity extends FragmentActivity {

    private ArrayList<AndroidInfo> elencoProposte;
    private String email;

    //Per QRCODE
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposte);
        ListView listView = findViewById(R.id.listViewProposte);

        //Ottento l'oggetto tramite decodifica JSON
        String elencoProposteJson = getIntent().getStringExtra("proposte");
        String emailJson = getIntent().getStringExtra("email");
        Gson gson = new Gson();
        elencoProposte = gson.fromJson(elencoProposteJson, new TypeToken<List<AndroidInfo>>(){}.getType());
        email = gson.fromJson(emailJson, String.class);

        //Definisco un ArrayList relativa all'oggetto cbe rappresenta la mia query, e lo popolo a seconda di quanti ne ho
        ArrayList<ElencoProposte> elencoProposteArray = new ArrayList<>();
        for (int i=0; i<elencoProposte.size(); i++){
            elencoProposteArray.add(new ElencoProposte(
                    elencoProposte.get(i).getId_richiesta(),
                    elencoProposte.get(i).getTitolo_richiesta(),
                    elencoProposte.get(i).getCategoria_richiesta(),
                    elencoProposte.get(i).getFoto_richiesta(),
                    elencoProposte.get(i).getId_proposta(),
                    elencoProposte.get(i).getPrezzo_proposta(),
                    elencoProposte.get(i).getNome_utente()
            ));
        }

        //Imposto l'adapter personalizzato e costruisco la GUI relativa
        ElencoProposteAdapter adapter = new ElencoProposteAdapter(this, elencoProposteArray);
        listView.setAdapter(adapter);
    }

    //Implementazione mediante libreria Google Play Services - Vision e libreria di NKDroid
    public void QRCode(View v){
        Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
    }

    //Risultato per il QRCode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;

                    //A questo punto si ritorna il valore ad una sub activity o altro. Io lo mostro nel log
                    Toast.makeText(this, "QRCode: "+barcode.displayValue, Toast.LENGTH_LONG).show();
                    Log.e("ElencoProposteActivity", "Risultato: "+barcode.displayValue);

                    //Da qui chiamo il metodo per validare il pagamento
                    validateJob(7);
                    //scanResult.setText(barcode.displayValue);
                } else {
                    Toast.makeText(this, "No result found", Toast.LENGTH_LONG).show();
                    Log.e("ElencoProposteActivity", "No result found");
                    //scanResult.setText("No Result Found");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Invio una richiesta a Spring per convalidare il lavoro
    private void validateJob(final int id){
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"validateJob",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("success")){
                            Toast.makeText(getApplicationContext(), "Pagamento avvenuto con successo!", Toast.LENGTH_LONG).show();
                            Log.i("ElencoProposteActivity", "Pagamento avvenuto con successo!");
                        }
                        else{
                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            Log.e("ElencoProposteActivity", response);
                        }
                    }
                },
                //In caso di errori
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Log.e("onErrorResponse", ""+error.toString());
                    }
                }
        ){
            //Per aggiungere i parametri al POST
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("id", ""+id);
                params.put("email", ""+email);
                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}

