package edu.osumc.bmi.oauthapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import net.openid.appauth.AuthState;

public class MainActivity extends AppCompatActivity {

    private static final int RC_AUTH = 100;
    AppAuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authService = AppAuthService.getInstance(this);

        setContentView(R.layout.activity_main);
        findViewById(R.id.login).setOnClickListener((View view) -> handleLogin());
    }

    private void handleLogin() {
        startActivityForResult(authService.getAuthRequestIntent(), RC_AUTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.login_cancelled, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Assuming we had a good login", Toast.LENGTH_SHORT).show();
            authService.getAuthToken(data, this::handleTokenResponse);
        }
    }

    private void handleTokenResponse(AuthState authstate) {
        Toast.makeText(this, "What's our current auth state", Toast.LENGTH_SHORT).show();
    }

}
