package video.api.android.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import video.api.android.app.R;
import video.api.android.app.activities.SessionEventActivity;
import video.api.android.sdk.domain.analytic.AnalyticData;

public class CustomAdapterAnalytics extends RecyclerView.Adapter<CustomAdapterAnalytics.ViewHolder> {
    private final Activity                activity;
    private final ArrayList<AnalyticData> analyticDataArrayList;

    public CustomAdapterAnalytics(Activity activity, ArrayList<AnalyticData> analyticDataArrayList) {
        this.activity = activity;
        this.analyticDataArrayList = analyticDataArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.list_item_analytics, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CustomAdapterAnalytics.ViewHolder holder, int position) {
        final AnalyticData analyticData = analyticDataArrayList.get(position);
        holder.sessionId.setText(analyticData.getSession().getSessionId());
        holder.analyticsCountry.setText(analyticData.getLocation().getCountry());
        holder.analyticsCity.setText(analyticData.getLocation().getCity());
        holder.analyticsUrl.setText(analyticData.getReferrer().getUrl());
        holder.analyticsMedium.setText(analyticData.getReferrer().getMedium());
        holder.analyticsSource.setText(analyticData.getReferrer().getSource());
        holder.analyticsSearchTerm.setText(analyticData.getReferrer().getSearch_term());
        holder.analyticsType.setText(analyticData.getDevice().getType());
        holder.analyticsVendor.setText(analyticData.getDevice().getVendor());
        holder.analyticsModel.setText(analyticData.getDevice().getModel());
        holder.analyticsName.setText(analyticData.getOs().getName());
        holder.analyticsShortname.setText(analyticData.getOs().getShortname());
        holder.analyticsVersion.setText(analyticData.getOs().getVersion());
        holder.analyticsClientType.setText(analyticData.getClient().getType());
        holder.analyticsClientName.setText(analyticData.getClient().getName());
        holder.analyticsClientVersion.setText(analyticData.getClient().getVersion());
        holder.buttonViewOption.setOnClickListener(view -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(activity, holder.buttonViewOption);
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu_analytics);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.showSession) {
                    Intent intentShow = new Intent(activity, SessionEventActivity.class);
                    intentShow.putExtra("sessionId", holder.sessionId.getText());
                    activity.startActivity(intentShow);
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return analyticDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sessionId;
        private TextView analyticsCountry, analyticsModel, analyticsName, analyticsShortname, analyticsVersion, analyticsClientType, analyticsClientName;
        private TextView analyticsCity, analyticsUrl, analyticsMedium, analyticsSource, analyticsSearchTerm, analyticsType, analyticsVendor;
        private TextView buttonViewOption, analyticsClientVersion;


        private ViewHolder(View itemView) {
            super(itemView);
            sessionId = itemView.findViewById(R.id.sessionId);
            analyticsCountry = itemView.findViewById(R.id.analyticsCountry);
            analyticsCity = itemView.findViewById(R.id.analyticsCity);
            analyticsUrl = itemView.findViewById(R.id.analyticsUrl);
            analyticsMedium = itemView.findViewById(R.id.analyticsMedium);
            analyticsSource = itemView.findViewById(R.id.analyticsSource);
            analyticsSearchTerm = itemView.findViewById(R.id.analyticsSearchTerm);
            analyticsType = itemView.findViewById(R.id.analyticsType);
            analyticsVendor = itemView.findViewById(R.id.analyticsVendor);
            analyticsModel = itemView.findViewById(R.id.analyticsModel);
            analyticsName = itemView.findViewById(R.id.analyticsName);
            analyticsShortname = itemView.findViewById(R.id.analyticsShortname);
            analyticsVersion = itemView.findViewById(R.id.analyticsVersion);
            analyticsClientType = itemView.findViewById(R.id.analyticsClientType);
            analyticsClientName = itemView.findViewById(R.id.analyticsClientName);
            analyticsClientVersion = itemView.findViewById(R.id.analyticsClientVersion);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);

        }
    }
}
