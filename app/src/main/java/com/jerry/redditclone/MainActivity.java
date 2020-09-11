package com.jerry.redditclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

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

    private static final  String BASE_URL = "https://www.reddit.com/r/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedApi feedapi = retrofit.create(FeedApi.class);

        Call<Feed> call = feedapi.getFeed();

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {

                Log.d("manik",response.body().toString());

                List<Entrys> entrys = response.body().getEntry();
                Log.d("manik",response.body().getEntry().toString());

                ArrayList<Post> posts = new ArrayList<Post>();
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
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {

            }
        });



    }
}