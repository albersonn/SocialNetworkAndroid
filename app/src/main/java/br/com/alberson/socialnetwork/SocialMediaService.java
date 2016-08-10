package br.com.alberson.socialnetwork;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by alber on 10/08/2016.
 */
public interface SocialMediaService {
    @GET("users/search")
    Call<List<User>> searchUsers(@Query("q")String query);

    @GET("users/search")
    Call<List<User>> searchMoreUsers(@QueryMap Map<String, String> options);

    @GET("users/{id}")
    Call<User> getUser(@Path("id")String userId);

    @GET("users/{id}/timeline/{skip}/{limit}")
    Call<List<Post>> getTimeline(@Path("id")String userId, @Path("skip") int skip, @Path("limit") int limit);
}
