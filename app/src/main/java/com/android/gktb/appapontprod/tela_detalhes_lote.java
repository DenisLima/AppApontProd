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
import android.widget.EditText;
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
 * Created by denis on 24/06/15.
 */
public class tela_detalhes_lote extends Activity {

    ProgressDialog dialog;
    JSONArray detalheslote = null;
    String TAG_PRIN = "detalheslote";
    String TAG_LOTE = "lote";
    String TAG_MAQUINA = "maquina";
    String TAG_MATERIAL = "material";
    String TAG_FABRICANTE = "fabricante";
    String TAG_PLANO = "qtdPlano";
    String TAG_ADICIONAL = "qtdAdicional";
    String TAG_ALIMENTADA = "qtdAlimentada";
    String TAG_PRODUZIDA = "qtdProduzida";
    String TAG_IDAPONT = "idApont";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;
    String idLote;
    String dataProd;
    String idMaquina;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.lista_detalhes_lote);

        Intent it = getIntent();
        idLote = it.getStringExtra("idLote");
        dataProd = it.getStringExtra("dataProd");
        idMaquina = it.getStringExtra("idMaquina");

        Button btnVoltarDetalhes = (Button) findViewById(R.id.btnVoltarDetalhes);
        btnVoltarDetalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(tela_detalhes_lote.this, tela_menu.class);
                startActivity(i);
                finish();
            }
        });

        CarregaDetalhesProgramacao();
    }

    public void CarregaDetalhesProgramacao(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_detalhes_lote.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php?acao=carrega_detalhes_lote&&idLote="+idLote+"&&data="+dataProd+"&&idMaquina="+idMaquina;
                JSONObject json = jParser.getJSONFromUrl(ord);

                return json;
            }

            protected void onPostExecute(JSONObject json){
                dialog.dismiss();

                try {

                    detalheslote = json.getJSONArray(TAG_PRIN);

                    for (int i = 0; i < detalheslote.length(); i++) {

                        JSONObject c = detalheslote.getJSONObject(i);

                        String lote   = c.getString(TAG_LOTE);
                        String maquina   = c.getString(TAG_MAQUINA);
                        String material   = c.getString(TAG_MATERIAL);
                        String fabricante = c.getString(TAG_FABRICANTE);
                        String qtdplano = c.getString(TAG_PLANO);
                        String qtdadicional = c.getString(TAG_ADICIONAL);
                        String qtdalimentada = c.getString(TAG_ALIMENTADA);
                        String qtdproduzida = c.getString(TAG_PRODUZIDA);
                        String adapont = c.getString(TAG_IDAPONT);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_LOTE, lote);
                        map.put(TAG_MAQUINA, maquina);
                        map.put(TAG_MATERIAL, material);
                        map.put(TAG_FABRICANTE, fabricante);
                        map.put(TAG_PLANO, qtdplano);
                        map.put(TAG_ADICIONAL, qtdadicional);
                        map.put(TAG_ALIMENTADA, qtdalimentada);
                        map.put(TAG_PRODUZIDA, qtdproduzida);
                        map.put(TAG_IDAPONT, adapont);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listaDetalhesLote);
                        final BaseAdapter adapter = new SimpleAdapter(tela_detalhes_lote.this, oslist, R.layout.lote_detalhe_custom,
                                new String[] {TAG_LOTE, TAG_MAQUINA, TAG_MATERIAL, TAG_FABRICANTE, TAG_PLANO, TAG_ADICIONAL, TAG_ALIMENTADA, TAG_PRODUZIDA, TAG_IDAPONT},
                                new int[] {R.id.txtLoteDetalhes, R.id.txtMaquinaDetalhes, R.id.txtItemDetalhes, R.id.txtFabricanteDetalhes, R.id.txtQtdPlanDetalhes, R.id.txtQtdAdicDetalhes, R.id.txtQtdAlimen, R.id.txtQtdReal, R.id.txtIdDetalhes});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final String idDetalhe = oslist.get(+position).get("idApont");
                                //Toast.makeText(tela_detalhes_lote.this, "O item clicado foi: " + idDetalhe, Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder alert = new AlertDialog.Builder(tela_detalhes_lote.this);
                                alert.setTitle("Aviso");
                                alert.setMessage("Informe a quantidade produzida!");

                                final EditText qtdProduzida = new EditText(tela_detalhes_lote.this);
                                qtdProduzida.setInputType(2);
                                alert.setView(qtdProduzida);

                                alert.setNegativeButton("CANCELAR",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                alert.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ValidaQtdProduzida(qtdProduzida.getText().toString(), idDetalhe);
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

    public void ValidaQtdProduzida(final String qtd, final String id){

        AsyncTask<String, Object, String> asyncTask = new AsyncTask<String, Object, String>() {

            @Override
            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_detalhes_lote.this);
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "valida_qtd_produzida"));
                    pairs.add(new BasicNameValuePair("qtd", qtd));
                    pairs.add(new BasicNameValuePair("id", id));
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

                    //Toast.makeText(tela_detalhes_lote.this, "Qtd OK: "+result, Toast.LENGTH_LONG).show();
                    InsereQtdProduzida(qtd,id);

                } else {
                    Toast.makeText(tela_detalhes_lote.this, "Quantidade informada maior que o saldo a ser apontado, verifique!", Toast.LENGTH_LONG).show();
                }
            }

        };
        asyncTask.execute();

    }

    public void InsereQtdProduzida(final String qtd, final String id){
        AsyncTask<String, Object, String> asyncTask = new AsyncTask<String, Object, String>() {

            @Override
            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_detalhes_lote.this);
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "insere_qtd_produzida"));
                    pairs.add(new BasicNameValuePair("qtd", qtd));
                    pairs.add(new BasicNameValuePair("id", id));
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

                    Toast.makeText(tela_detalhes_lote.this, "Material apontado com sucesso!", Toast.LENGTH_LONG).show();

                    Intent it = new Intent(tela_detalhes_lote.this, tela_detalhes_lote.class);
                    it.putExtra("idLote", idLote);
                    it.putExtra("dataProd", dataProd);
                    it.putExtra("idMaquina", idMaquina);
                    startActivity(it);
                    finish();

                } else {
                    Toast.makeText(tela_detalhes_lote.this, "Problemas nao inseriu!", Toast.LENGTH_LONG).show();
                }
            }

        };
        asyncTask.execute();
    }

}
