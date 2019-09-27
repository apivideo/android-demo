package video.api.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;
import video.api.android.app.adapter.CustomAdapterCaptions;

public class ListCaptionsActivity extends AppCompatActivity {
    private RecyclerView          view;
    private CustomAdapterCaptions adapter;
    private String                videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_captions);
        view = findViewById(R.id.recyclerView);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ListCaptionsActivity.this, CaptionsActivity.class);
            startActivity(intent);
        });
        videoId = getIntent().getExtras().getString("videoId");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.captions.getAll(videoId, captions -> {
            adapter = new CustomAdapterCaptions(ListCaptionsActivity.this, captions, videoId);
            view.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
    }
}
