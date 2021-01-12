package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

import pojo.Usuario;
import utilidades.Cesar;

public class RecuperarActivity extends AppCompatActivity {
    private TextInputLayout tilCodigo;
    private TextInputLayout tilClave;
    private TextInputLayout tilRClave;
    private TextInputLayout tilPin;
    private TextInputLayout tilRPin;
    private Button btnActualizar;
    //Parametros
    private Usuario usuario;
    private boolean paso = false;
    private Cesar clv = new Cesar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);
        tilCodigo = findViewById(R.id.tilCodigo);
        tilClave = findViewById(R.id.tilPasswordRe);
        tilRClave = findViewById(R.id.tilRePasswRe);
        tilPin = findViewById(R.id.tilPinRe);
        tilRPin = findViewById(R.id.tilRePinRe);
        btnActualizar = findViewById(R.id.btnActualizarRe);
        leerSharedPreferences();
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tilCodigo.getEditText().getText().toString().isEmpty()){
                    tilCodigo.setError("Favor ingrese el codigo que envio a su correo");
                } else if(tilClave.getEditText().getText().toString().isEmpty()){
                    tilCodigo.setError(null);
                    tilClave.setError("Favor ingrese una clave");
                } else if(tilRClave.getEditText().getText().toString().isEmpty()){
                    tilClave.setError(null);
                    tilRClave.setError("Favor ingrese su correo");
                } else if(tilPin.getEditText().getText().toString().isEmpty()){
                    tilRClave.setError(null);
                    tilPin.setError("Favor ingrese su correo");
                } else if(tilRPin.getEditText().getText().toString().isEmpty()){
                    tilPin.setError(null);
                    tilRPin.setError("Favor ingrese su correo");
                } else {
                    if (!tilClave.getEditText().getText().toString().equals(tilRClave.getEditText().getText().toString())){
                        tilRClave.setError("Ambas contraseñas deben ser iguales");
                    } else if(!tilPin.getEditText().getText().toString().equals(tilRPin.getEditText().getText().toString())){
                        tilRPin.setError("Ambos pin deben ser iguales");
                    } else{
                        String clave = clv.encriptar(tilClave.getEditText().getText().toString());
                        String pin = clv.encriptar(tilPin.getEditText().getText().toString());
                        if(tilCodigo.getEditText().getText().toString().equals(usuario.getCodigo())){
                            usuario = new Usuario(usuario.getId() +1, usuario.getNombre(), usuario.getCorreo(), clave, usuario.getCodigo(), pin);
                            guardarDatos();
                        } else {
                            Toast.makeText(RecuperarActivity.this, "Codigo invalido", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        tilCodigo.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                activarBoton();
                return false;
            }
        });

        tilClave.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                activarBoton();
                return false;
            }
        });
        tilRClave.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                activarBoton();
                return false;
            }
        });
        tilPin.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                activarBoton();
                return false;
            }
        });
        tilRPin.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                activarBoton();
                return false;
            }
        });
    }

    private void activarBoton(){
        if (!tilCodigo.getEditText().getText().toString().isEmpty() &&
                !tilClave.getEditText().getText().toString().isEmpty() &&
                !tilRClave.getEditText().getText().toString().isEmpty() &&
                !tilPin.getEditText().getText().toString().isEmpty() && !tilRPin.getEditText().getText().toString().isEmpty()) {
            btnActualizar.setBackgroundColor(Color.parseColor("#30323C"));
            btnActualizar.setEnabled(true);
        } else {
            btnActualizar.setBackgroundColor(Color.parseColor("#8F92AA"));
            btnActualizar.setEnabled(false);
        }
    }

    private void leerSharedPreferences() {
        try {
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
                usuario = new Usuario(0, nombre, correo, clave, codigo, pin);
                Log.d("TAG_", correo);
                Log.d("TAG_", k.getValue().toString());
            }
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
        }
    }

    private void guardarDatos(){
        //instancia
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editarPreferencias = preferencias.edit();
        //Pruebas
        Log.d("TAG_", clv.desencriptar(usuario.getPin()));
        //Edición
        String spu = usuario.getId() + "; " + usuario.getNombre() + "; " + usuario.getClave().toString() + "; " + usuario.getPin().toString() + "; " + usuario.getCodigo() + "; 1";
        editarPreferencias.putString(usuario.getCorreo(), spu);
        editarPreferencias.apply();
        //guardado
        boolean result = editarPreferencias.commit();

        if (result){
            Toast.makeText(this, "¡Actualización Exitosa!", Toast.LENGTH_LONG).show();
            finish();
        }
        else
            Toast.makeText(this, "¡Ups!, ha habido un problema", Toast.LENGTH_SHORT).show();
    }
}
