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

public class ShowLiveActivity extends AppCompatActivity {
    private TextView id, name, streamKey, record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_live);
        id = findViewById(R.id.liveStreamId);
        name = findViewById(R.id.liveName);
        streamKey = findViewById(R.id.liveStreamKey);
        record = findViewById(R.id.liveRecord);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ShowLiveActivity.this, LivesActivity.class);
            startActivity(intent);
        });
        String liveStreamId = getIntent().getExtras().getString("liveStreamId");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.liveStreams.get(liveStreamId, live -> {
            id.setText(live.getLiveStreamId());
            name.setText(live.getName());
            streamKey.setText(live.getStreamKey());
            record.setText(Boolean.toString(live.isRecord()));
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());

    }
}
