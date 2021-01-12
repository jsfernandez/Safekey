package com.example.safekey;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import pojo.Usuario;
import utilidades.Cesar;

public class RegistroUsuarioActivity extends AppCompatActivity {
    //Componentes
    private TextInputLayout tilNombre;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilClave;
    private TextInputLayout tilRClave;
    private TextInputLayout tilPin;
    private TextInputLayout tilRPin;
    private Button btnRegistro;
    //Parametros
    private Usuario usuario;
    private boolean paso = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        //Bindings
        tilNombre = findViewById(R.id.tilNombreMD);
        tilCorreo = findViewById(R.id.tilCorreoMD);
        tilClave = findViewById(R.id.tilPasswordMD);
        tilRClave = findViewById(R.id.tilRePasswMD);
        tilPin = findViewById(R.id.tilPinMD);
        tilRPin = findViewById(R.id.tilRePinMD);
        btnRegistro = findViewById(R.id.btnActualizarMD);
        bienvenido();
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tilNombre.getEditText().getText().toString().isEmpty()){
                    tilNombre.setError("Favor ingrese su nombre");
                } else if(tilCorreo.getEditText().getText().toString().isEmpty()){
                    tilNombre.setError(null);
                    tilCorreo.setError("Favor ingrese su correo");
                } else if(tilClave.getEditText().getText().toString().isEmpty()){
                    tilCorreo.setError(null);
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
                        Cesar enc = new Cesar();
                        String nombre = tilNombre.getEditText().getText().toString();
                        String correo = tilCorreo.getEditText().getText().toString();
                        String clave = enc.encriptar(tilClave.getEditText().getText().toString());
                        String pin = enc.encriptar(tilPin.getEditText().getText().toString());
                        String code = generarCodigo();
                        usuario = new Usuario(0, nombre, correo, clave, code, pin);
                        guardarDatos(usuario);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paso){
            Intent i = new Intent(RegistroUsuarioActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    private void enviarCorreoRespaldo() {
        String subject = "Bienvenido a SafeKeys";
        String message = "Hola " + usuario.getNombre() + ",\n";
        message = message + "Por favor guarde este codigo para que en caso necesario pueda restableces su contraseña: " + usuario.getCodigo();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ usuario.getCorreo()});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        //need this to prompts email client only
        email.setType("message/rfc822");
        try{
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }catch(Exception ex){
            Log.d("TAG_", "Error: " + ex.getMessage());
        }
    }

    private void guardarDatos(Usuario u){
        //instancia
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editarPreferencias = preferencias.edit();
        //Edición
        String spu = u.getId() + "; " + u.getNombre() + "; " + u.getClave() + "; " + u.getPin() + "; " + u.getCodigo() + "; 0";
        editarPreferencias.putString(u.getCorreo(), spu);
        //guardado
        editarPreferencias.commit();
        Log.d("TAG_", "Registro exitoso");
        paso = true;
        info();
        //Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
    }

    public void info(){
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(this);
        infoDialog.setTitle("Cuenta creada exitosamente");
        infoDialog.setMessage("Cuenta creada exitosamente\n" +
                "¿Desea enviar a su correo un codigo para restablecer su contraseña\n"+
                "de ser necesario en el futuro?");
        infoDialog.setIcon(R.drawable.ic_credencial_abt);
        infoDialog.setPositiveButton("¡Perfecto!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enviarCorreoRespaldo();
            }
        });
        AlertDialog aler = infoDialog.create();
        aler.show();
    }

    private String generarCodigo(){
        // define the range
        int max = 90;
        int min = 65;
        int range = max - min + 1;
        String s = "C";
        // generate random numbers within 1 to 10
        for (int i = 0; i < 8; i++) {
            int rand = (int)(Math.random() * range) + min;
            // Output is different everytime this code is executed
            char c = (char)rand;
            Log.d("TAG_", "Letra: " + c);
            s = s + "" + Character.toString(c);
        }
        return s;
    }

    public void bienvenido(){
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(this);
        infoDialog.setTitle("Bienvenido,\n¿Primera vez aquí?");
        infoDialog.setMessage("Vamos a crear una cuenta para tu primer uso.");
        infoDialog.setIcon(R.drawable.ic_shild_abt);
        infoDialog.setPositiveButton("¡Entiendo!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        infoDialog.setNegativeButton("¡Tengo un respaldo!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                irACargaDeRespaldos();
            }
        });
        AlertDialog aler = infoDialog.create();
        aler.show();
    }

    public void irACargaDeRespaldos(){
        Intent i = new Intent(RegistroUsuarioActivity.this, CargarActivity.class);
        startActivity(i);
    }
}
