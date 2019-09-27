package video.api.android.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import video.api.android.app.R;
import video.api.android.sdk.domain.ILoadMore;
import video.api.android.sdk.domain.analytic.AnalyticEvent;

public class CustomAdapterEvents extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final         Activity                 activity;
    private final ArrayList<AnalyticEvent> analyticEvents;
    private final int                      VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    private boolean   isLoading;
    private ILoadMore loadMore;

    public CustomAdapterEvents(RecyclerView recyclerView, Activity activity, ArrayList<AnalyticEvent> analyticEvents) {
        this.activity = activity;
        this.analyticEvents = analyticEvents;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
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

    public void addListItemToAdapter(List<AnalyticEvent> list) {
        //add list to current array of data
        analyticEvents.addAll(list);
        //notify UI
        this.notifyDataSetChanged();
    }

    public void addSpinner() {
        //add list to current array of data
        analyticEvents.add(null);
        //notify UI
        this.notifyItemInserted(analyticEvents.size() - 1);
        this.notifyDataSetChanged();
    }

    public void removeSpinner() {
        //add list to current array of data
        analyticEvents.remove(analyticEvents.size() - 1);
        //notify UI
        this.notifyItemRemoved(analyticEvents.size());
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return analyticEvents.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.list_item_events, parent, false);
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
        if (holder instanceof CustomAdapterEvents.ViewHolder) {
            final CustomAdapterEvents.ViewHolder viewHolder = (CustomAdapterEvents.ViewHolder) holder;
            final AnalyticEvent analyticEvent = analyticEvents.get(position);
            viewHolder.eventType.setText(analyticEvent.getType());
            viewHolder.eventEmittedAt.setText(analyticEvent.getEmitted_at());
            if (analyticEvent.getFrom() != null && analyticEvent.getTo() != null) {
                viewHolder.eventAt.setVisibility(View.INVISIBLE);
                viewHolder.at.setVisibility(View.INVISIBLE);
                viewHolder.eventFrom.setVisibility(View.VISIBLE);
                viewHolder.eventTo.setVisibility(View.VISIBLE);
                viewHolder.to.setVisibility(View.VISIBLE);
                viewHolder.from.setVisibility(View.VISIBLE);
                viewHolder.eventFrom.setText(String.valueOf(analyticEvent.getFrom()));
                viewHolder.eventTo.setText(String.valueOf(analyticEvent.getTo()));
            } else if (analyticEvent.getAt() != null) {
                viewHolder.to.setVisibility(View.INVISIBLE);
                viewHolder.from.setVisibility(View.INVISIBLE);
                viewHolder.eventFrom.setVisibility(View.INVISIBLE);
                viewHolder.eventTo.setVisibility(View.INVISIBLE);
                viewHolder.eventAt.setVisibility(View.VISIBLE);
                viewHolder.at.setVisibility(View.VISIBLE);
                viewHolder.eventAt.setText(String.valueOf(analyticEvent.getAt()));
            }
        } else if (holder instanceof CustomAdapterEvents.LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return analyticEvents.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView eventType;
        private TextView eventEmittedAt;
        private TextView eventAt, eventFrom, eventTo, at, from, to;

        private ViewHolder(View itemView) {
            super(itemView);
            eventType = itemView.findViewById(R.id.eventType);
            eventEmittedAt = itemView.findViewById(R.id.eventEmittedAt);
            eventAt = itemView.findViewById(R.id.eventAt);
            eventFrom = itemView.findViewById(R.id.eventFrom);
            eventTo = itemView.findViewById(R.id.eventTo);
            at = itemView.findViewById(R.id.at);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);

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
