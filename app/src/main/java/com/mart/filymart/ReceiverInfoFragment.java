

package com.mart.filymart;

        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

public class ReceiverInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.receiver_info_fragment, container, false);

//        TextView tv = (TextView) v.findViewById(R.id.title);
//        tv.setText(getArguments().getString("text"));
//
//        ImageView imageView = (ImageView) v.findViewById(R.id.image);
//        imageView.setBackgroundResource(getArguments().getInt("img"));
        Toast.makeText(getContext(), "Receiver", Toast.LENGTH_LONG).show();

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
}
