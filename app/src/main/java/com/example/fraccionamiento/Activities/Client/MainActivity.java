package com.example.fraccionamiento.Activities.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fraccionamiento.Activities.LoginActivity;
import com.example.fraccionamiento.Classes.FirebaseClass;
import com.example.fraccionamiento.Classes.PaymentInfoClass;
import com.example.fraccionamiento.Classes.UserClass;
import com.example.fraccionamiento.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    // Declaración de variables
    private FirebaseAuth firebaseAuth;
    private TextView txtPayDate, txtQuantityPay, txtIsPayed;
    private FirebaseUser user;
    private ImageView imgVwIconPayStatus;
    private final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mRefUser;
    private DatabaseReference mRefPaymentInfo;
    private ValueEventListener valueEventListenerPayInfo;

    // Creamos la vista de la aplicación y asignamos las respectivas vistas a los objetos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtPayDate = findViewById(R.id.txtPayDate);
        txtQuantityPay = findViewById(R.id.txtQuantityPay);
        txtIsPayed = findViewById(R.id.txtIsPayed);
        imgVwIconPayStatus = findViewById(R.id.imgVwIconPayStatus);
        ActionBar toolbar = getSupportActionBar();

        if(toolbar!=null){
            toolbar.setTitle(R.string.balance);
        }



        // Creamos las instancias a las bases de datos

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        // Creamos un objeto para obtener la fecha actual y al macenarla en formato numerico
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"));
        cal.setTime(date);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);


        // Obtenernos la información del usuario acerca de su deuda actual

        mRefUser = mRef.child(FirebaseClass.USERS).child(user.getUid());
        mRefPaymentInfo = mRef.child(FirebaseClass.PAYMENT_INFO).child(FirebaseClass.USERS).child(user.getUid());
        valueEventListenerPayInfo = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserClass userClass = dataSnapshot.getValue(UserClass.class);
                mRefPaymentInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        PaymentInfoClass paymentInfo = dataSnapshot.getValue(PaymentInfoClass.class);
                            if(userClass.getDebt()){
                                if(paymentInfo!=null){
                                    txtQuantityPay.setText(String.valueOf(paymentInfo.getRentTotal()));
                                    int realMonth;
                                    if (paymentInfo.getPaymentDay() >= day) {
                                        realMonth = month + 1;
                                    }else {
                                        realMonth = month + 2;
                                    }
                                    String limitDay;
                                    if(realMonth<12){
                                        limitDay = paymentInfo.getPaymentDay() + " / " + realMonth + " / " + year;
                                    }else {
                                        limitDay = paymentInfo.getPaymentDay() + " / " + 1 + " / " + (year+1);
                                    }

                                    txtPayDate.setText(limitDay);
                                }else {
                                    txtQuantityPay.setText(getString(R.string.error));
                                    txtPayDate.setText(getString(R.string.error));
                                }

                                txtIsPayed.setText(getString(R.string.not_payed));
                                imgVwIconPayStatus.setImageDrawable(getDrawable(R.drawable.ic_not_payed));

                            }else{
                                if(paymentInfo!=null){
                                    int realMonth;
                                    if (paymentInfo.getPaymentDay() >= day) {
                                        realMonth = month + 1;
                                    }else {
                                        realMonth = month + 2;
                                    }
                                    String limitDay;
                                    if(realMonth<12){
                                        limitDay = paymentInfo.getPaymentDay() + " / " + realMonth + " / " + year;
                                    }else {
                                        limitDay = paymentInfo.getPaymentDay() + " / " + 1 + " / " + (year+1);
                                    }

                                    txtPayDate.setText(limitDay);
                                }else {
                                    txtQuantityPay.setText(getString(R.string.error));
                                    txtPayDate.setText(getString(R.string.error));
                                }

                                txtQuantityPay.setText("0.00");
                                txtIsPayed.setText(getString(R.string.payed));
                                imgVwIconPayStatus.setImageDrawable(getDrawable(R.drawable.ic_payed));


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        mRefUser.addValueEventListener(valueEventListenerPayInfo);
    }


    // Creamos el menu de iconos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    // funcion para determinar accion tras presionar iconos
    public void iconAction(MenuItem menuItem){
        // Properties for item selected
        menuItem.setChecked(true);

        switch (menuItem.getItemId()){
            case R.id.action_logout:
                logout();
                break;
            case R.id.action_receipts:
                Intent intent = new Intent(MainActivity.this, ReceiptsActivity.class);
                startActivity(intent);
                break;
        }
    }

    // metodo de deslogeo
    private void logout() {
        firebaseAuth.signOut();
        goToLogin();


    }

    // Ir a login tras deslogearse
    private void goToLogin() {
        Intent goToLogin = new Intent(this, LoginActivity.class);
        startActivity(goToLogin);
    }


    // Remover eventos tras el abandono de la actividad

    @Override
    protected void onPause() {
        mRefUser.removeEventListener(valueEventListenerPayInfo);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mRefUser.addValueEventListener(valueEventListenerPayInfo);
        super.onResume();
    }
}
