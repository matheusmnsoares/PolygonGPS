package com.example.polygongps.ui.theme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.polygongps.MainActivity;
import com.example.polygongps.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class rese {
}
/*public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Location currentLocation;
    FusedLocationProviderClient fusedClient;
    private static final int REQUEST_CODE = 101;
    FrameLayout map;
    private List<LatLng> coordinateList = new ArrayList<>();
    private Polygon polygon;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        map = findViewById(R.id.map);

        coordinateList = new ArrayList<>();
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    // Agora que temos a localização, inicialize o mapa
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(com.example.polygongps.MapsActivity.this);
                } else {
                    Log.e("MapsActivity", "Localização atual é nula");
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("MapReady", "onMapReady called");
        this.googleMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Você está aqui!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        googleMap.addMarker(markerOptions);
    }


    public void slide(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void addLocationToList(View view) {
        // Verifique as permissões
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        getLocation();

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            coordinateList.add(latLng);

            String markerLabel = "Coordenada " + coordinateList.size();
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(markerLabel);
            googleMap.addMarker(markerOptions);

            Toast.makeText(this, "Coordenadas adicionada com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeLastLocation(View view) {
        if (!coordinateList.isEmpty()) {
            coordinateList.remove(coordinateList.size() - 1);
            Toast.makeText(this, "Última coordenada excluida com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lista de Coordenadas está vazia!", Toast.LENGTH_SHORT).show();
        }
    }

    public void plotPolygon(View view) {
        if (coordinateList.size() >= 3) { // Pelo menos 3 coordenadas são necessárias para um polígono
            if (polygon != null) {
                polygon.remove(); // Remove o polígono anterior, se existir
            }

            PolygonOptions polygonOptions = new PolygonOptions()
                    .addAll(coordinateList)
                    .strokeWidth(5)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(128, 255, 0, 0)); // Ajuste a cor conforme necessário

            if (googleMap != null) {
                polygon = googleMap.addPolygon(polygonOptions);

                // Foca a câmera no polígono
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng point : coordinateList) {
                    builder.include(point);
                }
                LatLngBounds bounds = builder.build();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50)); // 50 é o padding
            } else {
                Toast.makeText(this, "GoogleMap not ready yet", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(this, "Polígono plotado no mapa!", Toast.LENGTH_SHORT).show();
        } else {
            // Manipule o caso em que não há coordenadas suficientes para formar um polígono
            Toast.makeText(this, "Adicione pelo menos 3 coordenadas para formar um polígono!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
        }
    }

}


    <?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:id="@+id/frameLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

<!-- Map Fragment -->

<fragment
        android:id="@+id/map"
                android:name="com.google.android.gms.maps.SupportMapFragment"
                android:layout_width="0dp"
                android:layout_height="0dp"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                app:layout_constraintVertical_bias="0" />

<ImageView
        android:id="@+id/mapBar"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:src="@drawable/frame_4"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent" />

<ImageButton
        android:id="@+id/mapBack"
                android:layout_width="55dp"
                android:layout_height="48dp"
                android:layout_marginTop="4dp"
                android:layout_marginEnd="292dp"
                android:backgroundTint="#00FFFFFF"
                android:onClick="slide"
                android:src="@drawable/back"
                app:layout_constraintEnd_toEndOf="@+id/mapBar"
                app:layout_constraintTop_toTopOf="@+id/map" />

<ImageButton
        android:id="@+id/mapMinus"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:backgroundTint="#00FFFFFF"
                android:src="@drawable/map_minus"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                android:onClick="removeLastLocation" />

<ImageButton
        android:id="@+id/mapPlus"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:backgroundTint="#00FFFFFF"
                android:src="@drawable/map_plus"
                app:layout_constraintBottom_toTopOf="@+id/mapMinus"
                app:layout_constraintStart_toStartOf="parent"
                android:onClick="addLocationToList" />

<ImageButton
        android:id="@+id/mapCheck"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:backgroundTint="#00FFFFFF"
                android:src="@drawable/map_check"
                app:layout_constraintBottom_toTopOf="@+id/mapPlus"
                app:layout_constraintStart_toStartOf="parent"
                android:onClick="plotPolygon"/>


</androidx.constraintlayout.widget.ConstraintLayout>
*/