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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;

public class UploadThumbnailLiveActivity extends AppCompatActivity {
    private       String liveStreamId;
    private final int    IMAGE_REQUEST = 2;
    private final int    IMAGE_CAPTURE = 4;
    private       Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_thumbnail_live);
        Button chooseThumbnailBtn = findViewById(R.id.chooseThumbnailBtn);
        Button captureThumbnailBtn = findViewById(R.id.captureThumbnailBtn);
        liveStreamId = getIntent().getExtras().getString("liveStreamId");
        chooseThumbnailBtn.setOnClickListener(view -> selectImage());
        captureThumbnailBtn.setOnClickListener(view -> captureImage());
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(UploadThumbnailLiveActivity.this, LivesActivity.class);
            startActivity(intent);
        });
        client = ((App) getApplication()).getApiVideoClient();
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
            client.liveStreams.uploadThumbnail(liveStreamId, thumbnailPath, live -> Toast.makeText(getApplicationContext(), "Thumbnail uploaded", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            cursor.close();
        }
    }
}
