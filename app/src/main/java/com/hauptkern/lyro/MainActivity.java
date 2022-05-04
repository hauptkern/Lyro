package com.hauptkern.lyro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.SearchView;

import com.hauptkern.lyro.databinding.ActivityMainBinding;
import com.hauptkern.lyro.fragments.music;
import com.hauptkern.lyro.fragments.search;
import com.hauptkern.lyro.fragments.settings;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = this.getSharedPreferences("dark_mode",0);
        String appearance_type =pref.getString("dark_mode","").toString();
        if (appearance_type.equals("enabled")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if(appearance_type.equals("disabled")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        search searchtab = new search();
        music musictab = new music();
        settings settingstab = new settings();
        addFragment(searchtab);
        addFragment(musictab);
        addFragment(settingstab);
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().hide(settingstab).commit();
        fragmentManager.beginTransaction().hide(musictab).commit();
        fragmentManager.beginTransaction().show(searchtab).commit();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        binding.navbar.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.nav_search:
                    fragmentManager.beginTransaction().hide(settingstab).commit();
                    fragmentManager.beginTransaction().hide(musictab).commit();
                    fragmentManager.beginTransaction().show(searchtab).commit();
                    break;
                case R.id.nav_music:
                    fragmentManager.beginTransaction().hide(searchtab).commit();
                    fragmentManager.beginTransaction().hide(settingstab).commit();
                    fragmentManager.beginTransaction().show(musictab).commit();
                    break;
                case R.id.nav_settings:
                    fragmentManager.beginTransaction().hide(musictab).commit();
                    fragmentManager.beginTransaction().hide(searchtab).commit();
                    fragmentManager.beginTransaction().show(settingstab).commit();
                    break;
            }

            return true;
        });
    }
    private void addFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frm_wrapper,fragment);
        fragmentTransaction.commit();
    }
}