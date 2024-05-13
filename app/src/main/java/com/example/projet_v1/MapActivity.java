package com.example.projet_v1;

import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setupMap();
    }

    private void setupMap() {
        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setCenter(new GeoPoint(49.84526983293686, 3.2876876966705533));
        map.getController().setZoom(15);

        addMarker(map, 49.838762383443864, 3.2997569262111686);
        addMarker(map, 49.854778141717055, 3.2779157274471276);
    }

    private void addMarker(MapView map, double latitude, double longitude) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(latitude, longitude));
        map.getOverlays().add(marker);
    }
}