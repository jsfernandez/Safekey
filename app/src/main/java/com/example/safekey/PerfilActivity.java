package com.example.safekey;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

import pojo.ConexionSQLiteHelper;
import pojo.Usuario;
import utilidades.Cesar;
import utilidades.Utilidad;

public class PerfilActivity extends AppCompatActivity {
    //Componentes
    private TextInputLayout tilNombre;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilClave;
    private TextInputLayout tilRClave;
    private TextInputLayout tilPin;
    private TextInputLayout tilRPin;
    private TextInputLayout tilPinOp;
    private Button btnActualizar;
    private FloatingActionButton faBtnEliminar;
    //Parametros
    private Usuario usuario;
    private Usuario viejito;
    private boolean paso = false;
    private Cesar clv = new Cesar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tilNombre = findViewById(R.id.tilNombreMD);
        tilCorreo = findViewById(R.id.tilCorreoMD);
        tilClave = findViewById(R.id.tilPasswordMD);
        tilRClave = findViewById(R.id.tilRePasswMD);
        tilPin = findViewById(R.id.tilPinMD);
        tilRPin = findViewById(R.id.tilRePinMD);
        btnActualizar = findViewById(R.id.btnActualizarMD);
        faBtnEliminar = findViewById(R.id.faBtnEliminar);
        leerSharedPreferences();
        btnActualizar.setOnClickListener(new View.OnClickListener() {
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
                        String nombre = tilNombre.getEditText().getText().toString();
                        String correo = tilCorreo.getEditText().getText().toString();
                        String clave = clv.encriptar(tilClave.getEditText().getText().toString());
                        String pin = clv.encriptar(tilPin.getEditText().getText().toString());
                        usuario = new Usuario(usuario.getId() +1, usuario.getNombre(), usuario.getCorreo(), clave, usuario.getCodigo(), pin);
                        Log.d("TAG_", usuario.getCorreo());
                        Log.d("TAG_", usuario.getPin());
                        procesarOperacionGuardar();
                    }
                }
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

        faBtnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procesarOperacionEliminar();
            }
        });
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
                viejito = usuario;
                Log.d("TAG_", correo);
                Log.d("TAG_", k.getValue().toString());
            }
            cargarDatos();
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
            Toast.makeText(this, "¡Actualización Exitosa!", Toast.LENGTH_SHORT).show();
            //finish();
        }
        else
            Toast.makeText(this, "¡Ups!, ha habido un problema", Toast.LENGTH_SHORT).show();
    }

    private void cargarDatos(){
        tilNombre.getEditText().setText(usuario.getNombre());
        tilCorreo.getEditText().setText(usuario.getCorreo());
    }

    private void activarBoton(){
        if (!tilNombre.getEditText().getText().toString().isEmpty() &&
                !tilCorreo.getEditText().getText().toString().isEmpty() &&
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

    public void procesarOperacionGuardar(){
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(this);
        infoDialog.setTitle("Ingrese PIN o Contraseña actual");
        infoDialog.setIcon(R.drawable.ic_shild_abt);
        final EditText input = new EditText(PerfilActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setSingleLine(true);
        input.setLayoutParams(lp);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        infoDialog.setView(input);
        infoDialog.setPositiveButton("¡Listo!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    if (!input.getText().toString().isEmpty()){
                        if (input.getText().toString().equals(clv.desencriptar(viejito.getClave())) || input.getText().toString().equals(clv.desencriptar(viejito.getPin()))){
                            guardarDatos();
                        } else {
                            Toast.makeText(PerfilActivity.this, "PIN o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    }
                }catch(Exception ex){
                    Log.d("TAG_", ex.getMessage());
                }

            }
        });
        infoDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tilPin.getEditText().setText("");
                tilRPin.getEditText().setText("");
                tilPin.getEditText().setText("");
                tilRPin.getEditText().setText("");
            }
        });
        AlertDialog aler = infoDialog.create();
        aler.show();
    }
    public void procesarOperacionEliminar(){
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(this);
        infoDialog.setTitle("¡Advertencia!");
        infoDialog.setMessage("Usted esta a punto de borrar sus datos\n" +
                "¿Desea continuar con la operación?");
        infoDialog.setIcon(R.drawable.ic_shild_abt);
        final EditText input = new EditText(PerfilActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setSingleLine(true);
        input.setLayoutParams(lp);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        infoDialog.setView(input);
        infoDialog.setPositiveButton("¡Borrar cuenta!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    if (!input.getText().toString().isEmpty()){
                        if (input.getText().toString().equals(clv.desencriptar(viejito.getClave())) || input.getText().toString().equals(clv.desencriptar(viejito.getPin()))){
                            borrarDatos();
                        } else {
                            Toast.makeText(PerfilActivity.this, "PIN o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    }
                }catch(Exception ex){
                    Log.d("TAG_", ex.getMessage());
                }

            }
        });
        infoDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tilPin.getEditText().setText("");
                tilRPin.getEditText().setText("");
                tilPin.getEditText().setText("");
                tilRPin.getEditText().setText("");
            }
        });
        AlertDialog aler = infoDialog.create();
        aler.show();
    }
    public void borrarDatos(){
        try {
            //Iniciando la bd
            ConexionSQLiteHelper con = new ConexionSQLiteHelper(this, "db_claves", null, 1);
            SQLiteDatabase db = con.getWritableDatabase();
            //DROP TABLE IF EXISTS clave
            String delete = "delete from " + Utilidad.TABLA_CLAVE;
            db.execSQL(delete);
            db.close();
            //instancia
            SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editarPreferencias = preferencias.edit();
            //Limpieza
            editarPreferencias.clear();
            //guardado
            editarPreferencias.commit();
            finish();
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
        }

    }
}
