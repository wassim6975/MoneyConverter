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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// implémentation de AsyncTask
// TODO
public class AsyncTaskRunner extends AsyncTask<String, String, HashMap<String, String>> {

    protected HashMap<String, String> doInBackground(String... params) {
        // Création de la HashMap
        HashMap<String,String> Resultats = new HashMap<>();

        // implement API in background and store the response in current variable
        String current = "";
        try {
            Log.i("AsyncTask","Get xml change with AsyncTaskRunner");

            // Récupération du xml taux de change
            // Création de l'objet URL
            URL url = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
            // Instanciation de la classe DocumentBuilder
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
            // Using InputSource
            Document doc = docBuilder.parse(url.openStream());

            NodeList nodeList = doc.getElementsByTagName("Cube");
            for(int i = 0;i < nodeList.getLength(); i++){

                if(nodeList.item(0).getNodeType() == Node.ELEMENT_NODE){

                    Element ele = (Element)nodeList.item(i);

                    String nom = ele.getAttribute("currency");
                    String valeur = ele.getAttribute("rate");
                    //Log.i("AsyncTask","nom : " + nom);

                    // Stockage des valeurs dans la HashMap
                    Resultats.put(nom.toString(), valeur.toString());
                    //System.out.println("Name : " + nom);
                    //System.out.println("Valeur : " + valeur);

                }
            }
        } catch (NumberFormatException | ParserConfigurationException | MalformedURLException e) {
            System.out.println(e);
            Log.i("AsyncTask", "doInBackground: NumberFormatException | MalformedURLException | MalformedURLException");
        } catch (SAXException e) {
            e.printStackTrace();
            Log.i("AsyncTask", "doInBackground: IOException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("AsyncTask", "doInBackground: Exception");
        }
        Log.i("AsyncTask", "doInBackground: Finished");
        return Resultats;
    }

    protected void onProgressUpdate(Integer... progress) {
        // receive progress updates from doInBackground
    }

    protected void onPostExecute(Long result) {
        // update the UI after background processes completes
    }

}