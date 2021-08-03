package good.gda.appp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Activity extends AppCompatActivity {

    private EditText email_login, password_login;
    private Button Login_Button, Register_Button;
    private FirebaseAuth fbAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        email_login = findViewById(R.id.TextBox_Login_Email);
        password_login = findViewById(R.id.TextBox_Login_Password);
        Login_Button = findViewById(R.id.Button_Login);
        Register_Button = findViewById(R.id.Button_GoOver_to_Register_Form);
        fbAuth = FirebaseAuth.getInstance();

        final ProgressBar progBar = findViewById(R.id.ProgBar_Login);
        progBar.setVisibility(View.INVISIBLE);

        Register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
            }
        });

        Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progBar.setVisibility(View.VISIBLE);
                Login_Button.setVisibility(View.INVISIBLE);
                final String email = email_login.getText().toString(),
                        password = password_login.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    Message("Verify all fields"); progBar.setVisibility(View.INVISIBLE);
                }

                else if (password.length() < 6) Message("Password should be more 6 symbols.");
                else
                {
                    fbAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                progBar.setVisibility(View.INVISIBLE);
                                Message("You entered as " + fbAuth.getCurrentUser().getDisplayName());
                                Login_Button.setVisibility(View.VISIBLE);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                            else
                            {
                                Message(task.getException().getMessage());
                                progBar.setVisibility(View.INVISIBLE);
                                Login_Button.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }


    private void Message(String text)
    {
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
    }
}
