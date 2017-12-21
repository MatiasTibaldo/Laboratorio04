package ar.edu.utn.frsf.isi.dam.reclamosonlinelab04;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.dao.ReclamoDao;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.dao.ReclamoDaoHTTP;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.Estado;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.reclamosonlinelab04.modelo.TipoReclamo;

public class FormReclamo extends AppCompatActivity {

    private final static int SELECCION_LUGAR = 1;
    private final static int REQUEST_IMAGE_CAPTURE = 2;
    private final static int REQUEST_AUDIO = 3;

    private Reclamo rec;

    private Button elegirLugar;
    private Button btnGuardar;
    private Button btnEliminar;
    private Button btnCancelar;
    private Button btnSacarFoto;
    private Button btnGrabarAudio;
    private EditText etTitulo;
    private EditText etDetalle;
    private EditText etLugar;
    private Spinner spTipoReclamo;
    ReclamoDao daoReclamo;
    private TipoReclamoAdapter adapter;
    private List<TipoReclamo> listaTiposReclamos;
    private ImageView mImageView;
    private MediaRecorder mRecorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        daoReclamo = new ReclamoDaoHTTP();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_reclamo);

        listaTiposReclamos = new ArrayList<>();

        rec = new Reclamo();
        etTitulo = (EditText)findViewById(R.id.frmReclamoTextReclamo);
        etDetalle = (EditText)findViewById(R.id.frmReclamoTextDetReclamo);
        etLugar = (EditText)findViewById(R.id.frmReclamoTextLugar);
        if(getIntent().getExtras().getString("titulo")== null){ //para saber si es una reclamo existente o uno nuevo

            //btnEliminar.setEnabled(false);
            //no debria estar disponible el button eliminar

        }
        else{
            etTitulo.setText(getIntent().getExtras().getString("titulo"));
            etDetalle.setText(getIntent().getExtras().getString("detalle"));
            etLugar.setText(getIntent().getExtras().getString("lugar"));
        }


        btnSacarFoto = (Button) findViewById(R.id.frmReclamoSacarFoto);
        btnGrabarAudio = (Button) findViewById(R.id.frmReclamoRecAudio);

        btnSacarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sacarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(sacarFoto,REQUEST_IMAGE_CAPTURE);
            }
        });

        spTipoReclamo = (Spinner)findViewById(R.id.frmReclamoCmbTipo);
        adapter = new TipoReclamoAdapter(this, R.layout.support_simple_spinner_dropdown_item, listaTiposReclamos);
        spTipoReclamo.setAdapter(adapter);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                List<TipoReclamo> tiprec = daoReclamo.tiposReclamo();
                listaTiposReclamos.clear();
                listaTiposReclamos.addAll(tiprec);
                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();

        btnGuardar = (Button)findViewById(R.id.frmReclamoGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rec.setTitulo(etTitulo.getText().toString());
                rec.setDetalle(etDetalle.getText().toString());
                rec.setTipo((TipoReclamo)spTipoReclamo.getItemAtPosition(spTipoReclamo.getSelectedItemPosition()));
                rec.setFecha(new Date());
                Estado estado = new Estado();
                estado.setId(1);//1=enviado
                estado.setTipo("Enviado");
                rec.setEstado(estado);
                try {
                    if(getIntent().getExtras().getString("titulo")== null){// es un reclamo nuevo
                        daoReclamo.crear(rec);
                    }
                    else{
                        rec.setId(getIntent().getExtras().getInt("id"));
                        daoReclamo.actualizar(rec);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
        btnEliminar = (Button)findViewById(R.id.frmReclamoEliminar);

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rec.setId(getIntent().getExtras().getInt("id"));
                rec.setTitulo(etTitulo.getText().toString());
                rec.setDetalle(etDetalle.getText().toString());
                rec.setTipo((TipoReclamo)spTipoReclamo.getItemAtPosition(spTipoReclamo.getSelectedItemPosition()));
                daoReclamo.borrar(rec);
                Intent i = new Intent();
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
        btnCancelar = (Button)findViewById(R.id.frmReclamoCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                setResult(Activity.RESULT_CANCELED, i);
                finish();
            }
        });

        elegirLugar = (Button)findViewById(R.id.elegirLugar);
        elegirLugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FormReclamo.this, MapsActivity.class);
                startActivityForResult(i, SELECCION_LUGAR);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case SELECCION_LUGAR: {
                if (resultCode == Activity.RESULT_OK) {
                    etLugar.setText(data.getIntExtra("Lat", 0) + ";" + data.getIntExtra("Lon", 0));
                    rec.setLugar(new LatLng(data.getIntExtra("Lat", 0), data.getIntExtra("Lon", 0)));
                }
                break;
            }
            case REQUEST_IMAGE_CAPTURE:{
                if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mImageView.setImageBitmap(imageBitmap);
                    File directory = getApplicationContext().getDir("imagenes", Context.MODE_PRIVATE);
                    if(!directory.exists()) {
                        directory.mkdir();
                    }
                    File myPath = new File(directory, "reclamo_" +rec.getId()+".jpg");

                }
                break;
            }
            case REQUEST_AUDIO:
                if(requestCode == REQUEST_AUDIO && resultCode == RESULT_OK){
                    grabar();
                }
        }
    }

    private void grabar(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        File directory =getApplicationContext().getDir("audios", Context.MODE_PRIVATE);
        if(!directory.exists()) {
            directory.mkdir();
        }
        File myPath = new File(directory, "reclamo_" +rec.getId()+".3gp");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    }
}
