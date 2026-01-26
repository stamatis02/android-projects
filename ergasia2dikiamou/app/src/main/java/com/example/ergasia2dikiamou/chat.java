package com.example.ergasia2dikiamou;

 import android.annotation.SuppressLint;
 import android.content.Intent;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.TextView;
 import android.widget.Toast;
 import android.os.Bundle;

 import androidx.activity.EdgeToEdge;
 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
 import androidx.recyclerview.widget.LinearLayoutManager;
 import androidx.recyclerview.widget.RecyclerView;

 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.Query;
 import com.google.firebase.database.ValueEventListener;
 import com.google.firebase.firestore.auth.User;

 import java.util.ArrayList;
 import java.util.List;

public class chat extends AppCompatActivity {
private RecyclerView recyclerView;
private UserAdapter userAdapter;
private List<chatuser> mUsers;
private List<ChatList> userslist;
 private FirebaseUser user, user1;

    private TextView welcomeText, resultText;
    private EditText searchField;
    private Button searchButton, logoutbutt;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        logoutbutt= findViewById(R.id.logout);
        welcomeText = findViewById(R.id.welcometext1);
        searchField = findViewById(R.id.searchField1);
        searchButton = findViewById(R.id.searchButton1);
        resultText = findViewById(R.id.resultText1);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        user = FirebaseAuth.getInstance().getCurrentUser();
        userslist= new ArrayList<>(); // initialize list
        //we are going to find to who the person talks
        mDatabase = FirebaseDatabase.getInstance().getReference("Chatlist").child(user.getUid());
        //logout butt
        logoutbutt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(chat.this ,"Εγινε αποσυνδεση" , Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(chat.this, signup.class);
        startActivity(intent);
        finish();
    }
});
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userslist.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatlist = snapshot.getValue(ChatList.class);
                    userslist.add(chatlist);
                }

                // weare going to find the names
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference("chatuser");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //welcome name
        if (user != null) {
            String uid = user.getUid();
            mDatabase.child(uid).child("username").get().addOnCompleteListener(task ->{
                if (task.isSuccessful() && task.getResult().exists()) {
                    //if we founf the name
                    String nameFromDb = task.getResult().getValue(String.class);
                    welcomeText.setText("Γεια σου " + nameFromDb + "!");
                } else {
                    welcomeText.setText("Γεια σου Χρήστη!");
                }
            } );

        }

       //search button
        searchButton.setOnClickListener(v -> {
            String searchText = searchField.getText().toString().trim();
            if (searchText.isEmpty()) {
                Toast.makeText(chat.this, "Γράψε ένα όνομα!", Toast.LENGTH_SHORT).show();
                return;
            }

            searchForUser(searchText);
        });
    }

    private void searchForUser(String usernameToFind) {
        resultText.setText("Ψάχνω...");

       //we are creating a query about if there are any peopple tha have the smaae name as the name the user wrote to send a message
        Query query = mDatabase.orderByChild("username").equalTo(usernameToFind);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //if we found the name
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String foundName = userSnapshot.child("username").getValue(String.class);
                        //we ouput the welcome text
                        resultText.setText("Βρέθηκε χρήστης:\nΌνομα: " + foundName );
                        String receiverUserID = userSnapshot.getKey();
                        resultText.setOnClickListener(v -> {
                            Intent intent = new Intent(chat.this, messageactivity.class);
                            intent.putExtra("userid", receiverUserID); // we the user clicks the name we take the id and we are going in the chat activity
                            startActivity(intent);
                        });}
                } else {
                    resultText.setText("Δεν βρέθηκε χρήστης με αυτό το όνομα.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                resultText.setText("Σφάλμα: " + error.getMessage());
            }
        });

    }
    private void chatList() {
        mUsers = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("chatuser");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    chatuser user = snapshot.getValue(chatuser.class);

                    // we check if the person is on the list usersList;
                    for (ChatList chatlist : userslist) {
                        if (user != null && user.getUid() != null && user.getUid().equals(chatlist.getId())) {
                            mUsers.add(user);
                        }
                    }
                }
                // we put the adapter together
                userAdapter = new UserAdapter(chat.this, mUsers);
                recyclerView.setAdapter(userAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}