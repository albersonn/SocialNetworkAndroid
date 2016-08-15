package br.com.alberson.socialnetwork;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Id {

  @SerializedName("kind")
  @Expose
  public String kind;
  @SerializedName("videoId")
  @Expose
  public String videoId;

}
