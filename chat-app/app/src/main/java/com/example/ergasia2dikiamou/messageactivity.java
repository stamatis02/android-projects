package com.example.ergasia2dikiamou;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class messageactivity extends AppCompatActivity {
    FirebaseUser fuser;
    DatabaseReference reference;
    MessageAdapter messageAdapter;
    UserAdapter userAdapter ;
    List<ChatMessage> mChat;
    List<chatuser> chatuserlist;
    RecyclerView recyclerView;
    ImageButton btn_send;
    Button back;
    EditText text_send;
    TextView username_display; // TextView to display the username
    String userid; //id of the other user

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messageactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        back=findViewById(R.id.backbutt);
        recyclerView = findViewById(R.id.recycler_view);
        username_display = findViewById(R.id.username_display); // Find the TextView
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true); // to start from down
        recyclerView.setLayoutManager(linearLayoutManager);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(messageactivity.this,chat.class);
                startActivity(intent);
                finish();
            }
        });
        btn_send = findViewById(R.id.btn_send1);
        text_send = findViewById(R.id.text_send1);
        // we take the id from ChatActivity
        userid = getIntent().getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        // Fetch and display the username
        reference = FirebaseDatabase.getInstance().getReference("chatuser").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatuser user = snapshot.getValue(chatuser.class);
                if (user != null) {
                    username_display.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // send button
        btn_send.setOnClickListener(v -> {
            String msg = text_send.getText().toString();
            if (!msg.equals("")) {
                sendMessage(fuser.getUid(), userid, msg);
            } else {
                Toast.makeText(messageactivity.this, "Δεν μπορείς να στείλεις κενό μήνυμα", Toast.LENGTH_SHORT).show();
            }
            text_send.setText(""); // we clean it
        });
        readMessages(fuser.getUid(), userid);
    }

    //send mesaage function
    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();//we create a hashmap to put the id of sender recever the message and the time put them all together in the database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("timestamp", System.currentTimeMillis()); //to know when!
        // we save the mssage to both users
        reference.child("Chats").child(sender).child(receiver).push().setValue(hashMap);
        reference.child("Chats").child(receiver).child(sender).push().setValue(hashMap);
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());
    }
    //method that reads the message and show them realtime
    private void readMessages(String myid, String userid) {
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(myid).child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessage chat = snapshot.getValue(ChatMessage.class);
                    if (chat != null) {
                        mChat.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(messageactivity.this, mChat);
                 recyclerView.setAdapter(messageAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }}
