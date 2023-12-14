package com.example.polygongps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.widget.ImageButton;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    Location currentLocation;
    FusedLocationProviderClient fusedClient;
    private static final int REQUEST_CODE = 101;
    FrameLayout map;
    private List<LatLng> coordinateList = new ArrayList<>();
    private Polygon polygon;
    private GoogleMap googleMap;

    private Marker userMarker;

    private float currentBearing = 0;

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

        // Obtenha a latitude e longitude da localização atual
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

        // Crie um objeto LatLng com a localização atual
        LatLng latLng = new LatLng(latitude, longitude);

        // Configurar opções do marcador
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Sua localização");

        // Adicionar marcador ao mapa e manter a referência para atualizações
        userMarker = googleMap.addMarker(markerOptions);

        // Mover a câmera para a localização atual com zoom
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }


    // Método para atualizar a posição do marcador quando a localização do usuário muda
    public void updateMarkerLocation(Location newLocation) {
        if (googleMap != null && userMarker != null) {
            LatLng newLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
            userMarker.setPosition(newLatLng);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));
        }
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

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

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

