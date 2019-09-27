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
import video.api.android.sdk.domain.Player;

public class UpdatePlayerActivity extends AppCompatActivity {
    private String   playerId;
    private EditText playerShapeMargin, playerShapeRadius, playerEnableApi, playerEnableControls, playerForceAutoplay, playerHideTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_player);
        playerShapeMargin = findViewById(R.id.playerShapeMargin);
        playerShapeRadius = findViewById(R.id.playerShapeRadius);
        playerEnableApi = findViewById(R.id.playerEnableApi);
        playerEnableControls = findViewById(R.id.playerEnableControls);
        playerForceAutoplay = findViewById(R.id.playerForceAutoplay);
        playerHideTitle = findViewById(R.id.playerHideTitle);
        Button updatePlayer = findViewById(R.id.updatePlayer);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(UpdatePlayerActivity.this, PlayersActivity.class);
            startActivity(intent);
        });
        playerId = getIntent().getExtras().getString("playerId");
        Client client = ((App) getApplication()).getApiVideoClient();
        client.players.get(playerId, player -> {
            playerShapeMargin.setText(Integer.toString(player.getShapeMargin()));
            playerShapeRadius.setText(Integer.toString(player.getShapeRadius()));
            playerEnableApi.setText(Boolean.toString(player.isEnableApi()));
            playerEnableControls.setText(Boolean.toString(player.isEnableControls()));
            playerForceAutoplay.setText(Boolean.toString(player.isForceAutoplay()));
            playerHideTitle.setText(Boolean.toString(player.isHideTitle()));
        }, error -> Toast.makeText(getApplicationContext(), "error Show Player", Toast.LENGTH_LONG).show());
        updatePlayer.setOnClickListener(view -> {
            Player player = new Player();
            player.setShapeMargin(Integer.parseInt(playerShapeMargin.getText().toString()));
            player.setShapeRadius(Integer.parseInt(playerShapeRadius.getText().toString()));
            player.setEnableApi(Boolean.parseBoolean(playerEnableApi.getText().toString()));
            player.setEnableControls(Boolean.parseBoolean(playerEnableControls.getText().toString()));
            player.setForceAutoplay(Boolean.parseBoolean(playerForceAutoplay.getText().toString()));
            player.setHideTitle(Boolean.parseBoolean(playerHideTitle.getText().toString()));
            player.setPlayerId(playerId);
            try {
                client.players.update(player, player1 -> Toast.makeText(getApplicationContext(), "Player updated", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
