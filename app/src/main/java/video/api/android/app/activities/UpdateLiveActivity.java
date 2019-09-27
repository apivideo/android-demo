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
import video.api.android.sdk.domain.LiveStream;

public class UpdateLiveActivity extends AppCompatActivity {
    private String   liveStreamId;
    private EditText name, record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_live);
        name = findViewById(R.id.liveName);
        record = findViewById(R.id.liveRecord);
        Button updateLive = findViewById(R.id.updateLive);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateLiveActivity.this, LivesActivity.class);
            startActivity(intent);
        });
        liveStreamId = getIntent().getExtras().getString("liveStreamId");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.liveStreams.get(liveStreamId, live -> {
            name.setText(live.getName());
            record.setText(Boolean.toString(live.isRecord()));
        }, error -> {
        });
        updateLive.setOnClickListener(view -> {
            LiveStream live = new LiveStream();
            live.setName(name.getText().toString());
            live.setRecord(Boolean.parseBoolean(record.getText().toString()));
            live.setLiveStreamId(liveStreamId);
            try {
                client.liveStreams.update(live, live1 -> Toast.makeText(getApplicationContext(), "Live updated", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }
}
