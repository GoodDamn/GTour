package good.gda.appp.other;

import android.content.Context;
import android.widget.Toast;

public class Constants {

    public static void showMessage(Context context, String s)
    {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}
