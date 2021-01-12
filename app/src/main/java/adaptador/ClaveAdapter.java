package adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.safekey.R;

import java.util.ArrayList;

import pojo.Clave;

public class ClaveAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Clave> datos;

    public ClaveAdapter(Context context, ArrayList<Clave> dat) {
        super(context, R.layout.listview_item_layout, dat);
        // Guardamos los parámetros en variables de clase.
        this.context = context;
        this.datos = dat;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // En primer lugar "inflamos" una nueva vista, que será la que se
        // mostrará en la celda del ListView. Para ello primero creamos el
        // inflater, y después inflamos la vista.
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.listview_item_layout, null);

        // Recogemos el TextView para establecer parametros
        TextView titulo = (TextView) item.findViewById(R.id.tvTitulo);
        titulo.setText(datos.get(position).getTitulo().toString());

        TextView soft = (TextView) item.findViewById(R.id.tvSoftware);
        soft.setText(datos.get(position).getSoftware().toString());

        // Devolvemos la vista para que se muestre en el ListView.
        return item;
    }
}
