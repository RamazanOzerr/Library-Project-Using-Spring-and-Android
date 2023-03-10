package com.example.spring_android_project.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.spring_android_project.Adapters.CustomViewPager;
import com.example.spring_android_project.Fragments.AvailableBooksFragment;
import com.example.spring_android_project.Fragments.DowloadedBooksFragment;
import com.example.spring_android_project.R;
import com.example.spring_android_project.Utils.Book;
import com.example.spring_android_project.Utils.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int STORAGE_PERMISSION_CODE = 1;
    private List<User> listUser = new ArrayList<>();
    private List<Book> listBook = new ArrayList<>();
    private CustomViewPager customViewPager;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MaterialToolbar toolbar;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        replaceFragments(new AvailableBooksFragment());
        setFragments();
        runTimePermissions();
    }

    //initialize
    private void init(){
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.view_pager);
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
    }

    // methods to switch between fragments
    public void replaceFragments(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_main,fragment);
        fragmentTransaction.commit();
    }

    //set fragments into viewPager and Tablayout
    private void setFragments(){
        customViewPager = new CustomViewPager(getSupportFragmentManager());
        customViewPager.addFragment(new AvailableBooksFragment(),"Available Books");
        customViewPager.addFragment(new DowloadedBooksFragment(),"Downloaded Books");

        viewPager.setAdapter(customViewPager);

        tabLayout.setupWithViewPager(viewPager);
    }

    // set toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(getApplicationContext(),"CLICKED SEARCH",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getApplicationContext(), PDFViewActivity.class));
                return true;

            case R.id.log_out:
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // set required runtime permissions --> Read and write external storage
    private void runTimePermissions(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"already granted",Toast.LENGTH_LONG).show();
        }else{
            requestStoragePermission();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"PERMISSION GRANTED",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"PERMISSION DENIED",Toast.LENGTH_LONG).show();

            }
        }

    }
    private void requestStoragePermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ){
            new AlertDialog.Builder(this).setTitle("Permission needed")
                    .setMessage("This permission needed to access your storage and open pdf files")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                    {Manifest.permission.READ_EXTERNAL_STORAGE
                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }
}