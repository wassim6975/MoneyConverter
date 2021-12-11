package com.example.tp1_test;
// Author : wassim6975

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.Spinner;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    public HashMap<String, String> dataChangeFromXml;
    public HashMap<String, String> dataChange;
    public ArrayList<String> devises;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.modifications:
                intent = new Intent(this, MainActivity3.class);
                break;
            case R.id.parametres:
                intent = new Intent(this, MainActivity2.class);
                break;
            case R.id.home:
                intent = new Intent(this, MainActivity.class);
                break;
            default:
                //return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner SelectMonnaiSource = findViewById(R.id.selectMonnaieSource);
        Spinner SelectMonnaieDestination = (Spinner) findViewById(R.id.selectMonnaieDestination);

        // Connexion à la database
        SqlLite database = (SqlLite) new SqlLite(this);



        try {
            // Testing the server connexion
            InternetConnexionMessage connexionTest = (InternetConnexionMessage) new InternetConnexionMessage().execute();
            Boolean isConnected = connexionTest.get();
            if (isConnected) {
                // Connexion au server valide

                // Show a  toast message
                Toast toast= Toast.makeText(getApplicationContext(),
                        "Connexion au serveur valide", Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                // Getting the data change
                AsyncTaskRunner getXml = (AsyncTaskRunner) new AsyncTaskRunner().execute();
                dataChangeFromXml = getXml.get();
                dataChange = dataChangeFromXml;
                devises = new ArrayList<String>(dataChange.keySet());

                HashMap<String, String> dataFromDatabase = database.getAllDevises();

                // Mise à jour de la base de données
                for (int i = 1; i < dataChange.size(); i++){
                    String devise = (String) dataChange.keySet().toArray()[i];
                    String taux = dataChange.get(devise);
                    // Ajout-update dans la base de données

                    //Test pour savoir si la devise existe dans la base de donnée
                    String valueToCheck = dataFromDatabase.get(devise);
                    if (valueToCheck != null) {
                        // Si la devise exite : mettre à jour le taux
                        Log.i("Database","Devise exist");
                        database.updateDevise(devise, taux);
                    } else {
                        // Si la devise n'existe pas : l'ajouter
                        database.addDevise(devise, taux);
                    }
                }

            }
            else {
                // Connexion au server non valide

                // show a toast message
                Toast.makeText(this, "Connexion au serveur non valide", Toast.LENGTH_LONG).show();
                // show an alert dialog
                new AlertDialog.Builder(this)
                        .setTitle("Error server connexion")
                        .setMessage("Couldn't reach the server")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                // Mode hors-ligne
                // Récupération des valeurs (taux, devises) venant de la db
                //TODO
                dataChange = database.getAllDevises();
                devises = new ArrayList<String>(dataChange.keySet());
            }


            // Affichage des données obtenu dans le spinner
            Log.i("adaptater", "adaptater");
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, devises);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SelectMonnaiSource.setAdapter(arrayAdapter);
            SelectMonnaieDestination.setAdapter(arrayAdapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("MainActivity", "InterruptedException");

        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e("MainActivity", "ExecutionException");

        }
    }

    @Override
    // TP1 : question 5
    public void onStart() {
        super.onStart();

        Log.i(TAG,"onStart");
        Button buttonConversion = findViewById(R.id.buttonConvertir);

        buttonConversion.setOnClickListener(myHandler1);
    }

    View.OnClickListener myHandler1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            System.out.println("Hello world");

            EditText EditText1 = (EditText) findViewById(R.id.editText1);
            String editText1 = EditText1.getText().toString();
            System.out.println(editText1);

            // Modification du champ texte resultat
            TextView Resultat = (TextView) findViewById(R.id.textViewResult);


            // Récupération du pointeur du Spinner selectMonnaie
            Spinner SelectMonnaiSource = (Spinner) findViewById(R.id.selectMonnaieSource);
            String monnaieSource = SelectMonnaiSource.getSelectedItem().toString();

            // Selection du taux de conversion
            Spinner selectMonnaieDestination = (Spinner) findViewById(R.id.selectMonnaieDestination);
            String monnaieDestination = selectMonnaieDestination.getSelectedItem().toString();

            try {
                // Conversion string to Double
                Double valueMoneyToConvert = Double.parseDouble(editText1);

                // Selon la monnaie source et la monnaie destination : convertir
               // Double moneyConverted = changeConverter(monnaieSource, monnaieDestination, valueMoneyToConvert);

                // Utilisation des données venant de l'URL
                Double moneyConverted = change(monnaieSource, monnaieDestination, valueMoneyToConvert);

                // Renvoie du bon symbole pour la monnaie de destination
                String symboleMoney = "";

                switch (monnaieDestination) {
                    case "Yen" :
                        symboleMoney = " ¥";
                        break;
                    case "Euro" :
                        symboleMoney = "€";
                        break;
                    case "Dollar" :
                        symboleMoney = "$";
                        break;
                    case "Peso" :
                        symboleMoney = "$";
                        break;
                    default:
                        symboleMoney = "";
                        break;
                }

                // Conversion dollars to euros
                //Double euros = dollarsToEuro(dollars);

                // Verification
                System.out.println("Après conversion : "+ moneyConverted);
                // Conversion Double to string
                String moneyConvertedString = moneyConverted.toString();
                Resultat.setText(moneyConvertedString + symboleMoney);
                Log.i("Resultat", moneyConvertedString);


            } catch (NumberFormatException e) {
                //System.out.println(e);
                Resultat.setText("Error, not a number, try again.");
            }
            //catch (InterruptedException e) {
              //  e.printStackTrace();
           // } catch (ExecutionException e) {
             //   e.printStackTrace();
            //}

        }
    };


    /*public double dollarsToEuro (double dollars) {
        return (0.89 * dollars);
    }
    public double pesoToEuro (double dollars) {
        return (0.018 * dollars);
    }
    public double yensToEuro (double dollars) {
        return (0.0077 * dollars);
    }*/


    public double change (String From, String To, Double Value){
        double euros = Value / Double.parseDouble(dataChange.get(From));
        double toMonnaie = euros * Double.parseDouble(dataChange.get(To));

        return toMonnaie;
    }
    public double changeConverter (String From, String To, Double value) {

        switch (From) {
            case "Yen" :
                switch (To) {
                    case "Yen" :
                        return value;
                    case "Euro" :
                        return 0.0077 * value;
                    case "Dollar" :
                        return 0.0087 * value;
                    case "Peso" :
                        return 0.19 * value;
                    default:
                        return 0;
                }
            case "Euro" :
                switch (To) {
                    case "Yen" :
                        return 129.32 * value;
                    case "Euro" :
                        return value;
                    case "Dollar" :
                        return 1.12 * value;
                    case "Peso" :
                        return 24.17 * value;
                    default:
                        return 0;
                }
            case "Dollar" :
                switch (To) {
                    case "Yen" :
                        return 115.37 * value;
                    case "Euro" :
                        return 0.89 * value;
                    case "Dollar" :
                        return value;
                    case "Peso" :
                        return 21.57 * value;
                    default:
                        return 0;
                }
            case "Peso" :
                switch (To) {
                    case "Yen" :
                        return 2.29 * value;
                    case "Euro" :
                        return 0.041 * value;
                    case "Dollar" :
                        return 0.046 * value;
                    case "Peso" :
                        return value;
                    default:
                        return 0;
                }
            default:
                return 0;
        }
    }

    public boolean isConnectedToServer(){
        try {
            Log.i("isConnectedToServer", "debut");
            URL serverURL = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
            URLConnection urlconn = serverURL.openConnection();
            urlconn.connect();

            Log.i("isConnectedToServer", "connexion");
            //InternetConnexionMessage.showToast((Context) this, "Internet connexion");
            return true;

        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        //InternetConnexionMessage.showToast((Context) this, "No internet connexion");
        return false;
    }


}