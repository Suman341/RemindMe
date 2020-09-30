package com.reminder.remindme.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.jraska.livedata.TestObserver;
import com.reminder.remindme.data.UserStatus;
import com.reminder.remindme.data.model.Response;
import com.reminder.remindme.data.source.CouchBaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;

import static com.reminder.remindme.viewmodel.UserViewModel.USER_PROFILE_KEY;
import static com.reminder.remindme.viewmodel.UserViewModel.USER_STATUS_KEY;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class UserViewModelTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public TestRule testRule = new InstantTaskExecutorRule();

    public Database database;

    public CouchBaseDatabase databaseWrapper;


    public UserViewModel userViewModel;

    @Before
    public void setUp() throws Exception {
        database = Mockito.mock(Database.class);
        databaseWrapper = Mockito.mock(CouchBaseDatabase.class);
        Mockito.when(databaseWrapper.getDatabase()).thenReturn(database);

        userViewModel = new UserViewModel(databaseWrapper);
    }

    @Test
    public void testLogin() throws CouchbaseLiteException, InterruptedException {
        String email = "jane.doe@example.com";
        String password = "jane.doe";

        //arrange
        HashMap<String, Object> profileMap = new HashMap<String, Object>();
        profileMap.put("password", password);

        Document userProfileDocument = Mockito.mock(Document.class);
        Mockito.when(userProfileDocument.getProperties()).thenReturn(profileMap);
        Document loggedInStatusDocument = Mockito.mock(Document.class);
        Mockito.when(database.getExistingDocument(USER_PROFILE_KEY + email)).thenReturn(userProfileDocument);
        Mockito.when(database.getExistingDocument(USER_STATUS_KEY)).thenReturn(loggedInStatusDocument);


        // act
        LiveData<Response<Boolean>> loginObserver = userViewModel.login(email, password);

        // Verify
        Mockito.verify(loggedInStatusDocument).putProperties(Mockito.anyMap());

        TestObserver.test(loginObserver)
                .awaitValue()
                .assertHasValue()
                .assertValue(state -> state.getData() == Boolean.TRUE);
    }

    @Test
    public void testCheckAuthentication() throws InterruptedException {
        //arrange
        HashMap<String, Object> profileMap = new HashMap<String, Object>();
        profileMap.put(USER_STATUS_KEY, UserStatus.AUTHENTICATED.value);

        Document loggedInStatusDocument = Mockito.mock(Document.class);
        Mockito.when(loggedInStatusDocument.getProperties()).thenReturn(profileMap);
        Mockito.when(database.getExistingDocument(USER_STATUS_KEY)).thenReturn(loggedInStatusDocument);


        // act
        LiveData<Response<UserStatus>> loginObserver = userViewModel.checkAuthentication();

        // Verify
        TestObserver.test(loginObserver)
                .awaitValue()
                .assertHasValue()
                .assertValue(state -> state.getData() == UserStatus.AUTHENTICATED);
    }

    @Test
    public void testLogout() throws CouchbaseLiteException {
        //arrange
        Document loggedInStatusDocument = Mockito.mock(Document.class);
        Mockito.when(database.getExistingDocument(USER_STATUS_KEY)).thenReturn(loggedInStatusDocument);

        // act
        userViewModel.logout();

        // Verify
        Mockito.verify(loggedInStatusDocument).delete();
    }

    @After
    public void tearDown() throws Exception {
        database = null;
        databaseWrapper = null;
    }
}