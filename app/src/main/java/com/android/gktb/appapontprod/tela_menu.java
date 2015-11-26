package com.android.gktb.appapontprod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by denis on 11/06/15.
 */
public class tela_menu extends Activity {

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }


    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.menu_principal);

        Button btnSairMenu = (Button) findViewById(R.id.btnSairProd);
        btnSairMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnApontPar = (Button) findViewById(R.id.btnApontarParada);
        btnApontPar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(tela_menu.this,tela_parada_maquina.class);
                startActivity(in);
            }
        });

        Button btnApontProd = (Button) findViewById(R.id.btnApontarProd);
        btnApontProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(tela_menu.this);
                alert.setTitle("Aviso");
                alert.setMessage("Programação do dia ?");

                alert.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent(tela_menu.this, tela_data.class);
                        startActivity(it);
                        finish();
                    }
                });

                alert.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(tela_menu.this, tela_maquinas.class);
                        i.putExtra("data",getDateTime());
                        startActivity(i);
                        finish();

                    }
                });

                alert.show();

            }
        });

    }

}
