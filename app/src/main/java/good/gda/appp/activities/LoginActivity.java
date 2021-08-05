package good.gda.appp.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;

import good.gda.appp.R;
import good.gda.appp.fragments.LoginFragment;
import good.gda.appp.fragments.SignUpFragment;
import good.gda.appp.other.PagerAdapter;

public class LoginActivity extends AppCompatActivity {

    public ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewPager = findViewById(R.id.login_viewPager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(),
                new Fragment[]{
                    new SignUpFragment(),
                    new LoginFragment()
        }));

        final TextView tv_welcome = findViewById(R.id.login_textView_welcome),
                tv_welcomeBack = findViewById(R.id.login_textView_welcomeBack);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0)
                {
                    setAlphaScale(tv_welcome,1.0f - positionOffset);
                    setAlphaScale(tv_welcomeBack,  positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) { }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    private void setAlphaScale(TextView textView, float offset)
    {
        textView.setAlpha(offset);
        textView.setScaleX(offset);
        textView.setScaleY(offset);
    }
}
