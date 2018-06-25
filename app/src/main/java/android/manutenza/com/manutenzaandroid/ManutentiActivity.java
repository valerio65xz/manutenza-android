package android.manutenza.com.manutenzaandroid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vale- on 14/02/2018.
 */

public class ManutentiActivity extends FragmentActivity {

    private ArrayList<DistanzaManutenti> manutenti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutenti);

        //Ottento l'oggetto tramite decodifica JSON
        String manutentiJson = getIntent().getStringExtra("manutenti");
        Gson gson = new Gson();
        manutenti = gson.fromJson(manutentiJson, new TypeToken<List<DistanzaManutenti>>(){}.getType());

        //Prelevo i dati e li visualizzo nel nuovo layout. Tanto per prova, qui ci sarà l'interfaccia completa
        EditText area = findViewById(R.id.editText);
        String s = "";
        for (int i=0; i<manutenti.size(); i++){
            s += "["+i+"] - lat: "+manutenti.get(i).getLocation().getLatitude()
                    +", lng: "+manutenti.get(i).getLocation().getLongitude()
                    +", dist: "+manutenti.get(i).getDistance()
                    +"\n";
        }
        area.setText(s);
    }

}
