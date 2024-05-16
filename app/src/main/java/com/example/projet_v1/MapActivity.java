package com.example.projet_v1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MapActivity extends AppCompatActivity implements LocationListener {
    private MapView map;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupMap();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 500, this);
    }


    /**
     * Initializes the map using the osmdroid library
     * Setup map with marker of other users
     * The map is centered on user position with an initial zoom level
     */
    private void setupMap() {
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("Latitude", 0.0);
        double longitude = intent.getDoubleExtra("Longitude", 0.0);

        map.getController().setCenter(new GeoPoint(latitude, longitude));
        map.getController().setZoom(10);

        // Set marker based on users list from DB
        getUsersInfoFromDb();
    }

    /**
     * Adds a custom marker to the map.
     * The marker style is based on custom_marker.png and has a custom color
     * @param map The MapView to which the marker will be added.
     * @param latitude Latitude of the marker.
     * @param longitude Longitude of the marker.
     * @param text Text associated with the marker.
     * @param color Color of the marker.
     */
    private void addMarker(MapView map, double latitude, double longitude, String text, int color) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(latitude, longitude));

        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.custom_marker);
        if(markerBitmap!=null) {
            markerBitmap = Bitmap.createScaledBitmap(markerBitmap, 120, 120, false);
            markerBitmap = tintBitmap(markerBitmap, color);
            Drawable drawable = new BitmapDrawable(getResources(), markerBitmap);
            marker.setIcon(drawable);
        }

        marker.setTitle(text);
        map.getOverlays().add(marker);
    }

    /**
     * Applies a color filter to a Bitmap in order to customize marker with custom color
     */
    private Bitmap tintBitmap(Bitmap bitmap, int color) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mutableBitmap, 0, 0, paint);
        return mutableBitmap;
    }

    /**
     * Method called to update the zoom level and the location displayed on the map matching a selected user
     */
    public void onClickChangeView(View view) {
        map.getController().setZoom(18);

        double newLatitude = 49.838762383443864;
        double newLongitude = 3.2997569262111686;
        map.getController().setCenter(new GeoPoint(newLatitude, newLongitude));
    }

    /**
     * Remove all markers from the map
     */
    private void removeAllMarkers() {
        for (Overlay overlay : map.getOverlays()) {
            if (overlay instanceof Marker) {
                map.getOverlays().remove(overlay);
            }
        }
    }

    /**
     * Retrieves user information from the Firestore database and adds markers to the map based on the retrieved data.
     * When DB data changes, update the map based on the new retrieved data.
     * The markers represent the users on the map, and each marker's color is determined by the user's name.
     */
    private void getUsersInfoFromDb() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("Compte");

        usersCollection.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("DB_LISTENER", "Error while listening database changes", e);
                return;
            }

            // Remove old markers as modifications has been made in DB
            removeAllMarkers();

            for (DocumentSnapshot doc : snapshot) {
                if (doc.exists()) {
                    String userId = doc.getId();
                    String nom = doc.getString("Nom");
                    double latitude = doc.getDouble("Latitude");
                    double longitude = doc.getDouble("Longitude");

                    if (latitude != 0 || longitude != 0) {
                        // TODO - créer liste de users à afficher en dessous de la map, quand on click dessus on peut zoom vers l’endroit grâce à la fonction onClickChangeView()
                        addMarker(map, latitude, longitude, nom, getColorForUser(nom));
                    } else {
                        Log.d("DB_LISTENER", "Latitude ou longitude manquante pour l'utilisateur avec ID: " + userId + "et nom: " + nom);
                    }

                } else {
                    Log.d("DB_LISTENER", "Aucun utilisateur trouvé.");
                }
            }
        });
    }

    /**
     * Generates a color based on the user's name.
     * The same name will always result in the same color.
     */
    private int getColorForUser(String userName) {
        int hash = userName.hashCode();
        hash = Math.abs(hash); // Ensure that the hash code is always positive
        // Extract the least significant 3 bytes of the hash code and use them to generate an RGB color ( 0-255 )
        return Color.rgb(hash & 0xFF, (hash >> 8) & 0xFF, (hash >> 16) & 0xFF);
    }


    /**
     * Get location info from user
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Intent intent = getIntent();
        double oldLatitude = intent.getDoubleExtra("Latitude", 0.0);
        double oldLongitude = intent.getDoubleExtra("Longitude", 0.0);

        if(oldLongitude != longitude || oldLatitude != latitude) {
            updateLocationOfUserInDb(latitude, longitude, intent.getStringExtra("Nom"));
        }
    }

    /**
     * Update latitude and longitude values in DB for a user
     */
    private void updateLocationOfUserInDb(double latitude, double longitude, String nom) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("Compte");

        Query query = usersCollection.whereEqualTo("Nom", nom);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    document.getReference().update("Latitude", latitude, "Longitude", longitude)
                            .addOnSuccessListener(aVoid -> Log.d("DB_UPDATE", "Location updated successfully for user: " + nom))
                            .addOnFailureListener(e -> Log.w("DB_UPDATE", "Error updating location for user: " + nom, e));
                }
            } else {
                Log.d("DB_UPDATE", "Error getting documents: ", task.getException());
            }
        });
    }
}