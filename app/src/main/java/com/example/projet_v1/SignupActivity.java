package com.example.projet_v1;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
private FirebaseFirestore BDD;
TextView Location;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_signup);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });

    this.BDD = FirebaseFirestore.getInstance();

    Intent intentSingUp = getIntent();
    double latitude = intentSingUp.getDoubleExtra("Latitude", 0.0);
    double longitude = intentSingUp.getDoubleExtra("Longitude", 0.0);
    this.Location = findViewById(R.id.textView_location_info);
    Location.setText("Latitude: " + latitude + "\nLongitude: " + longitude);

    Button SignUp = findViewById(R.id.Button_signup);
    SignUp.setOnClickListener(View -> {
        EditText TextNom = findViewById(R.id.input_SignupName);
        String Nom = TextNom.getText().toString();

        EditText TextMdp = findViewById(R.id.input_SignupMDP);
        String Mdp = TextMdp.getText().toString();

        if (Nom.isEmpty() || Mdp.isEmpty()) {
            Toast.makeText(this, "Remplissez le Nom et Mot de passe", Toast.LENGTH_SHORT).show();
        } else {
            // Vérifier si le document existe déjà
            this.BDD.collection("Compte").document(Nom).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Le document existe déjà
                            Toast.makeText(this, "Un compte avec ce nom existe déjà.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Le document n'existe pas donc création du compte
                            Map<String, Object> CreationCompte = new HashMap<>();
                            CreationCompte.put("Nom", Nom);
                            CreationCompte.put("MotDePasse", Mdp);
                            CreationCompte.put("Latitude", latitude);
                            CreationCompte.put("Longitude", longitude);

                            this.BDD
                                    .collection("Compte")
                                    .document(Nom)
                                    .set(CreationCompte)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Création du compte avec succès", Toast.LENGTH_LONG).show();

                                        Intent intentMap = new Intent(this, MapActivity.class);
                                        intentMap.putExtra("Latitude", latitude);
                                        intentMap.putExtra("Longitude", longitude);
                                        intentMap.putExtra("Nom", Nom);
                                        startActivity(intentMap);

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Échec de la création du compte", Toast.LENGTH_LONG).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur lors de la vérification du compte", Toast.LENGTH_SHORT).show();
                    });
        }
    });
}
}