package com.reminder.remindme.ui.auth;

import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.reminder.remindme.R;
import com.reminder.remindme.data.model.User;
import com.reminder.remindme.di.injector.Injectable;
import com.reminder.remindme.viewmodel.UserViewModel;

import java.util.Calendar;

import javax.inject.Inject;

public class SignUpFragment extends Fragment implements Injectable, View.OnClickListener {

    Button mSignUBtn;
    TextView mLoginPageBack;
    ProgressDialog mDialog;

    @Inject
    ViewModelProvider.Factory viewModeFactory;

    private UserViewModel userViewModel;

    private EditText edtFullName, edtAddress, edtPhone, edtEmail, edtPassword, edtReEnterPassword;
    private RadioButton rBtnAmbulance;
    private RadioButton rBtnPatient;
    private RadioGroup userTypeRadioGroup;
    private Toolbar toolbar;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userViewModel = ViewModelProviders.of(this, viewModeFactory).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        findView(view);
        init();
        return view;
    }

    private void findView(View view) {
        edtFullName = (EditText) view.findViewById(R.id.edtName);
        edtAddress = (EditText) view.findViewById(R.id.edtAddress);
        edtPhone = (EditText) view.findViewById(R.id.edtPhone);
        edtEmail = (EditText) view.findViewById(R.id.edtEmail);
        edtPassword = (EditText) view.findViewById(R.id.edtPassword);
        edtReEnterPassword = (EditText) view.findViewById(R.id.edtReEnterPassword);

        mSignUBtn = view.findViewById(R.id.btnSignUp);
        mLoginPageBack = (TextView) view.findViewById(R.id.linkLogin);
    }

    private void init() {
        mSignUBtn.setOnClickListener(this);
        mLoginPageBack.setOnClickListener(this);
        mDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                registerUser();
                break;
            case R.id.linkLogin:
                navigateToLogin();
                break;
        }
    }

    private void navigateToLogin() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.authContainer, new LoginFragment())
                .commitAllowingStateLoss();

    }

    /**
     * This method tries to register new user to the database
     */
    private void registerUser() {
        String fullName = edtFullName.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String rePassword = edtReEnterPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(getContext(), "Enter Name", Toast.LENGTH_SHORT).show(); return;
        } else if (TextUtils.isEmpty(address)) {
            Toast.makeText(getContext(), "Enter Address", Toast.LENGTH_SHORT).show(); return;
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), "Enter Phone", Toast.LENGTH_SHORT).show(); return;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Enter Email", Toast.LENGTH_SHORT).show(); return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Enter Password", Toast.LENGTH_SHORT).show(); return;
        } else if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be greater then 6 digit", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(rePassword)) {
            Toast.makeText(getContext(), "Confirm password did not match", Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog.setMessage("Creating User please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        userViewModel.signUp(buildNewUser()).observe(this, response -> {
            if (response == null) return;
            if (response.isSuccessful()) {
                mDialog.dismiss();
                Toast.makeText(getContext(), "User account successfully created!", Toast.LENGTH_SHORT).show();
                navigateToLogin();
            } else {
                mDialog.dismiss();
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private User buildNewUser() {
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        User user = new User();
        user.setFullName(fullName);
        user.setMobileNumber(phone);
        user.setEmail(email);
        user.setAddress(address);
        user.setPassword(password);
        user.setCreatedAt(Calendar.getInstance().getTimeInMillis());

        return user;
    }

}