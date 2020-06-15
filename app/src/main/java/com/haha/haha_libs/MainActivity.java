package com.haha.haha_libs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.haha.login_lib.MainLogin;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainLogin mainLogin  = new MainLogin();

        boolean a = mainLogin.isLibrary(true);
    }
}
