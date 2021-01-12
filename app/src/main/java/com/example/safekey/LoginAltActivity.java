package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

import pojo.Usuario;
import utilidades.Cesar;

public class LoginAltActivity extends AppCompatActivity {
    //Componentes
    private TextInputLayout tilCorreo;
    private TextInputLayout tilClave;
    private Button btnIngreso;
    //
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_alt);
        tilCorreo = findViewById(R.id.tilCorreoLogin);
        tilClave = findViewById(R.id.tilClaveLogin);
        btnIngreso = findViewById(R.id.btnIngresoLoginC);

        btnIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tilCorreo.getEditText().getText().toString().isEmpty()){
                    tilCorreo.setError("Favor ingrese el correo con el que se registro");
                } else if(tilClave.getEditText().getText().toString().isEmpty()){
                    tilCorreo.setError(null);
                    tilClave.setError("Favor ingrese la contraseña con la que se registro");
                } else {
                    tilClave.setError(null);

                    if(loginConSharedPreferences(tilCorreo.getEditText().getText().toString(), tilClave.getEditText().getText().toString())){
                        Toast.makeText(LoginAltActivity.this, "¡Ingreso exitoso!", Toast.LENGTH_SHORT).show();
                        guardarDatos();
                    } else {
                        Toast.makeText(LoginAltActivity.this, "Error de correo o contraseña.", Toast.LENGTH_SHORT).show();
                        tilClave.getEditText().setText("");
                    }
                }
            }
        });
    }
    private boolean loginConSharedPreferences(String corr, String pass) {
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
                login = enc.desencriptar(clave).equals(pass) && correo.equals(corr);
                Log.d("TAG_", pass);
                Log.d("TAG_", enc.desencriptar(clave));
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
        Intent i = new Intent(LoginAltActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
