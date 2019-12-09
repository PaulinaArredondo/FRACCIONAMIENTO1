package com.example.fraccionamiento.Activities.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fraccionamiento.Activities.Admin.AddNewUserActivity;
import com.example.fraccionamiento.Adapters.AdapterPayments;
import com.example.fraccionamiento.Classes.FirebaseClass;
import com.example.fraccionamiento.Classes.PaymentsClass;
import com.example.fraccionamiento.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReceiptsActivity extends AppCompatActivity {
    // Declaración de variables
    private DatabaseReference mRef;
    private ValueEventListener valueEventListenerReceipts;
    private FirebaseUser fUser;
    private FirebaseAuth fAuth;
    private DatabaseReference paymentsRef;
    private ArrayList<PaymentsClass> payments;
    private AdapterPayments adapterPayments;
    private TextView lblNotPaymentsFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creación de vistas
        setContentView(R.layout.activity_receipts);
        ActionBar actionBar = getSupportActionBar();

        // Asignación del titulo
        if(actionBar!=null){
            actionBar.setTitle(R.string.receipts);
        }

        // Declaracoón del recicler para la creacion de cada uno de los conjuntos de datos obtenidos de las bases de datos
        // Para la inflación de vistas respecto al historial de pagos realizados por el usuario
        final RecyclerView rcVwPayments = findViewById(R.id.rcVwPayments);
        lblNotPaymentsFound = findViewById(R.id.lblNotPaymentsFound);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        mRef = FirebaseDatabase.getInstance().getReference();

        paymentsRef = mRef.child(FirebaseClass.PAYMENTS).child(FirebaseClass.USERS).child(fUser.getUid());

        payments = new ArrayList<>();
        adapterPayments = new AdapterPayments(payments);

        rcVwPayments.setLayoutManager(new LinearLayoutManager(this));

        rcVwPayments.setAdapter(adapterPayments);


        valueEventListenerReceipts = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                payments.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    PaymentsClass payment = snapshot.getValue(PaymentsClass.class);

                    payments.add(payment);
                }


                adapterPayments.notifyDataSetChanged();

                if(adapterPayments.getItemCount()==0){
                    lblNotPaymentsFound.setVisibility(View.VISIBLE);
                }else {
                    lblNotPaymentsFound.setVisibility(View.INVISIBLE);
                }

                adapterPayments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        paymentsRef.orderByChild("month").limitToLast(36).addValueEventListener(valueEventListenerReceipts);


    }









    @Override
    protected void onPause() {
        paymentsRef.removeEventListener(valueEventListenerReceipts);
        super.onPause();
    }

    @Override
    protected void onStop() {
        paymentsRef.removeEventListener(valueEventListenerReceipts);
        super.onStop();
    }



}
