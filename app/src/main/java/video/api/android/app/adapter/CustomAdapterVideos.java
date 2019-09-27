package video.api.android.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import video.api.android.app.App;
import video.api.android.app.R;
import video.api.android.app.activities.PlayActivity;
import video.api.android.app.activities.ShowVideoActivity;
import video.api.android.app.activities.StatusVideoActivity;
import video.api.android.app.activities.UpdateThumbnailVideoActivity;
import video.api.android.app.activities.UpdateVideoActivity;
import video.api.android.sdk.Client;
import video.api.android.sdk.domain.ILoadMore;
import video.api.android.sdk.domain.Video;

public class CustomAdapterVideos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Activity    activity;
    private final List<Video> videoArrayList;
    private final int         VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    private boolean   isLoading;
    private ILoadMore loadMore;

    public CustomAdapterVideos(RecyclerView recyclerView, final Activity activity, List<Video> videoArrayList) {
        this.activity = activity;
        this.videoArrayList = videoArrayList;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy == 0) {
                    return;
                }

                // Scrolling up
                // Scrolling down
                if (!isLoading) {
                    if (loadMore != null)
                        loadMore.onLoadMore();
                    isLoading = true;
                }
            }
        });
    }

    public void addListItemToAdapter(List<Video> list) {
        //add list to current array of data
        videoArrayList.addAll(list);
        //notify UI
        this.notifyDataSetChanged();
    }

    public void addSpinner() {
        //add list to current array of data
        videoArrayList.add(null);
        //notify UI
        this.notifyItemInserted(videoArrayList.size() - 1);
        this.notifyDataSetChanged();
    }

    public void removeSpinner() {
        //remove item current array of data
        videoArrayList.remove(videoArrayList.size() - 1);
        //notify UI
        this.notifyItemRemoved(videoArrayList.size());
        this.notifyDataSetChanged();
    }
    public void removeItems() {
        //remove item current array of data
        for (int i=0;i<videoArrayList.size();i++){
            videoArrayList.remove(i);
        }
        //notify UI
        this.notifyItemRemoved(videoArrayList.size());
        this.notifyDataSetChanged();
    }



    @Override
    public int getItemViewType(int position) {
        return videoArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.list_item_videos, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.footer_view, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            Video video = videoArrayList.get(position);
            Picasso.get().load(video.getAssets().getThumbnail()).into(((ViewHolder) holder).imageView);
            viewHolder.title.setText(video.getTitle());
            viewHolder.description.setText(video.getDescription());
            viewHolder.imageView.setOnClickListener(view -> {
                Intent intentPlay = new Intent(activity, PlayActivity.class);
                intentPlay.putExtra("url", video.getAssets().getPlayer());
                activity.startActivity(intentPlay);
            });
            viewHolder.buttonViewOption.setOnClickListener(view -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(activity, viewHolder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu_video);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.shareVideo:
                            if (video.isAccessible()) {
                                Intent intentShare = new Intent(Intent.ACTION_SEND);
                                intentShare.setType("text/plain");
                                String shareBody = video.getAssets().getPlayer();
                                String shareSub = "Player Url";
                                intentShare.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                                intentShare.putExtra(Intent.EXTRA_TEXT, shareBody);
                                activity.startActivity(Intent.createChooser(intentShare, "Share using"));
                            } else {
                                Toast.makeText(activity.getApplicationContext(), "you not allowed to share it", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case R.id.showVideo:
                            Intent intentShow = new Intent(activity, ShowVideoActivity.class);
                            intentShow.putExtra("videoId", video.getVideoId());
                            activity.startActivity(intentShow);
                            break;
                        case R.id.statusVideo:
                            Intent intentStatus = new Intent(activity, StatusVideoActivity.class);
                            intentStatus.putExtra("videoId", video.getVideoId());
                            activity.startActivity(intentStatus);
                            break;
                        case R.id.updateVideo:
                            Intent intentUpdate = new Intent(activity, UpdateVideoActivity.class);
                            intentUpdate.putExtra("videoId", video.getVideoId());
                            activity.startActivity(intentUpdate);
                            break;
                        case R.id.updateThumbnail:
                            Intent intentThumbnail = new Intent(activity, UpdateThumbnailVideoActivity.class);
                            intentThumbnail.putExtra("videoId", video.getVideoId());
                            intentThumbnail.putExtra("hls", video.getAssets().getHls());
                            activity.startActivity(intentThumbnail);
                            break;
                        case R.id.deleteVideo:
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage("Are you sure to delete ?");
                            builder.setCancelable(false);
                            builder.setTitle("Confirmation");
                            builder.setPositiveButton("Ok",
                                    (dialog, id) -> {
                                        Client client = ((App) activity.getApplication()).getApiVideoClient();
                                        client.videos.delete(video.getVideoId(), () -> {
                                            Toast.makeText(activity.getApplicationContext(), "Video deleted", Toast.LENGTH_LONG).show();
                                            removeItem(position);
                                        }, error -> Toast.makeText(activity.getApplicationContext(), "error Video deleted", Toast.LENGTH_LONG).show());
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
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    private void removeItem(int position) {
        videoArrayList.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;
        public final TextView  title;
        public final TextView  description;
        public final TextView  buttonViewOption;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public final ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }

}
