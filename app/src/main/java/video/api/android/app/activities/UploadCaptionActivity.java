package video.api.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;

public class UploadCaptionActivity extends AppCompatActivity {
    private String   videoId;
    private EditText fileContent, languageCaption;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_caption);
        fileContent = findViewById(R.id.fileContent);
        Button uploadCaption = findViewById(R.id.uploadCaption);
        languageCaption = findViewById(R.id.languageCaption);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(UploadCaptionActivity.this, CaptionsActivity.class);
            startActivity(intent);
        });

        videoId = getIntent().getExtras().getString("videoId");
        Client client = ((App) getApplication()).getApiVideoClient();

        uploadCaption.setOnClickListener(view -> {
            try {
                file = new File(Environment.getExternalStorageDirectory(), "caption.vtt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.write(fileContent.getText().toString());
                writer.newLine();
                writer.flush();
                writer.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            client.captions.upload(videoId, languageCaption.getText().toString(), file.getPath(), caption -> Toast.makeText(getApplicationContext(), "Caption uploaded", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
        });


    }
}
