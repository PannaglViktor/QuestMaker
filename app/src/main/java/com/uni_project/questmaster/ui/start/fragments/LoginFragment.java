
package com.uni_project.questmaster.ui.start.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.ui.start.viewmodel.LoginViewModel;
import com.uni_project.questmaster.ui.start.viewmodel.LoginViewModelFactory;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private NavController navController;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private LoginViewModel viewModel;

    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginViewModelFactory factory = new LoginViewModelFactory(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

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
                        viewModel.loginWithGoogle(account);
                    } catch (ApiException e) {
                        Log.w(TAG, "Google sign in failed", e);
                        Snackbar.make(requireView(), R.string.google_sign_in_failed, Snackbar.LENGTH_SHORT).show();
                    }
                });

        viewModel.isLoggedIn.observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                navigateToHome();
            }
        });

        viewModel.loginResult.observe(this, authResult -> {
            if (authResult != null) {
                Log.d(TAG, "signIn:success");
                Toast.makeText(getActivity(), "You're logged in", Toast.LENGTH_LONG).show();
                navigateToHome();
            } else {
                Log.w(TAG, "signIn:failure");
                Snackbar.make(requireView(), R.string.authentication_failed, Snackbar.LENGTH_SHORT).show();
            }
        });
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
        Button signupButton = view.findViewById(R.id.signup_button);
        Button googleLoginButton = view.findViewById(R.id.google_login_button);
        inputEmail = view.findViewById(R.id.textInputEmail);
        inputPassword = view.findViewById(R.id.textInputPassword);

        loginButton.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            Log.d(TAG, "Login attempt with email: [" + email + "], password: [" + password + "]");

            if (isEmailOk(email) && isPasswordOk(password)) {
                viewModel.login(email, password);
            } else if (!isEmailOk(email)) {
                inputEmail.setError(getString(R.string.check_email));
                Snackbar.make(view, R.string.check_email, Snackbar.LENGTH_SHORT).show();
            } else {
                inputPassword.setError(getString(R.string.check_password));
                Snackbar.make(view, R.string.check_password, Snackbar.LENGTH_SHORT).show();
            }
        });

        signupButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginFragment_to_signupFragment);
        });

        googleLoginButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
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
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.length() >= 6;
    }
}
