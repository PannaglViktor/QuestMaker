package com.uni_project.questmaster.ui.start.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.uni_project.questmaster.R;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FirebaseAuth mAuth;
    private NavController navController;

    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToHome();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        Button loginButton = view.findViewById(R.id.login_button);
        inputEmail = view.findViewById(R.id.textInputEmail);
        inputPassword = view.findViewById(R.id.textInputPassword);

        loginButton.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (isEmailOk(email) && isPasswordOk(password)) {
                loginUserWithFirebase(email, password, view);
            } else if (!isEmailOk(email)) {
                inputEmail.setError(getString(R.string.check_email));
                Snackbar.make(view, R.string.check_email, Snackbar.LENGTH_SHORT).show();
            } else {
                inputPassword.setError(getString(R.string.check_password));
                Snackbar.make(view, R.string.check_password, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUserWithFirebase(String email, String password, View view) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(getActivity(), "You're logged in", Toast.LENGTH_LONG).show();
                        navigateToHome();
                    } else {
                       Log.w(TAG, "signInWithEmail:failure", task.getException());
                        handleLoginFailure(task.getException(), view);
                    }
                });
    }

    private void handleLoginFailure(Exception exception, View view) {
        String errorMessage;
        if (exception instanceof FirebaseAuthInvalidUserException) {
            errorMessage = getString(R.string.error_user_not_found);
            inputEmail.setError(errorMessage);
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = getString(R.string.error_invalid_credentials);
            inputPassword.setError(errorMessage);
        } else {
            errorMessage = getString(R.string.authentication_failed);
        }
        Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show();
    }


    private void navigateToHome() {
        if (navController != null) {
            navController.navigate(R.id.action_loginFragment_to_homeActivity);
        }
    }

    boolean isEmailOk(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EmailValidator.getInstance().isValid(email);
    }

    boolean isPasswordOk(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{7,20}$";
        Pattern p = Pattern.compile(regex);

        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
