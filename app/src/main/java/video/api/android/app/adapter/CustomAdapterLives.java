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

import java.util.ArrayList;
import java.util.List;

import video.api.android.app.App;
import video.api.android.app.R;
import video.api.android.app.activities.PlayActivity;
import video.api.android.app.activities.ShowLiveActivity;
import video.api.android.app.activities.UpdateLiveActivity;
import video.api.android.app.activities.UploadThumbnailLiveActivity;
import video.api.android.app.liveVideoBroadcaster.LiveVideoBroadcasterActivity;
import video.api.android.sdk.Client;
import video.api.android.sdk.domain.ILoadMore;
import video.api.android.sdk.domain.LiveStream;

public class CustomAdapterLives extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Activity              activity;
    private final ArrayList<LiveStream> liveArrayList;
    private final int                   VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    private boolean   isLoading;
    private ILoadMore loadMore;
    private Client    client;

    public CustomAdapterLives(RecyclerView recyclerView, final Activity activity, ArrayList<LiveStream> liveArrayList) {
        this.activity = activity;
        this.liveArrayList = liveArrayList;
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

    public void addListItemToAdapter(List<LiveStream> list) {
        //add list to current array of data
        liveArrayList.addAll(list);
        //notify UI
        this.notifyDataSetChanged();
    }

    public void addSpinner() {
        //add list to current array of data
        liveArrayList.add(null);
        //notify UI
        this.notifyItemInserted(liveArrayList.size() - 1);
        this.notifyDataSetChanged();
    }

    public void removeSpinner() {
        //add list to current array of data
        liveArrayList.remove(liveArrayList.size() - 1);
        //notify UI
        this.notifyItemRemoved(liveArrayList.size());
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return liveArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.list_item_lives, parent, false);
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
            LiveStream live = liveArrayList.get(position);
            Picasso.get().load(live.getAssets().getThumbnail()).into(((ViewHolder) holder).imageView);
            viewHolder.name.setText(live.getName());
            viewHolder.imageView.setOnClickListener(view -> {
                Intent intentPlay = new Intent(activity, PlayActivity.class);
                intentPlay.putExtra("url", live.getAssets().getPlayer());
                activity.startActivity(intentPlay);
            });
            viewHolder.buttonViewOption.setOnClickListener(view -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(activity, viewHolder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu_live);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.startLive:
                            Intent intentStart = new Intent(activity, LiveVideoBroadcasterActivity.class);
                            intentStart.putExtra("rtmpUrl", "rtmp://broadcast-staging.api.video/s/" + live.getStreamKey());
                            activity.startActivity(intentStart);
                            break;
                        case R.id.showLive:
                            Intent intentShow = new Intent(activity, ShowLiveActivity.class);
                            intentShow.putExtra("liveStreamId", live.getLiveStreamId());
                            activity.startActivity(intentShow);
                            break;
                        case R.id.updateLive:
                            Intent intentUpdate = new Intent(activity, UpdateLiveActivity.class);
                            intentUpdate.putExtra("liveStreamId", live.getLiveStreamId());
                            activity.startActivity(intentUpdate);
                            break;
                        case R.id.uploadThumbnail:
                            Intent intentThumbnail = new Intent(activity, UploadThumbnailLiveActivity.class);
                            intentThumbnail.putExtra("liveStreamId", live.getLiveStreamId());
                            activity.startActivity(intentThumbnail);
                            break;
                        case R.id.deleteThumbnail:
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage("Are you sure to delete ?");
                            builder.setCancelable(false);
                            builder.setTitle("Confirmation");
                            builder.setPositiveButton("Ok",
                                    (dialog, id) -> {
                                        client = ((App) activity.getApplication()).getApiVideoClient();
                                        client.liveStreams.deleteThumbnail(live.getLiveStreamId(), live1 -> Toast.makeText(activity.getApplicationContext(), "Thumbnail deleted", Toast.LENGTH_LONG).show(), error -> Toast.makeText(activity.getApplicationContext(), "error Thumbnail deleted", Toast.LENGTH_LONG).show());
                                    });

                            builder.setNegativeButton("Cancel",
                                    (dialog, id) -> dialog.cancel());
                            AlertDialog alert = builder.create();
                            alert.show();
                            break;
                        case R.id.deleteLive:
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                            builder1.setMessage("Are you sure to delete ?");
                            builder1.setCancelable(false);
                            builder1.setTitle("Confirmation");
                            builder1.setPositiveButton("Ok",
                                    (dialog, id) -> {
                                        Client client = ((App) activity.getApplication()).getApiVideoClient();
                                        client.liveStreams.delete(live.getLiveStreamId(), () -> {
                                            Toast.makeText(activity.getApplicationContext(), "Live deleted", Toast.LENGTH_LONG).show();
                                            removeItem(position);
                                        }, error -> Toast.makeText(activity.getApplicationContext(), "error Live deleted", Toast.LENGTH_LONG).show());
                                    });
                            builder1.setNegativeButton("Cancel",
                                    (dialog, id) -> dialog.cancel());
                            AlertDialog alert1 = builder1.create();
                            alert1.show();
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
        liveArrayList.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return liveArrayList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView imageView;
        public final TextView  name;
        public final TextView  buttonViewOption;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
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
