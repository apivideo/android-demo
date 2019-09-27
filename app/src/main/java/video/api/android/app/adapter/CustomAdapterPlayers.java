package video.api.android.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;
import video.api.android.app.activities.ShowPlayerActivity;
import video.api.android.app.activities.UpdatePlayerActivity;
import video.api.android.app.activities.UploadLogoPlayerActivity;
import video.api.android.sdk.domain.ILoadMore;
import video.api.android.sdk.domain.Player;

public class CustomAdapterPlayers extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final         Activity          activity;
    final         ArrayList<Player> playerArrayList;
    private final int               VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    boolean   isLoading;
    ILoadMore loadMore;

    public CustomAdapterPlayers(RecyclerView recyclerView, final Activity activity, ArrayList<Player> playerArrayList) {
        this.activity = activity;
        this.playerArrayList = playerArrayList;
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

    public void addListItemToAdapter(List<Player> list) {
        //add list to current array of data
        playerArrayList.addAll(list);
        //notify UI
        this.notifyDataSetChanged();
    }

    public void addSpinner() {
        //add list to current array of data
        playerArrayList.add(null);
        //notify UI
        this.notifyItemInserted(playerArrayList.size() - 1);
        this.notifyDataSetChanged();
    }

    public void removeSpinner() {
        //add list to current array of data
        playerArrayList.remove(playerArrayList.size() - 1);
        //notify UI
        this.notifyItemRemoved(playerArrayList.size());
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return playerArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.list_item_players, parent, false);
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
            Player player = playerArrayList.get(position);
            viewHolder.playerId.setText(player.getPlayerId());
            viewHolder.buttonViewOption.setOnClickListener(view -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(activity, viewHolder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu_players);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.showPlayer:
                            Intent intentShow = new Intent(activity, ShowPlayerActivity.class);
                            intentShow.putExtra("playerId", viewHolder.playerId.getText());
                            activity.startActivity(intentShow);
                            break;
                        case R.id.updatePlayer:
                            Intent intentUpdate = new Intent(activity, UpdatePlayerActivity.class);
                            intentUpdate.putExtra("playerId", viewHolder.playerId.getText());
                            activity.startActivity(intentUpdate);
                            break;
                        case R.id.uploadLogo:
                            Intent updateLogo = new Intent(activity, UploadLogoPlayerActivity.class);
                            updateLogo.putExtra("playerId", viewHolder.playerId.getText());
                            activity.startActivity(updateLogo);
                            break;
                        case R.id.deletePlayer:
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage("Are you sure to delete ?");
                            builder.setCancelable(false);
                            builder.setTitle("Confirmation");
                            builder.setPositiveButton("Ok",
                                    (dialog, id) -> {
                                        Client client = ((App) activity.getApplication()).getApiVideoClient();
                                        client.players.delete(viewHolder.playerId.getText().toString(), () -> {
                                            Toast.makeText(activity.getApplicationContext(), "Player deleted", Toast.LENGTH_LONG).show();
                                            removeItem(position);
                                        }, error -> Toast.makeText(activity.getApplicationContext(), "error Player deleted", Toast.LENGTH_LONG).show());
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
        playerArrayList.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return playerArrayList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView playerId;
        public final TextView buttonViewOption;

        public ViewHolder(View itemView) {
            super(itemView);
            playerId = itemView.findViewById(R.id.playerId);
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
