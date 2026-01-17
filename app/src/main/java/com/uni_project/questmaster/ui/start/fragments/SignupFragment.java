package com.uni_project.questmaster.ui.start.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.User;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupFragment extends Fragment {
    private static final String TAG = "SignupFragment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private TextInputEditText inputUsername, inputEmail, inputPassword, inputConfirmPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        Log.w(TAG, "Google sign in failed", e);
                        Snackbar.make(requireView(), R.string.google_sign_in_failed, Snackbar.LENGTH_SHORT).show();
                    }
                });
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
        Button googleSignupButton = view.findViewById(R.id.google_signup_button);
        TextView alreadyLogin = view.findViewById(R.id.alreadyLogin);

        inputPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Snackbar.make(view, R.string.password_requirements, Snackbar.LENGTH_LONG).show();
            }
        });

        alreadyLogin.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_loginFragment);
        });

        signupButton.setOnClickListener(v -> {
            String username = inputUsername.getText().toString();
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            String confirmPassword = inputConfirmPassword.getText().toString();

            if (validateInput(username, email, password, confirmPassword)) {
                registerUser(email, password, username, view);
            }
        });

        googleSignupButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            String email = firebaseUser.getEmail();
                            String username = firebaseUser.getDisplayName();

                            User user = new User(uid, username, email);

                            db.collection("users").document(uid).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User profile created for " + uid);
                                        Toast.makeText(getActivity(), "Account successfully created with Google", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(requireView()).navigate(R.id.action_signupFragment_to_homeActivity);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error writing document", e);
                                        Snackbar.make(requireView(), "Error saving user data.", Snackbar.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(requireView(), R.string.authentication_failed, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String username, String email, String password, String confirmPassword) {
        if (!isUsernameOk(username)) {
            inputUsername.setError(getString(R.string.username_is_required));
            Snackbar.make(requireView(), R.string.username_is_required, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!isEmailOk(email)) {
            inputEmail.setError(getString(R.string.check_email));
            Snackbar.make(requireView(), R.string.check_email, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!isPasswordOk(password)) {
            inputPassword.setError(getString(R.string.check_password));
            Snackbar.make(requireView(), R.string.check_password, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError(getString(R.string.passwords_do_not_match));
            Snackbar.make(requireView(), R.string.passwords_do_not_match, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isUsernameOk(String username) {
        return username != null && !username.trim().isEmpty();
    }

    private boolean isEmailOk(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$";
        Pattern p = Pattern.compile(regex);
        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches() && password.length() >= 6;
    }

    private void registerUser(String email, String password, String username, View view) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            User user = new User(uid, username, email);

                            db.collection("users").document(uid)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User profile is created for " + uid);
                                        Toast.makeText(getActivity(),"Account successfully created",Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_homeActivity);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error writing document", e);
                                        Toast.makeText(getActivity(),"Failed to create account",Toast.LENGTH_SHORT).show();
                                        Snackbar.make(view, "Error saving user data.", Snackbar.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Snackbar.make(view, "Authentication failed: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

}
