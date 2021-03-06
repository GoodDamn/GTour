package good.gda.appp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import good.gda.appp.R;

public class SendErrorOrPlaceFragment extends Fragment  implements AdapterView.OnItemSelectedListener{

    private String textSpinner;
    private Button btn_send;
    private EditText textError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_error, container, false);
        btn_send = v.findViewById(R.id.Button_Send_Error);
        textError = v.findViewById(R.id.TextError);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textError.getText().toString();

                if (text.isEmpty())
                    Toast.makeText(getContext(), "Text message is empty.", Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"grigorydum@gmail.com"});
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                    intent.putExtra(Intent.EXTRA_SUBJECT, textSpinner);

                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent, "Choose an email client."));
                }
            }
        });
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        textSpinner = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
