package good.gda.appp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InformationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_information,container,false);
        TextView user_Nickname = v.findViewById(R.id.Nickname_Info_Label);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user_Nickname.setText(user.getDisplayName());
        ProgressBar progressBar = v.findViewById(R.id.ProgressBar_Experience);
        progressBar.setProgress(50);
        return v;
    }
}
