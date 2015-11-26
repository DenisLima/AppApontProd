package com.android.gktb.appapontprod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.gktb.library.AdapterListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by denis on 24/06/15.
 */
public class tela_lotes extends Activity {

    ProgressDialog dialog;
    JSONArray loteprod = null;
    String TAG_PRIN = "loteprod";
    String TAG_LOTE = "lote";
    String TAG_DATA = "data";
    String TAG_ID = "id";
    String TAG_MAQUINA = "maquina";
    String TAG_IDMAQUINA = "idMaquina";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;
    String dataProducao;
    String id;
    String dataPrd;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.lista_lotes);

        Button btnVoltar = (Button) findViewById(R.id.btnVoltarLote);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(tela_lotes.this, tela_menu.class);
                startActivity(i);
                finish();
            }
        });

        Intent it = getIntent();
        id = it.getStringExtra("id");
        dataPrd = it.getStringExtra("data");

        //Toast.makeText(tela_lotes.this, "O id é "+id+" e a data é "+data, Toast.LENGTH_LONG).show();

        CarregaDetalhesProgramacao();

    }

    public void CarregaDetalhesProgramacao(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_lotes.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php?acao=carrega_lote_prod&&maquina="+id+"&&data="+dataPrd;
                JSONObject json = jParser.getJSONFromUrl(ord);

                return json;
            }

            protected void onPostExecute(JSONObject json){
                dialog.dismiss();

                try {

                    loteprod = json.getJSONArray(TAG_PRIN);

                    for (int i = 0; i < loteprod.length(); i++) {

                        JSONObject c = loteprod.getJSONObject(i);

                        String lote   = c.getString(TAG_LOTE);
                        final String data   = c.getString(TAG_DATA);
                        String idLote = c.getString(TAG_ID);
                        String maquina = c.getString(TAG_MAQUINA);
                        String idMaquina = c.getString(TAG_IDMAQUINA);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_LOTE, lote);
                        map.put(TAG_DATA, data);
                        map.put(TAG_ID, idLote);
                        map.put(TAG_MAQUINA, maquina);
                        map.put(TAG_IDMAQUINA, idMaquina);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listaLotes);
                        final BaseAdapter adapter = new SimpleAdapter(tela_lotes.this, oslist, R.layout.lote_custom,
                                new String[] {TAG_LOTE, TAG_DATA, TAG_ID, TAG_MAQUINA, TAG_IDMAQUINA},
                                new int[] {R.id.txtLote, R.id.txtDataProd, R.id.txtIdLote, R.id.txtMaquinaLote, R.id.txtIdMaquinaLote});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final String idProg = oslist.get(+position).get("lote");
                                final String descMaquina = oslist.get(+position).get("id");
                                final String idMaquina = oslist.get(+position).get("idMaquina");
                                //Toast.makeText(tela_lotes.this, "O item clicado foi: " + idProg + descMaquina, Toast.LENGTH_SHORT).show();

                                Intent it = new Intent(tela_lotes.this, tela_detalhes_lote.class);
                                it.putExtra("idLote", descMaquina);
                                it.putExtra("dataProd", dataPrd);
                                it.putExtra("idMaquina", idMaquina);
                                startActivity(it);
                                finish();

                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };
        task.execute();

    }

}

