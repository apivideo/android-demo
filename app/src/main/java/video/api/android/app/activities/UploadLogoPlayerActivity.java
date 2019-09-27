package video.api.android.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;

public class UploadLogoPlayerActivity extends AppCompatActivity {
    private       String   playerId;
    private       EditText playerLink;
    private final int      IMAGE_REQUEST = 2;
    private final int      IMAGE_CAPTURE = 4;
    private       Client   client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_logo_player);
        Button chooseLogoBtn = findViewById(R.id.chooseLogoBtn);
        Button captureLogoBtn = findViewById(R.id.captureLogoBtn);
        playerLink = findViewById(R.id.playerLink);
        playerId = getIntent().getExtras().getString("playerId");
        chooseLogoBtn.setOnClickListener(view -> selectImage());
//        captureLogoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                captureImage();
//            }
//        });
        client = ((App) getApplication()).getApiVideoClient();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(UploadLogoPlayerActivity.this, PlayersActivity.class);
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
            String logoPath = cursor.getString(columnIndex);
            client.players.uploadLogo(playerId, logoPath, playerLink.getText().toString(), player -> Toast.makeText(getApplicationContext(), "Logo uploaded", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            cursor.close();
        }
    }
}
