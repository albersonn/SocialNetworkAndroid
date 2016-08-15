package br.com.alberson.socialnetwork;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thumbnails {

  @SerializedName("default")
  @Expose
  public Default _default;
  @SerializedName("medium")
  @Expose
  public Medium medium;
  @SerializedName("high")
  @Expose
  public High high;

}
