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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.gktb.library.AdapterListView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by denis on 01/07/15.
 */
public class tela_parada_maquina extends Activity {

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

        CarregaDetalhesProgramacao();

    }

    public void CarregaDetalhesProgramacao(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_parada_maquina.this);
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
                        final BaseAdapter adapter = new SimpleAdapter(tela_parada_maquina.this, oslist, R.layout.maquinas_custom,
                                new String[] {TAG_MAQUINA, TAG_ID},
                                new int[] {R.id.txtMaquina, R.id.txtIdMaquina});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final String idProg = oslist.get(+position).get("id");
                                final String descMaquina = oslist.get(+position).get("maquina");
                                //Toast.makeText(tela_parada_maquina.this, "O item clicado foi: " + idProg, Toast.LENGTH_SHORT).show();

                                VerificaSituacaoMaquina(idProg);

                                /*
                                Intent it = new Intent(tela_parada_maquina.this, tela_motivo_parada.class);
                                it.putExtra("idMaquina", idProg);
                                startActivity(it);

                             /*   AlertDialog.Builder alert = new AlertDialog.Builder(tela_maquinas.this);
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

                                alert.show();*/

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

    public void VerificaSituacaoMaquina(final String idMaquina){

        AsyncTask<String, Object, String> asyncTask = new AsyncTask<String, Object, String>() {

            @Override
            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_parada_maquina.this);
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "verifica_maquina_parada"));
                    pairs.add(new BasicNameValuePair("idMaquina", idMaquina));
                    post.setEntity(new UrlEncodedFormEntity(pairs));

                    HttpResponse response = client.execute(post);
                    String responseString = EntityUtils.toString(response.getEntity());

                    return responseString.trim();

                } catch (Exception e) {
                    return null;
                }
            }

            protected void onPostExecute(String result) {

                dialog.dismiss();

                String r[] = result.split(";");

                if (r[0].equals("1")) {
                    //Toast.makeText(tela_parada_maquina.this, "Iniciar parada: "+result, Toast.LENGTH_LONG).show();

                    Intent it = new Intent(tela_parada_maquina.this, tela_motivo_parada.class);
                    it.putExtra("idMaquina", idMaquina);
                    startActivity(it);

                } else {
                    //Toast.makeText(tela_parada_maquina.this, "Ja existe parada", Toast.LENGTH_LONG).show();

                    AlertDialog.Builder alert = new AlertDialog.Builder(tela_parada_maquina.this);
                    alert.setTitle("Aviso");
                    alert.setMessage("Deseja colocar essa máquina em produção?");

                    alert.setPositiveButton("SIM",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RetomarProducao(idMaquina);
                        }
                    });

                    alert.setNegativeButton("NÃO",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alert.show();

                }
            }

        };
        asyncTask.execute();

    }

    public void RetomarProducao(final String idMaquina){

        AsyncTask<String, Object, String> asyncTask = new AsyncTask<String, Object, String>() {

            @Override
            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_parada_maquina.this);
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "retomar_producao"));
                    pairs.add(new BasicNameValuePair("idMaquina", idMaquina));
                    post.setEntity(new UrlEncodedFormEntity(pairs));

                    HttpResponse response = client.execute(post);
                    String responseString = EntityUtils.toString(response.getEntity());

                    return responseString.trim();

                } catch (Exception e) {
                    return null;
                }
            }

            protected void onPostExecute(String result) {

                dialog.dismiss();

                String r[] = result.split(";");

                if (r[0].equals("1")) {
                    Toast.makeText(tela_parada_maquina.this, "Máquina liberada para produção!", Toast.LENGTH_LONG).show();

                    Intent it = new Intent(tela_parada_maquina.this, tela_menu.class);
                    startActivity(it);

                } else {
                    Toast.makeText(tela_parada_maquina.this, "Erro ao retomar produção, tente novamente!", Toast.LENGTH_LONG).show();
                }
            }

        };
        asyncTask.execute();

    }
}
