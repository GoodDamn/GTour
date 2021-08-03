package good.gda.appp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText Email_User, Password_User, Nickname_User;
    private Button Register_Button, Button_Back;
    private ProgressBar progBar;
    private FirebaseAuth fbAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        Button_Back = findViewById(R.id.Button_Register_Back);
        Register_Button = findViewById(R.id.Button_Register);
        Email_User = findViewById(R.id.TextBox_Register_Email);
        Password_User = findViewById(R.id.TextBox_Register_Password);
        Nickname_User = findViewById(R.id.TextBox_Register_Nickname);
        progBar = findViewById(R.id.ProgBar_Register);

        progBar.setVisibility(View.INVISIBLE);

        fbAuth = FirebaseAuth.getInstance();

        Button_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login_Activity.class));
            }
        });

        Register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progBar.setVisibility(View.VISIBLE);
                Register_Button.setVisibility(View.INVISIBLE);
                final String email = Email_User.getText().toString();
                final String password = Password_User.getText().toString();
                final String nick = Nickname_User.getText().toString();

                if (email.isEmpty() || password.isEmpty() || nick.isEmpty())
                {
                    Message("Not all field filled");
                    progBar.setVisibility(View.INVISIBLE);
                    Register_Button.setVisibility(View.VISIBLE);
                }
                else if (password.length() < 6)
                {
                    Message("Password should be more 6 symbols.");
                    progBar.setVisibility(View.INVISIBLE);
                    Register_Button.setVisibility(View.VISIBLE);
                }
                else
                {
                    fbAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        Message("Account created!");
                                        UserProfile();
                                    }
                                    else
                                    {
                                        Message("Error has happen.");
                                        progBar.setVisibility(View.INVISIBLE);
                                        Register_Button.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }


    private void Message(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void UserProfile()
    {
        FirebaseUser user = fbAuth.getCurrentUser();
        if (user != null)
        {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(Nickname_User.getText().toString()).build();

            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Message("Register completed.");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }
            });
        }
    }

}
