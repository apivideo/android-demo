package video.api.android.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import video.api.android.sdk.Client;
import video.api.android.app.R;
import video.api.android.app.App;

public class TokenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TextView tokenTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        tokenTextView = findViewById(R.id.token);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Client client = ((App) getApplication()).getApiVideoClient();
        client.tokens.generate(token -> tokenTextView.setText(token.getToken()), error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
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
            Intent intent = new Intent(TokenActivity.this, ApiActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_video) {
            Intent intent = new Intent( TokenActivity.this, VideosActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_live) {
            Intent intent = new Intent(TokenActivity.this, LivesActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_captions) {
            Intent intent = new Intent(TokenActivity.this, CaptionsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_players) {
            Intent intent = new Intent(TokenActivity.this, PlayersActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_analytics) {
            Intent intent = new Intent(TokenActivity.this, AnalyticsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_account) {
            Intent intent = new Intent(TokenActivity.this, AccountActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_token) {
            Intent intent = new Intent(TokenActivity.this, TokenActivity.class);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
