package android.manutenza.com.manutenzaandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SendProfilePicture extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {

        Bitmap bitmap = null;

        try {
            URL imageURL = new URL("https://graph.facebook.com/" + strings[0] + "/picture?type=large");
            bitmap =  BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            Log.e("SendProfilePicture/URL", ""+e.toString());
        } catch (IOException e) {
            Log.e("SendProfilePicture/IO", ""+e.toString());
        }

        Log.e("SendProfilePicture/IO", "è piena immagine? "+bitmap.getByteCount());

        return null;
    }
}
