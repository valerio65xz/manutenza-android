package android.manutenza.com.manutenzaandroid;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.manutenza.com.manutenzaandroid.MainActivity.URL;

/**
 * Created by vale- on 24/06/2018.
 */

public class ElencoProposteAdapter extends ArrayAdapter<ElencoProposte> {

    private Context context;
    private List<ElencoProposte> list = new ArrayList<>();

    public ElencoProposteAdapter(@NonNull Context context, @LayoutRes ArrayList<ElencoProposte> list) {
        super(context, 0 , list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.activity_proposte_item,parent,false);

        ElencoProposte record = list.get(position);

        TextView idRichiesta = listItem.findViewById(R.id.textViewIdR);
        idRichiesta.setText("Id richiesta: "+record.getIdRichiesta());

        TextView titoloRichiesta = listItem.findViewById(R.id.textViewTitolo);
        titoloRichiesta.setText("Titolo richiesta: "+record.getTitoloRichiesta());

        TextView categoriaRichiesta = listItem.findViewById(R.id.textViewCategoria);
        categoriaRichiesta.setText("Categoria richiesta: "+record.getCategoriaRichiesta());

        TextView fotoRichiesta = listItem.findViewById(R.id.textViewFoto);
        fotoRichiesta.setText("Foto richiesta: "+record.getFotoRichiesta());

        TextView idProposta = listItem.findViewById(R.id.textViewIdP);
        idProposta.setText("Id proposta: "+record.getIdProposta());

        TextView prezzoProposta = listItem.findViewById(R.id.textViewPrezzo);
        prezzoProposta.setText("Prezzo proposta: "+record.getPrezzoProposta());

        TextView nomeUtente = listItem.findViewById(R.id.textViewNome);
        nomeUtente.setText("Nome utente: "+record.getNomeUtente());

        WebView webView = listItem.findViewById(R.id.paypalButton);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(URL+"paypal");

        return listItem;
    }

}
