package good.gda.appp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import good.gda.appp.R;
import good.gda.appp.fragments.Places;


public class InsertDataToDataBaseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private String textSpinner;
    private Button add_username_Button;
    private EditText  name_Place_TextBox, positionX_TextBox,
            positionY_TextBox, Experience;
    private Spinner typeOfPlace_Spinner;
    private String nickname;

    private DatabaseReference Myfirebase;

    private  void Message(String text)
    {Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();}

    public void AddData() // Method, which insert data in database.
    {
        if (nickname.equals("GoodDemoDev"))
        {
            add_username_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Myfirebase.child(Myfirebase.push().getKey()).setValue(new Places(Myfirebase.push().getKey(),
                            name_Place_TextBox.getText().toString(),
                            Double.parseDouble(positionX_TextBox.getText().toString()),
                            Double.parseDouble(positionY_TextBox.getText().toString()),
                            Double.parseDouble(Experience.getText().toString()),
                            textSpinner, ""));
                    Message("Inserted successfully!");
                    name_Place_TextBox.getText().clear();
                    positionX_TextBox.getText().clear();
                    positionY_TextBox.getText().clear();
                    Experience.getText().clear();
                }
            });
        }
        else Message("Only for developers!");
    }

    private void FillTypes_Spinner()
    {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.TypesOfPlace, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfPlace_Spinner.setAdapter(arrayAdapter);
        typeOfPlace_Spinner.setOnItemSelectedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Initialize the fragment.
        View v = inflater.inflate(R.layout.fragment_insert_data_to_data_base_activity, container, false);
        //Components:
        add_username_Button = (Button) v.findViewById(R.id.Button_Add); // Button "ADD"
        positionX_TextBox = (EditText) v.findViewById(R.id.INSERT_PositionX_TextBox); // EditText "PositionX"
        positionY_TextBox = (EditText) v.findViewById(R.id.INSERT_PositionY_TextBox); // EditText "PositionY"
        Experience = (EditText) v.findViewById(R.id.INSERT_Experience_TextBox); // EditText "Experience"
        name_Place_TextBox = (EditText) v.findViewById(R.id.INSERT_Name_TextBox); // EditText "Name"
        typeOfPlace_Spinner = v.findViewById(R.id.INSERT_TypeOfPlace_Spinner); // EditText "Type"

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        nickname = user.getDisplayName();
        Myfirebase = FirebaseDatabase.getInstance().getReference("Places");


        FillTypes_Spinner();
        AddData(); // Insert data in database.
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        textSpinner = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
