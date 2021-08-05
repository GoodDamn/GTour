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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import good.gda.appp.R;

public class InformationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_information,container,false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final TextView tv_nickname = v.findViewById(R.id.information_textView_nickname),
            tv_currentLevel = v.findViewById(R.id.information_textView_currentLevel),
            tv_currentExp = v.findViewById(R.id.information_textView_currentEXP),
            tv_remainingExp = v.findViewById(R.id.information_textView_remainingEXP),
            tv_nextLevel = v.findViewById(R.id.information_textView_nextLevel);

        final ProgressBar pb_exp = v.findViewById(R.id.information_progressBar_exp);

        tv_nickname.setText(firebaseUser.getDisplayName());

        FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DataSnapshot expChild = dataSnapshot.child("exp");
                        if (expChild.exists())
                        {
                            int exp = Integer.parseInt(expChild.getValue().toString());
                            int curlevel = exp/100;
                            int curExp = exp % 100;
                            tv_currentLevel.setText(String.valueOf(curlevel));
                            tv_nextLevel.setText(String.valueOf(curlevel+1));
                            pb_exp.setProgress(curExp);
                            tv_currentExp.setText(String.valueOf(curExp));
                            tv_remainingExp.setText(String.valueOf(100-curExp));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return v;
    }
}
