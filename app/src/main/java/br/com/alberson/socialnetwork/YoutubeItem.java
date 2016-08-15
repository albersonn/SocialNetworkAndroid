

package br.com.alberson.socialnetwork;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YoutubeItem {

  @SerializedName("kind")
  @Expose
  public String kind;
  @SerializedName("etag")
  @Expose
  public String etag;
  @SerializedName("id")
  @Expose
  public Id id;
  @SerializedName("snippet")
  @Expose
  public Snippet snippet;

}