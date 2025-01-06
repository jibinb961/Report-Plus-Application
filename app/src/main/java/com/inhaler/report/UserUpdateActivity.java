package com.inhaler.report;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserUpdateActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private Uri filepath;
    private ImageButton infoButton;
    private RadioGroup typeGroup;
    private RadioButton schoolTypeRadio,coachingTypeRadio;
    private Bitmap bitmap=null;
    private byte[] imageByteArray;
    private Button updateButton,deleteAccountButton;
    private EditText SchoolName,SchoolAddress,SchoolContact,SchoolPin,CountryCode;
    private StorageReference LogoRef;
    private ProgressBar progressBar,progressBarbehindlogo;
    private DatabaseReference UsersRef;
    private FirebaseUser firebaseUser;
    private HashMap<String,String> map = new HashMap<>();
    private TextView sampleLogo;
    private String userid,imagename,abspath,uid,type,CCODE,countrypartialcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();

        userid = getIntent().getStringExtra("uid");
        map.put("Andorra, Principality Of", "AD");
        map.put("United Arab Emirates", "AE");
        map.put("Afghanistan, Islamic State Of", "AF");
        map.put("Antigua And Barbuda", "AG");
        map.put("Anguilla", "AI");
        map.put("Albania", "AL");
        map.put("Armenia", "AM");
        map.put("Netherlands Antilles", "AN");
        map.put("Angola", "AO");
        map.put("Antarctica", "AQ");
        map.put("Argentina", "AR");
        map.put("American Samoa", "AS");
        map.put("Austria", "AT");
        map.put("Australia", "AU");
        map.put("Aruba", "AW");
        map.put("Azerbaidjan", "AZ");
        map.put("Bosnia-Herzegovina", "BA");
        map.put("Barbados", "BB");
        map.put("Bangladesh", "BD");
        map.put("Belgium", "BE");
        map.put("Burkina Faso", "BF");
        map.put("Bulgaria", "BG");
        map.put("Bahrain", "BH");
        map.put("Burundi", "BI");
        map.put("Benin", "BJ");
        map.put("Bermuda", "BM");
        map.put("Brunei Darussalam", "BN");
        map.put("Bolivia", "BO");
        map.put("Brazil", "BR");
        map.put("Bahamas", "BS");
        map.put("Bhutan", "BT");
        map.put("Bouvet Island", "BV");
        map.put("Botswana", "BW");
        map.put("Belarus", "BY");
        map.put("Belize", "BZ");
        map.put("Canada", "CA");
        map.put("Cocos (Keeling) Islands", "CC");
        map.put("Central African Republic", "CF");
        map.put("Congo, The Democratic Republic Of The", "CD");
        map.put("Congo", "CG");
        map.put("Switzerland", "CH");
        map.put("Ivory Coast (Cote D'Ivoire)", "CI");
        map.put("Cook Islands", "CK");
        map.put("Chile", "CL");
        map.put("Cameroon", "CM");
        map.put("China", "CN");
        map.put("Colombia", "CO");
        map.put("Costa Rica", "CR");
        map.put("Former Czechoslovakia", "CS");
        map.put("Cuba", "CU");
        map.put("Cape Verde", "CV");
        map.put("Christmas Island", "CX");
        map.put("Cyprus", "CY");
        map.put("Czech Republic", "CZ");
        map.put("Germany", "DE");
        map.put("Djibouti", "DJ");
        map.put("Denmark", "DK");
        map.put("Dominica", "DM");
        map.put("Dominican Republic", "DO");
        map.put("Algeria", "DZ");
        map.put("Ecuador", "EC");
        map.put("Estonia", "EE");
        map.put("Egypt", "EG");
        map.put("Western Sahara", "EH");
        map.put("Eritrea", "ER");
        map.put("Spain", "ES");
        map.put("Ethiopia", "ET");
        map.put("Finland", "FI");
        map.put("Fiji", "FJ");
        map.put("Falkland Islands", "FK");
        map.put("Micronesia", "FM");
        map.put("Faroe Islands", "FO");
        map.put("France", "FR");
        map.put("France (European Territory)", "FX");
        map.put("Gabon", "GA");
        map.put("Great Britain", "UK");
        map.put("Grenada", "GD");
        map.put("Georgia", "GE");
        map.put("French Guyana", "GF");
        map.put("Ghana", "GH");
        map.put("Gibraltar", "GI");
        map.put("Greenland", "GL");
        map.put("Gambia", "GM");
        map.put("Guinea", "GN");
        map.put("Guadeloupe (French)", "GP");
        map.put("Equatorial Guinea", "GQ");
        map.put("Greece", "GR");
        map.put("S. Georgia & S. Sandwich Isls.", "GS");
        map.put("Guatemala", "GT");
        map.put("Guam (USA)", "GU");
        map.put("Guinea Bissau", "GW");
        map.put("Guyana", "GY");
        map.put("Hong Kong", "HK");
        map.put("Heard And McDonald Islands", "HM");
        map.put("Honduras", "HN");
        map.put("Croatia", "HR");
        map.put("Haiti", "HT");
        map.put("Hungary", "HU");
        map.put("Indonesia", "ID");
        map.put("Ireland", "IE");
        map.put("Israel", "IL");
        map.put("India", "IN");
        map.put("British Indian Ocean Territory", "IO");
        map.put("Iraq", "IQ");
        map.put("Iran", "IR");
        map.put("Iceland", "IS");
        map.put("Italy", "IT");
        map.put("Jamaica", "JM");
        map.put("Jordan", "JO");
        map.put("Japan", "JP");
        map.put("Kenya", "KE");
        map.put("Kyrgyz Republic (Kyrgyzstan)", "KG");
        map.put("Cambodia, Kingdom Of", "KH");
        map.put("Kiribati", "KI");
        map.put("Comoros", "KM");
        map.put("Saint Kitts & Nevis Anguilla", "KN");
        map.put("North Korea", "KP");
        map.put("South Korea", "KR");
        map.put("Kuwait", "KW");
        map.put("Cayman Islands", "KY");
        map.put("Kazakhstan", "KZ");
        map.put("Laos", "LA");
        map.put("Lebanon", "LB");
        map.put("Saint Lucia", "LC");
        map.put("Liechtenstein", "LI");
        map.put("Sri Lanka", "LK");
        map.put("Liberia", "LR");
        map.put("Lesotho", "LS");
        map.put("Lithuania", "LT");
        map.put("Luxembourg", "LU");
        map.put("Latvia", "LV");
        map.put("Libya", "LY");
        map.put("Morocco", "MA");
        map.put("Monaco", "MC");
        map.put("Moldavia", "MD");
        map.put("Madagascar", "MG");
        map.put("Marshall Islands", "MH");
        map.put("Macedonia", "MK");
        map.put("Mali", "ML");
        map.put("Myanmar", "MM");
        map.put("Mongolia", "MN");
        map.put("Macau", "MO");
        map.put("Northern Mariana Islands", "MP");
        map.put("Martinique (French)", "MQ");
        map.put("Mauritania", "MR");
        map.put("Montserrat", "MS");
        map.put("Malta", "MT");
        map.put("Mauritius", "MU");
        map.put("Maldives", "MV");
        map.put("Malawi", "MW");
        map.put("Mexico", "MX");
        map.put("Malaysia", "MY");
        map.put("Mozambique", "MZ");
        map.put("Namibia", "NA");
        map.put("New Caledonia (French)", "NC");
        map.put("Niger", "NE");
        map.put("Norfolk Island", "NF");
        map.put("Nigeria", "NG");
        map.put("Nicaragua", "NI");
        map.put("Netherlands", "NL");
        map.put("Norway", "NO");
        map.put("Nepal", "NP");
        map.put("Nauru", "NR");
        map.put("Neutral Zone", "NT");
        map.put("Niue", "NU");
        map.put("New Zealand", "NZ");
        map.put("Oman", "OM");
        map.put("Panama", "PA");
        map.put("Peru", "PE");
        map.put("Polynesia (French)", "PF");
        map.put("Papua New Guinea", "PG");
        map.put("Philippines", "PH");
        map.put("Pakistan", "PK");
        map.put("Poland", "PL");
        map.put("Saint Pierre And Miquelon", "PM");
        map.put("Pitcairn Island", "PN");
        map.put("Puerto Rico", "PR");
        map.put("Portugal", "PT");
        map.put("Palau", "PW");
        map.put("Paraguay", "PY");
        map.put("Qatar", "QA");
        map.put("Reunion (French)", "RE");
        map.put("Romania", "RO");
        map.put("Russian Federation", "RU");
        map.put("Rwanda", "RW");
        map.put("Saudi Arabia", "SA");
        map.put("Solomon Islands", "SB");
        map.put("Seychelles", "SC");
        map.put("Sudan", "SD");
        map.put("Sweden", "SE");
        map.put("Singapore", "SG");
        map.put("Saint Helena", "SH");
        map.put("Slovenia", "SI");
        map.put("Svalbard And Jan Mayen Islands", "SJ");
        map.put("Slovak Republic", "SK");
        map.put("Sierra Leone", "SL");
        map.put("San Marino", "SM");
        map.put("Senegal", "SN");
        map.put("Somalia", "SO");
        map.put("Suriname", "SR");
        map.put("Saint Tome (Sao Tome) And Principe", "ST");
        map.put("Former USSR", "SU");
        map.put("El Salvador", "SV");
        map.put("Syria", "SY");
        map.put("Swaziland", "SZ");
        map.put("Turks And Caicos Islands", "TC");
        map.put("Chad", "TD");
        map.put("French Southern Territories", "TF");
        map.put("Togo", "TG");
        map.put("Thailand", "TH");
        map.put("Tadjikistan", "TJ");
        map.put("Tokelau", "TK");
        map.put("Turkmenistan", "TM");
        map.put("Tunisia", "TN");
        map.put("Tonga", "TO");
        map.put("East Timor", "TP");
        map.put("Turkey", "TR");
        map.put("Trinidad And Tobago", "TT");
        map.put("Tuvalu", "TV");
        map.put("Taiwan", "TW");
        map.put("Tanzania", "TZ");
        map.put("Ukraine", "UA");
        map.put("Uganda", "UG");
        map.put("United Kingdom", "UK");
        map.put("USA Minor Outlying Islands", "UM");
        map.put("United States", "US");
        map.put("Uruguay", "UY");
        map.put("Uzbekistan", "UZ");
        map.put("Holy See (Vatican City State)", "VA");
        map.put("Saint Vincent & Grenadines", "VC");
        map.put("Venezuela", "VE");
        map.put("Virgin Islands (British)", "VG");
        map.put("Virgin Islands (USA)", "VI");
        map.put("Vietnam", "VN");
        map.put("Vanuatu", "VU");
        map.put("Wallis And Futuna Islands", "WF");
        map.put("Samoa", "WS");
        map.put("Yemen", "YE");
        map.put("Mayotte", "YT");
        map.put("Yugoslavia", "YU");
        map.put("South Africa", "ZA");
        map.put("Zambia", "ZM");
        map.put("Zaire", "ZR");
        map.put("Zimbabwe", "ZW");
        circleImageView = (CircleImageView) findViewById(R.id.school_profile_circle);
        LogoRef = FirebaseStorage.getInstance().getReference().child("Logos");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        SchoolName = (EditText) findViewById(R.id.school_name_input);
        SchoolAddress = (EditText) findViewById(R.id.school_address_input);
        SchoolContact = (EditText) findViewById(R.id.school_contact_number_input);
        updateButton = (Button) findViewById(R.id.update_institution_detail_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_of_india);
        progressBarbehindlogo = (ProgressBar) findViewById(R.id.progress_bar_behind_logo);
        infoButton = (ImageButton) findViewById(R.id.admin_password_info);
        SchoolPin = (EditText) findViewById(R.id.school_admin_password_input);
        progressBar.setVisibility(View.GONE);
        CountryCode = (EditText) findViewById(R.id.country_code_input);
        sampleLogo = (TextView) findViewById(R.id.view_sample_logo);
        progressBarbehindlogo.setVisibility(View.GONE);
        typeGroup = (RadioGroup) findViewById(R.id.inst_type_radio_group);
        schoolTypeRadio = (RadioButton) findViewById(R.id.school_type);
        coachingTypeRadio = (RadioButton) findViewById(R.id.coaching_type);


// extract the values you need

        

        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(schoolTypeRadio.getId()==checkedId)
                {
                    type = "school";
                }
                else if(coachingTypeRadio.getId()==checkedId)
                {
                    type = "coaching";
                }
            }
        });
        

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleImageView.setVisibility(View.GONE);
                progressBarbehindlogo.setVisibility(View.VISIBLE);
                     Intent actionintent = new Intent(Intent.ACTION_PICK);
                     actionintent.setType("image/*");
                     startActivityForResult(Intent.createChooser(actionintent, "Select IMage file"), 1);



            }
        });

        CountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] countries = {"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe", "Palestine"};

                // Create an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(UserUpdateActivity.this);
                builder.setTitle("Select country code");
                builder.setItems(countries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set the selected country code on the EditText
                        CountryCode.setText(countries[which]);
                        countrypartialcode = map.get(countries[which]);
                        String jsonString = readJSONFile("country_codes.json");

                        JSONObject json = null;
                        try {
                            json = new JSONObject(jsonString);
                            CCODE = json.getString(map.get(countries[which]));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                builder.create().show();
            }
        });

        sampleLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog builder = new AlertDialog.Builder(UserUpdateActivity.this).create();
                View dialogView = getLayoutInflater().inflate(R.layout.sample_logo, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button okbutton = (Button) dialogView.findViewById(R.id.sample_logo_button);
                okbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.cancel();
                    }
                });

                builder.show();
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserUpdateActivity.this);
                builder.setTitle("Admin Password");
                builder.setMessage("Admin password is required to create or update general modules like subjects, exams and co curricular activities. Class teachers or subject teachers wont be having these escalated privileges." +
                        "\n\nPlease remember the 4 digit PIN for future operations!");

                builder.setCancelable(true);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    updateButton.setVisibility(View.INVISIBLE);
                    String in_name = SchoolName.getText().toString().trim();
                    String in_contact= SchoolContact.getText().toString().trim();
                    String in_admin_password = SchoolPin.getText().toString().trim();
                    imagename = in_name.replace(" ","_") + Calendar.getInstance().getTime().toString();
                    String in_address = SchoolAddress.getText().toString().trim();
                    String countryphonecode = CCODE.trim();
                    String countrycode = CountryCode.getText().toString().trim();

                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    final StorageReference uploader = firebaseStorage.getReference(userid);


                    if (in_address.isEmpty() || in_name.isEmpty() || in_contact.isEmpty() || in_admin_password.isEmpty() || type.isEmpty() || countrycode.isEmpty())
                    {

                        progressBar.setVisibility(View.GONE);
                        updateButton.setVisibility(View.VISIBLE);
                        Toast.makeText(UserUpdateActivity.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                    }
                    else if(in_admin_password.length() != 4)
                    {
                            progressBar.setVisibility(View.GONE);
                            updateButton.setVisibility(View.VISIBLE);
                            Toast.makeText(UserUpdateActivity.this, "Enter 4 digit Admin PIN", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(bitmap==null)
                        {
                            new AlertDialog.Builder(UserUpdateActivity.this)
                                    .setTitle("Warning")
                                    .setMessage("You have not uploaded a logo, but you can always update it later!")
                                    .setIcon(R.drawable.ic_info)

                                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();

                                            progressBar.setVisibility(View.GONE);
                                            HashMap<String,String> institiondata = new HashMap<>();
                                            institiondata.put("schoolname",in_name);
                                            institiondata.put("schoolcontact",in_contact);
                                            institiondata.put("schooladdress",in_address);
                                            institiondata.put("abspath",abspath);
                                            institiondata.put("type",type);
                                            institiondata.put("schoolpin",in_admin_password);
                                            institiondata.put("countryphonecode",countryphonecode);
                                            institiondata.put("countrycode",countrycode);
                                            institiondata.put("countrypartialcode",countrypartialcode);
                                            UsersRef.child(userid).child("Details").setValue(institiondata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                                    myEdit.putString("admin_pin", in_admin_password);
                                                    myEdit.commit();
                                                    Toast.makeText(UserUpdateActivity.this, "Successfully updated info", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(UserUpdateActivity.this,OnboardingActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            });

                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton("Upload Logo", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            updateButton.setVisibility(View.VISIBLE);
                                            Intent actionintent = new Intent(Intent.ACTION_PICK);
                                            actionintent.setType("image/*");
                                            startActivityForResult(Intent.createChooser(actionintent, "Select IMage file"), 1);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                        else
                        {
                            uploader.putBytes(imageByteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            progressBar.setVisibility(View.GONE);
                                            HashMap<String,String> institiondata = new HashMap<>();
                                            institiondata.put("schoolname",in_name);
                                            institiondata.put("schoolcontact",in_contact);
                                            institiondata.put("schooladdress",in_address);
                                            institiondata.put("schoollogo",uri.toString());
                                            institiondata.put("abspath",abspath);
                                            institiondata.put("type",type);
                                            institiondata.put("schoolpin",in_admin_password);
                                            institiondata.put("countryphonecode",countryphonecode);
                                            institiondata.put("countrycode",countrycode);
                                            institiondata.put("countrypartialcode",countrypartialcode);
                                            UsersRef.child(userid).child("Details").setValue(institiondata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                                    myEdit.putString("admin_pin", in_admin_password);
                                                    myEdit.commit();
                                                    Toast.makeText(UserUpdateActivity.this, "Successfully updated info", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(UserUpdateActivity.this,OnboardingActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            });

                                        }
                                    });

                                }
                            });
                        }


                    }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK)
        {
            filepath = data.getData();
            Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
            cursor.moveToFirst();
            @SuppressLint("Range") int sizeInBytes = cursor.getInt(cursor.getColumnIndex(OpenableColumns.SIZE));
            int sizeInKB = sizeInBytes / 1024;
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);

                if(sizeInKB>500)
                {
                    new AlertDialog.Builder(UserUpdateActivity.this)
                            .setTitle("Upload Failed")
                            .setMessage("Logo should be less than 500KB")
                            .setIcon(R.drawable.ic_baseline_error)

                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    circleImageView.setVisibility(View.VISIBLE);
                    progressBarbehindlogo.setVisibility(View.GONE);
                }
                else
                {
                    abspath = saveToInternalStorage(bitmap);
                    imageByteArray = compressImage(bitmap,100);

                    circleImageView.setImageBitmap(bitmap);
                    circleImageView.setVisibility(View.VISIBLE);
                    progressBarbehindlogo.setVisibility(View.GONE);
                }

            }
            catch(Exception e)
            {

            }
        }
        else
        {
            circleImageView.setVisibility(View.VISIBLE);
            progressBarbehindlogo.setVisibility(View.GONE);

        }


    }




    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,userid+".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();

    }

    private String readJSONFile(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static byte[] compressImage(Bitmap image, int quality) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        int newWidth = originalWidth / 2;
        int newHeight = originalHeight / 2;

        Bitmap newBitmap = Bitmap.createScaledBitmap(image, newWidth, newHeight, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        return byteArray;
    }
}