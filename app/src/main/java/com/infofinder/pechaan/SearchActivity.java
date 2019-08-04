package com.infofinder.pechaan;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.infofinder.pechaan.models.ContactResultModel;
import com.infofinder.pechaan.models.SignupModel;
import com.infofinder.pechaan.services.HttpService;
import com.infofinder.pechaan.services.SearchResultAdapter;
import com.infofinder.pechaan.services.ToastService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ShareActionProvider shareActionProvider;
    private static String WHATSAPP_PACKAGE = "com.whatsapp";
    private SignupModel signupModel;
    private ToastService toastService;
    private EditText searchNumberField;
    private HttpService httpService;
    private Button searchButton;
    private RecyclerView recyclerView;
    private SearchResultAdapter searchResultAdapter;
    private List<ContactResultModel> contactResultModels = new LinkedList<>();
    private Gson gson = new Gson();
    private static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 1;
    private boolean permissionForCall = false;
    private UserSignedUp userSignedUp = null;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.permissionForCall = true;
                } else {
                    toastService.showLongToast("Please give Call permission to allow calls");
                }
                return;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupModel = (SignupModel)getIntent().getSerializableExtra(Constants.USER);
        setContentView(R.layout.activity_search);
        searchNumberField = findViewById(R.id.searchField);
        toastService = new ToastService(this);
        httpService = new HttpService(this);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchNumberFieldText = searchNumberField.getText().toString();
                searchCommonContact(searchNumberFieldText);
//                hideKeyboard(this);
            }
        });

        searchResultAdapter = new SearchResultAdapter(contactResultModels, this);
        recyclerView = findViewById(R.id.search_recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(searchResultAdapter);
//        shareApp();
    }

    private void setShareIntent(Intent shareIntent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }
    private String getTheAppLink(){
        return "https://play.google.com/store/apps/details?id=com.infofinder.pechaan";
    }
    private String generateShareText(){
        return "Hi, Please install the InfoFinder app to access the common references. The app link is " + getTheAppLink();
    }

//    If there is no whatsapp I will have to choose the sms
//    This will work for now, I will come later to solve this
//    https://wa.me/whatsappphonenumber/?text=urlencodedtext

    private String getNumber(){
        String number = this.searchNumberField.getText().toString();
        if ( number.startsWith("+91")){
            return number;
        }
        if (number.startsWith("91")){
            return "+" + number;
        }
        return "+91" + number;
    }

    private Uri createWhatsAppUrl(String text){
        String encodedText = "Please%20install%20the%20app.";
        try {
            encodedText = URLEncoder.encode(text,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "https://wa.me/" + getNumber() + "/?text=" + encodedText;
        return Uri.parse(url);
    }

    private void shareApp(){
        Intent sendIntent;
        String intentTitle = "Invite People";
//      Share this part of the sendIntent, to check if it will work or not
        if (this.appInstalledOrNot(WHATSAPP_PACKAGE)){
            sendIntent = new Intent("android.intent.action.VIEW",
                    this.createWhatsAppUrl(this.generateShareText())
            );
        }
        else {
//          add code for sending the message to a particular number
            sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "The Person with the contact "+ searchNumberField.getText().toString() + " is not on InfoFinder App. Please invite the person.");
            sendIntent.setType("text/plain");
            sendIntent = Intent.createChooser(sendIntent,intentTitle);
        }
        startActivity(sendIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        shareActionProvider = (ShareActionProvider) item.getActionProvider();
        return true;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private List<ContactResultModel> parseSearchResponse(JSONArray response){
        if (response == null){
            toastService.showLongToast(Constants.SEARCH_RESULT_CAN_NOT_NULL);
        }
        List<ContactResultModel> contactResultModels = new LinkedList<>();
        try {
//            String sourceNumber = response.getString(Constants.SOURCE_NUMBER);
//            String sourceName = response.getString(Constants.SOURCE_NAME);
//            String destinationNumber = response.getString(Constants.DESTINATION_NUMBER);
//            String destinationName = response.getString(Constants.DESTINATION_NAME);
            Type type = new TypeToken<List<ContactResultModel>>(){}.getType();
            String jsonContactResultModels = response.toString();
//            String jsonEdges = response.getString(Constants.EDGES);
            contactResultModels = gson.fromJson( jsonContactResultModels, type);
//            contactResultModel = new ContactResultModel(sourceNumber, sourceName, destinationNumber, destinationName, edges);

            searchResultAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            toastService.showLongToast(Constants.ERROR_PARSING_SEARCH_RESULT);
        }
//        populateSearchFragment(contactResultModel);
//        ((LinkedList<ContactResultModel>) contactResultModels).push(contactResultModel);
        return contactResultModels;
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

    private void populateSearchFragment(ContactResultModel contactResultModel) {
        if ( contactResultModel == null){
            toastService.showShortToast(Constants.ERROR_PARSING_SEARCH_RESULT);
        }
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isResultPresent(List<ContactResultModel> contactResultModels){
        if(contactResultModels == null)
            return false;
        if(contactResultModels.get(0).getEdges().size() == 0){
            return false;
        }
        return true;
    }

    private void processSearchRequest(String number) {
        httpService.makeGet(Constants.URL + "/search" + "?source=" + signupModel.getNumber() + "&destination=" + number, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        contactResultModels.clear();
                        try {
                            List<ContactResultModel> contactResultModelsUtil = parseSearchResponse(response.getJSONArray("results"));
                            if ( !isResultPresent(contactResultModelsUtil)){
                                shareApp();
                            }
                            else {
                                contactResultModels.addAll(
                                        contactResultModelsUtil
                                );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        searchResultAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                });
    }

    private void searchCommonContact(final String number) {
        hideKeyboard(this);
        if (number == null) {
            toastService.showLongToast(Constants.PHONE_CAN_NOT_NULL);
            return;
        }
        if(signupModel == null){
            toastService.showLongToast("Please Signout and login again.");
            startMainActivity();
            return;
        }
        httpService.makeGet(Constants.URL + "/signup" + "?number=" +  number, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         userSignedUp = null;
                        try {
                            userSignedUp = new UserSignedUp(response.getString("number"),
                                    response.getBoolean("signedUp"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(userSignedUp == null){
                            shareApp();
//                           toastService.showLongToast("Unable to detec");
                           return;
                        }
                        processSearchRequest(number);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
    }
}

class UserSignedUp{

    private String number;
    private boolean signedUp;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isSignedUp() {
        return signedUp;
    }

    public void setSignedUp(boolean signedUp) {
        this.signedUp = signedUp;
    }

    public UserSignedUp() {
    }

    public UserSignedUp(String number, boolean signedUp) {
        this.number = number;
        this.signedUp = signedUp;
    }
}