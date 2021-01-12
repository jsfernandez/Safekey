package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import pojo.Clave;
import pojo.ConexionSQLiteHelper;
import utilidades.Cesar;
import utilidades.Utilidad;

public class ClaveActivity extends AppCompatActivity {
    //Componentes
    private TextInputLayout tilTitulo;
    private TextInputLayout tilSoftware;
    private TextInputLayout tilClave;
    private TextInputLayout tilComentario;
    private TextView tvTitulo;
    private Button btnListo;
    //
    private Clave clave;
    private Cesar enc = new Cesar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave);

        tilTitulo = findViewById(R.id.tilClaveTitulo);
        tilSoftware = findViewById(R.id.tilClaveSoftware);
        tilClave = findViewById(R.id.tilClaveClave);
        tilComentario = findViewById(R.id.tilClaveComentario);
        tvTitulo = findViewById(R.id.tvPageClave);
        btnListo = findViewById(R.id.btnClaveCompleta);

        btnListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG_", tilClave.getEditText().getText().toString());
                try {
                    if(comprovarInputs()){
                        if (comprovarMinLargo()){
                            String titulo = tilTitulo.getEditText().getText().toString();
                            String software = tilSoftware.getEditText().getText().toString();
                            String llave = enc.encriptar(tilClave.getEditText().getText().toString());
                            String comentario = tilComentario.getEditText().getText().toString();
                            clave = new Clave(titulo, software, llave, comentario);
                            registrarClave();
                        }
                    }
                } catch (Exception ex){
                    Log.d("TAG_", ex.getMessage());
                }
            }
        });

        tilTitulo.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
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


    }

    private boolean comprovarInputs() {
        if(tilTitulo.getEditText().getText().toString().isEmpty()){
            tilTitulo.setError("Favor ingrese un titulo");
            return false;
        } else if(tilClave.getEditText().getText().toString().isEmpty()){
            tilTitulo.setError(null);
            tilTitulo.setError("Favor ingrese Una clave a guardar");
            return false;
        } else {
            return true;
        }
    }

    private boolean comprovarMinLargo() {
        if(tilTitulo.getEditText().getText().toString().length() < 4){
            tilTitulo.setError("El titulo debe tener al menos un largo de 4 caracteres");
            return false;
        } else if(tilClave.getEditText().getText().toString().length() < 4){
            tilTitulo.setError(null);
            tilTitulo.setError("La Clave debe tener al menos un largo de 4 caracteres");
            return false;
        } else {
            return true;
        }
    }
    private void activarBoton(){
        if (!tilTitulo.getEditText().getText().toString().isEmpty() && !tilClave.getEditText().getText().toString().isEmpty()) {
            btnListo.setBackgroundColor(Color.parseColor("#30323C"));
            btnListo.setEnabled(true);
        } else {
            btnListo.setBackgroundColor(Color.parseColor("#8F92AA"));
            btnListo.setEnabled(false);
        }
    }

    public void registrarClave(){
        try{
            Log.d("TAG_", "Entro");
            //Iniciando la bd
            ConexionSQLiteHelper con = new ConexionSQLiteHelper(this, "db_claves", null, 1);
            SQLiteDatabase db = con.getWritableDatabase();
            ContentValues values  = new ContentValues();
            //insertando datos
            values.put(Utilidad.CAMPO_IDENTIFICADOR, ultimoId() + 1);
            values.put(Utilidad.CAMPO_TITULO, clave.getTitulo());
            values.put(Utilidad.CAMPO_SOFTWARE, clave.getSoftware());
            values.put(Utilidad.CAMPO_LLAVE, clave.getLlave());
            values.put(Utilidad.CAMPO_COMENTARIO, clave.getComentario());
            db.insert(Utilidad.TABLA_CLAVE, Utilidad.CAMPO_IDENTIFICADOR, values);
            //cerrar db
            db.close();
            //Completando ciclo
            Toast.makeText(this, "!Registro Exitoso¡", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
        }
    }

    public int ultimoId(){
        int n = 0;
        try{
            ConexionSQLiteHelper con = new ConexionSQLiteHelper(this, "db_claves", null, 1);
            SQLiteDatabase db = con.getReadableDatabase();
            //Por medio de sentencia
            Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidad.TABLA_CLAVE, null);
            cursor.moveToLast();
            n =  Integer.parseInt(cursor.getString(0));
            cursor.close();
            return n;
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
            return n;
        }
    }
}
