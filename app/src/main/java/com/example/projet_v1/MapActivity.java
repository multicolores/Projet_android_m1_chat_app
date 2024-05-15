package com.example.projet_v1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

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
    private MapView map;

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

       //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));


        setupMap();
    }


    /**
     * Initializes the map using the osmdroid library
     * Setup map with marker of other users
     * The map is centered on user position with an initial zoom level
     */
    private void setupMap() {
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        // TODO - use user location
        map.getController().setCenter(new GeoPoint(49.84526983293686, 3.2876876966705533));
        map.getController().setZoom(15);

        // TODO - Set marker based on users list from DB
        addMarker(map, 49.838762383443864, 3.2997569262111686, "Benois", Color.RED);
        addMarker(map, 49.854778141717055, 3.2779157274471276, "Antoine", Color.BLUE);
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
}