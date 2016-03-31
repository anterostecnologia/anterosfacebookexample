package br.com.anteros.facebook.example;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

import java.util.List;

import br.com.anteros.social.facebook.AnterosFacebook;
import br.com.anteros.social.facebook.actions.Permission;
import br.com.anteros.social.facebook.entities.FacebookProfile;
import br.com.anteros.social.facebook.listeners.OnLoginFacebookListener;
import br.com.anteros.social.facebook.listeners.OnLogoutFacebookListener;
import br.com.anteros.social.facebook.listeners.OnProfileFacebookListener;
import br.com.anteros.social.facebook.utils.Attributes;
import br.com.anteros.social.facebook.utils.FacebookUtils;
import br.com.anteros.social.facebook.utils.PictureAttributes;

/**
 * Activity to demonstrate basic retrieval of the facebook user's ID, email address, and basic
 * profile.
 */
public class MainActivity extends ActionBarActivity implements
        View.OnClickListener, OnLoginFacebookListener, OnLogoutFacebookListener {

    private TextView status;
    private AnterosFacebook anterosFacebook;
    private ImageView userPhoto;
    private TextView detail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        anterosFacebook = AnterosFacebook.getInstance(this,this,this);

        // Views
        status = (TextView) findViewById(R.id.status);
        userPhoto = (ImageView) findViewById(R.id.user_photo);
        detail = (TextView) findViewById(R.id.detail);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        FacebookUtils.printHashKey(this.getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        anterosFacebook.silentLogin();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        anterosFacebook.onActivityResult(requestCode, resultCode, data);
    }


    private void updateUI(boolean signedIn)  {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            status.setText("Connected");


            PictureAttributes pictureAttributes = Attributes.createPictureAttributes();
            pictureAttributes.setHeight(500);
            pictureAttributes.setWidth(500);
            pictureAttributes.setType(PictureAttributes.PictureType.SQUARE);
            FacebookProfile.Properties properties = new FacebookProfile.Properties.Builder()
                    .add(FacebookProfile.Properties.PICTURE, pictureAttributes)
                    .add(FacebookProfile.Properties.FIRST_NAME)
                    .add(FacebookProfile.Properties.LAST_NAME)
                    .add(FacebookProfile.Properties.AGE_RANGE)
                    .add(FacebookProfile.Properties.BIRTHDAY)
                    .add(FacebookProfile.Properties.EMAIL)
                    .add(FacebookProfile.Properties.GENDER)
                    .add(FacebookProfile.Properties.LINK)
                    .add(FacebookProfile.Properties.MIDDLE_NAME)
                    .build();


            anterosFacebook.getProfile(properties, new OnProfileFacebookListener(){

                @Override
                public void onThinking() {
                }
                @Override
                public void onException(Throwable throwable) {
                    desconectado();
                }
                @Override
                public void onFail(String reason) {
                    desconectado();
                }

                @Override
                public void onComplete(FacebookProfile response) {
                    detail.setText(response.toString());
                    userPhoto.setImageBitmap(response.getImageBitmap());
                }
            });
        } else {
            desconectado();
        }
    }

    private void desconectado() {
        status.setText("Desconnected");

        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        detail.setText("");
        userPhoto.setImageBitmap(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                anterosFacebook.login();
                break;
            case R.id.sign_out_button:
                anterosFacebook.logout();
                break;
            case R.id.disconnect_button:
                anterosFacebook.revoke();
                break;
        }
    }


    @Override
    public void onLogin(String s, List<Permission> list, List<Permission> list1) {
        updateUI(true);
    }

    @Override
    public void onCancel() {
        updateUI(false);
    }

    @Override
    public void onLogout() {
        updateUI(false);
    }

    @Override
    public void onException(Throwable throwable) {
        updateUI(false);
    }

    @Override
    public void onFail(String s) {
        updateUI(false);
    }
}