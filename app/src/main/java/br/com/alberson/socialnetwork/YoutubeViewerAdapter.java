package br.com.alberson.socialnetwork;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

public class YoutubeViewerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements YouTubePlayer.OnInitializedListener {

  private static final String YOUTUBE_KEY = "AIzaSyC3JPhKKVoLd1Z12xgFtLAgZW680Kbpg2s";

  @Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
    YoutubeViewerAdapter.this.player = youTubePlayer;
    player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
    player.setPlayerStateChangeListener(new VideoListener());
  }

  @Override
  public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

  }

  private final class ViewHolder extends RecyclerView.ViewHolder {
    YouTubeThumbnailView thumb;
    ImageView image;
    View fPlayer;
    FrameLayout vg;

    public ViewHolder(View itemView) {
      super(itemView);
      thumb = (YouTubeThumbnailView) itemView.findViewById(R.id.y_thumbnail);
      image = (ImageView) itemView.findViewById(R.id.y_img);
      fPlayer = itemView.findViewById(R.id.y_fplayer);
      vg = (FrameLayout) itemView.findViewById(R.id.y_vg);
    }
  }

  private final List<String> videos;
  private final View playerView;
  private YouTubePlayer player;
  private ViewGroup currentView;
  private String currentVideo;
  private ViewGroup baseViewGroup;

  public YoutubeViewerAdapter(List<String> videos, Activity act, ViewGroup vg) {
    this.videos = videos;
    playerView = new FrameLayout(act);
    playerView.setId(R.id.player_view);
    playerView.setVisibility(View.INVISIBLE);
    vg.addView(playerView);
    YouTubePlayerFragment playerFragment = YouTubePlayerFragment.newInstance();
    playerFragment.initialize(YOUTUBE_KEY, this);
    act.getFragmentManager().beginTransaction().add(R.id.player_view, playerFragment).commit();
    playerView.setTag("YT_PLAYER");
    baseViewGroup = vg;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_layout, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    final ViewHolder mHolder = (ViewHolder) holder;
    mHolder.image.setVisibility(View.VISIBLE);
    mHolder.vg.setId(View.generateViewId());

    mHolder.thumb.initialize(YOUTUBE_KEY, new YouTubeThumbnailView.OnInitializedListener() {
      @Override
      public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
        youTubeThumbnailLoader.setVideo(videos.get(mHolder.getAdapterPosition()));
        youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
          @Override
          public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
            mHolder.image.setVisibility(View.GONE);
            youTubeThumbnailLoader.release();
            mHolder.thumb.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                if (currentView != null) {
                  if (currentView.findViewWithTag("YT_PLAYER") != null)
                    currentView.removeView(playerView);
                } else {
                  baseViewGroup.removeView(playerView);
                }
                currentView = mHolder.vg;
                mHolder.vg.addView(playerView, new ViewGroup.LayoutParams(mHolder.vg.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT));
                playerView.setVisibility(View.VISIBLE);

//                YouTubePlayerFragment ytPlayer = (YouTubePlayerFragment) act.get().getFragmentManager().findFragmentByTag("YT_PLAYER");
//                if(ytPlayer == null) {
//                  ytPlayer = YouTubePlayerFragment.newInstance();
//                  ytPlayer.initialize(YOUTUBE_KEY, YoutubeViewerAdapter.this);
//                } else {
//                  act.get().getFragmentManager().beginTransaction().remove(ytPlayer).commit();
//                }
//                FragmentTransaction t = act.get().getFragmentManager().beginTransaction();
//                t.replace(mHolder.vg.getId(), ytPlayer, "YT_PLAYER");
//                t.commit();
////                t.replace(mHolder.vg.getId(), playerFragment).commit();
//                currentThumb = (YouTubeThumbnailView) v;
//                currentThumb.setVisibility(View.INVISIBLE);
////                  playerView.setX(v.getX());
////                  playerView.setY(v.getY());
////                  playerView.setVisibility(View.VISIBLE);
                currentVideo = videos.get(mHolder.getAdapterPosition());
                player.cueVideo(currentVideo);
//                player.play();
//                }
              }
            });
          }

          @Override
          public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

          }
        });
      }

      @Override
      public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

      }
    });
  }

  @Override
  public int getItemCount() {
    return videos == null ? 0 : videos.size();
  }

  private final class VideoListener implements YouTubePlayer.PlayerStateChangeListener {

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {
      player.play();
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
  }

}
