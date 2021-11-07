package com.happ.hackathonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TipoActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button btntipo;
    String id_usuario;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo);
        id_usuario=getIntent().getStringExtra("id_usuario");
        spinner=findViewById(R.id.spinner);
        btntipo=findViewById(R.id.btntipo);
        requestQueue = Volley.newRequestQueue(this);
        String[] tipos;




        btntipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readTipos("http://192.168.56.1/hackathon/tipos.php");
                //String seleccion=spinner.getSelectedItem().toString();
            }
        });
    }
    //codigo para pedir datos
    private void readTipos(String URL){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                            String tipos=response.toString();
                            //String ob1=response.getJSONArray("nombre").getString("nombre");
                            Toast.makeText(TipoActivity.this,tipos,Toast.LENGTH_SHORT).show();
                            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipo);
                            //spinner.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TipoActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}