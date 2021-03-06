package com.example.messangerapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messangerapplication.Models.Mess;
import com.example.messangerapplication.Models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserPageActivity extends AppCompatActivity {

    ScrollView scrollView;
    String name;
    Button startDialog;
    Button messButton;
    ImageButton back;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        scrollView = findViewById(R.id.scrollView);
        LinearLayout layout = findViewById(R.id.linearLayout);

        View view = getLayoutInflater().inflate(R.layout.activity_user_page_up,null);
        layout.addView(view);

        Bundle arguments = getIntent().getExtras();
        String UID = arguments.get("UID").toString();

        startDialog = findViewById(R.id.button2);
        messButton = findViewById(R.id.mess_b);
        back = findViewById(R.id.back);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("User").child(UID).child("name").addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);
                TextView textView = findViewById(R.id.nickname);
                textView.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        FirebaseDatabase.getInstance().getReference().child("UserMessageList").child(FirebaseAuth.
                getInstance().getCurrentUser().getUid()).child(UID).addListenerForSingleValueEvent(
                        new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    startDialog.setVisibility(View.GONE);
                    messButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        startDialog.setOnClickListener(view1 -> {
            Mess message = new Mess();
            message.setMes("Теперь вы можете переписываться");
            message.setType("mess");
            message.setUid("0");
            message.setUs("Admin");

            DatabaseReference mR = FirebaseDatabase.getInstance().getReference().
                    child("UserMessageList").child(FirebaseAuth.getInstance().getCurrentUser().
                    getUid()).child(UID).push();

            String uid = mR.getKey();

            FirebaseDatabase.getInstance().getReference().child("UserMessageList").
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(UID).
                    setValue(uid);

            FirebaseDatabase.getInstance().getReference().child("UserMessageList").child(UID).
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(uid);

            mR = FirebaseDatabase.getInstance().getReference().child("UserMessages").child(uid).
                    push();

            message.setMesuid(uid);

            mR.setValue(message);

            Toast.makeText(getBaseContext(),"Диалог был создан", Toast.LENGTH_SHORT).show();

            startDialog.setVisibility(View.GONE);
            messButton.setVisibility(View.VISIBLE);
        });

        messButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getBaseContext(), UserMessagesActivity.class);
            FirebaseDatabase.getInstance().getReference().child("UserMessageList").child(
                    FirebaseAuth.getInstance().getCurrentUser().getUid()).child(UID).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String uid = dataSnapshot.getValue(String.class);
                            intent.putExtra("ID", uid);
                            intent.putExtra("UID",UID);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });



        });

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Post").
                child(UID);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String tm = dataSnapshot.child("time").getValue(String.class);
                String uid = dataSnapshot.child("uid").getValue(String.class);
                String txt = dataSnapshot.child("text").getValue(String.class);
                String nm = dataSnapshot.child("name").getValue(String.class);
                String id = dataSnapshot.getKey();

                Post post = new Post();
                post.setName(nm);
                post.setUid(uid);
                post.setId(id);
                post.setText(txt);
                post.setTime(tm);

                View view = getLayoutInflater().inflate(R.layout.item_post,null);

                TextView name = view.findViewById(R.id.name);
                TextView time = view.findViewById(R.id.time);
                TextView textPost = view.findViewById(R.id.post);
                ImageView like = view.findViewById(R.id.like);
                ImageView dislike = view.findViewById(R.id.dislike);

                TextView likeKol = view.findViewById(R.id.like_col);
                TextView dislikeKol = view.findViewById(R.id.dislike_col);

                name.setText(post.getName());
                time.setText(post.getTime());
                textPost.setText(post.getText());


                myRef.child(id).child("like").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
                                             @Nullable String s) {
                        likeKol.setText(String.valueOf(Integer.parseInt(likeKol.getText().
                                toString()) + 1));
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot,
                                               @Nullable String s) { }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        likeKol.setText(String.valueOf(Integer.parseInt(likeKol.getText().
                                toString()) - 1));
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot,
                                             @Nullable String s) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                myRef.child(id).child("dislike").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
                                             @Nullable String s) {
                        dislikeKol.setText(String.valueOf(Integer.parseInt(dislikeKol.getText().
                                toString()) + 1));
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot,
                                               @Nullable String s) { }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        dislikeKol.setText(String.valueOf(Integer.parseInt(dislikeKol.getText().
                                toString()) - 1));
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot,
                                             @Nullable String s) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });


                if(dataSnapshot.child("like").child(FirebaseAuth.getInstance().getCurrentUser().
                        getUid().toString()).exists()) {
                    like.setImageResource(R.drawable.thumb_up_clicked);
                }

                if(dataSnapshot.child("dislike").child(FirebaseAuth.getInstance().getCurrentUser().
                        getUid().toString()).exists()) {
                    dislike.setImageResource(R.drawable.thumb_down_clicked);
                }

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myRef.child(id).child("like").child(FirebaseAuth.getInstance().
                                getCurrentUser().getUid()).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            like.setImageResource(R.drawable.thumb_up);
                                            myRef.child(id).child("like").child(FirebaseAuth.getInstance().
                                                    getCurrentUser().getUid()).removeValue();
                                        } else {
                                            myRef.child(id).child("like").child(FirebaseAuth.getInstance().
                                                    getCurrentUser().getUid()).setValue(1);
                                            like.setImageResource(R.drawable.thumb_up_clicked);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                    }
                });

                dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myRef.child(id).child("dislike").child(FirebaseAuth.getInstance().
                                getCurrentUser().getUid()).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            dislike.setImageResource(R.drawable.thumb_down);
                                            myRef.child(id).child("dislike").child(FirebaseAuth.getInstance().
                                                    getCurrentUser().getUid()).removeValue();
                                        } else {
                                            myRef.child(id).child("dislike").child(FirebaseAuth.getInstance().
                                                    getCurrentUser().getUid()).setValue(1);
                                            dislike.setImageResource(R.drawable.thumb_down_clicked);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                    }
                });

                layout.addView(view);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        back.setOnClickListener(view1 -> {
            startActivity(new Intent(UserPageActivity.this, Messages.class));
            finish();
        });
    }



    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
