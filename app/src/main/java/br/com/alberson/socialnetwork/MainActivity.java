package br.com.alberson.socialnetwork;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

  private static final String BASE_URL = "https://evo-social.herokuapp.com/api/";
  private RecyclerView mFriends;
  private ProgressBar mProgress;
  private UserResultsAdapter mAdapter;
  private TextView mTBusca;
  private SearchView mSearchView;
  private Switch mSYoutube;
  private boolean isYoutube = false;

  /**
   * Global instance of the HTTP transport.
   */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /**
   * Global instance of the JSON factory.
   */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  private YouTube youtube;
  private RelativeLayout mRelativeLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mRelativeLayout = (RelativeLayout) findViewById(R.id.m_viewgroup);

    youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
      @Override
      public void initialize(HttpRequest request) throws IOException {

      }
    }).setApplicationName("Test").build();

    mFriends = (RecyclerView) findViewById(R.id.friends);
    mProgress = (ProgressBar) findViewById(R.id.progress);
    mTBusca = (TextView) findViewById(R.id.m_buscar);
    mSYoutube = (Switch) findViewById(R.id.m_switchyoutube);

    mSYoutube.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isYoutube = isChecked;
      }
    });

    mTBusca.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
      }
    });

    mFriends.setHasFixedSize(true);

    LinearLayoutManager llm = new LinearLayoutManager(this);
    llm.setOrientation(LinearLayoutManager.VERTICAL);

    mFriends.setLayoutManager(llm);


    handleIntent(getIntent());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options_menu, menu);

    SearchManager searchManager =
      (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    mSearchView =
      (SearchView) menu.findItem(R.id.search).getActionView();
    mSearchView.setSearchableInfo(
      searchManager.getSearchableInfo(getComponentName()));

    return true;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    handleIntent(intent);
  }

  private void handleIntent(Intent intent) {
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      if (!TextUtils.isEmpty(query)) {
        if (!isYoutube) {
          new BuscarUsuariosAsync(query).execute();
        } else {
          new BuscarVideoYoutubeAsync(query).execute();
        }
      }
    }
  }

  private class BuscarVideoYoutubeAsync extends AsyncTask<Void, Void, List<SearchResult>> {

    private final String query;

    private BuscarVideoYoutubeAsync(String query) {
      this.query = query;
    }

    @Override
    protected List<SearchResult> doInBackground(Void... params) {
      try {
        YouTube.Search.List search = youtube.search().list("id,snippet");
        search.setKey("AIzaSyCEW-3YcmzdjK1D8CjL68LaUXxZ1pwej30");
        search.setQ(query);
        search.setType("video");

        search.setFields("items(id/kind,id/videoId)");
        search.setMaxResults(10l);
        SearchListResponse response = search.execute();
        return response.getItems();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(List<SearchResult> response) {
      List<String> items = new ArrayList<>();
      if (response != null) {
        for (SearchResult result : response) {
          items.add(result.getId().getVideoId());
        }
      }
      YoutubeViewerAdapter adapter = new YoutubeViewerAdapter(items, MainActivity.this, mRelativeLayout);
      mTBusca.setVisibility(View.GONE);
      mSYoutube.setVisibility(View.GONE);

      mFriends.setVisibility(View.VISIBLE);
      mFriends.setAdapter(adapter);
      mProgress.setVisibility(View.GONE);
    }
  }

  private class BuscarUsuariosAsync extends AsyncTask<Void, Void, List<User>> {

    private final String query;

    public BuscarUsuariosAsync(String query) {
      this.query = query;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mFriends.setVisibility(View.GONE);
      mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<User> doInBackground(Void... params) {
      Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
      SocialMediaService service = retrofit.create(SocialMediaService.class);
      Call<List<User>> call = service.searchUsers(query);
      try {
        Response<List<User>> response = call.execute();
        if (response.isSuccessful())
          return response.body();
        return null;
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(List<User> users) {
      mTBusca.setVisibility(View.GONE);
      mAdapter = new UserResultsAdapter(users, mFriends);
      mAdapter.query = query;
      mFriends.setVisibility(View.VISIBLE);
      mFriends.setAdapter(mAdapter);
      mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
          if (mAdapter.results.size() > 0) {
            String lastId = mAdapter.results.get(mAdapter.results.size() - 1).get_id();
            mAdapter.results.add(null);
            mAdapter.notifyItemInserted(mAdapter.results.size() - 1);
            new BuscarMaisUsuariosAsync(query, lastId).execute();
          }
        }
      });
      mProgress.setVisibility(View.GONE);
    }
  }

  private class BuscarMaisUsuariosAsync extends AsyncTask<Void, Void, List<User>> {

    private final String query;
    private final String ultimoId;

    public BuscarMaisUsuariosAsync(String query, String ultimoId) {
      this.query = query;
      this.ultimoId = ultimoId;
    }

    @Override
    protected List<User> doInBackground(Void... params) {
      Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
      SocialMediaService service = retrofit.create(SocialMediaService.class);
      HashMap<String, String> options = new HashMap<String, String>() {
        {
          put("q", query);
          put("l", ultimoId);
        }
      };
      Call<List<User>> call = service.searchMoreUsers(options);
      try {
        Response<List<User>> response = call.execute();
        if (response.isSuccessful())
          return response.body();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(List<User> users) {
      mAdapter.results.remove(mAdapter.results.size() - 1);

      mAdapter.setLoaded();

      if (users != null)
        mAdapter.results.addAll(users);
      if (users == null || users.size() == 0)
        mAdapter.setEnd();

      mAdapter.notifyDataSetChanged();
    }
  }
}
