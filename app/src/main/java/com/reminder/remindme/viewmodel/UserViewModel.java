package com.reminder.remindme.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.reminder.remindme.data.UserStatus;
import com.reminder.remindme.data.model.Response;
import com.reminder.remindme.data.model.State;
import com.reminder.remindme.data.model.User;
import com.reminder.remindme.data.source.CouchBaseDatabase;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


public class UserViewModel extends ViewModel {
    private final String TAG = UserViewModel.class.getSimpleName();

    static final String USER_STATUS_KEY = "user_status, s:";
    static final String USER_PROFILE_KEY = "user_profile, p:";

    private CouchBaseDatabase databaseManager;

    @Inject
    public UserViewModel(CouchBaseDatabase databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * @param email    from which user existence will be checked
     * @param password is used to verify the user
     * @return {@link LiveData} contains {@link Response}
     */
    public LiveData<Response<Boolean>> login(String email, String password) {
        MutableLiveData<Response<Boolean>> result = new MutableLiveData<>();
        result.setValue(new Response<>("", State.LOADING, false));
        Database database = databaseManager.getDatabase();

        String profileDocID = USER_PROFILE_KEY + email;
        Document profile = database.getExistingDocument(profileDocID);
        if (profile != null) {
            Map<String, Object> data = profile.getProperties();
            String pw = (String) data.get("password");
            if (pw != null && pw.equals(password)) {
                try {
                    Document loggedInStatus = database.getExistingDocument(USER_STATUS_KEY);
                    Map<String, Object> userData = new HashMap<>();
                    userData.put(USER_STATUS_KEY, UserStatus.AUTHENTICATED.value);
                    userData.put(USER_PROFILE_KEY, profile.getId());
                    if (loggedInStatus == null)
                        loggedInStatus = database.getDocument(USER_STATUS_KEY);
                    loggedInStatus.putProperties(userData);
                    result.setValue(new Response<>("Login successful", State.SUCCESS, true));
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                    result.setValue(new Response<>("Login failed! please try again later.",
                            State.SUCCESS, true));
                }
            } else {
                result.setValue(new Response<>("Invalid login credentials!",
                        State.ERROR, false));
            }
        } else {
            result.setValue(new Response<>("Invalid login credentials!",
                    State.ERROR, false));
        }
        return result;
    }

    /**
     * @param user The unser's information to be saved in database
     * @return {@link LiveData} of {@link Response} which contains the signUp process status
     */
    public LiveData<Response<Boolean>> signUp(User user) {
        MutableLiveData<Response<Boolean>> result = new MutableLiveData<>();
        result.setValue(new Response<>("", State.LOADING, false));

        Database database = databaseManager.getDatabase();
        String profileDocID = USER_PROFILE_KEY + user.getEmail();
        Document profile = database.getExistingDocument(profileDocID);
        if (profile == null) {
            try {
                profile = database.getDocument(profileDocID);
                profile.putProperties(user.toProperties());
                result.setValue(new Response<>("User created successfully!", State.SUCCESS, true));
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                result.setValue(new Response<>("Something went wrong while creating user!", State.ERROR, false));
            }
        } else {
            result.setValue(new Response<>("Email address already exist!", State.ERROR, false));
        }
        return result;
    }

    /**
     * @return {@link LiveData} contains observable {@link Response} with user status
     */
    public LiveData<Response<UserStatus>> checkAuthentication() {
        MutableLiveData<Response<UserStatus>> result = new MutableLiveData<>();
        result.setValue(new Response<>("", State.LOADING, UserStatus.UNKNOWN));

        Document loggedInStatus = databaseManager.getDatabase()
                .getExistingDocument(USER_STATUS_KEY);

        if (loggedInStatus != null) {
            Map<String, Object> data = loggedInStatus.getProperties();
            Object status = data.get(USER_STATUS_KEY);
            if (status != null &&
                    ((int) status) == UserStatus.AUTHENTICATED.value) {
                result.setValue(new Response<>("User session found",
                        State.SUCCESS, UserStatus.AUTHENTICATED));
            } else {
                result.setValue(new Response<>("User session not found",
                        State.ERROR, UserStatus.UNAUTHENTICATED));
            }
        } else {
            result.setValue(new Response<>("User session not found",
                    State.ERROR, UserStatus.UNAUTHENTICATED));
        }
        return result;
    }

    public void logout() {
        Document loggedInStatus = databaseManager.getDatabase().getExistingDocument(USER_STATUS_KEY);
        if (loggedInStatus == null) {
            loggedInStatus = databaseManager.getDatabase().getDocument(USER_STATUS_KEY);
        }

        try {
            loggedInStatus.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }

    public LiveData<Response<User>> profile() {
        MutableLiveData<Response<User>> result = new MutableLiveData<>();
        result.setValue(new Response<>("", State.LOADING, null));

        Document loggedInStatus = databaseManager.getDatabase().getExistingDocument(USER_STATUS_KEY);

        if (loggedInStatus == null) {
            result.setValue(new Response<>("No profile found", State.ERROR, null));
        } else {
            Map<String, Object> properties = loggedInStatus.getProperties();

            String profileDocID = (String) properties.get(USER_PROFILE_KEY);
            Document profileDocument = databaseManager.getDatabase().getExistingDocument(profileDocID);

            if (profileDocument == null) {
                result.setValue(new Response<>("No profile found", State.ERROR, null));
            } else {
                User profile = User.fromProperties(profileDocument.getProperties());
                result.postValue(new Response<>("Success", State.SUCCESS, profile));
            }
        }


        return result;
    }
}
