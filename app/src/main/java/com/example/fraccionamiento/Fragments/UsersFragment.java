package com.example.fraccionamiento.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fraccionamiento.Activities.Admin.AddNewUserActivity;
import com.example.fraccionamiento.Adapters.AdapterUsers;
import com.example.fraccionamiento.Classes.FirebaseClass;
import com.example.fraccionamiento.Classes.UserClass;
import com.example.fraccionamiento.Activities.Admin.EditUserActivity;
import com.example.fraccionamiento.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


// Fragment que muestra la información de los usuarios y sus pagos

public class UsersFragment extends Fragment {

    // Declaración de variables
    private ArrayList<UserClass> userList;
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private AppCompatSpinner spnOrderBy;
    private String order;
    private TextView lblNoFoundUsers;
    private EditText txtSearchUser;
    private RecyclerView rcVwUsers;
    private AdapterUsers adapterUsers;
    private FloatingActionButton fABAddUser;


    // Asignación de recursos en creación de vista

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);
        lblNoFoundUsers = root.findViewById(R.id.lblNotUsersFound);
        spnOrderBy = root.findViewById(R.id.spnOrderBy);
        txtSearchUser = root.findViewById(R.id.txtSearchUser);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcVwUsers = root.findViewById(R.id.rcVwUsers);
        rcVwUsers.setLayoutManager(linearLayoutManager);
        userList = new ArrayList<>();
        adapterUsers = new AdapterUsers(userList);
        fABAddUser = root.findViewById(R.id.fabAddNewUser);


        // Rcicler utilizado para creaun multiples vistas a permitir de un formato

        rcVwUsers.setAdapter(adapterUsers);


        // Registro de cambios en el buscador
        txtSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findUserFromFireBase(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        //Obtener datos de filtrado de los spimner
        spnOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                findUserFromFireBase("spinner");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        fABAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUser();
            }
        });

        return root;
    }

    private void newUser() {
        Intent intent = new Intent(getActivity(), AddNewUserActivity.class);
        startActivity(intent);
    }

    // Buscar a los usaurios con base a los filtros seleccionados
    private void findUserFromFireBase(final String s) {
        // Indicamos el tipo de filtro
        order = spnOrderBy.getSelectedItem().toString();
        // validamos cual es
        if(s.equals("spinner")){
            // borramos los datos existentes para cada nueva busqueda
            userList.removeAll(userList);
            // nuscamos los datos que cumplan con los filtros establecidos
            dbRef.child(FirebaseClass.BUILDS).child(order).child(FirebaseClass.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Iteramos los datos con las clases y generamos las vistas para cada una de las tuplas obteinida, esto con los recyclers
                    // Adaptadores y classses
                    if (dataSnapshot.exists()){

                        for (final DataSnapshot snapshot: dataSnapshot.getChildren()){
                            dbRef.child(FirebaseClass.USERS).child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    HashMap<String, String> user = new HashMap<>();
                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                        user.put(snapshot1.getKey(), snapshot1.getValue().toString());
                                    }
                                    UserClass userClass = new UserClass(user);

                                    userClass.setDeptNum(snapshot.getValue().toString());
                                    userClass.setDebt(Boolean.parseBoolean(user.get("debt")));
                                    userClass.setKey(snapshot.getKey());

                                    if(userClass.getRole().equals(FirebaseClass.CLIENT)){
                                        userList.add(userClass);
                                    }


                                    adapterUsers.notifyDataSetChanged();

                                    if(adapterUsers.getItemCount()==0){
                                        lblNoFoundUsers.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        lblNoFoundUsers.setVisibility(View.INVISIBLE);
                                    }


                                    // Si el usuario da click sobre al gun elemento se direccionara a una vista de detalle

                                    adapterUsers.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int rcvPosition = rcVwUsers.getChildLayoutPosition(v);
                                            String name = userList.get(rcvPosition).getName();
                                            String lastName = userList.get(rcvPosition).getLastName();
                                            String uid = userList.get(rcvPosition).getKey();

                                            // Pasamos los datos requeridos a la siguiente vista
                                            Bundle data = new Bundle();
                                            data.putString("name", name);
                                            data.putString("lastName", lastName);
                                            data.putString("uid", uid);
                                            Intent intent = new Intent(getActivity(), EditUserActivity.class);
                                            intent.putExtras(data);
                                            startActivity(intent);
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }else {
                        if(adapterUsers.getItemCount()==0){
                            lblNoFoundUsers.setVisibility(View.VISIBLE);
                        }
                        else {
                            lblNoFoundUsers.setVisibility(View.INVISIBLE);
                        }
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            // Se valida junto con el otro filtro y se repite el condicionamiento
            userList.removeAll(userList);
            dbRef.child(FirebaseClass.BUILDS).child(order).child(FirebaseClass.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (final DataSnapshot snapshot: dataSnapshot.getChildren()){
                                dbRef.child(FirebaseClass.USERS).child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        HashMap<String, String> user = new HashMap<>();
                                        for (DataSnapshot snapshot1: dataSnapshot.getChildren()){
                                            user.put(snapshot1.getKey(), snapshot1.getValue().toString());
                                        }
                                        UserClass userClass = new UserClass(user);
                                        String completeName = userClass.getName() + " " + userClass.getLastName();
                                        userClass.setKey(snapshot.getKey());
                                        userClass.setDebt(Boolean.parseBoolean(user.get("debt")));
                                        if(completeName.toLowerCase().contains(s.toLowerCase())){
                                            userClass.setDeptNum(snapshot.getValue().toString());
                                            userList.add(userClass);
                                        }
                                        adapterUsers.notifyDataSetChanged();

                                        if(adapterUsers.getItemCount()==0){
                                            lblNoFoundUsers.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            lblNoFoundUsers.setVisibility(View.INVISIBLE);
                                        }

                                        adapterUsers.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int rcvPosition = rcVwUsers.getChildLayoutPosition(v);
                                                String name = userList.get(rcvPosition).getName();
                                                String lastName = userList.get(rcvPosition).getLastName();
                                                String uid = userList.get(rcvPosition).getKey();
                                                Bundle data = new Bundle();
                                                data.putString("name", name);
                                                data.putString("lastName", lastName);
                                                data.putString("uid", uid);
                                                Intent intent = new Intent(getActivity(), EditUserActivity.class);
                                                intent.putExtras(data);
                                                startActivity(intent);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        }

                    }else {

                    if(adapterUsers.getItemCount()==0){
                        lblNoFoundUsers.setVisibility(View.VISIBLE);
                    }
                    else {
                        lblNoFoundUsers.setVisibility(View.INVISIBLE);
                    }
                }




                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}