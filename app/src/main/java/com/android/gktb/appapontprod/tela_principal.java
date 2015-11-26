package com.android.gktb.appapontprod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;


public class tela_principal extends Activity {

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        Button btnSair = (Button) findViewById(R.id.btnSairLogin);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText txtUser = (EditText) findViewById(R.id.txtUsuario);
        final EditText txtPass = (EditText) findViewById(R.id.txtSenha);

        Button btnEnviar = (Button) findViewById(R.id.btnEnviarLogin);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidaAcesso(txtUser.getText().toString(), txtPass.getText().toString());
            }
        });

    }

    public void ValidaAcesso(final String user, final String pass){

        AsyncTask<String, Object, String> asyncTask = new AsyncTask<String, Object, String>() {

            @Override
            protected void onPreExecute(){
                dialog = new ProgressDialog(tela_principal.this);
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/pcp/pcp00016/web_service.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "valida_acesso"));
                    pairs.add(new BasicNameValuePair("usuario", user));
                    pairs.add(new BasicNameValuePair("senha", pass));
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
                    //Toast.makeText(tela_principal.this, "Sucesso: "+result, Toast.LENGTH_LONG).show();

                    Intent it = new Intent(tela_principal.this, tela_menu.class);
                    startActivity(it);
                    finish();

                } else {
                    Toast.makeText(tela_principal.this, "Usuário ou senha incorretos, favor verificar!", Toast.LENGTH_LONG).show();
                }
            }

        };
        asyncTask.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tela_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
