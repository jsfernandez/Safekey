package com.example.safekey;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

import pojo.Clave;
import pojo.Usuario;
import utilidades.Cesar;

public class VerClaveActivity extends AppCompatActivity {
    //Componentes
    private TextView tvTitulo;
    private TextInputLayout tilClave;
    private TextInputLayout tilSoftware;
    private TextView tvComentario;
    private ImageButton ibtnVer;
    private ImageButton ibtnWeb;
    //
    private Usuario usuario;
    private Clave clave;
    private Cesar enc = new Cesar();
    private boolean sw = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_clave);
        //Binding
        tvTitulo = findViewById(R.id.tvPageVerClave);
        tilClave = findViewById(R.id.tilVerClave);
        tilSoftware = findViewById(R.id.tilVerSoftware);
        tvComentario = findViewById(R.id.tvVerComentario);
        ibtnVer = findViewById(R.id.iBtnVer);
        ibtnWeb = findViewById(R.id.ibtnWebOpen);
        leerSharedPreferences();
        try {
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                clave = this.getIntent().getExtras().getParcelable("clave");
                if (clave != null) {
                    tvTitulo.setText(clave.getTitulo());
                    tilClave.getEditText().setText(enc.desencriptar(clave.getLlave()));
                    tilSoftware.getEditText().setText(clave.getSoftware());
                    tvComentario.setText(clave.getComentario());
                    activarWebButton();
                } else {
                    Log.d("TAG_","Vacio");
                    finish();
                }
            }
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
        }
        ibtnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Aqui va
                if (!sw)
                    procesarOperacion();
                else
                    activarSwitch();
            }
        });
        ibtnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irWeb();
            }
        });
    }
    public void procesarOperacion(){
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(this);
        infoDialog.setTitle("¡Smart Security!");
        infoDialog.setMessage("Ingrese PIN O CONTRASEÑA para ver");
        infoDialog.setIcon(R.drawable.ic_shild_abt);
        final EditText input = new EditText(VerClaveActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setSingleLine(true);
        input.setLayoutParams(lp);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        infoDialog.setView(input);
        infoDialog.setPositiveButton("¡Mostrar Clave!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    if (!input.getText().toString().isEmpty()){
                        Cesar clv = new Cesar();
                        if (input.getText().toString().equals(clv.desencriptar(usuario.getClave())) || input.getText().toString().equals(clv.desencriptar(usuario.getPin()))){
                            activarSwitch();
                        } else {
                            Toast.makeText(VerClaveActivity.this, "PIN o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(Exception ex){
                    Log.d("TAG_", ex.getMessage());
                }

            }
        });
        AlertDialog aler = infoDialog.create();
        aler.show();
    }

    public void activarSwitch(){
        if (sw){
            tilClave.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ibtnVer.setImageResource(R.mipmap.ic_visible_on);
            sw = false;
        } else {
            tilClave.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
            ibtnVer.setImageResource(R.mipmap.ic_visible_off);
            sw = true;
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
            }
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
        }
    }
    public void activarWebButton(){
        if (tilSoftware.getEditText().getText().toString().indexOf("https://") >= 0 ){
            Log.d("TAG_", "Rango: " + tilSoftware.getEditText().getText().toString().indexOf("https://"));
            ibtnWeb.setVisibility(View.VISIBLE);
        } else {
            Log.d("TAG_", "Rango: " + tilSoftware.getEditText().getText().toString().indexOf("https://"));

        }

    }

    public void irWeb(){
        try {
            Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(tilSoftware.getEditText().getText().toString()));
            startActivity(web);
        } catch (Exception ex){
            Log.d("TAG_", "Nope");
        }
    }
}
