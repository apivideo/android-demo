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
import video.api.android.sdk.domain.Caption;

public class UpdateCaptionActivity extends AppCompatActivity {
    private String   videoId;
    private EditText captionDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_caption);
        Button updateCaption = findViewById(R.id.updateCaption);
        captionDefault = findViewById(R.id.captionDefault);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateCaptionActivity.this, CaptionsActivity.class);
            startActivity(intent);
        });
        videoId = getIntent().getExtras().getString("videoId");
        String srclang = getIntent().getExtras().getString("srclang");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.captions.get(videoId, srclang, caption -> captionDefault.setText(Boolean.toString(caption.isDefaut())), error -> Toast.makeText(getApplicationContext(), "error Show Caption", Toast.LENGTH_LONG).show());
        updateCaption.setOnClickListener(view -> {
            Caption caption = new Caption();
            caption.setDefaut(Boolean.parseBoolean(captionDefault.getText().toString()));
            try {
                client.captions.update(videoId, "fr", caption, caption1 -> Toast.makeText(getApplicationContext(), "Caption updated", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
}
