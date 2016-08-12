package br.com.alberson.socialnetwork;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

/**
 * Created by alber on 12/08/2016.
 */
public class YoutubeViewerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  class ViewHolder extends RecyclerView.ViewHolder {
    YouTubeThumbnailView thumb;
    public ViewHolder(View itemView) {
      super(itemView);
      thumb = (YouTubeThumbnailView) itemView.findViewById(R.id.y_thumbnail);
    }
  }

  private final List<String> videos;

  public YoutubeViewerAdapter(List<String> videos) {
    this.videos = videos;
  }

  public void addVideo(String video) {
    this.videos.add(video);
    notifyItemChanged(this.videos.size() - 1);
  }

  public void removeVideo(int location) {
    this.videos.remove(location);
    notifyItemRemoved(location);
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return 0;
  }
}
