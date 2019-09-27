package video.api.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;
import video.api.android.app.adapter.CustomAdapterEvents;
import video.api.android.sdk.domain.analytic.AnalyticEvent;
import video.api.android.sdk.domain.pagination.Pager;

public class SessionEventActivity extends AppCompatActivity {
    private RecyclerView         view;
    private SwipeRefreshLayout   refreshLayout;
    private CustomAdapterEvents  adapter;
    private Pager<AnalyticEvent> pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_event);
        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        view = findViewById(R.id.recyclerView);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(SessionEventActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });
        String sessionId = getIntent().getExtras().getString("sessionId");
        Client client = ((App) getApplication()).getApiVideoClient();
        adapter = new CustomAdapterEvents(view, SessionEventActivity.this, new ArrayList<>());
        adapter.setLoadMore(this::loadNextPage);
        view.setAdapter(adapter);
        pager = client.sessionEventAnalytics.list(sessionId, 5);
        loadNextPage();
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            adapter = new CustomAdapterEvents(view, SessionEventActivity.this, new ArrayList<>());
            adapter.setLoadMore(this::loadNextPage);
            view.setAdapter(adapter);
            pager = client.sessionEventAnalytics.list(sessionId, 5);
            loadNextPage();
        });
    }
    private void loadNextPage() {
        //adapter.addSpinner();
        pager.next(page -> {
            //adapter.removeSpinner();
            if (page != null) {
                adapter.addListItemToAdapter(page.getItems());
            }
            adapter.setLoaded();
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());

    }
}
