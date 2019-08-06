package com.example.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class listdeals extends AppCompatActivity {


    private ArrayList<traveldeal> melist;
    RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listdeals);
        rv = findViewById(R.id.rv_deals);


        getTaveldeals();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        insertMenu.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                Intent intent = new Intent(this, insertActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getApplicationContext(), " User Logged Out", Toast.LENGTH_LONG).show();

                            }
                        });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getTaveldeals() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("TRAVEL_DEALS").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            String title, amount, description, image;

                            melist = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                title = document.getString("title");
                                amount = document.getString("price").toString();
                                description = document.getString("description");
                                image = document.getString("imageUrl");


                                melist.add(new traveldeal(title, amount, description, image));

                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

                            RecyclerView.LayoutManager rvlayoutManager = linearLayoutManager;

                            rv.setLayoutManager(rvlayoutManager);

                            list_deal_adapter adapter = new list_deal_adapter(getApplicationContext(), melist);

                            rv.setAdapter(adapter);

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });


    }

}
