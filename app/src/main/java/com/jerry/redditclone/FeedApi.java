package com.jerry.redditclone;

import com.jerry.redditclone.model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FeedApi {

    String BASE_URL = "https://www.reddit.com/r/";

    @GET("funny/.rss")
    Call<Feed> getFeed();
}
