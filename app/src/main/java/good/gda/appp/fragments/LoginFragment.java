package good.gda.appp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import good.gda.appp.activities.MainActivity;
import good.gda.appp.R;
import good.gda.appp.other.Constants;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText email_login = v.findViewById(R.id.TextBox_Login_Email),
            password_login = v.findViewById(R.id.TextBox_Login_Password);
        final Button login_Button = v.findViewById(R.id.Button_Login);

        login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_Button.setEnabled(false);
                final String email = email_login.getText().toString(),
                        password = password_login.getText().toString();
                if (!email.isEmpty() && !password.isEmpty())
                {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful())
                                        {
                                            getActivity().finish();
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                        }
                                        else login_Button.setEnabled(true);
                                    }
                                });
                    } else Constants.showMessage(getContext(), getString(R.string.invalid_email_address));
                } else Constants.showMessage(getContext(), getString(R.string.not_all_fields_are_filled));
            }
        });

        return v;
    }
}
