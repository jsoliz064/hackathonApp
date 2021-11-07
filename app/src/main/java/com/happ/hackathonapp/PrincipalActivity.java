package com.happ.hackathonapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class PrincipalActivity extends AppCompatActivity {
    int PICK_IMAGE = 100;
    Uri imageUri;
    Button btnGaleria, btnSubirImagenes;
    GridView gvImagenes;
    List<Uri> listaImagenes = new ArrayList<>();
    List<String> listaBase64Imagenes = new ArrayList<>();
    GridViewAdapter baseAdapter;
    String id_usuario;
    String id_reporte;
    String URL_UPLOAD_IMAGENES = "http://192.168.56.1/hackathon/cargarimagen.php/";
    String URL_REPORTE = "http://192.168.56.1/hackathon/reporte.php";
    RequestQueue requestQueue;
    EditText desc;

public static final int REQUEST_CODE=1;
String latitud;
String longitud;
FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        latitud="";
        longitud="";
        desc=findViewById(R.id.edtdesc);
        btnSubirImagenes=findViewById(R.id.btnSubirImagenes);
        requestQueue = Volley.newRequestQueue(this);

        gvImagenes = findViewById(R.id.gvImagenes);
        btnGaleria = findViewById(R.id.btnGaleria);
        id_usuario=getIntent().getStringExtra("id_usuario");
        id_reporte="";
        ObtenerCoordendasActual();
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnSubirImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descripcion=desc.getText().toString()+"";
                if (descripcion.length()>0){
                    //subirImagenes();
                    registrarReporte(id_usuario,latitud,longitud,descripcion);
                }else{
                    Toast.makeText(PrincipalActivity.this, "Ingrese una descripcion", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    //crear reporte
    public void registrarReporte(final String id_usuario,final String latitud, final String longitud,final String descripcion) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REPORTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        id_reporte=response;
                        Toast.makeText(PrincipalActivity.this, "reporte creado", Toast.LENGTH_LONG).show();
                        subirImagenes();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("id_usuario", id_usuario);
                params.put("latitud", latitud);
                params.put("longitud", longitud);
                params.put("desc",descripcion);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    //
    public void ObtenerCoordendasActual() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PrincipalActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        } else {
            getCoordenada();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCoordenada();
            } else {
                Toast.makeText(this, "Permiso Denegado ..", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void getCoordenada() {
        try {
            //progressBar.setVisibility(View.VISIBLE);
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(PrincipalActivity.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        double lati = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        double longi = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        latitud=String.valueOf(lati);
                        longitud=String.valueOf(longi);
                    }
                }

            }, Looper.myLooper());

        }catch (Exception ex){
            System.out.println("Error es :" + ex);
        }

    }
    public void subirImagenes() {
        if (id_reporte.length()>0){
            listaBase64Imagenes.clear();
            for(int i = 0 ; i < listaImagenes.size() ; i++) {
                try {
                    InputStream is = getContentResolver().openInputStream(listaImagenes.get(i));
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    String cadena = convertirUriToBase64(bitmap);

                    enviarImagenes(id_reporte, cadena);

                    bitmap.recycle();

                } catch (IOException e) { }
            }
        }else{
            Toast.makeText(PrincipalActivity.this, "ERROR: Hubo un problema", Toast.LENGTH_LONG).show();
        }

    }

    public void enviarImagenes(final String reporte, final String cadena) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD_IMAGENES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(PrincipalActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("id_reporte", reporte);
                params.put("imagenes", cadena);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public String convertirUriToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String encode = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encode;
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECCIONA LAS IMAGENES"), PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ClipData clipData = data.getClipData();

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            if(clipData == null) {
                imageUri = data.getData();
                listaImagenes.add(imageUri);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    listaImagenes.add(clipData.getItemAt(i).getUri());
                }
            }
        }

        baseAdapter = new GridViewAdapter(PrincipalActivity.this, listaImagenes);
        gvImagenes.setAdapter(baseAdapter);

    }
}