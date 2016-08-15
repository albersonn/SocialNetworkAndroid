package br.com.alberson.socialnetwork;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by alber on 13/08/2016.
 */
public interface YoutubeService {
  @GET("youtube/v3/search?part=snippet&key=AIzaSyC3JPhKKVoLd1Z12xgFtLAgZW680Kbpg2s")
  Call<List<YoutubeItem>> getVideos(@Query("q") String query);
}
