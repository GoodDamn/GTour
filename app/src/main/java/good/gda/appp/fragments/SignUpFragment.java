package good.gda.appp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import good.gda.appp.activities.LoginActivity;
import good.gda.appp.activities.MainActivity;
import good.gda.appp.R;
import good.gda.appp.other.Constants;

public class SignUpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        final Button b_signUp = v.findViewById(R.id.Button_Register);
        final EditText email_User = v.findViewById(R.id.TextBox_Register_Email),
            password_User = v.findViewById(R.id.TextBox_Register_Password),
            nickname_User = v.findViewById(R.id.TextBox_Register_Nickname);

        TextView tv_login = v.findViewById(R.id.sign_up_textView_login);
        SpannableString spannableString = new SpannableString(tv_login.getText());
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ((LoginActivity) getActivity()).viewPager.setCurrentItem(1);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#8E97FD"));
                ds.setUnderlineText(false);
            }
        }, spannableString.toString().indexOf('?') + 2, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_login.setText(spannableString);
        tv_login.setMovementMethod(LinkMovementMethod.getInstance());

        b_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_signUp.setEnabled(false);
                final String email = email_User.getText().toString(),
                     password = password_User.getText().toString(),
                     nick = nickname_User.getText().toString();

                if (!email.isEmpty() && !password.isEmpty() && !nick.isEmpty())
                {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            if (user != null) {
                                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(nickname_User.getText().toString()).build();

                                                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            startActivity(new Intent(getContext(), MainActivity.class));
                                                            getActivity().finish();
                                                        }
                                                    }
                                                });
                                            }
                                        } else b_signUp.setEnabled(true);
                                    }
                                });
                    } else Constants.showMessage(getContext(), getString(R.string.invalid_email_address));
                } else Constants.showMessage(getContext(), getString(R.string.not_all_fields_are_filled));
            }
        });
        return v;
    }
}
