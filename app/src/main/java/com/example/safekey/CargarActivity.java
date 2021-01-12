package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import pojo.Clave;
import pojo.ConexionSQLiteHelper;
import pojo.Usuario;
import utilidades.Cesar;
import utilidades.Utilidad;

public class CargarActivity extends AppCompatActivity {
    //Componentes
    private TextInputLayout tilClave;
    private TextInputLayout tilDatos;
    private Button btnCargar;
    //
    private ArrayList<Clave> lisClaves = new ArrayList<>();
    private Cesar dsc = new Cesar();
    private Usuario usuario;
    //Conexion
    private ConexionSQLiteHelper con = new ConexionSQLiteHelper(this, "db_claves", null, 1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar);
        tilClave = findViewById(R.id.tilClaveClave);
        tilDatos = findViewById(R.id.tilDatosCargar);
        btnCargar = findViewById(R.id.btnCargarDatos);

        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tilClave.getEditText().getText().toString().trim().isEmpty()){
                    tilClave.setError("Favor ingrese su Clave o PIN para la operaición");
                } else if(tilClave.getEditText().getText().toString().trim().length()<4){
                    tilClave.setError("Ingrese un valor con mas de 3 digitos");
                } else if (tilDatos.getEditText().getText().toString().trim().isEmpty()){
                    tilClave.setError(null);
                    tilDatos.setError("Favor ingrese su Clave o PIN para la operaición");
                } else if(tilDatos.getEditText().getText().toString().trim().length()<4){
                    tilClave.setError(null);
                    tilDatos.setError("Ingrese un valor con mas de 3 digitos");
                } else {
                    tilClave.setError(null);
                    tilDatos.setError(null);
                    procesarBundle(tilDatos.getEditText().getText().toString().trim());
                }
            }
        });
    }

    private void procesarBundle(String bund){
        lisClaves.clear();
        if ( !bund.isEmpty()){
            try {
                //Todo
                String[] hax = dsc.desencriptar(bund).split("XXTXX");
                //Usuario
                String[] usuHax = dsc.desencriptar(hax[0]).split("; ");
                usuario = new Usuario(Integer.parseInt(usuHax[0]), usuHax[1], usuHax[3], usuHax[5],usuHax[2], usuHax[4]);
                //Claves
                String[] haxClaves = dsc.desencriptar(hax[1]).split("; ");
                for (int x = 0; haxClaves.length - 1 > x; x += 5){
                    Clave tempClave = new Clave(haxClaves[x + 1], haxClaves[x + 2], haxClaves[x + 3], haxClaves[x + 4]);
                    tempClave.setIdentificador(Integer.parseInt(haxClaves[x]));
                    lisClaves.add(tempClave);
                }
                Log.d("TAG_", "Proceso completado");
                if (tilClave.getEditText().getText().toString().equals(dsc.desencriptar(usuario.getClave())) || tilClave.getEditText().getText().toString().equals(dsc.desencriptar(usuario.getPin()))){
                    boolean resultados = eleminarClave() && registrarClave() && guardarDatos();
                    if (resultados){
                        Toast.makeText(this, "¡Proceso Exitoso!", Toast.LENGTH_SHORT).show();
                        irLogin();
                    }
                    else
                        Toast.makeText(this, "Ups, datos corruptos", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error de Clave o PIN", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex){
                tilDatos.getEditText().setText("");
                Log.d("TAG_", ex.getMessage());
                Toast.makeText(this, "Datos invalidos", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("TAG_", "Vacio");
        }
    }
    private boolean guardarDatos(){
        //instancia
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editarPreferencias = preferencias.edit();
        //Edición
        String spu = usuario.getId() + "; " + usuario.getNombre() + "; " + usuario.getClave().toString() + "; " + usuario.getPin().toString() + "; " + usuario.getCodigo() + "; 0";
        editarPreferencias.putString(usuario.getCorreo(), spu);
        editarPreferencias.apply();
        //guardado
        boolean result = editarPreferencias.commit();
        return result;
    }

    public boolean eleminarClave(){
        try{
            SQLiteDatabase db = con.getWritableDatabase();
            //DROP TABLE IF EXISTS clave
            String delete = "delete from " + Utilidad.TABLA_CLAVE;
            db.execSQL(delete);
            db.close();
            return true;
        } catch (Exception ex) {
            Log.d("TAG_", ex.getMessage());
            return false;
        }
    }

    public boolean registrarClave(){
        try{
            //Iniciando la bd
            SQLiteDatabase db = con.getWritableDatabase();
            for (int z = 0; lisClaves.size() > z; z++){
                ContentValues values  = new ContentValues();
                //insertando datos
                values.put(Utilidad.CAMPO_IDENTIFICADOR, "" + lisClaves.get(z).getIdentificador());
                values.put(Utilidad.CAMPO_TITULO, lisClaves.get(z).getTitulo());
                values.put(Utilidad.CAMPO_SOFTWARE, lisClaves.get(z).getSoftware());
                values.put(Utilidad.CAMPO_LLAVE, lisClaves.get(z).getLlave());
                values.put(Utilidad.CAMPO_COMENTARIO, lisClaves.get(z).getComentario());
                db.insert(Utilidad.TABLA_CLAVE, Utilidad.CAMPO_IDENTIFICADOR, values);
            }
            //cerrar db
            db.close();
            //Completando ciclo
            Log.d("TAG_", "Claves instaladas");
            return true;
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
            return false;
        }
    }

    public void irLogin(){
        Intent i = new Intent(CargarActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
