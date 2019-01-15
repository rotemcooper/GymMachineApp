package com.felhr.serialportexample;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendMessageFragment extends DialogFragment {

    String trainerName;
    public SendMessageFragment( String trainerName ) {
        // Required empty public constructor
        this.trainerName = trainerName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("To: " + trainerName );

        View view = inflater.inflate(R.layout.fragment_send_message, container, false);
        view.findViewById(R.id.buttonSend).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                //caller.doOkConfirmClick();

                Toast toast = Toast.makeText(getActivity(), "Message Sent", Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) toast.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(45);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                toast.show();

                getDialog().dismiss();
            }
        });

        return view;
    }

    public void buttonSendTextMessage(View view) {

    }
}
