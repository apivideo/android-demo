package video.api.android.app.uiLives.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;



import java.util.ArrayList;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;
import video.api.android.app.adapter.CustomAdapterLives;
import video.api.android.sdk.domain.LiveStream;
import video.api.android.sdk.domain.pagination.Pager;

public class FragmentList extends Fragment {
    private RecyclerView       view;
    private SwipeRefreshLayout refreshLayout;
    private CustomAdapterLives adapter;
    private Pager<LiveStream>  pager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_list_lives, container, false);
        view = root.findViewById(R.id.recyclerView);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(root.getContext()));
        refreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        Client client = ((App) getActivity().getApplication()).getApiVideoClient();
        adapter = new CustomAdapterLives(view, getActivity(), new ArrayList<>());
        adapter.setLoadMore(this::loadNextPage);
        view.setAdapter(adapter);
        pager = client.liveStreams.list(5);
        loadNextPage();
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            adapter = new CustomAdapterLives(view, getActivity(), new ArrayList<>());
            adapter.setLoadMore(this::loadNextPage);
            view.setAdapter(adapter);

            pager = client.liveStreams.list(5);

            loadNextPage();
        });
        return root;
    }

    private void loadNextPage() {
        //adapter.addSpinner();
        pager.next(page -> {
            //adapter.removeSpinner();
            if (page != null) {
                adapter.addListItemToAdapter(page.getItems());
            }
            adapter.setLoaded();
        }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show());


    }
}
