package video.api.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;

public class StatusVideoActivity extends AppCompatActivity {
    private TextView stat, filesize, playable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_video);
        stat = findViewById(R.id.status);
        filesize = findViewById(R.id.filesize);
        playable = findViewById(R.id.playable);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(StatusVideoActivity.this, VideosActivity.class);
            startActivity(intent);
        });
        String videoId = getIntent().getExtras().getString("videoId");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.videos.getStatus(videoId, status -> {
            stat.setText(status.getIngest().getStatus());
            filesize.setText(Integer.toString(status.getIngest().getFilesize()));
            playable.setText(Boolean.toString(status.getEncoding().isPlayable()));
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());

    }
}
