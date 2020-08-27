package edu.dartmouth.cs.myruns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    // Keys for Shared Preferences
    private static final String URI_KEY = "uri";
    private static final String PROFILE_GENDER = "gender";
    private static final String PROFILE_NAME = "name";
    public static final String PROFILE_EMAIL = "email";
    public static final String PROFILE_PASSWORD = "password";
    private static final String PROFILE_MAJOR = "major" ;
    private static final String PROFILE_PHONE = "phone" ;
    private static final String PROFILE_CLASS = "class";
    private static final String PROFILE_URI = "uri";
    // Request codes for intentets
    private static final int REQUEST_SUBMITTED = 2;
    private static final int REQUEST_TAKE_PHOTO = 4;
    private static final int REQUEST_CHOOSE_PHOTO = 5;

    // xml objects to manipulate and sharedprefs
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedEdit;
    private File mPhoto;
    private String mCurrentPhotoPath;
    private Bitmap rotatedBitmap;

    /**
     * Overwritten to instantiate objects and shared prefs, if there was a savedinstance, set the image uri! Y e e
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // SharedPreferences and editor to add things to shared prefs
        mSharedPreferences = getSharedPreferences("ProfileData", MODE_PRIVATE);
        mSharedEdit = mSharedPreferences.edit();
        mImageView = findViewById(R.id.profile_photo);

        // If there was a saved instance, set the image uri
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState.getParcelable(URI_KEY);
            mImageView.setImageURI(mImageCaptureUri);
        }
    }


    /**
     * When the app is exited or paused, make sure to save the current uri if there is one
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(URI_KEY, mImageCaptureUri);
    }

    /**
     * On Activity result activated when an intent is started and then returns. Can either be
     * take photo, choose photo from gallery, and crop also would have a request code
     * @param requestCode TakePhoto, Gallery, Crop
     * @param resultCode Result_OK
     * @param data Intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ResultCode
        if (resultCode != RESULT_OK) {
            return;
        }

        // Switch Statement Request Code
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                // Orient the photo and save and then start cropping
                Bitmap rotatedBitmap = imageOrient(mPhoto);
                try{
                    FileOutputStream fout = new FileOutputStream(mPhoto);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                    fout.flush();
                    fout.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                startCrop(mImageCaptureUri);
                break;
            case REQUEST_CHOOSE_PHOTO:
                // For gallery, get the imageUri from the intent and then start the crop
                mImageView.setRotation(90);
                mImageCaptureUri = data.getData();
                startCrop(mImageCaptureUri);
                break;
            case Crop.REQUEST_CROP:
                //update crop on the imageview of the profile activity
                updateCrop(resultCode, data);


        }
    }

    /**
     * Changing profile image, gives a dialog fragment that asks to take photo or choose from library
     * @param v View that is acted upon for onClick
     */
    public void onChangePhotoClicked(View v) {
        // First prompt permissions dialog for user
        checkPermissions();
        // Dialog for photo MyRuns Dialog Fragment
        MyRunsDialogFragment fragment = MyRunsDialogFragment.newInstance("photo");
        fragment.show(getSupportFragmentManager(), getString(R.string.profile_photo));
    }


    /**
     * Checks for permissions of external storage as well as camera. Adapted from Andrew Campbell
     */
    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)     {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }


    /**
     * If user denies, asks again upon result. Grant results used to see if user granted permission.
     * Adapted from Andrew Campbell
     * @param requestCode Not used in this case because we are only ever checking one request
     * @param permissions   permissions being requested
     * @param grantResults  granted results array
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                    || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Alert the user that it would be a good idea to set these permissions uo
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.").setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                        }
                    });
                    builder.show();
                }
        }
    }

    /**
     * Method to allow choosing from gallery or using the camera. Called by the MyRunsDialogFragment
     * @param option the option that the user chooses, either take photo or choose photo
     */
    public void takeProfilePhoto(int option)  {
        // Intent to fire
        Intent intent;

        // Depends on gallery/ taking photo
        switch(option) {

            case MyRunsDialogFragment.TAKE_PHOTO:
                // Photo from camera
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // File to store the photo
                try {
                    mPhoto = createImageFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                // Continue if file created successfully
                // Create the photo, store the imageUri
                if (mPhoto != null) {
                    mImageCaptureUri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID,
                            mPhoto);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    // Only run intent if there is a valid fill
                }
                // Start camera for taking photo
                try {
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case MyRunsDialogFragment.CHOOSE_PHOTO:
                // In the case we are choosing photo, MediaStore action pick intent.
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                // File to store the photo
                try {
                    mPhoto = createImageFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                // photo uri and store it in the intent
                if (mPhoto != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID,
                            mPhoto);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }

                // Start activity for choosing photo
                try {
                    startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Creates a temporary image file. Adapted from Campbell and Android Development Documentation
     * @return A file where the photo will be stored
     * @throws IOException if file is not created
     */
    private File createImageFile() throws IOException {
        //time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Method that may or may not get used. Adapted from Andrew Campbell
//    private void storePhoto() {
//        mImageView.buildDrawingCache();
//        Bitmap bitmap = mImageView.getDrawingCache();
//        try {
//            // Internally store photo
//            FileOutputStream fout = openFileOutput(getString(R.string.profilephoto), MODE_PRIVATE);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
//            fout.flush();
//            fout.close();
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
//    }

    /**
     * Starts the cropping process using the soundcloud Cropping API. c r o p c r o p
     * @param source source uri
     */
    private void startCrop(Uri source) {
        if (mPhoto != null) {
            // destination uri, source, and then crop to destination
            Uri destination = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, mPhoto);
            Crop.of(source, destination).asSquare().start(this);
        }
    }

    /**
     * Update the ImageView with the cropped result :)
     * @param resultCode result_ok if returned successfully and not cancelled crop.
     * @param result cropped intent
     */
    private void updateCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mImageCaptureUri = Crop.getOutput(result);
            mImageView.setImageURI(mImageCaptureUri);
        // If not ok, error toast
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to orient the image correctly. Adapted from Andrew Campbell.
     * Props to Professor here, I had no clue on how to do this :) But now I do
     * @param photoFile
     * @return
     */
    private Bitmap imageOrient(File photoFile) {
        ExifInterface ei;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID,
                    photoFile));
            ei = new ExifInterface(photoFile.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            rotatedBitmap = null;

            // Switch orientation based on current orientation
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);

                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);

                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;

                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    /**
     * Rotate image for the orientation method
     * @param source bitmap source
     * @param angle the angle to be rotated by
     * @return
     */
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }




    // Text Fields Validations
    public boolean isEmailValid (String email_attempt) {
        return (!TextUtils.isEmpty(email_attempt) && Patterns.EMAIL_ADDRESS.matcher(email_attempt).matches());
    }

    public boolean isNameValid(String name_attempt){
        return ((!TextUtils.isEmpty(name_attempt)) && name_attempt.matches("^[\\p{L} .'-]+$"));
    }

    public boolean isClassYearValid(String year_attempt_string){
        Integer year_attempt;
        if (TextUtils.isEmpty(year_attempt_string)) return false;
        try { year_attempt = Integer.parseInt(year_attempt_string); }
        catch (NumberFormatException ex) { return false; }
        return ((year_attempt > 1900) && (year_attempt < 2100));
    }

    public boolean isMajorValid(String name_attempt){
        return (!TextUtils.isEmpty(name_attempt) && name_attempt.matches("^[\\p{L} .'-]+$"));
    }

    public boolean isPasswordValid(String password_attempt){
        return (!TextUtils.isEmpty(password_attempt) && password_attempt.length() > 5);
    }

    public boolean isPhoneValid (String phone_attempt) {
        return (!TextUtils.isEmpty(phone_attempt) && Patterns.PHONE.matcher(phone_attempt).matches());
    }


    /**
     * If oncliuck, check if things were filled out and save to sharedprefs if done correctly!
     * @param v
     */
    public void onSubmitClicked(View v) {
        View focusView = null;
        boolean cancel = false;
        int fields = 7;

        // Gender Buttons
        RadioGroup radioGroup = findViewById(R.id.gender);
        RadioButton mLastRadioBtn = findViewById(R.id.nonBinaryButton);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        mLastRadioBtn.setError(null);
        // Name
        EditText mNameView = findViewById(R.id.name);
        String name = mNameView.getText().toString();
        mNameView.setError(null);
        // Email
        EditText mEmailView = findViewById(R.id.emailAddress);
        String email = mEmailView.getText().toString();
        mEmailView.setError(null);
        // Password
        EditText mPasswordView = findViewById(R.id.password);
        String password = mPasswordView.getText().toString();
        mPasswordView.setError(null);
        // Major
        EditText mMajorView = findViewById(R.id.major);
        String major = mMajorView.getText().toString();
        mMajorView.setError(null);
        // Major
        EditText mPhoneView = findViewById(R.id.phoneNumber);
        String phone = mPhoneView.getText().toString();
        mPhoneView.setError(null);
        // Class Year
        EditText mClassYear = findViewById(R.id.classYear);
        String classYear = mClassYear.getText().toString();
        mMajorView.setError(null);

        // Gender
        if (selectedId <= 0) {
            focusView = mLastRadioBtn;
            mLastRadioBtn.setError(getString(R.string.error_gender));
            fields--;
        }
        // Name
        if (!isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            fields--;
        }
        if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            fields--;
        }
        // Password
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            fields--;
        }
        //
        if (!isMajorValid(major)) {
            mMajorView.setError(getString(R.string.error_invalid_major));
            focusView = mMajorView;
            fields--;
        }
        // Phone
        if (!isPhoneValid(phone)) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            fields--;
        }
        // Class Year
        if (!isClassYearValid(classYear)) {
            mClassYear.setError(getString(R.string.error_invalid_class));
            focusView = mClassYear;
            fields--;
        }

        if (fields == 7) {
            //gender
            mSharedEdit.putInt(PROFILE_GENDER, selectedId);
            mSharedEdit.putString(PROFILE_NAME, name);
            mSharedEdit.putString(PROFILE_EMAIL, email);
            mSharedEdit.putString(PROFILE_PASSWORD, password);
            mSharedEdit.putString(PROFILE_MAJOR, major);
            mSharedEdit.putString(PROFILE_PHONE, phone);
            mSharedEdit.putString(PROFILE_CLASS, classYear);
            mSharedEdit.putString(PROFILE_URI, mImageCaptureUri.toString());
            mSharedEdit.commit();

            Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
            startActivityForResult(intent, REQUEST_SUBMITTED);
        }
    }
}
