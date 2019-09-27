package video.api.android.app;

import android.app.Application;

import video.api.android.sdk.Client;

public class App extends Application {
    private Client apiVideoClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Client getApiVideoClient() {
        return apiVideoClient;
    }

    public void setApiVideoClient(Client apiVideoClient) {
        this.apiVideoClient = apiVideoClient;
    }
}
