package com.example.p07_ps;


import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class fragmentRight extends Fragment {

    Button btnNumber;


    public fragmentRight() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_right, container, false);
        final EditText etWord = view.findViewById(R.id.etWord);
        btnNumber = view.findViewById(R.id.btnWord);
        final TextView tvWord = view.findViewById(R.id.tvWord);

        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getActivity().getContentResolver();

                String filter = "body LIKE ?";
                String number = etWord.getText().toString();
                String[] filterArgs = {"%" + number + "%"};

                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                if (filter.contains(",")){
                    String[] filterList = filter.split(",");
                    String[] filterArgs2 = new String[filterList.length];
                    filterArgs2[0] = "%" + filterList[0] + "%";
                    for (int i = 1; i <filterList.length; i++){
                        filter +="AND body LIKE ? ";
                        filterArgs2[i] = "%" + filterList[i] + "%";
                    }
                    cursor = cr.query(uri, reqCols, filter, filterArgs2, null);
                }



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
                tvWord.setText(smsBody);
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