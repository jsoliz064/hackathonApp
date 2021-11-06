package com.happ.hackathonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
Button btnLogin;
TextView txtSignUp;
EditText edtUsuario,edtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin=findViewById(R.id.buttonLogin);
        txtSignUp=findViewById(R.id.signUpText);
        edtUsuario=findViewById(R.id.usuario);
        edtPassword=findViewById(R.id.password);

        txtSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(getApplicationContext(),SingUp.class);
                //intent.putExtra("id_usuario",id);
                startActivity(intent);
                finish();
            }

        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                validarUsuario("http://192.168.56.1/hackathon/validar_usuariopg.php");
            }

        });
    }
    private void validarUsuario(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    //id=response;
                    Intent intent=new Intent(getApplicationContext(),PrincipalActivity.class);
                    //intent.putExtra("id_usuario",id);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "Usuario o contraseña incorrecta",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("usuario",edtUsuario.getText().toString());
                parametros.put("password",edtPassword.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}