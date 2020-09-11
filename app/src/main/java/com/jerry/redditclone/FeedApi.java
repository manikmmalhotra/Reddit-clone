package com.jerry.redditclone;

import com.jerry.redditclone.model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FeedApi {

    String BASE_URL = "https://www.reddit.com/r/";

    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);


/*
    @GET("earthporn/.rss")
    Call<Feed> getFeed();
*/
}
