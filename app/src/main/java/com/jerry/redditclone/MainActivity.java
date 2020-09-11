package com.jerry.redditclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.jerry.redditclone.comments.CommentsActivity;
import com.jerry.redditclone.model.Feed;
import com.jerry.redditclone.model.entry.Entrys;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    URLS urls = new URLS();

    private Button btnRefreshFeed;
    private EditText mFeedName;
    private String currentFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRefreshFeed = (Button) findViewById(R.id.btnRefreshFeed);

        mFeedName = (EditText) findViewById(R.id.etFeedName);

        init();

        btnRefreshFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedName = mFeedName.getText().toString();
                if(!feedName.equals("")){
                    currentFeed = feedName;
                    init();
                }
                else{
                    init();
                }
            }
        });



    }

    private void init(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urls.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedApi feedapi = retrofit.create(FeedApi.class);

        Call<Feed> call = feedapi.getFeed(currentFeed);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {

                Log.d("manik",response.body().toString());

                List<Entrys> entrys = response.body().getEntry();
                Log.d("manik",response.body().getEntry().toString());

               final ArrayList<Post> posts = new ArrayList<Post>();
                for( int i = 0;i < entrys.size();i++){
                    ExtractXML extractXML1 = new ExtractXML(entrys.get(i).getContent(), "<a href=");
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML(entrys.get(i).getContent(), "<img src=");

                    try {
                        postContent.add(extractXML2.start().get(0));
                    }catch (NullPointerException e){
                        postContent.add(null);
                    }catch (IndexOutOfBoundsException e){
                        postContent.add(null);
                    }
                    int lastposition = postContent.size() - 1;
                    posts.add(new Post(
                            entrys.get(i).getTitle(),
                            entrys.get(i).getAuthor().getName(),
                            entrys.get(i).getUpdated(),
                            postContent.get(0),
                            postContent.get(lastposition)
                    ));
                }

         /*       for (int j =0;j < posts.size();j++){
                    Log.d("doe","onResponse: \n" + "PostURl: " + posts.get(j).getPostUrl() + "\n" +
                            "thumbnail : " + posts.get(j).getThumbnailURL() + "\n" +
                            "thumbnail : " + posts.get(j).getTitle() + "\n" +
                            "thumbnail : " + posts.get(j).getAuthor() + "\n" +
                            "thumbnail : " + posts.get(j).getDate_updated() + "\n");
                }*/

                ListView listView = (ListView) findViewById(R.id.listView);
                CustomListAdapter customListAdapter = new CustomListAdapter(MainActivity.this, R.layout.card_layout_main, posts);
                listView.setAdapter(customListAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                        intent.putExtra("@string/post_url", posts.get(position).getPostUrl());
                        intent.putExtra("@string/post_thumbnail", posts.get(position).getThumbnailURL());
                        intent.putExtra("@string/post_title", posts.get(position).getTitle());
                        intent.putExtra("@string/post_author", posts.get(position).getAuthor());
                        intent.putExtra("@string/post_updated", posts.get(position).getDate_updated());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {

            }
        });


    }
}