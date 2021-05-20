

package com.mart.filymart;

        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

public class ReceiverInfoFragment extends Fragment {
    public SharedPreferences pref;
    private EditText shortMessage;
    private  EditText email;
    private EditText phone;
    private EditText receiver;
    private EditText long_message;
    private EditText event_date;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.receiver_info_fragment, container, false);

        pref= getContext().getSharedPreferences("filymart", 0);
        Toast.makeText(getContext(), "Receiver", Toast.LENGTH_LONG).show();
        shortMessage = v.findViewById(R.id.short_message_input);
        email = v.findViewById(R.id.recepient_email);
        phone = v.findViewById(R.id.recepient_phone);
        receiver = v.findViewById(R.id.receiver_name);
        long_message = v.findViewById(R.id.long_message_);
        event_date = v.findViewById(R.id.date);

        v.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return v;
    }

    public static ReceiverInfoFragment newInstance(String text, int image) {

        ReceiverInfoFragment f = new ReceiverInfoFragment();
        Bundle b = new Bundle();
        b.putString("text", text);
        b.putInt("img", image);

        f.setArguments(b);

        return f;
    }

    public boolean setShortMessage(String short_message){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("short_message", short_message);
        editor.commit(); //commit changes
        return true;
    }

    public boolean setEmail(String email){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", email);
        editor.commit(); //commit changes
        return true;
    }
    public boolean setPhone(String phone){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("phone", phone);
        editor.commit(); //commit changes
        return true;
    }
    public boolean setReceiverName(String receiver){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("receiver", receiver);
        editor.commit(); //commit changes
        return true;
    }

    public boolean setLongMessage(String message){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("message", message);
        editor.commit(); //commit changes
        return true;
    }

    public boolean setReceiverDate(String date){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("date", date);
        editor.commit(); //commit changes
        return true;
    }

}
