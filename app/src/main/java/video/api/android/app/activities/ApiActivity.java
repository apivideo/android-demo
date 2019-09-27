package video.api.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import video.api.android.app.R;

public class ApiActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout videos = findViewById(R.id.videos);
        LinearLayout lives = findViewById(R.id.lives);
        LinearLayout captions = findViewById(R.id.captions);
        LinearLayout players = findViewById(R.id.players);
        LinearLayout analytics = findViewById(R.id.analytics);
        LinearLayout account = findViewById(R.id.account);


        videos.setOnClickListener(view -> {
            videos.setBackgroundColor(10);
            Intent intent = new Intent(ApiActivity.this, VideosActivity.class);
            startActivity(intent);
        });
        lives.setOnClickListener(view -> {
            lives.setBackgroundColor(10);
            Intent intent = new Intent(ApiActivity.this, LivesActivity.class);
            startActivity(intent);
        });
        captions.setOnClickListener(view -> {
            captions.setBackgroundColor(10);
            Intent intent = new Intent(ApiActivity.this, CaptionsActivity.class);
            startActivity(intent);
        });
        players.setOnClickListener(view -> {
            players.setBackgroundColor(10);
            Intent intent = new Intent(ApiActivity.this, PlayersActivity.class);
            startActivity(intent);
        });
        analytics.setOnClickListener(view -> {
            analytics.setBackgroundColor(10);
            Intent intent = new Intent(ApiActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });
        account.setOnClickListener(view -> {
            account.setBackgroundColor(10);
            Intent intent = new Intent(ApiActivity.this, AccountActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(ApiActivity.this, ApiActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_video) {
            Intent intent = new Intent(ApiActivity.this, VideosActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_live) {
            Intent intent = new Intent(ApiActivity.this, LivesActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_captions) {
            Intent intent = new Intent(ApiActivity.this, CaptionsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_players) {
            Intent intent = new Intent(ApiActivity.this, PlayersActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_analytics) {
            Intent intent = new Intent(ApiActivity.this, AnalyticsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_account) {
            Intent intent = new Intent(ApiActivity.this, AccountActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_token) {
            Intent intent = new Intent(ApiActivity.this, TokenActivity.class);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
