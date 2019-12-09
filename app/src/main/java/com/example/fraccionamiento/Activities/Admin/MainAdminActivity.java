package com.example.fraccionamiento.Activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import com.example.fraccionamiento.Activities.LoginActivity;
import com.example.fraccionamiento.Classes.FirebaseClass;
import com.example.fraccionamiento.Classes.UserClass;
import com.example.fraccionamiento.R;

import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdminActivity extends AppCompatActivity {
// Declaramos las variables
    private AppBarConfiguration mAppBarConfiguration;
    private TextView lblUser;
    private TextView lblEmail;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth firebaseAuth;
    private CircleImageView imgUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Asignamos las variables a las vistas

        firebaseAuth = FirebaseAuth.getInstance();


        // Asignamos las vistas a los objetos manipulables, la barra de navegación lateral izquierda
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View navHeader = navigationView.getHeaderView(0);
        lblUser = navHeader.findViewById(R.id.lblNavAdminUser);
        lblEmail = navHeader.findViewById(R.id.lblNavAdminEmail);
        imgUser = navHeader.findViewById(R.id.imageNavAdminUser);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Creamos una instancia a la base de datos y obtenemos los datos del usuario
        DatabaseReference myRef = database.getReference(FirebaseClass.USERS);

        myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserClass userDatabase = dataSnapshot.getValue(UserClass.class);
                if(userDatabase.getName()!=null){
                    lblUser.setText(userDatabase.getName());
                }
                if(userDatabase.getEmail()!=null){
                    lblEmail.setText(userDatabase.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Configuramos la App bar y asignamos sus respectivos items
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    // metodo para regresar al login
    private void goToLogin() {
        Intent intent = new Intent(MainAdminActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // Asignamos los iconos del menu superior derecho
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_admin, menu);
        return true;
    }

    // Brindamos accion a los items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            toggle.getToolbarNavigationClickListener();
        }
        return super.onOptionsItemSelected(item);
    }

    // Controller para la navigation web
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void iconAction(MenuItem menuItem){
        // Properties for item selected
        menuItem.setChecked(true);

        switch(menuItem.getItemId()){
            case R.id.action_logout:
                logout();
                break;

        }
    }


    private void logout() {
        firebaseAuth.signOut();
        goToLogin();
    }
}
