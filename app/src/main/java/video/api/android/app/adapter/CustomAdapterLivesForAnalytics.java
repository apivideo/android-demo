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
import video.api.android.app.activities.AnalyticsLiveActivity;
import video.api.android.app.activities.ChartLiveActivity;
import video.api.android.sdk.domain.ILoadMore;
import video.api.android.sdk.domain.LiveStream;

public class CustomAdapterLivesForAnalytics extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Activity              activity;
    private final ArrayList<LiveStream> liveArrayList;
    private final int                   VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    private boolean   isLoading;
    private ILoadMore loadMore;

    public CustomAdapterLivesForAnalytics(RecyclerView recyclerView, final Activity activity, ArrayList<LiveStream> liveArrayList) {
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            LiveStream live = liveArrayList.get(position);
            Picasso.get().load(live.getAssets().getThumbnail()).into(((ViewHolder) holder).imageView);
            viewHolder.name.setText(live.getName());
            viewHolder.buttonViewOption.setOnClickListener(view -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(activity, viewHolder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu_for_analytics);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.analytics:
                            Intent intentShow = new Intent(activity, AnalyticsLiveActivity.class);
                            intentShow.putExtra("liveStreamId", live.getLiveStreamId());
                            activity.startActivity(intentShow);
                            break;
                        case R.id.chart:
                            Intent intentChart = new Intent(activity, ChartLiveActivity.class);
                            intentChart.putExtra("liveStreamId", live.getLiveStreamId());
                            activity.startActivity(intentChart);
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
