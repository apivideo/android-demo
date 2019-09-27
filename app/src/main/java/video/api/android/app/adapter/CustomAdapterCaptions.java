package video.api.android.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;
import video.api.android.app.activities.ShowCaptionActivity;
import video.api.android.app.activities.UpdateCaptionActivity;
import video.api.android.sdk.domain.Caption;

public class CustomAdapterCaptions extends RecyclerView.Adapter<CustomAdapterCaptions.ViewHolder> {
    final         Activity           activity;
    private final ArrayList<Caption> captionArrayList;
    private final String             videoId;


    public CustomAdapterCaptions(Activity activity, ArrayList<Caption> captionArrayList, String videoId) {
        this.activity = activity;
        this.captionArrayList = captionArrayList;
        this.videoId = videoId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.list_item_captions, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CustomAdapterCaptions.ViewHolder holder, int position) {
        final Caption caption = captionArrayList.get(position);
        holder.uriCaption.setText(caption.getUri());
        holder.srclangCaption.setText(caption.getSrclang());
        holder.defaultCaption.setText(Boolean.toString(caption.isDefaut()));
        holder.buttonViewOption.setOnClickListener(view -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(activity, holder.buttonViewOption);
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu_captions);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.showCaption:
                        Intent intentShow = new Intent(activity, ShowCaptionActivity.class);
                        intentShow.putExtra("videoId", videoId);
                        intentShow.putExtra("srclang", holder.srclangCaption.getText());
                        activity.startActivity(intentShow);
                        break;
                    case R.id.updateCaption:
                        Intent intentUpdate = new Intent(activity, UpdateCaptionActivity.class);
                        intentUpdate.putExtra("videoId", videoId);
                        intentUpdate.putExtra("srclang", holder.srclangCaption.getText());
                        activity.startActivity(intentUpdate);
                        break;
                    case R.id.deleteCaption:
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("Are you sure to delete ?");
                        builder.setCancelable(false);
                        builder.setTitle("Confirmation");
                        builder.setPositiveButton("Ok",
                                (dialog, id) -> {
                                    Client client = ((App) activity.getApplication()).getApiVideoClient();
                                    client.captions.delete(videoId, holder.srclangCaption.getText().toString(), () -> Toast.makeText(activity.getApplicationContext(), "Caption deleted", Toast.LENGTH_LONG).show(), error -> Toast.makeText(activity.getApplicationContext(), "error Caption deleted", Toast.LENGTH_LONG).show());
                                });

                        builder.setNegativeButton("Cancel",
                                (dialog, id) -> dialog.cancel());
                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                }
                return false;
            });
            //displaying the popup
            popup.show();

        });
    }

    @Override
    public int getItemCount() {
        return captionArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView uriCaption;
        private final TextView srclangCaption;
        private final TextView defaultCaption;
        private final TextView buttonViewOption;

        private ViewHolder(View itemView) {
            super(itemView);

            uriCaption = itemView.findViewById(R.id.uriCaption);
            srclangCaption = itemView.findViewById(R.id.srclangCaption);
            defaultCaption = itemView.findViewById(R.id.defaultCaption);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
        }
    }
}
