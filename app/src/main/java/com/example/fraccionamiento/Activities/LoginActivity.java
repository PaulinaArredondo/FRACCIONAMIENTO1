package com.example.fraccionamiento.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fraccionamiento.Activities.Client.MainActivity;
import com.example.fraccionamiento.Classes.FirebaseClass;
import com.example.fraccionamiento.Classes.UserClass;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.fraccionamiento.R;

import com.example.fraccionamiento.Activities.Admin.MainAdminActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    // Declaramos las variables a utilizar
    private Button btnLogin;
    private EditText txtEmail;
    private EditText txtPass;
    private AlertDialog alertDialog;
    private FirebaseAuth fBAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Asignamos los campos a las variables correspondientes por su ID
        txtEmail = findViewById(R.id.inputEmail);
        txtPass = findViewById(R.id.inputUserPass);
        btnLogin = findViewById(R.id.btnLogin);

        fBAuth = FirebaseAuth.getInstance();

        // Declaramos la acción a seguir al pulsar el botón de iniciar sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si los campos estan correctos
                if(areCorrectFields()){
                    // intentamos iniciar sesión
                    tryLogin();
                }
            }
        });

        // validamos si existe una sesión activa en la app
        if(fBAuth.getCurrentUser()!=null){
            // si existe una sesión, vamos a la Activity principal
            goToMainActivity(fBAuth.getCurrentUser());
        }
    }

    private void tryLogin() {
        // Asignamos los valores introducidos por el usuario a variables tipo string
        String email = txtEmail.getText().toString().trim();
        String password = txtPass.getText().toString().trim();

        // Declaramos un ProgressDialog que indique que la operación de inicio de sesión se este llevando acabo
        final ProgressDialog prgDlg = new ProgressDialog(this);
        // Asignamos el mensaje
        prgDlg.setMessage(getString(R.string.staring_session));
        // Mostramos la ventana
        prgDlg.show();

        // Iniciamos una instancia de autenticacion con firebase
        final FirebaseAuth fBAuth = FirebaseAuth.getInstance();
        // Tratamos de iniciar sesión con los datos introdcidos por el usuario
        fBAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Al iniciar sesión, vamos a la Activity principal
                            FirebaseUser user = fBAuth.getCurrentUser();
                            if(user != null){
                                goToMainActivity(user);
                            }

                            prgDlg.dismiss();

                        } else {
                            // Indicamos al usuario que los datos ingresados son invalidos
                            prgDlg.dismiss();
                            alertDialog = crateAlertDialog(getString(R.string.invalid_credentials));
                            alertDialog.show();
                        }

                    }
                }).addOnCanceledListener(LoginActivity.this, new OnCanceledListener() {
            @Override
            public void onCanceled() {
                // Mostramos una ventana con un error fatal al iniciar sesión
                alertDialog = crateAlertDialog(getString(R.string.fatal_error_login));
                alertDialog.show();
            }
        });
    }

    // Metodo para crear un mensaje de alerta, asignando como parametro el mensaje a mostrar
    private AlertDialog crateAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(getString(R.string.warning));
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();

    }


    private boolean areCorrectFields(){
        if(txtEmail.getText().toString().isEmpty()){
            // Si el campo de correo esta vacío
            // Arrojamos el error de campo correo vacío
            txtEmail.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }else if(txtPass.getText().toString().isEmpty()){
            // Si el campo de contrasña esta vacío
            // Arrojamos el error de campo contraseña vacío
            txtPass.setError(getString(R.string.error_empty_field));
            // retornamos el valor de falla en la condición
            return false;

        }else {
            // retornamos el valor de cumplimiento en la condición
            return true;
        }

    }




    // Una vez tengamos los datos de conexín verificamos el valor del rol en la base de datos para direccionarlo al menu correspondiente
    private void goToMainActivity(FirebaseUser currentUser){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(FirebaseClass.USERS).child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserClass userDatabase = dataSnapshot.getValue(UserClass.class);
                if(userDatabase.getRole().equals(FirebaseClass.ADMIN)){
                    Intent intent = new Intent(LoginActivity.this, MainAdminActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }if (userDatabase.getRole().equals(FirebaseClass.CLIENT)){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            // si existe una sesión, vamos a la Activity principal
            goToMainActivity(FirebaseAuth.getInstance().getCurrentUser());
        }
        super.onStart();
    }

    //    @Override
//    protected void onResume() {
//
//        super.onResume();
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//            // si existe una sesión, vamos a la Activity principal
//            goToMainActivity(FirebaseAuth.getInstance().getCurrentUser());
//        }
//    }
}
