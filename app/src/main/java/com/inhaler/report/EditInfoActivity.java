package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Locale;
import java.util.Properties;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditInfoActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private CardView updateSchoolProfileCard;
    private Uri filepath;
    private ImageView backButton;
    private TextView updateLogoText;
    private Bitmap bitmap=null;
    private Button updateButton,logoutButton,staffSignin;
    private boolean isStaff = false;
    private EditText SchoolName,SchoolAddress,SchoolContact;
    private StorageReference LogoRef;
    private Button deleteAccountButton;
    private byte[] imageByteArray;
    private ProgressBar progressBar,progressBarbehind;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private FirebaseUser currentUser;
    private String userid,imagename,abspath="",emailid,schoolnameforemail,adminPIN,countryphonecode,countrycode,countrypartialcode,pdfcount;
    private Boolean flag = false,activated=false;
    private TextView forgotAdminPin;
    private String staffkey="",staffpassword="",staffClasses="",activationkey;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userid = currentUser.getUid();
        circleImageView = (CircleImageView) findViewById(R.id.update_school_profile_circle);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        SchoolName = (EditText) findViewById(R.id.update_school_name_input);
        updateSchoolProfileCard = (CardView) findViewById(R.id.update_school_profile_card);
        SchoolAddress = (EditText) findViewById(R.id.update_school_address_input);
        SchoolContact = (EditText) findViewById(R.id.update_school_contact_number_input);
        updateButton = (Button) findViewById(R.id.update_institution_detail_button_update);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_of_kerala);
        progressBarbehind = (ProgressBar) findViewById(R.id.update_progress_bar_behind_logo);
        forgotAdminPin = (TextView) findViewById(R.id.forgot_admin_pin_text);
        staffSignin = (Button) findViewById(R.id.staff_sign_in);
        logoutButton = (Button) findViewById(R.id.logout_button);
        backButton = (ImageView) findViewById(R.id.profile_back_button);
        updateLogoText = (TextView) findViewById(R.id.update_logo_text);
        deleteAccountButton = (Button) findViewById(R.id.delete_account_button);
        emailid = currentUser.getEmail();
        mAuth = FirebaseAuth.getInstance();


        progressBar.setVisibility(View.GONE);
        progressBarbehind.setVisibility(View.GONE);


        staffSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("staff_pin","");
                myEdit.putString("admin_pin","");
                myEdit.putString("staff_key","");
                myEdit.commit();
                Intent loginIntent = new Intent(EditInfoActivity.this,UserSelectorActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DeleteAccountFragment fragment = new DeleteAccountFragment();
                fragment.show(getSupportFragmentManager(),"Delete Account");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isStaff)
                {
                    mAuth.signOut();
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("staff_pin","");
                    myEdit.putString("admin_pin","");
                    myEdit.putString("staff_key","");
                    myEdit.commit();
                    Intent loginIntent = new Intent(EditInfoActivity.this,LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                }
                else
                {
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("staff_pin","");
                    myEdit.putString("admin_pin","");
                    myEdit.putString("staff_key","");
                    myEdit.commit();
                    Intent loginIntent = new Intent(EditInfoActivity.this,UserSelectorActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                }

            }
        });

        forgotAdminPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isStaff)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditInfoActivity.this);
                    builder.setTitle("Forgot Admin PIN");
                    builder.setMessage("Admin PIN will be send to your registered email id");


                    builder.setCancelable(true);
                    builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            JavaMailAPI javaMailAPI = new JavaMailAPI(EditInfoActivity.this , emailid, "[Do not Share] Report Plus Admin PIN ", "Hi "+schoolnameforemail+", \n\nThe Admin PIN for your account is "+adminPIN+ " \n\nPlease do not share this code with anyone!\nThank You, Report Plus");

                            javaMailAPI.execute();
                            Toast.makeText(EditInfoActivity.this, "Successfully sent Admin PIN to "+emailid, Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }
                else
                {
                    Bundle staffbundle = new Bundle();
                    staffbundle.putString("staffkey",staffkey);
                    ResetStaffPasswordFragment fragment = new ResetStaffPasswordFragment();
                    fragment.setArguments(staffbundle);
                    fragment.show(getSupportFragmentManager(),"Change Password");
                }

            }
        });



        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleImageView.setVisibility(View.GONE);
                progressBarbehind.setVisibility(View.VISIBLE);

                    Intent actionintent = new Intent(Intent.ACTION_PICK);
                    actionintent.setType("image/*");
                    startActivityForResult(Intent.createChooser(actionintent, "Select Logo"), 1);




            }
        });



        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStaff)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    updateButton.setVisibility(View.INVISIBLE);
                    String staff_name = SchoolName.getText().toString().trim();
                    String staff_contact= SchoolContact.getText().toString().trim();
                    String staff_id = SchoolAddress.getText().toString().trim();

                    if(staff_contact.isEmpty() || staff_name.isEmpty())
                    {
                        Toast.makeText(EditInfoActivity.this, "Enter all fields!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        updateButton.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        HashMap<String,String> staffmap = new HashMap<>();
                        staffmap.put("staffname",staff_name);
                        staffmap.put("staffid",staff_id);
                        staffmap.put("staffpassword",staffpassword);
                        staffmap.put("staffphone",staff_contact);
                        staffmap.put("classes",staffClasses);
                        UsersRef.child(userid).child("Staffs").child(staffkey).setValue(staffmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    progressBar.setVisibility(View.GONE);
                                    updateButton.setVisibility(View.VISIBLE);
                                    Toast.makeText(EditInfoActivity.this, "Successfully updated staff info!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }




                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    updateButton.setVisibility(View.INVISIBLE);
                    String in_name = SchoolName.getText().toString().trim();
                    String in_contact= SchoolContact.getText().toString().trim();
                    imagename = in_name.replace(" ","_") + Calendar.getInstance().getTime().toString();
                    String in_address = SchoolAddress.getText().toString().trim();

                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    final StorageReference uploader = firebaseStorage.getReference(userid);

                    if (in_address.isEmpty() || in_name.isEmpty() || in_contact.isEmpty() )
                    {

                        progressBar.setVisibility(View.GONE);
                        updateButton.setVisibility(View.VISIBLE);
                        Toast.makeText(EditInfoActivity.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(abspath.isEmpty())
                        {
                            HashMap<String,String> institiondata = new HashMap<>();
                            institiondata.put("schoolname",in_name);
                            institiondata.put("schoolcontact",in_contact);
                            institiondata.put("schooladdress",in_address);
                            UsersRef.child(userid).child("Details").child("schoolname").setValue(in_name);
                            UsersRef.child(userid).child("Details").child("schoolcontact").setValue(in_contact);
                            UsersRef.child(userid).child("Details").child("schooladdress").setValue(in_address);
                            progressBar.setVisibility(View.GONE);
                            updateButton.setVisibility(View.VISIBLE);
                            Toast.makeText(EditInfoActivity.this, "Successfully updated info", Toast.LENGTH_SHORT).show();



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
                                            if(activated)
                                            {
                                                institiondata.put("activationkey",activationkey);
                                            }
                                            institiondata.put("schoolcontact",in_contact);
                                            institiondata.put("schooladdress",in_address);
                                            institiondata.put("schoollogo",uri.toString());
                                            institiondata.put("abspath",abspath);
                                            institiondata.put("schoolpin",adminPIN);
                                            institiondata.put("countrycode",countrycode);
                                            institiondata.put("countryphonecode",countryphonecode);
                                            institiondata.put("countrypartialcode",countrypartialcode);
                                            institiondata.put("pdfcount",pdfcount);
                                            UsersRef.child(userid).child("Details").setValue(institiondata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(EditInfoActivity.this, "Successfully updated info", Toast.LENGTH_SHORT).show();

                                                    progressBar.setVisibility(View.GONE);
                                                    updateButton.setVisibility(View.VISIBLE);
                                                }
                                            });

                                        }
                                    });

                                }
                            });
                        }

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
            flag = true;
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
                    new AlertDialog.Builder(EditInfoActivity.this)
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
                    progressBarbehind.setVisibility(View.GONE);
                }
                else
                {
                    abspath = saveToInternalStorage(bitmap);
                    imageByteArray = compressImage(bitmap,100);

                    circleImageView.setImageBitmap(bitmap);
                    circleImageView.setVisibility(View.VISIBLE);
                    progressBarbehind.setVisibility(View.GONE);
                }



            }
            catch(Exception e)
            {

            }
        }
        else
        {
            circleImageView.setVisibility(View.VISIBLE);
            progressBarbehind.setVisibility(View.GONE);
        }


    }




    private String saveToInternalStorage(Bitmap bitmapImage){


        int originalWidth = bitmapImage.getWidth();
        int originalHeight = bitmapImage.getHeight();

        int newWidth = originalWidth / 2;
        int newHeight = originalHeight / 2;

        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmapImage, newWidth, newHeight, true);



        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,userid+".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
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

    @Override
    protected void onStart() {
        super.onStart();

        staffkey = getIntent().getStringExtra("staffkey");

        if(flag==false)
        {

            if(!staffkey.isEmpty())
            {
                isStaff = true;
                UsersRef.child(userid).child("Staffs").child(staffkey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String staffname = snapshot.child("staffname").getValue(String.class);
                        String staffid = snapshot.child("staffid").getValue(String.class);
                         staffpassword = snapshot.child("staffpassword").getValue(String.class);
                        staffClasses = snapshot.child("classes").getValue(String.class);
                        String phone = snapshot.child("staffphone").getValue(String.class);
                        SchoolName.setText(staffname);
                        SchoolAddress.setText(staffid);
                        SchoolContact.setText(phone);
                        circleImageView.setImageResource(R.drawable.teacher);
                        updateLogoText.setVisibility(View.GONE);
                        staffSignin.setVisibility(View.GONE);
                        SchoolAddress.setEnabled(false);
                        deleteAccountButton.setVisibility(View.GONE);
                        forgotAdminPin.setText("Change Password");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else
            {
                UsersRef.child(userid).child("Details").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String schoolname = snapshot.child("schoolname").getValue(String.class);
                        schoolnameforemail = schoolname;
                        if(snapshot.child("schoolpin").exists())
                        {
                            adminPIN = snapshot.child("schoolpin").getValue(String.class);
                        }
                        if(snapshot.child("activationkey").exists())
                        {
                            activationkey = snapshot.child("activationkey").getValue(String.class);
                            activated = true;
                        }
                        
                        String schooladdress = snapshot.child("schooladdress").getValue(String.class);
                        String schoolcontact = snapshot.child("schoolcontact").getValue(String.class);
                        countrypartialcode = snapshot.child("countrypartialcode").getValue(String.class);
                        countrycode = snapshot.child("countrycode").getValue(String.class);
                        countryphonecode = snapshot.child("countryphonecode").getValue(String.class);
                        pdfcount = snapshot.child("pdfcount").getValue(String.class);
                        SchoolName.setText(schoolname);
                        SchoolAddress.setText(schooladdress);
                        SchoolContact.setText(schoolcontact);
                        if(snapshot.child("schoollogo").exists())
                        {
                            String schoollogo = snapshot.child("schoollogo").getValue(String.class);
                            Picasso.get().load(schoollogo).into(circleImageView);
                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        }

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