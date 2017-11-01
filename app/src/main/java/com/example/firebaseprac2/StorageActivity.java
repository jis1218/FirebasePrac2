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
import android.widget.Button;
import android.widget.EditText;
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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StorageActivity extends AppCompatActivity implements RecyclerViewAdapter.TransferId {
    private StorageReference mStorageRef;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    TextView tvUserId, tvToken;
    FirebaseDatabase database;
    DatabaseReference userRef;
    EditText editMsg;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        initView();
        setRecyclerView();
        setDatabase();
        getDataFromDB();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        });
    }

    public void send(){
        String token = tvToken.getText().toString();
        String msg = editMsg.getText().toString();

        if(token == null || "".equals(token)){
            Toast.makeText(this, "받는 사람을 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }if(msg==null || "".equals(msg)){
            Toast.makeText(this, "메세지 입력", Toast.LENGTH_SHORT).show();
            return;
        }
        //Retrofit 스레드 + Httpconnection
        //네트워킹 하는 툴
        String json = "{\"to\":\"" + token + "\", \"msg\":\""+msg + "\"}";

        // 레트로핏 선언
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.138:8090/")
                .build();
        // 인터페이스와 결합
        IRetro service = retrofit.create(IRetro.class);
         RequestBody body = RequestBody.create(MediaType.parse("text/plain"), json); //body에 들어갈 타입을 설정해준다.

        Call<ResponseBody> remote = service.sendNotification(body);

        remote.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    ResponseBody resultdata = response.body();

                    try {
                        Toast.makeText(StorageActivity.this, resultdata.toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(StorageActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        Log.d("확인", resultdata.toString()); //okhttp3.ResponseBody$1@c555988라고 뜸
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Retro", t.getMessage());
            }
        });
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
                    User user = snapshot.getValue(User.class);
                    //String userID = snapshot.getKey();
                    //Log.d("======", userID);
                    //String token = snapshot.getValue().toString();
                    list.add(user);
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
        editMsg = (EditText) findViewById(R.id.editMsg);
        btnSend = (Button) findViewById(R.id.btnSend);
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
