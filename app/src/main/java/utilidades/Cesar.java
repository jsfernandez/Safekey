package utilidades;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;

public class Cesar {
    private String encrptiado;
    private String desencriptado;
    private String aux;

    public String encriptar(String str){
        this.aux = "";
        for (int x = 0; str.length()>x; x ++ ){
            int codePointAtx = Character.codePointAt(str, x);
            int rand = (int)(Math.random() * 81) + 10;
            if (x == 0)
                this.aux = rand + "I" + codePointAtx;
            else
                this.aux = this.aux + "I" + rand + "I" + codePointAtx;
        }
        byte[] data = this.aux.getBytes(StandardCharsets.UTF_8);
        this.encrptiado = Base64.encodeToString(data, Base64.DEFAULT);
        return this.encrptiado;
    }

    public String desencriptar(String str){
        byte[] dato = Base64.decode(str, Base64.DEFAULT);
        this.aux = new String(dato, StandardCharsets.UTF_8);
        String[] parts = this.aux.split("I");
        this.desencriptado = "";
        for (int y = 1; parts.length > y; y += 2){
            char c = (char) Integer.parseInt(parts[y]);
            this.desencriptado = this.desencriptado + "" + Character.toString(c);
        }
        return this.desencriptado;
    }
}
