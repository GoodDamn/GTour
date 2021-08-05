package good.gda.appp.fragments;

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

import good.gda.appp.R;

public class InformationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_information,container,false);
        ((TextView)  v.findViewById(R.id.information_textView_nickname))
                .setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        ((ProgressBar) v.findViewById(R.id.information_progressBar_exp)).setProgress(50);
        return v;
    }
}
