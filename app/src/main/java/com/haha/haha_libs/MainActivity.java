package com.haha.haha_libs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.haha.libreria.MetodosGenerales;
import com.haha.libreria.clsGlobales;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new MetodosGenerales(this).LlenarSpinnerCustomVerEjemplos();
    }
}
