package ar.edu.utn.frsf.isi.dam.reclamosonlinelab04;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.TipoReclamo;

public class TipoReclamoAdapter extends ArrayAdapter<TipoReclamo> {

    private List<TipoReclamo> itemList;

    public TipoReclamoAdapter(Context context, int textViewResourceId, List<TipoReclamo> itemList) {
        super(context, textViewResourceId, itemList);
        this.itemList=itemList;
    }

    public TextView getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setText(itemList.get(position).getTipo().toString());
        return v;
    }

    public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setText(itemList.get(position).getTipo().toString());
        return v;
    }

    public TipoReclamo getItemAt(int position){
        return itemList.get(position);
    }

}