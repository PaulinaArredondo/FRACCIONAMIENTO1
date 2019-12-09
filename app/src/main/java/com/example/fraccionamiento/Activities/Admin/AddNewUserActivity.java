package com.example.fraccionamiento.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fraccionamiento.Activities.LoginActivity;
import com.example.fraccionamiento.Classes.FirebaseClass;
import com.example.fraccionamiento.Classes.PaymentInfoClass;
import com.example.fraccionamiento.Classes.UserClass;
import com.example.fraccionamiento.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddNewUserActivity extends AppCompatActivity {
    // Declaramos las variables a utilizar
    private TextView name, lastName, email, pass, confirmPass, txtMaintenance, txtRent, paymentDay, txtBuild, txtDepartment;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String nameS, lastNameS, emailS, passS, confirmPassS, build, departmaent;
    private int maintenance, rent, payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        //Asignamos un titulo a la toolbar
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar!=null){
            mActionBar.setTitle(getString(R.string.add_new_user));
        }

        //Asignamos las vistas con su respectivo objeto
        name = findViewById(R.id.txtAddName);
        lastName = findViewById(R.id.txtAddLastName);
        email = findViewById(R.id.txtAddEmail);
        pass = findViewById(R.id.txtAddPass);
        confirmPass = findViewById(R.id.txtAddConfirmPass);
        txtMaintenance = findViewById(R.id.txtAddInfoUserMaintenance);
        txtRent = findViewById(R.id.txtAddPaymentDay);
        paymentDay = findViewById(R.id.txtAddPaymentDay);
        txtBuild = findViewById(R.id.txtBuild);
        txtDepartment = findViewById(R.id.txtDepartment);
        Button btnAddUser = findViewById(R.id.btnAddUser);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Declaramos un dialogo con progreso y asignamos sus variables
        final ProgressDialog prgDlg = new ProgressDialog(this);
        prgDlg.setMessage(getString(R.string.creating_user));
        prgDlg.setCancelable(false);
        // Agregamos a función al botón
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Asignamos los valores brindados por el usuario a cada variable
                prgDlg.show();
                nameS = name.getText().toString();
                emailS = email.getText().toString();
                passS = pass.getText().toString();
                lastNameS = lastName.getText().toString();
                confirmPassS = confirmPass.getText().toString();
                maintenance = Integer.parseInt(txtMaintenance.getText().toString());
                rent = Integer.parseInt(txtRent.getText().toString());
                payment = Integer.parseInt(paymentDay.getText().toString());
                build = txtBuild.getText().toString();
                departmaent = txtDepartment.getText().toString();

                // Validamos los campos
                if(areCorrectFields()){
                    //Nos aseguramos que las contraseñas cincidan
                    if(passMatch()){
                        // Intentamos crear un usuario con los datos brindados
                        mAuth.createUserWithEmailAndPassword(emailS, passS).addOnCompleteListener(AddNewUserActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    // Si es exitoso, almacenamos los datos del usuario en un nodo de firebase
                                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                                    final UserClass userClass = new UserClass(nameS, emailS, lastNameS, "client", "url", false);
                                    // Cerramos el proceso
                                    prgDlg.dismiss();


                                    // Subimos la información adicional
                                    prgDlg.setMessage(getString(R.string.uploading_data));
                                    prgDlg.show();

                                    // Insertamos los datos del usuario
                                    mRef.child(fUser.getUid()).setValue(userClass);

                                    // Creamos una instacia con la base de datos donde guardaremos la información de pago
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(FirebaseClass.PAYMENT_INFO).child(FirebaseClass.USERS).child(fUser.getUid());
                                    int rentTotal = maintenance+rent;
                                    PaymentInfoClass paymentInfo = new PaymentInfoClass(maintenance, payment, rent, rentTotal);
                                    Map<String, Object> paymentInfoMap = paymentInfo.toMap();
                                    userRef.updateChildren(paymentInfoMap);

                                    // Creamos una instacia con la base de datos donde guardaremos la información de alojamiento del huesped

                                    DatabaseReference mRefBuild = FirebaseDatabase.getInstance().getReference().child(FirebaseClass.BUILDS).child(build).child(FirebaseClass.USERS);
                                    Map<String, Object> uidAdd = new HashMap<>();
                                    uidAdd.put(fUser.getUid(), departmaent);
                                    mRefBuild.updateChildren(uidAdd);
                                    Toast.makeText(AddNewUserActivity.this, getString(R.string.successfull_user_add), Toast.LENGTH_LONG).show();

                                    // Cerramos el proceso - cerramos la sesión del usuario que se creo
                                    prgDlg.dismiss();
                                    FirebaseAuth.getInstance().signOut();

                                    // Iniciamos la actividad de login para iniciar sesión
                                    Intent intent = new Intent(AddNewUserActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();


                                }else {
                                    // Si falla enviamos el error
                                    prgDlg.dismiss();
                                    Toast.makeText(AddNewUserActivity.this, getString(R.string.already_user), Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                // Si falla enviamos el error

                                prgDlg.dismiss();
                                Toast.makeText(AddNewUserActivity.this, getString(R.string.fatal_error),Toast.LENGTH_LONG).show();
                            }
                        });

                    }else {
                        // Si falla enviamos el error

                        prgDlg.dismiss();
                        Toast.makeText(AddNewUserActivity.this, getString(R.string.pass_not_match), Toast.LENGTH_LONG).show();
                    }

                }else {
                    // Verificamos que el proceso de carga de cierre
                    prgDlg.dismiss();
                }
            }
        });
    }

    // Validamos que las contraseñas introducidas coincidan
    private boolean passMatch() {
        if(passS.equals(confirmPassS)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean areCorrectFields(){
        if(nameS.isEmpty()){
            // Si el campo de correo esta vacío
            // Arrojamos el error de campo correo vacío
            name.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;
        }

        else if(lastNameS.isEmpty()){
            // Si el campo de contrasña esta vacío
            // Arrojamos el error de campo contraseña vacío
            lastName.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }else if(passS.isEmpty()){
            // Si el campo de contrasña esta vacío
            // Arrojamos el error de campo contraseña vacío
            pass.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }else if(passS.length()<7){
            pass.setError(getString(R.string.minor_7));
            return false;

        }else if(confirmPassS.isEmpty()){
            // Si el campo de contrasña esta vacío
            // Arrojamos el error de campo contraseña vacío
            confirmPass.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }else if(maintenance==0){
            // Si el campo de contrasña esta vacío
            // Arrojamos el error de campo contraseña vacío
            txtMaintenance.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }else if(rent==0){
            // Si el campo de contrasña esta vacío
            // Arrojamos el error de campo contraseña vacío
            txtRent.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }else if(payment==0){
            // Si el campo de contrasña esta vacío
            // Arrojamos el error de campo contraseña vacío
            paymentDay.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }else if(txtBuild.equals("")){
            // Si el campo de contrasña esta vacío
            // Arrojamos el error de campo contraseña vacío
            txtBuild.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }else if(txtDepartment.equals("")){
            // Si el campo de contrasña esta vacío
            // Arrojamos el error de campo contraseña vacío
            txtDepartment.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }
        else {
            // retornamos el valor de cumplimiento en la condición
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_close:
                finish();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

//    private AlertDialog crateAlertDialog(String message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewUserActivity.this);
//        builder.setTitle(getString(R.string.warning));
//        builder.setMessage(message);
//        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        return builder.create();
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_close, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
