package com.jerry.redditclone.comments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jerry.redditclone.ExtractXML;
import com.jerry.redditclone.FeedApi;
import com.jerry.redditclone.R;
import com.jerry.redditclone.URLS;
import com.jerry.redditclone.WebViewActivity;
import com.jerry.redditclone.model.Feed;
import com.jerry.redditclone.model.entry.Entrys;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.simpleframework.xml.util.Entry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = "CommentsActivity";
    URLS urls = new URLS();

    private static String postURL;
    private static String postThumbnailURL;
    private static String postTitle;
    private static String postAuthor;
    private static String postUpdated;

    private int defaultImage;

    private String currentFeed;
    private ListView mListView;

    private ArrayList<Comment> mComments;
    private ProgressBar mProgressBar;
    private TextView progressText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mProgressBar = (ProgressBar) findViewById(R.id.commentsLoadingProgressBar);
        progressText = (TextView) findViewById(R.id.progressText);
        Log.d(TAG, "onCreate: Started.");

        mProgressBar.setVisibility(View.VISIBLE);

        setupImageLoader();

        initPost();

        init();

    }

    private void init(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urls.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedApi feedAPI = retrofit.create(FeedApi.class);

        Call<Feed> call = feedAPI.getFeed(currentFeed);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                //Log.d(TAG, "onResponse: feed: " + response.body().toString());
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                mComments = new ArrayList<Comment>();
                List<Entrys> entrys = response.body().getEntry();
                for ( int i = 0; i < entrys.size(); i++){
                    ExtractXML extract = new ExtractXML(entrys.get(i).getContent(), "<div class=\"md\"><p>","</p>");
                    List<String> commentDetails = extract.start();


                    try{
                        mComments.add(new Comment(
                                commentDetails.get(0),
                                entrys.get(i).getAuthor().getName(),
                                entrys.get(i).getUpdated(),
                                entrys.get(i).getId()

                        ));
                    }catch (IndexOutOfBoundsException e){
                        mComments.add(new Comment(
                                "Error reading comment",
                                "None",
                                "None",
                                "None"
                        ));
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException: " + e.getMessage() );
                    }
                    catch (NullPointerException e){
                        mComments.add(new Comment(
                                commentDetails.get(0),
                                "None",
                                entrys.get(i).getUpdated(),
                                entrys.get(i).getId()

                        ));
                        Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage() );
                    }
                }
                mListView = (ListView) findViewById(R.id.commentsListView);
                CommentsListAdapter adapter = new CommentsListAdapter(CommentsActivity.this, R.layout.comments_layout, mComments);
                mListView.setAdapter(adapter);

                mProgressBar.setVisibility(View.GONE);
                progressText.setText("");
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage() );
                Toast.makeText(CommentsActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPost(){
        Intent incomingIntent = getIntent();
        postURL = incomingIntent.getStringExtra("@string/post_url");
        postThumbnailURL = incomingIntent.getStringExtra("@string/post_thumbnail");
        postTitle = incomingIntent.getStringExtra("@string/post_title");
        postAuthor = incomingIntent.getStringExtra("@string/post_author");
        postUpdated = incomingIntent.getStringExtra("@string/post_updated");

        TextView title = (TextView) findViewById(R.id.postTitle);
        TextView author = (TextView) findViewById(R.id.postAuthor);
        TextView updated = (TextView) findViewById(R.id.postUpdated);
        ImageView thumbnail = (ImageView) findViewById(R.id.postThumbnail);
        Button btnReply = (Button) findViewById(R.id.btnPostReply);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.postLoadingProgressBar);

        title.setText(postTitle);
        author.setText(postAuthor);
        updated.setText(postUpdated);
        displayImage(postThumbnailURL, thumbnail, progressBar);

        //NOTE: NSFW posts will cause an error. We can catch it with ArrayIndexOutOfBoundsException
        try{
            String[] splitURL = postURL.split(urls.BASE_URL);
            currentFeed = splitURL[1];
            Log.d(TAG, "initPost: current feed: " + currentFeed);
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundsException: " + e.getMessage() );
        }

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommentsActivity.this, WebViewActivity.class);
                intent.putExtra("url", postURL);
                startActivity(intent);
            }
        });

    }

    private void displayImage(String imageURL, ImageView imageView, final ProgressBar progressBar){

        //create the imageloader object
        ImageLoader imageLoader = ImageLoader.getInstance();

        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //download and display image from url
        imageLoader.displayImage(imageURL, imageView, options , new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                CommentsActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP

        defaultImage = CommentsActivity.this.getResources().getIdentifier("@drawable/image_failed",null,CommentsActivity.this.getPackageName());
    }
}