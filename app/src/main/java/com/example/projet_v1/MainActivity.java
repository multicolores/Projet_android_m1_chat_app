package com.example.projet_v1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.location.LocationManager;
import android.location.LocationListener;
import android.Manifest;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int PERMISSION_REQUEST_CODE = 100;
    TextView textView;

    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private FirebaseFirestore BDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.BDD = FirebaseFirestore.getInstance();
        Button SignIn = findViewById(R.id.Button_signin);

        SignIn.setOnClickListener(View -> {
            EditText TextNom = findViewById(R.id.input_name);
            String Nom = TextNom.getText().toString();

            EditText TextMdp = findViewById(R.id.input_mdp);
            String Mdp = TextMdp.getText().toString();

            if (Nom.isEmpty() || Mdp.isEmpty()) {
                Toast.makeText(this, "Remplissez le Nom et Mot de passe", Toast.LENGTH_SHORT).show();
            } else {
                Map<String, Object> Connexion = new HashMap<>();
                Connexion.put("Nom", Nom);
                Connexion.put("MotDePasse", Mdp);

                this.BDD
                        .collection("Compte")
                        .document(Nom)
                        .set(Connexion)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Succes ", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Echec ", Toast.LENGTH_LONG).show();
                        });
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if permissions are valids
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if they are not granted yet
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        // Get device location
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 500, this);
    }

    public void onClickGoMapPage(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
         latitude = location.getLatitude();
         longitude = location.getLongitude();

        this.textView=findViewById(R.id.textView_location_info);
        this.textView.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
    }

    // Other LocationListener methods
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void onClickSignupPage(View view) {
        Intent intentSingUp = new Intent(this, SignupActivity.class);
        intentSingUp.putExtra("Latitude", latitude);
        intentSingUp.putExtra("Longitude", longitude);
        startActivity(intentSingUp);
    }
}