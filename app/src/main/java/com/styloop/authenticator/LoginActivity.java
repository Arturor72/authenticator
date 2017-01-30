package com.styloop.authenticator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 9001;

    private static  String TAG=LoginActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        facebookLogin();
        googleLogin();
    }


    public void facebookLogin(){

        callbackManager=CallbackManager.Factory.create();
        loginButton=(LoginButton) findViewById(R.id.login_button);
        registerCallback();
    }

    private void registerCallback(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                goMainScreen();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancel Login", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error Login", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goMainScreen() {
        Intent intentGotoActivityMain=new Intent(this,MainActivity.class);
        intentGotoActivityMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentGotoActivityMain);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInGoogle(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInGoogle(GoogleSignInResult result) {

        Log.d(TAG, "handleSignInGoogle: "+ result.isSuccess());

        if (result.isSuccess()){
            GoogleSignInAccount googleSignInAccount=result.getSignInAccount();
            Intent goMain=new Intent(this, MainActivity.class);
            startActivity(goMain);
        }else{
            Log.d(TAG, "handleSignInGoogle: Error");
        }

    }


    public void googleLogin(){
        googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        SignInButton signInButton=(SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }


    @Override
    public void onClick(View v) {
        switch ( v.getId()){
            case R.id.sign_in_button:
                signInGoogle();
                break;
        }


    }

    private void signInGoogle() {
        Log.d(TAG, "SignInGoogle: ");
        Intent signIntent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signIntent,RC_SIGN_IN);
    }




}
