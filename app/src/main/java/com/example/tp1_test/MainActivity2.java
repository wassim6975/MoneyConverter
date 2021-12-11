package com.example.tp1_test;
// Author : wassim6975

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity2 extends AppCompatActivity {

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
        setContentView(R.layout.activity_main2);


        // Affichage de l'ensemble des taux des différentes devises
        ListView listDevises = (ListView) findViewById(R.id.listViewDevises);
        SqlLite database = (SqlLite) new SqlLite(this);

        try {
            InternetConnexionMessage connexionTest = (InternetConnexionMessage) new InternetConnexionMessage().execute();
            Boolean isConnected = connexionTest.get();

            if (isConnected == true) {
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
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, devises);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listDevises.setAdapter(arrayAdapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}