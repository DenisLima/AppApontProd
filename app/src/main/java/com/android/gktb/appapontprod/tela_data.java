package com.android.gktb.appapontprod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

/**
 * Created by denis on 22/06/15.
 */
public class tela_data extends Activity {

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.tela_data);

        Button btnSair = (Button) findViewById(R.id.btnSair);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(tela_data.this, tela_menu.class);
                startActivity(it);
                finish();
            }
        });

        final DatePicker dp = (DatePicker) findViewById(R.id.datePicker);

        Button btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dia = String.valueOf(dp.getDayOfMonth());
                String mes = String.valueOf((dp.getMonth())+1);
                String ano = String.valueOf(dp.getYear());

                if (mes.length() == 1){
                    mes = "0"+mes;
                }

                Intent it = new Intent(tela_data.this, tela_maquinas.class);
                it.putExtra("data",dia+"/"+mes+"/"+ano);
                startActivity(it);

                //Toast.makeText(tela_data.this, "A escolha foi: "+dia+"/"+mes+"/"+ano,Toast.LENGTH_LONG).show();
            }
        });

    }

}
