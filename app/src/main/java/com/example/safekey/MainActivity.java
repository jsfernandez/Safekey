package com.example.safekey;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.fonts.Font;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.WriterException;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adaptador.ClaveAdapter;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import pojo.Clave;
import pojo.ConexionSQLiteHelper;
import pojo.Usuario;
import utilidades.Cesar;
import utilidades.Utilidad;

public class MainActivity extends AppCompatActivity {
    //Arreglos
    private ArrayList<Clave> lisClaves = new ArrayList();
    private ArrayList<Clave> memoria = new ArrayList();
    private ArrayList<Clave> vacio = new ArrayList();
    //Componente
    private FloatingActionButton fabtnCerrarSesion;
    private FloatingActionButton fabtnMiPerfil;
    private FloatingActionButton fabtnClave;
    private FloatingActionButton fabtnRespaldar;
    private ListView lvClaves;
    private SearchView svBuscar;
    private ImageView qrImage;
    //Conexion
    private ConexionSQLiteHelper con = new ConexionSQLiteHelper(this, "db_claves", null, 1);
    //OTROS
    private Usuario usuario;
    private ClaveAdapter adapter;
    private ArrayAdapter<Clave> adaptador;
    private int pasaPalabras = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Revisar si hay session del usuario
        leerSharedPreferences();
        //Bindings
        fabtnCerrarSesion = findViewById(R.id.btnfCerrarSesion);
        fabtnMiPerfil = findViewById(R.id.btnfMisDatos);
        fabtnClave = findViewById(R.id.btnfAgregarPass);
        fabtnRespaldar = findViewById(R.id.btnfRespaldar);
        lvClaves = findViewById(R.id.lvClaves);
        svBuscar = findViewById(R.id.svBuscar);
        qrImage = findViewById(R.id.ImageViewQR);
        vacio.add(new Clave("¡Sin Resultados!", "", "", ""));
        consultaLista();
        fabtnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesionDialog();
            }
        });
        fabtnMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irMiPerfil();
            }
        });

        fabtnClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAClave();
            }
        });

        fabtnRespaldar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generarQR();
                if (lisClaves.size() > 0)
                    enviarEmail();
                else
                    Toast.makeText(MainActivity.this, "Aún no ha agregado datos para respaldar", Toast.LENGTH_SHORT).show();
            }
        });

        lvClaves.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Clave clv;
                    if(!memoria.isEmpty()){
                        clv = new Clave(memoria.get(position).getTitulo(),
                                memoria.get(position).getSoftware(),
                                memoria.get(position).getLlave(),
                                memoria.get(position).getComentario());
                        clv.setIdentificador(memoria.get(position).getIdentificador());
                    } else if(memoria.isEmpty() && pasaPalabras > 0){
                        clv = new Clave(vacio.get(position).getTitulo(),
                                vacio.get(position).getSoftware(),
                                vacio.get(position).getLlave(),
                                vacio.get(position).getComentario());
                        clv.setIdentificador(vacio.get(position).getIdentificador());
                    } else {
                        clv = new Clave(lisClaves.get(position).getTitulo(),
                                lisClaves.get(position).getSoftware(),
                                lisClaves.get(position).getLlave(),
                                lisClaves.get(position).getComentario());
                        clv.setIdentificador(lisClaves.get(position).getIdentificador());
                    }
                    if(!clv.getTitulo().equals("¡Sin Resultados!")) {
                        Log.d("TAG_", clv.getTitulo());
                        irVerClave(clv);
                    }
                } catch (Exception ex) {
                    Log.d("TAG_", ex.getMessage());
                }
            }
        });

        lvClaves.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Clave clv;
                    if(!memoria.isEmpty()){
                        clv = new Clave(memoria.get(position).getTitulo(),
                                memoria.get(position).getSoftware(),
                                memoria.get(position).getLlave(),
                                memoria.get(position).getComentario());
                        clv.setIdentificador(memoria.get(position).getIdentificador());
                    } else if(memoria.isEmpty() && pasaPalabras > 0){
                        clv = new Clave(vacio.get(position).getTitulo(),
                                vacio.get(position).getSoftware(),
                                vacio.get(position).getLlave(),
                                vacio.get(position).getComentario());
                        clv.setIdentificador(vacio.get(position).getIdentificador());
                    } else {
                        clv = new Clave(lisClaves.get(position).getTitulo(),
                                lisClaves.get(position).getSoftware(),
                                lisClaves.get(position).getLlave(),
                                lisClaves.get(position).getComentario());
                        clv.setIdentificador(lisClaves.get(position).getIdentificador());
                    }
                    if(!clv.getTitulo().equals("¡Sin Resultados!")) {
                        Log.d("TAG_", "Eliminar a :" + clv.getTitulo());
                        advertencia(clv);
                    }
                } catch (Exception ex) {
                    Log.d("TAG_", ex.getMessage());
                }
                return true;
            }
        });

        svBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.toString().trim().length() > 3){
                    try{
                        pasaPalabras = newText.toString().trim().length();
                        memoria.clear();
                        for (int x = 0; x < lisClaves.size(); x ++){
                            if (lisClaves.get(x).getTitulo().toLowerCase().indexOf(newText.toString().trim().toLowerCase())>=0){
                                memoria.add(lisClaves.get(x));
                            } else if (lisClaves.get(x).getSoftware().toLowerCase().indexOf(newText.toString().trim().toLowerCase())>=0){
                                memoria.add(lisClaves.get(x));
                            }
                        }
                        buscarClaves();
                    } catch (Exception ex){
                        memoria.clear();
                        Log.d("TAG_", ex.getMessage());
                    }
                } else {
                    pasaPalabras = 0;
                    consultaLista();
                }
                return true;
            }
        });

    }

    private void buscarClaves() {
        if (memoria.isEmpty()){
            adaptador = new ClaveAdapter(this, vacio);
            lvClaves.setAdapter(adaptador);
        } else {
            adaptador = new ClaveAdapter(this, memoria);
            lvClaves.setAdapter(adaptador);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        leerSharedPreferences();
        consultaLista();
    }

    private void leerSharedPreferences() {
        try {
            String sesion = "0";
            boolean reg = false;
            SharedPreferences p = getSharedPreferences("datos", Context.MODE_PRIVATE);
            Map<String, ?> keys = p.getAll();
            for(Map.Entry<String, ?>k: keys.entrySet()){
                reg = true;
                String correo = k.getKey().toString();
                String[] parts = k.getValue().toString().split("; ");
                String id = parts[0];
                String nombre = parts[1];
                String clave = parts[2];
                String pin = parts[3];
                String codigo = parts[4];
                sesion = parts[5];
                usuario = new Usuario(0, nombre, correo, clave, codigo, pin);
                Log.d("TAG_", TextUtils.join(",", parts));
            }
            if (sesion.equals("0") && reg){
                irLogin();
            } else if(!reg){
                irRegistro();
            }
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
        }
    }



    public void cerrarSesionDialog(){
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(this);
        infoDialog.setTitle("Cierre de sesión");
        infoDialog.setMessage("\n" +
                "¿Desea cerrar sesión?");
        infoDialog.setIcon(R.drawable.ic_credencial_abt);
        infoDialog.setPositiveButton("¡Sí!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                guardarDatosCierre();
            }
        });
        infoDialog.setNegativeButton("No por ahora", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog aler = infoDialog.create();
        aler.show();
    }

    private void guardarDatosCierre(){
        //instancia
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editarPreferencias = preferencias.edit();
        //Edición
        String spu = usuario.getId() + "; " + usuario.getNombre() + "; " + usuario.getClave() + "; " + usuario.getPin() + "; " + usuario.getCodigo() + "; 0";
        editarPreferencias.putString(usuario.getCorreo(), spu);
        //guardado
        editarPreferencias.commit();
        irLogin();
    }

    public void consultaLista(){
        memoria.clear();
        lisClaves.clear();
        //Por medio de sentencia
        try {
            SQLiteDatabase db = con.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidad.TABLA_CLAVE, null);
            while (cursor.moveToNext()){
                //Generando usuarios nuevos con la data obtenida.
                Clave clave = new Clave(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                clave.setIdentificador(cursor.getInt(0));
                //Rellenar lista de usuarios
                lisClaves.add(clave);
            }
            cursor.close();
            adaptador = new ClaveAdapter(this, lisClaves);
            lvClaves.setAdapter(adaptador);
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
        }
    }

    public void advertencia(Clave clv){
        final Clave clave = clv;
        AlertDialog.Builder borraDialog = new AlertDialog.Builder(this);
        borraDialog.setTitle("¡Advertencia!");
        borraDialog.setMessage("Esta a punto de borrar a " + clv.getTitulo() + "\n¿Desea Continuar?");
        borraDialog.setIcon(R.drawable.ic_trash_abt_red);
        borraDialog.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    eleminarClave(clave);

                } catch (Exception ex) {
                    Log.d("TAG_", ex.getMessage());
                }
            }
        });
        AlertDialog aler = borraDialog.create();
        aler.show();
    }

    private void irRegistro() {
        Intent i = new Intent(MainActivity.this, RegistroUsuarioActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void irLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void irMiPerfil() {
        Intent i = new Intent(MainActivity.this, PerfilActivity.class);
        startActivity(i);
    }

    private void irAClave() {
        Intent i = new Intent(MainActivity.this, ClaveActivity.class);
        startActivity(i);
    }

    public void irVerClave(Clave clv){
        if (clv.getSoftware() == null)
            clv.setSoftware("  ");
        if (clv.getComentario() == null)
            clv.setComentario("  ");

        Intent i = new Intent(MainActivity.this,VerClaveActivity.class );
        Bundle bundle = new Bundle();
        bundle.putParcelable("clave", clv);
        i.putExtras(bundle);
        startActivity(i);
    }

    public boolean eleminarClave(Clave clave){
        try{
            SQLiteDatabase db = con.getWritableDatabase();
            String[] parametos = { clave.getIdentificador() + "" };
            db.delete(Utilidad.TABLA_CLAVE,Utilidad.CAMPO_IDENTIFICADOR + "=?", parametos);
            db.close();
            consultaLista();
            return true;
        } catch (Exception ex) {
            Log.d("TAG_", ex.getMessage());
            return false;
        }
    }

    public void generarQR(){
        //QRGEncoder qrgEncoder = new QRGEncoder("Basura", null, QRGContents.Type.TEXT,22);
        try {
            QRGEncoder qrgEncoder;
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    "Prueba", null,
                    QRGContents.Type.TEXT,
                    smallerDimension);

            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.v("TAG_", e.getMessage());
        }
    }

    private String generarBundle(){
        leerSharedPreferences();
        consultaLista();
        String resultBundle = "";
        try {
            String usuBundle = usuario.getId() + "; " + usuario.getNombre() + "; " + usuario.getCodigo()
                    + "; " + usuario.getCorreo() + "; " + usuario.getPin() + "; " + usuario.getClave();
            String clavesBundle = "";
            for (int x = 0; lisClaves.size() > x; x ++){
                Clave cl = lisClaves.get(x);
                clavesBundle = clavesBundle + "" + cl.getIdentificador() + "; " + cl.getTitulo() + "; " + cl.getSoftware()
                        + "; " + cl.getLlave() + "; " + cl.getComentario() + "; ";
            }
            Cesar enc = new Cesar();
            resultBundle = enc.encriptar(enc.encriptar(usuBundle) + "XXTXX" + enc.encriptar(clavesBundle));
        } catch (Exception ex){
            Log.d("TAG_", ex.getMessage());
        }
        return resultBundle;
    }

    private void enviarQR(){
        try {
            QRGEncoder qrgEncoder;
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    generarBundle(), null,
                    QRGContents.Type.TEXT,
                    smallerDimension);

            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, usuario.getNombre(), null);
            Uri bmpUri = Uri.parse(pathofBmp);
            String subject = "Respaldo QR SafeKeys";
            String message = "Hola " + usuario.getNombre() + ",\n";
            message = message + "Te hemos adjuntado el Codigo QR con el que solo deberás escanear " +
                    "para cargar todos tus datos y claves, cuando vuelvas instalar esta aplicación";

            final Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent1.putExtra(Intent.EXTRA_EMAIL, new String[]{ usuario.getCorreo()});
            emailIntent1.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent1.putExtra(Intent.EXTRA_TEXT, message);
            emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
            emailIntent1.setType("image/png");
            startActivity(Intent.createChooser(emailIntent1, "Choose an Email client :"));
        } catch (Exception e) {
            Log.v("TAG_", e.getMessage());
        }
    }
    public void enviarEmail(){
        String subject = "SafeKey email de respaldo.";
        String message = generarBundle();
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ usuario.getCorreo()});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822");
        try{
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }catch(Exception ex){
            Log.d("TAG_", "Error: " + ex.getMessage());
        }
    }

}
