package com.egkhan.retrofittutorial.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.egkhan.retrofittutorial.BuildConfig;
import com.egkhan.retrofittutorial.R;
import com.egkhan.retrofittutorial.api.model.GitHubRepo;
import com.egkhan.retrofittutorial.api.model.User;
import com.egkhan.retrofittutorial.api.service.GitHubClient;
import com.egkhan.retrofittutorial.api.service.UserClient;
import com.egkhan.retrofittutorial.ui.adapter.GitHubRepoAdapter;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getAndShowUserRepos();
        createNewUser();
    }

    private void createNewUser() {
        setContentView(R.layout.user_register);

        final EditText name = (EditText) findViewById(R.id.input_name);
        final EditText email = (EditText) findViewById(R.id.input_email);
        final EditText age = (EditText) findViewById(R.id.input_age);
        final EditText topics = (EditText) findViewById(R.id.input_topics);

        Button createAccountButton = (Button) findViewById(R.id.btn_signup);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = buildRetrofit("http://10.0.2.2:3000/api");
                User user = new User(name.getText().toString(),
                        email.getText().toString(),
                        Integer.parseInt(age.getText().toString()),
                        topics.getText().toString().split(","));
                //get client & call object for the request
                UserClient client = retrofit.create(UserClient.class);
                Call<User> call =client.createAccount(user);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(MainActivity.this, "User created successfully with the ID : "+response.body().getId(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "something work wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getAndShowUserRepos() {
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.pagination_list);

        Retrofit retrofit = buildRetrofit("https://api.github.com/");

        GitHubClient client = retrofit.create(GitHubClient.class);
        retrofit2.Call<List<GitHubRepo>> call = client.reposForUser("fs-opensource");

        call.enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                List<GitHubRepo> repos = response.body();
                listView.setAdapter(new GitHubRepoAdapter(MainActivity.this, repos));
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Retrofit buildRetrofit(String baseUrl) {
        //create OkHttp client
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        //add logging interceptor to client
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//body'de çok büyük şeyler gelebilir,log için kötü olabilir

        if (BuildConfig.DEBUG) { //sadece debug modunda yapar
            okHttpClientBuilder.addInterceptor(loggingInterceptor);
        }


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build());

        return builder.build();
    }
}
