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

public class ShowCaptionActivity extends AppCompatActivity {
    private TextView uriCaption, srcCaption, srclangCaption, defaultCaption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_caption);
        uriCaption = findViewById(R.id.uriCaption);
        srcCaption = findViewById(R.id.srcCaption);
        srclangCaption = findViewById(R.id.srclangCaption);
        defaultCaption = findViewById(R.id.defaultCaption);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ShowCaptionActivity.this, CaptionsActivity.class);
            startActivity(intent);
        });
        String videoId = getIntent().getExtras().getString("videoId");
        String srclang = getIntent().getExtras().getString("srclang");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.captions.get(videoId, srclang, caption -> {
            uriCaption.setText(caption.getUri());
            srcCaption.setText(caption.getSrc());
            srclangCaption.setText(caption.getSrclang());
            defaultCaption.setText(Boolean.toString(caption.isDefaut()));
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
    }
}
