package ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.dao;

import android.os.StrictMode;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.Estado;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.TipoReclamo;

public class ReclamoDaoHTTP implements ReclamoDao {

    private List<TipoReclamo> tiposReclamos = null;
    private List<Estado> tiposEstados = null;
    private List<Reclamo> listaReclamos = null;
    private String server;
    private MyGenericHTTPClient cliente;

    public ReclamoDaoHTTP(){
        server="http://10.0.2.2:3000";
        cliente = new MyGenericHTTPClient(server);
    }

    public ReclamoDaoHTTP(String server){
        this.server=server;
        cliente=new MyGenericHTTPClient(server);
    }


    @Override
    public List<Estado> estados() {
        if(tiposEstados!=null && tiposEstados.size()>0) return this.tiposEstados;
        else{
            tiposEstados = new ArrayList<>();
            String estadosJSON = cliente.getAll("estado");
            try {
                JSONArray arr = new JSONArray(estadosJSON);
                for(int i=0;i<arr.length();i++){
                    JSONObject unaFila = arr.getJSONObject(i);
                    tiposEstados.add(new Estado(unaFila.getInt("id"),unaFila.getString("tipo")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tiposEstados;
    }

    @Override
    public List<TipoReclamo> tiposReclamo() {
        if(tiposReclamos!=null && tiposReclamos.size()>0) return this.tiposReclamos;
        else{
            tiposReclamos = new ArrayList<>();
            try {
                String estadosJSON = cliente.getAll("tipo");
                JSONArray arr = new JSONArray(estadosJSON);
                for(int i=0;i<arr.length();i++){
                    JSONObject unaFila = arr.getJSONObject(i);
                    tiposReclamos.add(new TipoReclamo(unaFila.getInt("id"),unaFila.getString("tipo")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tiposReclamos;
    }

    @Override
    public List<Reclamo> reclamos() {
        listaReclamos = new ArrayList<>();
        String reclamosJSON = cliente.getAll("reclamo");
        try {
            JSONArray arr = new JSONArray(reclamosJSON);
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            for(int i=0;i<arr.length();i++){
                JSONObject unaFila = arr.getJSONObject(i);
                Reclamo recTmp = new Reclamo();
                recTmp.setId(unaFila.getInt("id"));
                recTmp.setTitulo(unaFila.getString("titulo"));
                recTmp.setTipo(this.getTipoReclamoById(unaFila.getInt("tipoId")));
                recTmp.setEstado(this.getEstadoById(unaFila.getInt("estadoId")));
                if(!unaFila.getString("fecha").equals("")){
                    recTmp.setFecha(formatoFecha.parse(unaFila.getString("fecha")));
                }
                if(unaFila.has("lan") && unaFila.has("lon")){
                    recTmp.setLugar(new LatLng(unaFila.getInt("lat"), unaFila.getInt("lon")));
                }
                listaReclamos.add(recTmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return listaReclamos;
    }

    @Override
    public Estado getEstadoById(Integer id){
        Estado objResult =new Estado(99,"no encontrado");
        if(this.tiposEstados!=null){
            for(Estado e:tiposEstados){
                if(e.getId()==id) return e;
            }
        }else{
            String estadoJSON = cliente.getById("estado",id);
            try {
                JSONObject unaFila = new JSONObject(estadoJSON);
                objResult = new Estado(unaFila.getInt("id"),unaFila.getString("tipo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return objResult;
    }

    @Override
    public TipoReclamo getTipoReclamoById(Integer id){
        TipoReclamo objResult =new TipoReclamo(99,"NO ENCONTRADO");
        if(this.tiposEstados!=null){
            for(TipoReclamo e:tiposReclamos){
                if(e.getId()==id) return e;
            }
        }else{
            String estadoJSON = cliente.getById("tipo",id);
            try {
                JSONObject unaFila = new JSONObject(estadoJSON);
                objResult = new TipoReclamo(unaFila.getInt("id"),unaFila.getString("tipo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return objResult;
    }

    @Override
    public void crear(Reclamo r) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("titulo", r.getTitulo());
        jsonObject.put("detalle", r.getDetalle());
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        jsonObject.put("fecha", formatoFecha.format(r.getFecha()));
        jsonObject.put("tipoId", r.getTipo().getId());
        jsonObject.put("estadoId", r.getEstado().getId());

        System.out.print(jsonObject.toString());

        cliente.post("reclamo", jsonObject);
}


    @Override
    public void actualizar(Reclamo r) throws JSONException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

      /*  String reclamosJSON = cliente.getAll("reclamo");
        try {
            JSONArray arr = new JSONArray(reclamosJSON);
            for(int i=0;i<arr.length();i++){
                JSONObject unaFila = arr.getJSONObject(i);
                    if(unaFila.getInt("id") ==r.getId()){ // compare for the key-value
                        ((JSONObject)arr.get(i)).put("id", 25); // put the new value for the key
                    }
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        String rec= cliente.getById("reclamo", r.getId());
        try{
            JSONObject jsonObject = new JSONObject();
            //jsonObject.put("id", r.getId());
            jsonObject.put("titulo", r.getTitulo());
            jsonObject.put("detalle", r.getDetalle());
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            jsonObject.put("fecha", formatoFecha.format(r.getFecha()));
            jsonObject.put("tipoId", r.getTipo().getId());
            jsonObject.put("estadoId", r.getEstado().getId());

            cliente.put("reclamo", jsonObject, r.getId());

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void borrar(Reclamo r)  {
        //verificar cual es el reclamo a borrar
        cliente.delete("reclamo",r.getId());
    }

}
