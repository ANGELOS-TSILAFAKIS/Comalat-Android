package org.sakaiproject.api.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.sakaiproject.api.cryptography.PasswordEncryption;
import org.sakaiproject.api.internet.NetWork;
import org.sakaiproject.sakai.LoginFragment;
import org.sakaiproject.sakai.R;
import org.sakaiproject.sakai.UserActivity;

/**
 * Created by vasilis on 10/19/15.
 */
public class LoginAsync extends AsyncTask<Void, Void, LoginType> {

    private String username, password, url;

    private OnlineLogin onlineOnlineLogin;
    private OfflineLogin offlineOfflineLogin;


    private Context context;
    private Activity activity;
    private Fragment fragment = null;

    private ProgressBar progressBar;
    private EditText usernameEditText, passwordEditText;

    public LoginAsync(Fragment fragment, String url, String username, String password) {
        this.fragment = fragment;
        context = this.fragment.getContext();
        activity = this.fragment.getActivity();
        this.url = url;
        this.username = username;
        this.password = password;

        if (fragment != null && fragment instanceof LoginFragment) {
            progressBar = (ProgressBar) activity.findViewById(R.id.loginProgressBar);
            usernameEditText = (EditText) activity.findViewById(R.id.loginUsername_EditText);
            passwordEditText = (EditText) activity.findViewById(R.id.loginPassword_EditText);
        }
    }

    private void clearEditTexts() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        usernameEditText.requestFocus();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (fragment != null && fragment instanceof LoginFragment)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected LoginType doInBackground(Void... params) {

        onlineOnlineLogin = new OnlineLogin(context);
        offlineOfflineLogin = new OfflineLogin(context);

        if (NetWork.getConnectionEstablished()) {
            return onlineOnlineLogin.login(url, username, password);
        }
        return offlineOfflineLogin.login(username, password);
    }

    @Override
    protected void onPostExecute(LoginType type) {
        super.onPostExecute(type);


        if (fragment != null && fragment instanceof LoginFragment)
            progressBar.setVisibility(View.GONE);

        if (type == LoginType.LOGIN_WITH_INTERNET) /* connection completed with internet */ {

            PasswordEncryption passwordEncryption = new PasswordEncryption();

            SharedPreferences.Editor editor = context.getSharedPreferences("user_data", context.MODE_PRIVATE).edit();
            editor.putString("user_id", username);
            editor.putString("password", passwordEncryption.encrypt(password));
            editor.commit();

        } else if (type == LoginType.LOGIN_WITHOUT_INTERNET) /* connection completed without internet */ {

        } else if (type == LoginType.FIRST_TIME_LOGIN_WITHOUT_INTERNET) /* if the user tries to login without internet connection for the first time */ {
            Toast.makeText(context, "You have never login again!\nTo login without internet\nyou have to access at least\none time with internet connection", Toast.LENGTH_LONG).show();
            if (fragment != null && fragment instanceof LoginFragment)
                clearEditTexts();
            return;
        } else if (type == LoginType.SOCKETEXCEPTION) {
            Toast.makeText(context, "Error on the connection with server.\nTry later", Toast.LENGTH_LONG).show();
        } else /* connection failed */ {
            Toast.makeText(context, "Invalid login", Toast.LENGTH_SHORT).show();
            if (fragment != null && fragment instanceof LoginFragment)
                clearEditTexts();
            return;
        }


        if (fragment != null && fragment instanceof LoginFragment)
            clearEditTexts();

        Intent i = new Intent(context, UserActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        activity.finish();

    }


}