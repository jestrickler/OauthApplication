package edu.osumc.bmi.oauthapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import net.openid.appauth.*;

public class AppAuthService {

    Uri authUri;
    Uri tokenUri;
    String clientId;
    Uri redirectUri;
    String authScopes;

    AuthState authState;
    AuthorizationService service;
    AuthorizationRequest request;

    private static AppAuthService instance;

    public static AppAuthService getInstance(Context context) {
        if (instance ==null)
            instance = new AppAuthService(context);
        return instance;
    }

    private AppAuthService(Context context) {
        initializeSettings(context);
        service = new AuthorizationService(context);
        authState = new AuthState(initializeConfiguration());
    }

    private void initializeSettings(Context context) {
        authUri = Uri.parse(context.getString(R.string.auth_uri));
        tokenUri = Uri.parse(context.getString(R.string.token_uri));
        clientId = context.getString(R.string.client_id);
        redirectUri = Uri.parse(context.getString(R.string.redirect_uri));
        authScopes = context.getString(R.string.auth_scopes);
    }

    private AuthorizationServiceConfiguration initializeConfiguration() {
        return new AuthorizationServiceConfiguration(authUri, tokenUri);
    }

    public AuthState getAuthState() {
        return authState;
    }

    public Intent getAuthRequestIntent() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
            authState.getAuthorizationServiceConfiguration(),
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        );
        builder.setScopes(authScopes);
        request = builder.build();
        return service.getAuthorizationRequestIntent(request);
    }

    public void getAuthToken(Intent intent, TokenResponseCallback callback) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException ex = AuthorizationException.fromIntent(intent);
        authState.update(response, ex);

        if (response != null) {
            service.performTokenRequest(response.createTokenExchangeRequest(), (response1, ex1) -> {
                    authState.update(response1, ex1);
                    callback.onComplete();
            });
        }
    }

    public interface TokenResponseCallback {
        void onComplete();
    }

}
