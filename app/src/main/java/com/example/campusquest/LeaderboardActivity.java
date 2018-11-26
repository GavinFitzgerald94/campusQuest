package com.example.campusquest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LeaderboardActivity extends AppCompatActivity {

    private List<User> userList = new ArrayList<>();
    private UserAdapter adapter;
    private RecyclerView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_activity);
        restAPI();

        FloatingActionButton fab = findViewById(R.id.contact_invitation);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LeaderboardActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });


    }

    private void restAPI(){
        OkHttpClient client = new OkHttpClient();

        String url = "https://sleepy-cliffs-99612.herokuapp.com/findall";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("Ask API", "onFailure: Network Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String myResponse = response.body().string();
                    Log.d("res", myResponse);
                    try{
                        final JSONObject json = new JSONObject(myResponse);
                        LeaderboardActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initUsers(json);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(LeaderboardActivity.this);
                                adapter = new UserAdapter(userList);
                                listView = findViewById(R.id.list_view);
                                listView.setAdapter(adapter);
                                listView.setLayoutManager(layoutManager);
                                listView.setHasFixedSize(true);
                            }
                        });
                    }catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("Ask API", "onFailure: Network Error");
                    } }
            }
        });

    }

    private void initUsers(JSONObject json){
        int[] image ={ R.drawable.rank_1,R.drawable.rank_2,R.drawable.rank_3 };
        try {
            JSONArray users = json.getJSONArray("content");
            for(int i =0; i< users.length();i++){
                JSONObject user =  users.getJSONObject(i);
                try {
                    User customer = new User(user.getString("firstName"),i+1,1000, image[i]);
                    Log.d("user", user.getString("firstName"));
                    userList.add(customer);}
                catch(JSONException e){
                    e.printStackTrace();
                    Log.d("JSON", "onPostExecute: Can not get user");
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
            Log.d("JSON", "onPostExecute: Can not get the content from the res");
        }

//        for(int i=0; i<5; i++){
//            User customer1 = new User("WuDi",1,1000, R.drawable.dog_pic);
//            userList.add(customer1);
//            User customer2 = new User("Zhu",2,2000, R.drawable.cat_pic);
//            userList.add(customer2);
//            User customer3 = new User("Chen",3,3000, R.drawable.pig_pic);
//            userList.add(customer3);
//            User customer4 = new User("Yao",4,1000, R.drawable.owl_pic);
//            userList.add(customer4);
//        }
    }
}
