package com.example.questmaster.ui.start.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.example.questmaster.R;

import java.util.regex.*;
import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {



    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText inputEmail = view.findViewById(R.id.textInputEmail);
                TextInputEditText inputPassword = view.findViewById(R.id.textInputPassword);

                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                if (isEmailOk(email)) {
                    if (isPasswordOk(password)) {
                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeActivity);
                    } else {
                        inputPassword.setError(getString(R.string.check_password));
                        Snackbar.make(view, R.string.check_password, Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    inputEmail.setError(getString(R.string.check_email));
                    Snackbar.make(view, R.string.check_email, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    boolean isEmailOk(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    boolean isPasswordOk(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{7,20}$";
        Pattern p = Pattern.compile(regex);

        // If the password is empty
        // return false
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }
}