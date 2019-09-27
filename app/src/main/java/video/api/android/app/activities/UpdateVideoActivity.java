package video.api.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;
import video.api.android.sdk.domain.Video;

public class UpdateVideoActivity extends AppCompatActivity {
    private String   videoId;
    private EditText title, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_video);
        title = findViewById(R.id.videoTitle);
        description = findViewById(R.id.videoDescription);
        Button updateVideo = findViewById(R.id.updateVideo);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateVideoActivity.this, VideosActivity.class);
            startActivity(intent);
        });
        videoId = getIntent().getExtras().getString("videoId");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.videos.get(videoId, video -> {
            title.setText(video.getTitle());
            description.setText(video.getDescription());
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
        updateVideo.setOnClickListener(view -> {
            Video video = new Video();
            video.setTitle(title.getText().toString());
            video.setDescription(description.getText().toString());
            video.setVideoId(videoId);
            try {
                client.videos.update(video, video1 -> Toast.makeText(getApplicationContext(), "Video updated", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
