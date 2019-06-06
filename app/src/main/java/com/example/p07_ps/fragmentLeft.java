package com.example.p07_ps;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentLeft extends Fragment {

    Button btnNumber,btnEmail;
    String emailBody = "";



    public fragmentLeft() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_left, container, false);
        final EditText etNumber = view.findViewById(R.id.etNumber);
        btnNumber = view.findViewById(R.id.btnNumber);
        btnEmail = view.findViewById(R.id.emailLeft);
        final TextView tvNumber = view.findViewById(R.id.tvNumber);

        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getActivity().getContentResolver();

                String filter = "address LIKE ?";
                String number = etNumber.getText().toString();
                String[] filterArgs = {"%" + number + "%"};

                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                String smsBody = "";

                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);

                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";

                    } while (cursor.moveToNext());
                }
                tvNumber.setText(smsBody);
                emailBody = smsBody;


            }
        });



         btnEmail.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 // The action you want this intent to do;
                 // ACTION_SEND is used to indicate sending text
                 Intent email = new Intent(Intent.ACTION_SEND);
                 // Put essentials like email address, subject & body text
                 email.putExtra(Intent.EXTRA_EMAIL,
                         new String[]{"17010407@myrp.edu.sg"});
                 email.putExtra(Intent.EXTRA_SUBJECT,
                         "SMS Content");
                 email.putExtra(Intent.EXTRA_TEXT, emailBody);
                 // This MIME type indicates email
                 email.setType("message/rfc822");
                 // createChooser shows user a list of app that can handle
                 // this MIME type, which is, email
                 startActivity(Intent.createChooser(email,
                         "Choose an Email client :"));
             }
         });
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnNumber.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getContext(), "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}
