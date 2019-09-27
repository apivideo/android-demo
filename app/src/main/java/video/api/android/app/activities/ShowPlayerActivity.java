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

public class ShowPlayerActivity extends AppCompatActivity {
    private TextView id, playerLanguage, PlayerEnableApi, playerEnableControls, playerForceAutoplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_player);
        id = findViewById(R.id.playerId);
        playerLanguage = findViewById(R.id.playerLanguage);
        PlayerEnableApi = findViewById(R.id.PlayerEnableApi);
        playerEnableControls = findViewById(R.id.playerEnableControls);
        playerForceAutoplay = findViewById(R.id.playerForceAutoplay);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ShowPlayerActivity.this, PlayersActivity.class);
            startActivity(intent);
        });
        String playerId = getIntent().getExtras().getString("playerId");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.players.get(playerId, player -> {
            id.setText(player.getPlayerId());
            playerLanguage.setText(player.getLanguage());
            PlayerEnableApi.setText(Boolean.toString(player.isEnableApi()));
            playerEnableControls.setText(Boolean.toString(player.isEnableControls()));
            playerForceAutoplay.setText(Boolean.toString(player.isForceAutoplay()));
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
    }
}
