package video.api.android.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;

public class UpdateThumbnailVideoActivity extends AppCompatActivity {
    private       String          videoId;
    private final int             IMAGE_REQUEST = 2;
    private final int             IMAGE_CAPTURE = 4;
    private       SimpleExoPlayer player;
    private       Client          client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_thumbnail);
        //ExoPlayer implementation
        //Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        // Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();
        //Bis. Create a RenderFactory
        RenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        //Create the player
        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        //Player
        SimpleExoPlayerView simpleExoPlayerView;
        simpleExoPlayerView = findViewById(R.id.exoplayerView);
        //Set media controller
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);

        // Set the media source
        String hls = getIntent().getExtras().getString("hls");
        Uri mp4VideoUri = Uri.parse(hls);

        //Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();

        //Produces DataSource instances through which media data is loaded.
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "PiwikVideoApp"), bandwidthMeterA);

        //Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //FOR LIVE STREAM LINK:
        MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
        player.prepare(videoSource);
        Button chooseThumbnailBtn = findViewById(R.id.chooseThumbnailBtn);
        Button captureThumbnailBtn = findViewById(R.id.captureThumbnailBtn);
        Button pickThumbnailBtn = findViewById(R.id.pickThumbnailBtn);
        videoId = getIntent().getExtras().getString("videoId");
        client = ((App) getApplication()).getApiVideoClient();
        chooseThumbnailBtn.setOnClickListener(view -> selectImage());
        captureThumbnailBtn.setOnClickListener(view -> captureImage());
        pickThumbnailBtn.setOnClickListener(view -> {
            long position = player.getCurrentPosition();
            SimpleDateFormat format = new SimpleDateFormat("00:mm:ss:SS");
            client.videos.updateThumbnailWithTimeCode(videoId, format.format(position), video -> Toast.makeText(getApplicationContext(), "Thumbnail updated", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateThumbnailVideoActivity.this, VideosActivity.class);
            startActivity(intent);
        });
    }

    private void captureImage() {
        Intent videoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, IMAGE_CAPTURE);
        }
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == IMAGE_REQUEST || requestCode == IMAGE_CAPTURE) && resultCode == RESULT_OK && data != null) {
            Uri picUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(picUri, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String thumbnailPath = cursor.getString(columnIndex);
            client.videos.uploadThumbnail(videoId, thumbnailPath, video -> Toast.makeText(getApplicationContext(), "Thumbnail updated", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), "error Thumbnail updated", Toast.LENGTH_LONG).show());
            cursor.close();
        }
    }
}
