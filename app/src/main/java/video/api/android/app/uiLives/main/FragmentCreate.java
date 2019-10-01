package video.api.android.app.uiLives.main;

import android.content.Intent;
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

import video.api.android.app.App;
import video.api.android.app.R;
import video.api.android.app.liveVideoBroadcaster.LiveVideoBroadcasterActivity;
import video.api.android.sdk.Client;
import video.api.android.sdk.domain.LiveStream;

public class FragmentCreate extends Fragment {
    private EditText liveName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_create_lives, container, false);
        Client client = ((App) getActivity().getApplication()).getApiVideoClient();
        Button startLiveBtn = root.findViewById(R.id.startLiveBtn);
        liveName = root.findViewById(R.id.liveName);
        startLiveBtn.setOnClickListener(view -> {
            LiveStream live = new LiveStream();
            live.setName(liveName.getText().toString());
            try {
                client.liveStreams.create(live, live1 -> {
                    liveName.setText("");
                    Intent intent = new Intent(FragmentCreate.this.getContext(), LiveVideoBroadcasterActivity.class);
                    intent.putExtra("rtmpUrl", "rtmp://broadcast.api.video/s/" + live1.getStreamKey());
                    FragmentCreate.this.startActivity(intent);
                }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show());

            } catch (JSONException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
        return root;
    }
}
