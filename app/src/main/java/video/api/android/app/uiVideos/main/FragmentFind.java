package video.api.android.app.uiVideos.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import video.api.android.app.App;
import video.api.android.app.R;
import video.api.android.app.adapter.CustomAdapterVideos;
import video.api.android.sdk.Client;
import video.api.android.sdk.domain.Video;
import video.api.android.sdk.domain.pagination.Pager;
import video.api.android.sdk.domain.pagination.PagerBuilder;
import video.api.android.sdk.domain.pagination.VideoFilter;

public class FragmentFind extends Fragment {
    private RecyclerView        recyclerView;
    private EditText            title;
    private CustomAdapterVideos adapter;
    private Pager<Video>        pager;
    private VideoFilter         videoFilter = new VideoFilter();
    private PagerBuilder        pagerBuilder = new PagerBuilder();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_find_videos, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        title = root.findViewById(R.id.titleVideo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        Client client = ((App) getActivity().getApplication()).getApiVideoClient();
        adapter = new CustomAdapterVideos(recyclerView, getActivity(), new ArrayList<>());
        FloatingActionButton fab = root.findViewById(R.id.find);
        fab.setOnClickListener(view -> {
            if (!title.getText().toString().equals("")) {
                adapter.removeItems();
//                Map<String, String> metadatas = new HashMap<>();
//                metadatas.put("sexe", "h");
//                videoFilter.withTitle(title.getText().build()).withMetadata(metadatas);
                videoFilter.withTitle(title.getText().toString());
                pagerBuilder.withPageSize(5);
                pagerBuilder.withSortBy("publishedAt");
                pagerBuilder.withSortOrder("desc");
                adapter.setLoadMore(FragmentFind.this::loadNextPage);
                recyclerView.setAdapter(adapter);
                pager = client.videos.search(pagerBuilder, videoFilter);
                FragmentFind.this.loadNextPage();
            }

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
