package com.example.safekey;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import pojo.Usuario;
import utilidades.Cesar;

public class LoginActivity extends AppCompatActivity {
    //componentes
    private Button btnCero;
    private Button btnUno;
    private Button btnDos;
    private Button btnTres;
    private Button btnCuatro;
    private Button btnCinco;
    private Button btnSeis;
    private Button btnSiete;
    private Button btnOcho;
    private Button btnNueve;
    private Button btnBorrar;
    private TextView tvPin;
    private TextView tvLinkLogin;
    private TextView tvLinkRecuperar;
    //
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvLinkLogin = findViewById(R.id.tvLinkLogin);
        tvLinkRecuperar = findViewById(R.id.tvLinkRecuperar);
        btnCero = findViewById(R.id.btnCero);
        btnUno = findViewById(R.id.btnUno);
        btnDos = findViewById(R.id.btnDos);
        btnTres = findViewById(R.id.btnTres);
        btnCuatro = findViewById(R.id.btnCuatro);
        btnCinco = findViewById(R.id.btnCinco);
        btnSeis = findViewById(R.id.btnSeis);
        btnSiete = findViewById(R.id.btnSiete);
        btnOcho = findViewById(R.id.btnOcho);
        btnNueve = findViewById(R.id.btnNueve);
        btnBorrar = findViewById(R.id.btnBorrar);
        tvPin = findViewById(R.id.tvPin);
        btnCero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(0);
            }
        });

        btnUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(1);
            }
        });

        btnDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(2);
            }
        });

        btnTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(3);
            }
        });

        btnCuatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(4);
            }
        });

        btnCinco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(5);
            }
        });

        btnSeis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(6);
            }
        });

        btnSiete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(7);
            }
        });

        btnOcho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(8);
            }
        });

        btnNueve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDigito(9);
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvPin.getText().toString().isEmpty()){
                    String pin = tvPin.getText().toString().substring(0, tvPin.getText().length() -1);
                    tvPin.setText(pin);
                }
            }
        });

        tvLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irALoginAlt();
            }
        });

        tvLinkRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irARecuperar();
            }
        });

    }

    private void agregarDigito(int digito) {
        String add = tvPin.getText() + "" + digito;
        tvPin.setText(add);
        if (tvPin.getText().toString().length() == 4){
            //Aqui irà la función para hacer login
            Log.d("TAG_", "agregarDigito: " + tvPin.getText());
            if(loginConSharedPreferences(tvPin.getText().toString())){
                Toast.makeText(this, "¡Ingreso exitoso!", Toast.LENGTH_SHORT).show();
                guardarDatos();
            } else {
                Toast.makeText(this, "¡Error: PIN EQUIVOCADO!", Toast.LENGTH_SHORT).show();
                tvPin.setText("");
            }
        }
    }

    private boolean loginConSharedPreferences(String pn) {
        try {
            Cesar enc = new Cesar();
            boolean login = false;
            SharedPreferences p = getSharedPreferences("datos", Context.MODE_PRIVATE);
            Map<String, ?> keys = p.getAll();
            for(Map.Entry<String, ?>k: keys.entrySet()){
                String correo = k.getKey().toString();
                String[] parts = k.getValue().toString().split("; ");
                String id = parts[0];
                String nombre = parts[1];
                String clave = parts[2];
                String pin = parts[3];
                String codigo = parts[4];
                login = enc.desencriptar(pin).equals(pn);
                Log.d("TAG_", pn);
                Log.d("TAG_", enc.desencriptar(pin));
                usuario = new Usuario(0, nombre, correo, clave, codigo, pin);
            }
            return login;
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
            return false;
        }
    }
    public void guardarDatos(){
        //instancia
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editarPreferencias = preferencias.edit();
        //Edición
        String spu = usuario.getId() + "; " + usuario.getNombre() + "; " + usuario.getClave() + "; " + usuario.getPin() + "; " + usuario.getCodigo() + "; 1";
        editarPreferencias.putString(usuario.getCorreo(), spu);
        //guardado
        editarPreferencias.commit();
        irAPanel();
    }
    private void irAPanel() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void irALoginAlt() {
        Intent i = new Intent(LoginActivity.this, LoginAltActivity.class);
        startActivity(i);
    }

    private void irARecuperar() {
        Intent i = new Intent(LoginActivity.this, RecuperarActivity.class);
        startActivity(i);
    }
}
