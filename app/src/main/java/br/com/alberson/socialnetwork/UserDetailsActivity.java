package br.com.alberson.socialnetwork;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserDetailsActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://evo-social.herokuapp.com/api/";
    private TextView mUsername;
    private TextView mCategory;
    private RecyclerView mTimeline;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        mUsername = (TextView) findViewById(R.id.d_username);
        mCategory = (TextView) findViewById(R.id.d_cateogory);
        mTimeline = (RecyclerView) findViewById(R.id.timeline);
        mImage = (ImageView) findViewById(R.id.d_image);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mTimeline.setLayoutManager(llm);

        Bundle extras = getIntent().getExtras();
        String userId = null;
        if (extras != null) {
            userId = extras.getString("user_id", null);
        }

        if (userId != null) {
            new ObterDadosUsuarioAsync(userId).execute();
        }
    }

    private class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

        public final List<Post> mPosts;

        private TimelineAdapter(List<Post> mPosts) {
            this.mPosts = mPosts;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView content;
            public TextView date;
            public TextView author;

            public ViewHolder(View itemView) {
                super(itemView);
                content = (TextView) itemView.findViewById(R.id.p_content);
                date = (TextView) itemView.findViewById(R.id.p_date);
                author = (TextView) itemView.findViewById(R.id.p_author);
            }
        }

        @Override
        public TimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TimelineAdapter.ViewHolder holder, int position) {
            Post post = mPosts.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            holder.content.setText(post.getContent());
            holder.date.setText(sdf.format(post.getDate()));
            holder.author.setText(post.getAuthor());
            new ObterUsuarioAsync(post.getAuthor(), holder).execute();
        }

        @Override
        public int getItemCount() {
            return mPosts == null ? 0 : mPosts.size();
        }
    }

    private class ObterUsuarioAsync extends AsyncTask<Void, Void, User> {
        private final String userId;
        private final WeakReference<TimelineAdapter.ViewHolder> vh;

        public ObterUsuarioAsync(String userId, TimelineAdapter.ViewHolder vh) {
            this.userId = userId;
            this.vh = new WeakReference<TimelineAdapter.ViewHolder>(vh);
        }

        @Override
        protected User doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SocialMediaService service = retrofit.create(SocialMediaService.class);
            Call<User> call = service.getUser(userId);
            try {
                Response<User> response = call.execute();
                if (response.isSuccessful())
                    return response.body();
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            TimelineAdapter.ViewHolder vhi = vh.get();
            if (vhi != null) {
                vhi.author.setText(user.getName());
            }
        }
    }

    private class ObterDadosUsuarioAsync extends AsyncTask<Void, Void, User> {

        private final String userId;

        public ObterDadosUsuarioAsync(String userId) {
            this.userId = userId;
        }

        @Override
        protected User doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SocialMediaService service = retrofit.create(SocialMediaService.class);
            Call<User> call = service.getUser(userId);
            try {
                Response<User> response = call.execute();
                if (response.isSuccessful())
                    return response.body();
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                new ObterTimelineAsync(user.get_id()).execute();
                mUsername.setText(user.getName());
                mCategory.setText(user.getCategory().toUpperCase() + " USER");
                Picasso.with(UserDetailsActivity.this)
                        .load("http://lorempixel.com/100/100/people?" + user.get_id())
                        .into(mImage);
            }
        }
    }

    private class ObterTimelineAsync extends AsyncTask<Void, Void, List<Post>> {

        private final String userId;

        public ObterTimelineAsync(String userId) {
            this.userId = userId;
        }

        @Override
        protected List<Post> doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SocialMediaService service = retrofit.create(SocialMediaService.class);
            Call<List<Post>> call = service.getTimeline(userId, 0, 1000);
            try {
                Response<List<Post>> response = call.execute();
                if (response.isSuccessful())
                    return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            if (posts != null) {
                TimelineAdapter adapter = new TimelineAdapter(posts);
                mTimeline.setAdapter(adapter);
            }
        }
    }
}
