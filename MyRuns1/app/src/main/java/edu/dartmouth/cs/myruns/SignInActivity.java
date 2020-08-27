package edu.dartmouth.cs.myruns;

// Import Statements
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;


/**
 * Activity for signing in to the MyRuns App or moving into creating a profile before you login.
 *
 * @author Sudharsan Balasubramni, Dartmouth CS65. Spring 2020
 * @author Maria Roodnitsky, Dartmouth CS65, Spring2020
 * Parts inspired and adapted from projects from Andrew Campbell
 */
public class SignInActivity extends AppCompatActivity {


    // Request Codes for profile profile activity, or login activity
    private static final int REQUEST_PROFILE = 01;
    private static final int REQUEST_LOGIN = 03;
    // XML objects and shared preferences to grab data from SharedPreferences stored from registration
    private VideoView mVideoView;
    private Button mSignUpButton;
    private Button mLoginButton;
    private EditText mEmailView;
    private EditText mPasswordView;
    private SharedPreferences mSharedPreferences;

    /**
     * Makes sure to resume video when onResume is called
     */
    @Override
    protected void onResume() {
        super.onResume();
        // R e s u m e s Dartmouth
        mVideoView.start();
    }

    /**
     * Overwritten to start video, instantiate our xml objects and shared preferences
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // sets the flags so that video plays full screen and notification bar is hidden
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);

        // Shared Preferences
        mSharedPreferences = getSharedPreferences("ProfileData", MODE_PRIVATE);

        // Call and find variables by ID
        mVideoView = (VideoView)findViewById(R.id.DartmouthVideo);
        mSignUpButton = findViewById(R.id.signup);
        mLoginButton = findViewById(R.id.login);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

        // Starts video of dartmouth to play in the background
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dartmouthdrone);
        mVideoView.setVideoURI(uri);
        mVideoView.start();

        // Scales and starts video
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0,0);
                mp.setLooping(true);

                float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                float screenRatio = mVideoView.getWidth() / (float) mVideoView.getHeight();
                float scaleX = videoRatio / screenRatio;
                if (scaleX >= 1f) {
                    mVideoView.setScaleX(scaleX);
                } else {
                    mVideoView.setScaleY(1f / scaleX);
                }
            }
        });

        // Onclick listener for the Sign Up Button
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Used to start the profile activity class
                Intent intent = new Intent(SignInActivity.this, ProfileActivity.class);
                startActivityForResult(intent, REQUEST_PROFILE);
            }
        });

        // OnClick listener for the Login Button -- will check if email and password have been filled out and are correct
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                boolean emailBool= false;
                boolean passwordBool = false;

                // Checking if fields are covered; if not, make them enter if they are clicking login.
                emailBool = errorCheck(mEmailView, email);
                passwordBool = errorCheck(mPasswordView, password);

                // Now make sure if that they are filled in, that they are the ones stored in sharedprefs
                if (!emailBool && !passwordBool) {
                    if (!mSharedPreferences.getString(ProfileActivity.PROFILE_EMAIL, "").equals(email)) {
                        mEmailView.setError(getString(R.string.error_invalid_email));
                    } else if (!mSharedPreferences.getString(ProfileActivity.PROFILE_PASSWORD, "").equals(password)) {
                        mPasswordView.setError(getString(R.string.error_invalid_password));
                    }
                    else {
                        // If so, to the m a i n activity we go :)
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivityForResult(intent, REQUEST_LOGIN);
                    }
                }
            }
        });
    }


    /**
     * Makes sure that no one logs in with e m p t y fields... b a d user. Focuses on the field if not filled correctly
     * @param mField Email or password field
     * @param fieldText The actual text in that field
     * @return returns a boolean that lets onCreate know whether fields were entered correctly
     */
    public boolean errorCheck(EditText mField, String fieldText) {
        // View to focus the view on the specific error
        // Adapted from Andrew Campbell
        View focusView = null;
        boolean cancel = false;

        mField.setError(null);

        if (TextUtils.isEmpty(fieldText)) {
            mField.setError(getString(R.string.error_field_required));
            focusView = mField;
            cancel = true;
        }
        if (cancel) {
            if( focusView instanceof EditText) {
                focusView.requestFocus();
            }
        }
        return cancel;
    }

}
