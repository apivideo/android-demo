package video.api.android.app.uiVideos.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import video.api.android.app.App;
import video.api.android.app.R;
import video.api.android.app.adapter.CustomAdapterVideos;
import video.api.android.sdk.Client;
import video.api.android.sdk.domain.Video;
import video.api.android.sdk.domain.pagination.Pager;
import video.api.android.sdk.domain.pagination.PagerBuilder;

public class FragmentList extends Fragment {
    private SwipeRefreshLayout  refreshLayout;
    private CustomAdapterVideos adapter;
    private Pager<Video>        pager;
    private Boolean             reverse      = false;
    private RecyclerView        recyclerView;
    private PagerBuilder        pagerBuilder = new PagerBuilder();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_list_videos, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        refreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        //-- Drop Down LIST
        final Spinner spinnerRegion = root.findViewById(R.id.spinnerRegion);
        String[] order = {"Newest", "Oldest"};
        ArrayAdapter<String> dataAdapterR = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, order);
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(dataAdapterR);
        //-- gestion du Click sur la liste
        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String order = String.valueOf(spinnerRegion.getSelectedItem());
                if (order.equals("Newest")) {
                    load("desc");
                } else if (order.equals("Oldest")) {
                    load("asc");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        return root;
    }

    private void load(String sortOrder) {
        Client client = ((App) getActivity().getApplication()).getApiVideoClient();
        adapter = new CustomAdapterVideos(recyclerView, getActivity(), new ArrayList<>());
        adapter.setLoadMore(this::loadNextPage);
        recyclerView.setAdapter(adapter);
        pagerBuilder.withPageSize(5);
        pagerBuilder.withSortBy("publishedAt");
        pagerBuilder.withSortOrder(sortOrder);
        pager = client.videos.list(pagerBuilder);
        loadNextPage();
        refreshLayout.setOnRefreshListener(() -> {
            adapter.removeItems();
            refreshLayout.setRefreshing(false);
            adapter = new CustomAdapterVideos(recyclerView, getActivity(), new ArrayList<>());
            adapter.setLoadMore(this::loadNextPage);
            recyclerView.setAdapter(adapter);
            pagerBuilder.withPageSize(5);
            pagerBuilder.withSortBy("publishedAt");
            pagerBuilder.withSortOrder(sortOrder);
            pager = client.videos.list(pagerBuilder);

            loadNextPage();
        });
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


