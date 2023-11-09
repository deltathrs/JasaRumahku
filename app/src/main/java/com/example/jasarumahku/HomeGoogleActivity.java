package com.example.jasarumahku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.jasarumahku.databinding.ActivityHomeBinding;
import com.example.jasarumahku.databinding.ActivityHomeGoogleBinding;

public class HomeGoogleActivity extends AppCompatActivity {

    ActivityHomeGoogleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityHomeGoogleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.activity:
                    replaceFragment(new ActivityFragment());
                    break;
                case R.id.wallet:
                    replaceFragment(new WalletFragment());
                    break;
                case R.id.message:
                    replaceFragment(new MessageFragment());
                    break;
                case R.id.account:
                    replaceFragment(new AccountFragment());
                    break;
            }


            return true;
        });
    }

    private  void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}