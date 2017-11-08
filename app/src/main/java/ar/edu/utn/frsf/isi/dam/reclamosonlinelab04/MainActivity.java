package ar.edu.utn.frsf.isi.dam.reclamosonlinelab04;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.dao.ReclamoDao;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.dao.ReclamoDaoHTTP;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.Reclamo;

public class MainActivity extends AppCompatActivity {
    private ReclamoDao daoReclamo;
    private ListView listViewReclamos;
    private List<Reclamo> listaReclamos;
    private ReclamoAdapter adapter;
    private Button btnNuevoReclamo;


    Runnable r;

    private final static int CREACION_RECLAMO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        daoReclamo = new ReclamoDaoHTTP();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewReclamos = (ListView) findViewById(R.id.mainListaReclamos);
        listaReclamos = new ArrayList<>();
        adapter = new ReclamoAdapter(this, listaReclamos);
        listViewReclamos.setAdapter(adapter);

        r = new Runnable() {
            @Override
            public void run() {
                List<Reclamo> rec = daoReclamo.reclamos();
                listaReclamos.clear();
                listaReclamos.addAll(rec);
                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
        btnNuevoReclamo = (Button) findViewById(R.id.btnNuevoReclamo);
        btnNuevoReclamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, FormReclamo.class);
                startActivityForResult(i, CREACION_RECLAMO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case CREACION_RECLAMO:{
                if(resultCode == Activity.RESULT_OK){
                    Thread t = new Thread(r);
                    t.start();
                }
                break;
            }
        }
    }
}
