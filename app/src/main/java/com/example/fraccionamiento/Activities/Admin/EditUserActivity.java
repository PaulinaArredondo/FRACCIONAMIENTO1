package com.example.fraccionamiento.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fraccionamiento.Activities.Client.ReceiptsActivity;
import com.example.fraccionamiento.Activities.LoginActivity;
import com.example.fraccionamiento.Adapters.AdapterPayments;
import com.example.fraccionamiento.Adapters.AdapterPaymentsAdmin;
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
import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {
    // Declaramos las variables
    private TextView name;
    private DatabaseReference mRef;
    private ValueEventListener valueEventListenerReceipts;
    private DatabaseReference paymentsRef;
    private ArrayList<PaymentsClass> payments;
    private AdapterPaymentsAdmin adapterPayments;
    private TextView lblNotPaymentsFound;
    private String uid;
    private DatabaseReference ref;
    private ValueEventListener valueEventListenerPayments;
    private int debtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        name = findViewById(R.id.txtEditUserCompleteName);
        lblNotPaymentsFound = findViewById(R.id.lblNotPaymentsFound);
        final RecyclerView rcVwPayments = findViewById(R.id.rcVwPayments);



        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(getString(R.string.user_payments_info));
        }else {
            Toast.makeText(EditUserActivity.this,getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
            finish();
        }

        Bundle bundle = getIntent().getExtras();

        // almacenamos los datos envíados por la actividad principal

        if(bundle!=null){
            String completeName = bundle.getString("name") +" "+ bundle.getString("lastName");
            name.setText(completeName);
            uid = bundle.getString("uid");

        }else {
            Toast.makeText(EditUserActivity.this,getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
            finish();
        }

        // Creamos una instancia a la base de datos para traer los pagos del usuario solicitado
        mRef = FirebaseDatabase.getInstance().getReference();
        paymentsRef = mRef.child(FirebaseClass.PAYMENTS).child(FirebaseClass.USERS).child(uid);

        payments = new ArrayList<>();
        adapterPayments = new AdapterPaymentsAdmin(payments);

        rcVwPayments.setLayoutManager(new LinearLayoutManager(this));

        rcVwPayments.setAdapter(adapterPayments);


        // Creamos un evento que nos permitirar iterar sobre los multiples resultados
        valueEventListenerReceipts = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                payments.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    PaymentsClass payment = snapshot.getValue(PaymentsClass.class);
                    payment.setKey(snapshot.getKey());
                    payment.setUid(uid);

                    payments.add(payment);
                }


                adapterPayments.notifyDataSetChanged();


                // Si no se encuentran resultados mostrara un mensaje
                if(adapterPayments.getItemCount()==0){
                    lblNotPaymentsFound.setVisibility(View.VISIBLE);
                }else {
                    lblNotPaymentsFound.setVisibility(View.INVISIBLE);
                }

                // AL dar click sobre un usuario este dara la opción si se desea registrar como mensualidad pagada
                adapterPayments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = rcVwPayments.getChildAdapterPosition(v);
                        if(payments.get(position).isDebt()){
                            PaymentsClass paymentsUpdateYes = new PaymentsClass(payments.get(position).getMonth(), payments.get(position).getYear(), payments.get(position).getMountPayed(), true, false, payments.get(position).getMountDebt());
                            PaymentsClass paymentsUpdateNo = new PaymentsClass(payments.get(position).getMonth(), payments.get(position).getYear(), payments.get(position).getMountPayed(), false, true, payments.get(position).getMountDebt());

                            AlertDialog alertDialog = crateAlertDialog(getString(R.string.check_as_payed), payments.get(position).getUid(), payments.get(position).getKey(), paymentsUpdateYes, paymentsUpdateNo);
                            alertDialog.show();
                        }

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        paymentsRef.orderByChild("month").limitToLast(36).addValueEventListener(valueEventListenerReceipts);






    }


    // Creamos la alerta con el titulo, el mensaje y los botones de si y no para asignar el pago como realizado e insertamos los resultados en la base de datos

    private AlertDialog crateAlertDialog(String message, final String uid, final String key, final PaymentsClass paymentsYes, final PaymentsClass paymentsNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
        builder.setTitle(getString(R.string.warning_info));
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ref = FirebaseDatabase.getInstance().getReference().child(FirebaseClass.PAYMENTS).child(FirebaseClass.USERS).child(uid);
                valueEventListenerPayments = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        debtCount = 0;
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            PaymentsClass paymentsClass = snapshot.getValue(PaymentsClass.class);
                            if(paymentsClass.isDebt()){
                                debtCount++;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };


                // Imprimimos resultados de las acciones realizadas
                ref.addValueEventListener(valueEventListenerPayments);
                ref.child(key).updateChildren(paymentsYes.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditUserActivity.this, getString(R.string.successfully_updated_info), Toast.LENGTH_LONG).show();
                        } else if (task.isCanceled()) {
                            Toast.makeText(EditUserActivity.this, getString(R.string.fatal_error), Toast.LENGTH_LONG).show();

                        }
                    }
                });


                // generamos una istancia con la base de datos para traer la cantidades mesuales a pagar y las ya pagadas
                final DatabaseReference refPaymentInfo = FirebaseDatabase.getInstance().getReference().child(FirebaseClass.PAYMENT_INFO).child(FirebaseClass.USERS).child(uid);

                refPaymentInfo.child("rentTotal").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> map = new HashMap<>();
                        map.put(dataSnapshot.getKey(), dataSnapshot.getValue());
                        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child(FirebaseClass.USERS).child(uid);
                        int rentTotal = Integer.parseInt(map.get("rentTotal").toString())*debtCount;
                        refPaymentInfo.child("rentTotal").setValue(rentTotal);
                        if(rentTotal==0){
                            refUser.child("debt").setValue(false);
                        }else {
                            refUser.child("debt").setValue(true);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        return builder.create();
    }


    // Al parar y pausar la aplicacion removemos los eventos para no ocacionar cierres inesperados
    @Override
    protected void onStop() {
        paymentsRef.removeEventListener(valueEventListenerReceipts);
        ref.removeEventListener(valueEventListenerPayments);
        super.onStop();
    }

    @Override
    protected void onPause() {
        paymentsRef.removeEventListener(valueEventListenerReceipts);
        ref.removeEventListener(valueEventListenerPayments);
        super.onPause();
    }

    // Asignamos opciones a los iconos superiores
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_close:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // metodo para regresar al menu principal
    private void goToMain() {
        Intent intent = new Intent(EditUserActivity.this, MainAdminActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_close, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
