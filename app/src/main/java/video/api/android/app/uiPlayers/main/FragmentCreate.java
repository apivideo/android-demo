package video.api.android.app.uiPlayers.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;
import video.api.android.sdk.domain.Player;

public class FragmentCreate extends Fragment {
    private EditText playerShapeMargin, playerShapeRadius, playerEnableApi, playerEnableControls, playerForceAutoplay, playerHideTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_create_players, container, false);
        playerShapeMargin = root.findViewById(R.id.playerShapeMargin);
        playerShapeRadius = root.findViewById(R.id.playerShapeRadius);
        playerEnableApi = root.findViewById(R.id.playerEnableApi);
        playerEnableControls = root.findViewById(R.id.playerEnableControls);
        playerForceAutoplay = root.findViewById(R.id.playerForceAutoplay);
        playerHideTitle = root.findViewById(R.id.playerHideTitle);
        Button createPlayer = root.findViewById(R.id.createPlayer);
        Client client = ((App) getActivity().getApplication()).getApiVideoClient();
        createPlayer.setOnClickListener(view -> {
            Player player = new Player();
            if (!playerShapeMargin.getText().toString().isEmpty()) {
                player.setShapeMargin(Integer.parseInt(playerShapeMargin.getText().toString()));
            }
            if (!playerShapeRadius.getText().toString().isEmpty()) {
                player.setShapeRadius(Integer.parseInt(playerShapeRadius.getText().toString()));
            }
            player.setEnableApi(Boolean.parseBoolean(playerEnableApi.getText().toString()));
            player.setEnableControls(Boolean.parseBoolean(playerEnableControls.getText().toString()));
            player.setForceAutoplay(Boolean.parseBoolean(playerForceAutoplay.getText().toString()));
            player.setHideTitle(Boolean.parseBoolean(playerHideTitle.getText().toString()));
            try {
                client.players.create(player, player1 -> Toast.makeText(getContext(), "Player created", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            } catch (JSONException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }
}
