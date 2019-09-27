package video.api.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import video.api.android.app.App;
import video.api.android.app.R;
import video.api.android.sdk.Client;

public class ShowVideoActivity extends AppCompatActivity {
    private TextView id, title, description, accessibility, publishedAt, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        id = findViewById(R.id.videoId);
        title = findViewById(R.id.videoTitle);
        description = findViewById(R.id.videoDescription);
        accessibility = findViewById(R.id.videoPublic);
        publishedAt = findViewById(R.id.videoPublishedAt);
        url = findViewById(R.id.videoUrl);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ShowVideoActivity.this, VideosActivity.class);
            startActivity(intent);
        });
        String videoId = getIntent().getExtras().getString("videoId");
        Client client = ((App) getApplication()).getApiVideoClient();

        client.videos.get(videoId, video -> {
            id.setText(video.getVideoId());
            title.setText(video.getTitle());
            description.setText(video.getDescription());
            accessibility.setText(Boolean.toString(video.isAccessible()));
            publishedAt.setText(video.getPublishedAt());
            url.setText(video.getAssets().getPlayer());
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
    }
}
