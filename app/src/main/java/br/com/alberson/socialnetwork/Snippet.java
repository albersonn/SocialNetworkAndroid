package br.com.alberson.socialnetwork;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Snippet {

  @SerializedName("publishedAt")
  @Expose
  public String publishedAt;
  @SerializedName("channelId")
  @Expose
  public String channelId;
  @SerializedName("title")
  @Expose
  public String title;
  @SerializedName("description")
  @Expose
  public String description;
  @SerializedName("thumbnails")
  @Expose
  public Thumbnails thumbnails;
  @SerializedName("channelTitle")
  @Expose
  public String channelTitle;
  @SerializedName("liveBroadcastContent")
  @Expose
  public String liveBroadcastContent;

}
