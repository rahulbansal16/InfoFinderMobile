package com.infofinder.pechaan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.infofinder.pechaan.models.ContactModel;
import com.infofinder.pechaan.models.SignupModel;
import com.infofinder.pechaan.services.HttpService;
import com.infofinder.pechaan.services.ToastService;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.view.View.VISIBLE;
import static com.infofinder.pechaan.Constants.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private LoaderManager loaderManager;
    private int LoaderId = 1;
//    private View progressOverLay;
    private ProgressBar progressBar;

    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts.LOOKUP_KEY,
    };

    private String PROJECTION_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
    private String PROJECTION_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private boolean canLoadContacts = false;

    private SignupModel user;
    private EditText nameFeild;
    private EditText phoneField;
    private HttpService httpService;
    private ToastService toastService;
    private Logger logger = Logger.getLogger("MainActivity.java");

    private void setTokenForFCM(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        httpService.makePost(
                            null,
                                null,
                                null,
                                null
                        );
                    }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
//                            msg = getString(R.string.msg_subscribe_failed);
                        }
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        httpService = new HttpService(this);
        toastService = new ToastService(this);

        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(this);
        nameFeild = findViewById(R.id.nameField);
        phoneField = findViewById(R.id.phoneField);
        this.logger.log(Level.INFO,"Starting the Access Contact");
//        progressOverLay = findViewById(R.id.progress_overlay);
//        progressOverLay.bringToFront();
        progressBar = findViewById(R.id.pBar);
        progressBar.setVisibility(View.GONE);
        AccessContact();
//        privacyPolicy();
//        setTokenForFCM();

    }

    public static void hideKeyboard(AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void loadContacts(){
        if ( this.loaderManager == null)
            this.loaderManager = getSupportLoaderManager();
        if ( this.loaderManager.getLoader(this.LoaderId) == null)
            loaderManager.initLoader(LoaderId,null,this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    loadContacts();
                    this.canLoadContacts = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    toastService.showLongToast("Please allow Contacts permission for proper working");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private boolean showDialogForPermission(String permission, int code ){
//        Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this, permission)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        code);

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        code);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
              canLoadContacts = true;
//            loadContacts();
            return true;
        }
        return false;
    }

    private void AccessContact(){
        showDialogForPermission(Manifest.permission.READ_CONTACTS, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    private void startSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
    }

    private void signup(){
        progressBar.setVisibility(VISIBLE);
        hideKeyboard(this);
        SignupModel signupModel = this.getSignupModel();
        JsonObjectRequest jsonObjectRequest = httpService.makePost(
                URL + "/signup",
                signupModel.toJSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        toastService.showShortToast(SUCCESS_SIGNUP);
                        try {
                            user = new SignupModel(
                                    response.getString("name"),
                                    response.getString("number"),
                                    response.getBoolean("selfSigned"));
                            if (canLoadContacts){
                                loadContacts();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startSearchActivity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toastService.showLongToast(ERROR_SIGNUP);
                    }
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(1000000000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean formCheck(){
        if (nameFeild.getText() == null || nameFeild.getText().toString().trim().equals("")){
            toastService.showLongToast("Please Enter your name.");
            return false;
        }
        if (phoneField.getText() == null || phoneField.getText().toString().trim().equals("")){
            toastService.showLongToast("Please Enter your Phone Number.");
            return false;
        }
        if (phoneField.getText().toString().trim().length() != 10){
            toastService.showLongToast("Phone Number should be of 10 length");
            return false;
        }
        return true;
    }

    private SignupModel getSignupModel(){
        if (formCheck())
            return new SignupModel(nameFeild.getText().toString().trim(), phoneField.getText().toString(), true);
        else
            return null;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.signupButton:
                if (formCheck())
                    signup();
                break;
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Loader<Cursor> cursorLoader = new CursorLoader(this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
                );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        ContactModel contactModel = this.getContactModel(cursor);
        try {
            httpService.makePost(URL + "/contacts",
                    contactModel.toJSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            toastService.showLongToast("Contacts Synced");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.loaderManager.destroyLoader(this.LoaderId);
    }

    private String getColumnValue(Cursor cursor, String column){
        int index = cursor.getColumnIndex(column);
        return cursor.getString(index);
    }

    private List<SignupModel> processRawContacts(Cursor cursor){
        List<SignupModel> contacts = new LinkedList<>();
        while (cursor.moveToNext()){
            ((LinkedList<SignupModel>) contacts).push(
                    new SignupModel(
                        this.getColumnValue(cursor,PROJECTION_NAME),
                            this.getColumnValue(cursor,PROJECTION_NUMBER),
                            false)
            );
        }
        return contacts;
    }

    private ContactModel getContactModel(Cursor cursor){
        ContactModel contactModel = new ContactModel();
        contactModel.setUser(user);
        contactModel.setContacts(this.processRawContacts(cursor));
        return contactModel;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    private void privacyPolicy(){
        Button privacyPolicyButton = (Button) this.findViewById(R.id.privacyPolicyButton);
        privacyPolicyButton.setTextSize(18);
        privacyPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
    }
}
