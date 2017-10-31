package com.example.firebaseprac2;




import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class StorageActivity extends AppCompatActivity implements RecyclerViewAdapter.TransferId {
    private StorageReference mStorageRef;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    TextView tvUserId, tvToken;
    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        initView();
        setRecyclerView();
        setDatabase();
        getDataFromDB();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void transferUserId(String current_id, String token) {
        tvUserId.setText(current_id);
        tvToken.setText(token);
    }

    private void setDatabase(){
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("message");
    }

    private void getDataFromDB() {
        userRef.addValueEventListener(new ValueEventListener() {
            ArrayList<User> list = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //User user = snapshot.getValue(User.class);
                    String userID = snapshot.getKey();
                    Log.d("======", userID);
                    String token = snapshot.getValue().toString();
                    list.add(new User(userID, token));
                }
                adapter.refreshList(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvUserId = (TextView) findViewById(R.id.tvUserId);
        tvToken = (TextView) findViewById(R.id.tvToken);
    }

    public void setRecyclerView(){
        adapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    //파일 탐색기
    public void chooseFile(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // 갤러리 image/* , 동영상 video/*
        startActivityForResult(intent.createChooser(intent, "Select App"), 999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Uri uri = data.getData();

            upload(uri);
        }
    }

    public void upload(Uri file){
        // 실제 파일이 있는 경로
        //Uri file = Uri.fromFile(new File(path));
        // 파이어베이스의 스토리지 node
        String filename = file.getPath().substring(file.getPath().lastIndexOf("/"));

        StorageReference riversRef = mStorageRef.child("/files/" + filename);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d("Storage", "downloadUrl="+downloadUrl.getPath());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(StorageActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }


}
