package com.uni_project.questmaster.ui.start.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uni_project.questmaster.utils.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.uni_project.questmaster.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputUsername = view.findViewById(R.id.textInputUsername);
        inputEmail = view.findViewById(R.id.textInputEmail);
        inputPassword = view.findViewById(R.id.textInputPassword);
        inputConfirmPassword = view.findViewById(R.id.textInputConfirmPassword);
        Button signupButton = view.findViewById(R.id.signup_button);
        TextView alreadyLogin = view.findViewById(R.id.alreadyLogin);

        alreadyLogin.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_loginFragment);
        });

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

            Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_homeActivity);
            registerUser(email, password, username, view);
        });
    }

    private boolean isUsernameOk(String username) {
        return username != null && !username.trim().isEmpty();
    }

    private boolean isEmailOk(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=S+$).{7,20}$";
        Pattern p = Pattern.compile(regex);
        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches();
    }

    private void registerUser(String email, String password, String username, View view) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> { // task for user creation
                    if (task.isSuccessful()) {
                        // save to Firestore
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            User user = new User(uid, username, email);

                            db.collection("users").document(uid)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Register", "User profile is created for " + uid);
                                        Toast.makeText(getActivity(),"Account successfully created",Toast.LENGTH_SHORT).show();
                                        // Navigate to home after saving data to Firestore
                                        Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_homeActivity);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failed user data to Firestore
                                        Log.w("Register", "Error writing document", e);
                                        Toast.makeText(getActivity(),"Failed to create account",Toast.LENGTH_SHORT).show();
                                        Snackbar.make(view, "Error saving user data.", Snackbar.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        // Error user creation
                        Log.w("Register", "createUserWithEmail:failure", task.getException());
                        Snackbar.make(view, "Authentication failed: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

}
