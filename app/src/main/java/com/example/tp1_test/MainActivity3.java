package com.example.tp1_test;
// Author : wassim6975

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity3 extends AppCompatActivity {

    public SqlLite database;
    public HashMap<String, String> dataChange;
    public ArrayList<String> devises = new ArrayList<>();

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
        setContentView(R.layout.activity_main3);
    }

    public void onStart() {
        super.onStart();


        // Modification des valeurs des taux par l'utilisateur
        Button buttonModifier = findViewById(R.id.buttonModificationTaux);
        buttonModifier.setOnClickListener(myHandler);

        // Connexion à la base de données
        database = (SqlLite) new SqlLite(this);

        ListView listViewDevises = (ListView) findViewById(R.id.listViewDevises);
        Spinner deviseSelectedSpinner = (Spinner) findViewById(R.id.selectMonnaieToModify);

        // Affichage dans la tableView
        try {
            InternetConnexionMessage connexionTest = (InternetConnexionMessage) new InternetConnexionMessage().execute();
            Boolean isConnected = connexionTest.get();

            if (isConnected) {
                // If connexion : take the data from the online server
                AsyncTaskRunner getXml = (AsyncTaskRunner) new AsyncTaskRunner().execute();
                dataChange = getXml.get();
            }
            else {
                // If no connexion : take the data from the database
                dataChange = database.getAllDevises();
            }

            for (int i = 1; i < dataChange.size(); i++){
                String devise = (String) dataChange.keySet().toArray()[i];
                String taux = dataChange.get(devise);
                devises.add(devise+" ("+taux+")");
                Log.i("Activity2", "devise : "+devise);
                Log.i("Activity2", "taux : "+ taux);
            }
            // For tableview
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, devises);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listViewDevises.setAdapter(arrayAdapter);

            // For spinner
            ArrayList test = new ArrayList<String>(dataChange.keySet());
            ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, test);
            arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            deviseSelectedSpinner.setAdapter(arrayAdapter2);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    View.OnClickListener myHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // Nouvelle valeur du taux venant de l'utilisateur
            EditText EditnewTaux = (EditText) findViewById(R.id.newTaux);
            String newTaux = EditnewTaux.getText().toString();

            Spinner deviseSelectedSpinner = (Spinner) findViewById(R.id.selectMonnaieToModify);
            String deviseSelected = deviseSelectedSpinner.getSelectedItem().toString();
            Log.i("Devise", deviseSelected);
            Log.i("Taux", newTaux);

            // Proposer à l'utilisateur de modifier les taux dans la base de données
            // Modification du taux dans la base de données
            database.updateDevise(deviseSelected, newTaux);
        }
    };
}