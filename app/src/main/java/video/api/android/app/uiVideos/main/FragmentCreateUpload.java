package video.api.android.app.uiVideos.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import video.api.android.app.App;
import video.api.android.app.R;
import video.api.android.sdk.Client;
import video.api.android.sdk.domain.Video;

import static android.app.Activity.RESULT_OK;

public class FragmentCreateUpload extends Fragment {
    private EditText title, description;
    private final        int    VIDEO_REQUEST = 1;
    private final        int    VIDEO_CAPTURE = 3;
    //Notification
    private static final String CHANNEL_ID    = "api.video";
    private static final String CHANNEL_NAME  = "api.video";
    private static final String CHANNEL_DESC  = "api.vidoe";
    final                int    progressMax   = 100;
    private              Client client;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_create_upload_videos, container, false);
        Button chooseVideoBtn = root.findViewById(R.id.chooseVideoBtn);
        Button captureVideoBtn = root.findViewById(R.id.captureVideoBtn);
        title = root.findViewById(R.id.title);
        description = root.findViewById(R.id.description);
        chooseVideoBtn.setOnClickListener(view -> selectVideo());
        captureVideoBtn.setOnClickListener(view -> captureVideo());
        client = ((App) getActivity().getApplication()).getApiVideoClient();
        //Notification Channel
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        return root;
    }

    private void captureVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(videoIntent, VIDEO_CAPTURE);

    }

    private void selectVideo() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, VIDEO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == VIDEO_REQUEST || requestCode == VIDEO_CAPTURE) && resultCode == RESULT_OK && data != null) {
            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};
            final Cursor cursor = getContext().getContentResolver().query(selectedVideo, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String videoPath = cursor.getString(columnIndex);
            Video video = new Video();
            if (!title.getText().toString().equals("")) {
                video.setTitle(title.getText().toString());
                video.setDescription(description.getText().toString());
            }
//            Map<String, String> metadatas = new HashMap<>();
//            metadatas.put("age", "__age__");
//            metadatas.put("sexe", "__sexe__");
//            video.setMetadata(metadatas);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_logo_apivideo)
                    .setContentTitle("Upload")
                    .setContentText("Upload in progress")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setProgress(progressMax, 0, false);
            //Notification Manager
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
            notificationManagerCompat.notify(1, mBuilder.build());
            title.setText("");
            description.setText("");
            mBuilder.setProgress(progressMax, 50, false);
            notificationManagerCompat.notify(1, mBuilder.build());
            client.videos.upload(video, videoPath, video1 -> {
                //displayNotification();
                mBuilder.setProgress(progressMax, progressMax, false);
                notificationManagerCompat.notify(1, mBuilder.build());
                SystemClock.sleep(1000);
                mBuilder.setContentText("Upload Finished")
                        .setProgress(0, 0, false)
                        .setOngoing(false);
                notificationManagerCompat.notify(1, mBuilder.build());
                Toast.makeText(getContext(), "Video uploaded", Toast.LENGTH_LONG).show();
            }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            cursor.close();
        }
    }

}