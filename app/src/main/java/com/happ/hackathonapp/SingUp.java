package com.happ.hackathonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Hashtable;
import java.util.Map;

public class SingUp extends AppCompatActivity {
    String URL_REGISTRAR = "http://192.168.56.1/hackathon/registrar.php";
    EditText edtNombre,edtEmail,edtPassword,getEdtPasswordConfirm;
    Button btnRegistrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        edtNombre=findViewById(R.id.nombre);
        edtEmail=findViewById(R.id.email);
        edtPassword=findViewById(R.id.password);
        getEdtPasswordConfirm=findViewById(R.id.passwordconfirm);
        btnRegistrar=findViewById(R.id.buttonSignUp);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre=edtNombre.getText().toString()+"";
                String email=edtEmail.getText().toString()+"";
                String password=edtPassword.getText().toString()+"";
                String passwordConfirm=getEdtPasswordConfirm.getText().toString()+"";
                if (password.equals(passwordConfirm)){
                    registrar(nombre,email,password);
                }else{
                    Toast.makeText(SingUp.this, "ERROR: Las Contraseñas no coinciden", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    public void registrar(final String nombre, final String email,final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SingUp.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("nombre", nombre);
                params.put("email", email);
                params.put("password",password);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}