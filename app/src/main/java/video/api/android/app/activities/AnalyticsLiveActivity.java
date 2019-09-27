package video.api.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import video.api.android.app.App;
import video.api.android.app.R;
import video.api.android.app.adapter.CustomAdapterAnalytics;
import video.api.android.sdk.Client;

public class AnalyticsLiveActivity extends AppCompatActivity {
    private RecyclerView           view;
    private CustomAdapterAnalytics adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_live);
        view = findViewById(R.id.recyclerView);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(AnalyticsLiveActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });
        String liveStreamId = getIntent().getExtras().getString("liveStreamId");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.liveStreamAnalytics.getLiveAnalytics(liveStreamId, "2019", analyticLive -> {
            adapter = new CustomAdapterAnalytics(AnalyticsLiveActivity.this, analyticLive.getData());
            view.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());

    }
}