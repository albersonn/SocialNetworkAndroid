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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mFriends = (RecyclerView) findViewById(R.id.friends);
    mProgress = (ProgressBar) findViewById(R.id.progress);
    mTBusca = (TextView) findViewById(R.id.m_buscar);

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
        new BuscarUsuariosAsync(query).execute();
      }
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
