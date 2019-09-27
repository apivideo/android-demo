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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import video.api.android.app.R;
import video.api.android.app.activities.ListCaptionsActivity;
import video.api.android.app.activities.UploadCaptionActivity;
import video.api.android.sdk.domain.ILoadMore;
import video.api.android.sdk.domain.Video;

public class CustomAdapterVideosForCaptions extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Activity         activity;
    private final ArrayList<Video> videoArrayList;
    private final int              VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    private boolean   isLoading;
    private ILoadMore loadMore;

    public CustomAdapterVideosForCaptions(RecyclerView recyclerView, final Activity activity, ArrayList<Video> videoArrayList) {
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
        //add list to current array of data
        videoArrayList.remove(videoArrayList.size() - 1);
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            Video video = videoArrayList.get(position);
            Picasso.get().load(video.getAssets().getThumbnail()).into(((ViewHolder) holder).imageView);
            viewHolder.title.setText(video.getTitle());
            viewHolder.description.setText(video.getDescription());
            viewHolder.buttonViewOption.setOnClickListener(view -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(activity, viewHolder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu_videos_for_captions);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.uploadCaption:
                            Intent intentUpload = new Intent(activity, UploadCaptionActivity.class);
                            intentUpload.putExtra("videoId", video.getVideoId());
                            intentUpload.putExtra("srclang", "fr");
                            activity.startActivity(intentUpload);
                            break;
                        case R.id.listCaptions:
                            Intent intentList = new Intent(activity, ListCaptionsActivity.class);
                            intentList.putExtra("videoId", video.getVideoId());
                            activity.startActivity(intentList);
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
