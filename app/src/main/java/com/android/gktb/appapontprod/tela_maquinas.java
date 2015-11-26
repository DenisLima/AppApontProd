package com.android.gktb.appapontprod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
 * Created by denis on 23/06/15.
 */
public class tela_maquinas extends Activity {

    ProgressDialog dialog;
    JSONArray maquinas = null;
    String TAG_PRIN = "maquinas";
    String TAG_MAQUINA = "maquina";
    String TAG_ID = "id";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;
    String dataProducao;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.lista_maquinas);

        Intent it = getIntent();
        dataProducao = it.getStringExtra("data");

        Button btnVoltar = (Button) findViewById(R.id.btnVoltarMaquina);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(tela_maquinas.this, tela_menu.class);
                startActivity(it);
                finish();
            }
        });

        CarregaDetalhesProgramacao();

    }

    public void CarregaDetalhesProgramacao(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_maquinas.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php?acao=carrega_maquinas";
                JSONObject json = jParser.getJSONFromUrl(ord);

                return json;
            }

            protected void onPostExecute(JSONObject json){
                dialog.dismiss();

                try {

                    maquinas = json.getJSONArray(TAG_PRIN);

                    for (int i = 0; i < maquinas.length(); i++) {

                        JSONObject c = maquinas.getJSONObject(i);

                        String descricaoMaquina = c.getString(TAG_MAQUINA);
                        String idMaquina        = c.getString(TAG_ID);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_MAQUINA, descricaoMaquina);
                        map.put(TAG_ID, idMaquina);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listaMaquinas);
                        final BaseAdapter adapter = new SimpleAdapter(tela_maquinas.this, oslist, R.layout.maquinas_custom,
                                new String[] {TAG_MAQUINA, TAG_ID},
                                new int[] {R.id.txtMaquina, R.id.txtIdMaquina});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final String idProg = oslist.get(+position).get("id");
                                final String descMaquina = oslist.get(+position).get("maquina");
                                //Toast.makeText(tela_maquinas.this, "O item clicado foi: " + dataProducao, Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder alert = new AlertDialog.Builder(tela_maquinas.this);
                                alert.setTitle("Aviso");
                                alert.setMessage("Efetuar apontamento para a máquina: "+descMaquina+" ?");

                                alert.setNegativeButton("Não",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {



                                    }
                                });

                                alert.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent it = new Intent(tela_maquinas.this, tela_lotes.class);
                                        it.putExtra("id",idProg);
                                        it.putExtra("data",dataProducao);
                                        startActivity(it);
                                        finish();
                                    }
                                });

                                alert.show();

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
