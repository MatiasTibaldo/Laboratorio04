package ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.dao;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            for(int i=0;i<arr.length();i++){
                JSONObject unaFila = arr.getJSONObject(i);
                Reclamo recTmp = new Reclamo();
                recTmp.setId(unaFila.getInt("id"));
                recTmp.setTitulo(unaFila.getString("titulo"));
                recTmp.setTipo(this.getTipoReclamoById(unaFila.getInt("tipoId")));
                recTmp.setEstado(this.getEstadoById(unaFila.getInt("estadoId")));
                if(unaFila.has("lan") && unaFila.has("lon")){
                    recTmp.setLugar(new LatLng(unaFila.getInt("lat"), unaFila.getInt("lon")));
                }
                listaReclamos.add(recTmp);
            }
        } catch (JSONException e) {
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
        jsonObject.put("id", r.getId());
        jsonObject.put("titulo", r.getTitulo());
        jsonObject.put("detalle", r.getDetalle());
        jsonObject.put("fecha", r.getFecha());
        jsonObject.put("tipo", r.getTipo());
        jsonObject.put("estado", r.getEstado());
        new MyGenericHTTPClient(server).put(cliente,jsonObject);


    }

    @Override
    public void actualizar(Reclamo r) {

    }

    @Override
    public void borrar(Reclamo r) {

    }
}
