package com.styloop.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static  String TAG=MainActivity.class.getSimpleName();

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;

    private String from;
    private String token;

    private boolean isLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareGoogleLogout();
//        AuthenticatorApp app=(AuthenticatorApp)getApplicationContext();
//        from=app.getFrom_Login();
        from=getIntent().getStringExtra("from");

    }


    @Override
    protected void onStart() {
        super.onStart();


        if(from==null){
            AuthenticatorApp app=(AuthenticatorApp)getApplicationContext();
        }
        validateSession();
        Toast.makeText(MainActivity.this, from, Toast.LENGTH_SHORT).show();
    }

    private void validateSession() {
        if (from==null){
            gotoLoginScreen();
        }
        if(getString(R.string.from_facebok).equals(from)){
            if (AccessToken.getCurrentAccessToken()==null){
                gotoLoginScreen();

            }
        }else if(getString(R.string.from_google).equals(from)){
            verifyLogin();
        }
    }

    private void verifyLogin() {
        OptionalPendingResult<GoogleSignInResult> optionResult=Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(optionResult.isDone()){
            GoogleSignInResult result=optionResult.get();
            handleResult(result);
        }else{
            optionResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleResult(googleSignInResult);
                }
            });

        }
    }

    private void handleResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            Toast.makeText(MainActivity.this, "Resultado Exitoso", Toast.LENGTH_SHORT).show();
        }else{
            gotoLoginScreen();
        }

    }


    private void gotoLoginScreen() {
        Intent intentLoginScreen=new Intent(getApplicationContext(),LoginActivity.class);
        intentLoginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        isLogout=true;
        startActivity(intentLoginScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void prepareGoogleLogout(){

        googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).
                enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }


    public void logout_facebook(View view) {
        Toast.makeText(MainActivity.this, "Facebook logout", Toast.LENGTH_SHORT).show();
        if(getString(R.string.from_facebok).equals(from)){
            if (AccessToken.getCurrentAccessToken()!=null){
                LoginManager.getInstance().logOut();
                gotoLoginScreen();
            }
        }
    }

    public void logout_google(View view) {
        if(getString(R.string.from_google).equals(from)){
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if(status.isSuccess()){
                     gotoLoginScreen();
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    private void signOutGoogle() {
        prepareGoogleLogout();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Log.d(TAG, "onResult: Disconected" + status.isSuccess());
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: "+from);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + from);
        if (!isLogout){
        if(getString(R.string.from_google).equals(from)){
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if(status.isSuccess()){
                        gotoLoginScreen();
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        if(getString(R.string.from_facebok).equals(from)){
            if (AccessToken.getCurrentAccessToken()!=null){
                LoginManager.getInstance().logOut();
                gotoLoginScreen();
            }
        }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: "+from);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: "+from);
    }
}
