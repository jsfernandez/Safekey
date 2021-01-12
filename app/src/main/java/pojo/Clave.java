package pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Clave implements Parcelable {
    private int identificador;
    private String titulo;
    private String software;
    private String llave;
    private String comentario;

    public Clave(String titulo, String software, String llave, String comentario) {
        this.titulo = titulo;
        this.software = software;
        this.llave = llave;
        this.comentario = comentario;
    }

    public int getIdentificador() { return identificador; }

    public void setIdentificador(int identificador) { this.identificador = identificador; }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getLlave() {
        return llave;
    }

    public void setLlave(String llave) {
        this.llave = llave;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    protected Clave(Parcel in) {
        identificador = in.readInt();
        titulo = in.readString();
        software = in.readString();
        llave = in.readString();
        comentario = in.readString();
    }

    public static final Creator<Clave> CREATOR = new Creator<Clave>() {
        @Override
        public Clave createFromParcel(Parcel in) {
            return new Clave(in);
        }

        @Override
        public Clave[] newArray(int size) {
            return new Clave[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(identificador);
        dest.writeString(titulo);
        dest.writeString(software);
        dest.writeString(llave);
        dest.writeString(comentario);
    }
}
