package video.api.android.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import video.api.android.app.App;
import video.api.android.app.R;
import video.api.android.sdk.ClientFactory;

public class AuthenticationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getApplicationContext().getSharedPreferences("authentication", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        setContentView(R.layout.activity_authentication);
        EditText apiKey = findViewById(R.id.apiKey);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        Button validateBtn = findViewById(R.id.validate_btn);
        loadApiKey(settings);

        validateBtn.setOnClickListener(view -> {
            if (!apiKey.getText().toString().equals("")) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                switch (radioId) {
                    case R.id.production:
                        ((App) getApplication()).setApiVideoClient(new ClientFactory().create(getApplicationContext(), apiKey.getText().toString()));
                        editor.putString("environment", "production");
                        editor.putString("apiKey", apiKey.getText().toString());
                        // Apply the edits!
                        editor.apply();
                        break;
                    case R.id.sandbox:
                        ((App) getApplication()).setApiVideoClient(new ClientFactory().createSandbox(getApplicationContext(), apiKey.getText().toString()));
                        editor.putString("environment", "sandbox");
                        editor.putString("apiKey", apiKey.getText().toString());
                        // Apply the edits!
                        editor.apply();
                        break;
                }
                //Toast.makeText(getApplicationContext(), "API Key added", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "API Key added", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(AuthenticationActivity.this, ApiActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Please enter your Api Key", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void loadApiKey(SharedPreferences settings) {
        // Get from the SharedPreferences
        String key = settings.getString("apiKey", "");
        String environment = settings.getString("environment", "");
        if (key != "" && environment.equals("production")) {
            ((App) getApplication()).setApiVideoClient(new ClientFactory().create(getApplicationContext(), key));
            Intent intent = new Intent(AuthenticationActivity.this, ApiActivity.class);
            startActivity(intent);
            finish();
        } else if (key != "" && environment.equals("sandbox")) {
            ((App) getApplication()).setApiVideoClient(new ClientFactory().createSandbox(getApplicationContext(), key));
            Intent intent = new Intent(AuthenticationActivity.this, ApiActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
