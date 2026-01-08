package com.example.questmaster.ui.start.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.questmaster.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupFragment extends Fragment {

    private TextInputEditText inputUsername, inputEmail, inputPassword, inputConfirmPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputUsername = view.findViewById(R.id.textInputUsername);
        inputEmail = view.findViewById(R.id.textInputEmail);
        inputPassword = view.findViewById(R.id.textInputPassword);
        inputConfirmPassword = view.findViewById(R.id.textInputConfirmPassword);
        Button signupButton = view.findViewById(R.id.signup_button);

        signupButton.setOnClickListener(v -> {
            String username = inputUsername.getText().toString();
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            String confirmPassword = inputConfirmPassword.getText().toString();

            boolean isUsernameValid = isUsernameOk(username);
            boolean isEmailValid = isEmailOk(email);
            boolean isPasswordValid = isPasswordOk(password);
            boolean passwordsMatch = password.equals(confirmPassword);

            if (!isUsernameValid) {
                inputUsername.setError(getString(R.string.username_is_required));
                Snackbar.make(view, R.string.username_is_required, Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (!isEmailValid) {
                inputEmail.setError(getString(R.string.check_email));
                Snackbar.make(view, R.string.check_email, Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (!isPasswordValid) {
                inputPassword.setError(getString(R.string.check_password));
                Snackbar.make(view, R.string.check_password, Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (!passwordsMatch) {
                inputConfirmPassword.setError(getString(R.string.passwords_do_not_match));
                Snackbar.make(view, R.string.passwords_do_not_match, Snackbar.LENGTH_SHORT).show();
                return;
            }

            //Firebase database

            Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_homeActivity);

        });
    }

    private boolean isUsernameOk(String username) {
        return username != null && !username.trim().isEmpty();
    }

    private boolean isEmailOk(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password) {

        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{7,20}$";
        Pattern p = Pattern.compile(regex);

        if (password == null) {
            return false;
        }

        Matcher m = p.matcher(password);
        return m.matches();
    }
}
