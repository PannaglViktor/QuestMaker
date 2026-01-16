package com.uni_project.questmaster.ui.start.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;

import com.uni_project.questmaster.R;

import org.apache.commons.validator.routines.EmailValidator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.login_button);
        TextInputEditText inputEmail = view.findViewById(R.id.textInputEmail);
        TextInputEditText inputPassword = view.findViewById(R.id.textInputPassword);

        button.setOnClickListener(v -> {
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
                        // FirebaseUser user = mAuth.getCurrentUser();
                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeActivity);
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                        Snackbar.make(view, "Authentication failed: " + errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                });
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
