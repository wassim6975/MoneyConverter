package com.example.tp1_test;
// Author : wassim6975
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class InternetConnexionMessage extends AsyncTask<String, Void, Boolean>{

    private Boolean True;

    @Override
    protected Boolean doInBackground(String...params) {
        String current = "";
        try {
            Log.i("isConnectedToServer", "debut");
            URL serverURL = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
            URLConnection urlconn = serverURL.openConnection();
            urlconn.connect();

            Log.i("isConnectedToServer", "connexion");
            //InternetConnexionMessage.showToast((Context) this, "Internet connexion");
            return true;

        } catch (IOException e) {
            Log.e("IOException", e.getLocalizedMessage(), e);
        } catch (IllegalStateException e) {
            Log.e("IllegalStateException", e.getLocalizedMessage(), e);
        }
        return false;

        }

        protected void onProgressUpdate() {
            // receive progress updates from doInBackground
        }

        protected void onPostExecute() {
            // update the UI after background processes completes
        }


}