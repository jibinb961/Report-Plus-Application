package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import top.defaults.colorpicker.ColorPickerPopup;

public class PdfSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private SignaturePad signaturePad;
    private Button tableColorPickerButton,bgColorPickerButton,PdfSettingsSaveButton,coTableColorPickerButton;
    private View tableColorPreviewView,bgColorPreviewView,coTableColorPreviewView;
    private int redValue, greenValue, blueValue;
    private ArrayAdapter<String> adapter;
    private DatabaseReference DetailsRef;
    private byte[] imageByteArray;
    private ProgressBar progressBar;
    private boolean isCleared = false;
    private TextView clearCanvas,resetStyle;
    private CheckBox enableSign;
    private String bgColorValues,tableColorValues,uid,SelectedBorderType,coTableColorValues,abspath;
    private EditText getPdfBorderWidth,getLogoSize,SigneeName,SigneeRole;
    private FirebaseUser currentUser;
    private FrameLayout signatureFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_settings);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        DetailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");

        // Get reference to spinner in layout
        spinner = (Spinner) findViewById(R.id.pdf_border_type_spinner);
        signatureFrame = (FrameLayout) findViewById(R.id.signature_pad_frame);
        enableSign = (CheckBox) findViewById(R.id.enable_signature_checkbox);
        signaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        tableColorPickerButton = (Button) findViewById(R.id.color_picker_button);
        tableColorPreviewView = (View) findViewById(R.id.color_preview_view);
        SigneeName = (EditText) findViewById(R.id.signee_name_input);
        resetStyle = (TextView) findViewById(R.id.reset_style_heading);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_of_pdf_style);
        SigneeRole = (EditText) findViewById(R.id.signee_role_input);
        clearCanvas = (TextView) findViewById(R.id.clear_canvas_textView);
        bgColorPickerButton = (Button) findViewById(R.id.bg_color_picker_button);
        bgColorPreviewView = (View) findViewById(R.id.font_color_preview_view);
        PdfSettingsSaveButton = (Button)findViewById(R.id.pdf_settings_save_button);
        coTableColorPickerButton = (Button) findViewById(R.id.co_table_color_picker_button);
        coTableColorPreviewView = (View) findViewById(R.id.co_table_color_preview_view);
        getPdfBorderWidth = (EditText) findViewById(R.id.pdf_border_width_input);
        getLogoSize = (EditText) findViewById(R.id.pdf_logo_size_input);

        // Create a list of items for the spinner
        ArrayList<String> items = new ArrayList<>();
        items.add("Groove Border");
        items.add("Double Border");
        items.add("Solid Border");
        items.add("Dotted Border");
        items.add("Round Dots Border");

        clearCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
                isCleared = true;

            }
        });


        resetStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToDefaults();
            }
        });
        enableSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    signatureFrame.setVisibility(View.VISIBLE);
                }
                else
                {
                    signatureFrame.setVisibility(View.GONE);
                }
            }
        });

        tableColorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == tableColorPickerButton) {
                    showColorPickerPopup(0);
                }
            }
        });

        coTableColorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerPopup(2);
            }
        });

        PdfSettingsSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                PdfSettingsSaveButton.setVisibility(View.GONE);

                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Signatures");

                // Create a reference to the image file
                String filename = uid+"sign.png"; // Replace with your own filename
                StorageReference imageRef = storageRef.child(filename);


                if(enableSign.isChecked())
                {
                    if(!(SigneeName.getText().toString().isEmpty() || SigneeRole.getText().toString().isEmpty()))
                    {
                        Bitmap signatureBitmap = signaturePad.getSignatureBitmap();

                        abspath = saveToInternalStorage(signatureBitmap);
                        imageByteArray = compressImage(signatureBitmap,100);
                        UploadTask uploadTask = imageRef.putBytes(imageByteArray);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Handle successful upload
                                // Get the download URL for the uploaded image
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        HashMap<String,String> pdfMap = new HashMap<>();
                                        pdfMap.put("borderwidth",getPdfBorderWidth.getText().toString().trim());
                                        pdfMap.put("bordertype",SelectedBorderType);
                                        pdfMap.put("tablecolor",tableColorValues);
                                        pdfMap.put("fontcolor",bgColorValues);
                                        pdfMap.put("cotablecolor",coTableColorValues);
                                        pdfMap.put("logosize",getLogoSize.getText().toString().trim());
                                        pdfMap.put("signature",imageUrl);
                                        pdfMap.put("signeename",SigneeName.getText().toString().trim());
                                        pdfMap.put("signeerole",SigneeRole.getText().toString().trim());
                                        DetailsRef.child("PdfSettings").setValue(pdfMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                PdfSettingsSaveButton.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(PdfSettingsActivity.this, "Successfully updated preferences!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                PdfSettingsSaveButton.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(PdfSettingsActivity.this, "Something went wrong in uploading Signature image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        PdfSettingsSaveButton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PdfSettingsActivity.this, "Please fill in Signee Field and Signee Role", Toast.LENGTH_SHORT).show();
                    }


                }
                else
                {
                    HashMap<String,String> pdfMap = new HashMap<>();
                    pdfMap.put("borderwidth",getPdfBorderWidth.getText().toString().trim());
                    pdfMap.put("bordertype",SelectedBorderType);
                    pdfMap.put("tablecolor",tableColorValues);
                    pdfMap.put("fontcolor",bgColorValues);
                    pdfMap.put("cotablecolor",coTableColorValues);
                    pdfMap.put("logosize",getLogoSize.getText().toString().trim());
                    DetailsRef.child("PdfSettings").setValue(pdfMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            PdfSettingsSaveButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(PdfSettingsActivity.this, "Successfully updated preferences!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        bgColorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerPopup(1);
            }
        });


        // Create an ArrayAdapter to populate the spinner
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        // Set a listener to be notified when an item is selected
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        signatureFrame.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        DetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("PdfSettings").exists())
                {
                    if(snapshot.child("PdfSettings").child("signature").exists())
                    {
                        enableSign.setChecked(true);
                        String signature = snapshot.child("PdfSettings").child("signature").getValue(String.class);
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Signatures");

                        // Create a reference to the image file
                        String filename = uid+"sign.png"; // Replace with your own filename
                        StorageReference imageRef = storageRef.child(filename);
                        imageRef.getBytes(1024 * 1024)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // Convert the byte array to a Bitmap object
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        signaturePad.setSignatureBitmap(bmp);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors that occur during the download
                                        Toast.makeText(PdfSettingsActivity.this, "Couldnt load Signature!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        String signee_name = snapshot.child("PdfSettings").child("signeename").getValue(String.class);
                        String signee_role = snapshot.child("PdfSettings").child("signeerole").getValue(String.class);
                        SigneeRole.setText(signee_role);
                        SigneeName.setText(signee_name);
                    }

                    String borderwidth = snapshot.child("PdfSettings").child("borderwidth").getValue(String.class);
                    getPdfBorderWidth.setText(borderwidth);
                   String bordertype = snapshot.child("PdfSettings").child("bordertype").getValue(String.class);
                   String tablecolor[] = snapshot.child("PdfSettings").child("tablecolor").getValue(String.class).split(",");
                   tableColorValues = snapshot.child("PdfSettings").child("tablecolor").getValue(String.class);
                   coTableColorValues = snapshot.child("PdfSettings").child("cotablecolor").getValue(String.class);
                   bgColorValues = snapshot.child("PdfSettings").child("fontcolor").getValue(String.class);
                   String fontcolor[] = snapshot.child("PdfSettings").child("fontcolor").getValue(String.class).split(",");
                   String cotablecolor[] = snapshot.child("PdfSettings").child("cotablecolor").getValue(String.class).split(",");
                   String logosize = snapshot.child("PdfSettings").child("logosize").getValue(String.class);
                   getLogoSize.setText(logosize);


                    int position = adapter.getPosition(bordertype);
                    spinner.setSelection(position);
                    tableColorPreviewView.setBackgroundColor(Color.rgb(Integer.parseInt(tablecolor[0].trim()),
                            Integer.parseInt(tablecolor[1].trim()), Integer.parseInt(tablecolor[2].trim())));
                    bgColorPreviewView.setBackgroundColor(Color.rgb(Integer.parseInt(fontcolor[0].trim()),
                            Integer.parseInt(fontcolor[1].trim()), Integer.parseInt(fontcolor[2].trim())));
                    coTableColorPreviewView.setBackgroundColor(Color.rgb(Integer.parseInt(cotablecolor[0].trim()),
                            Integer.parseInt(cotablecolor[1].trim()), Integer.parseInt(cotablecolor[2].trim())));


                }
                else
                {
                    setToDefaults();
                    
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showColorPickerPopup(int Viewcode) {
        int initialColor = Color.rgb(redValue, greenValue, blueValue);

        new ColorPickerPopup.Builder(this)
                .initialColor(initialColor)
                .enableBrightness(true)
                .enableAlpha(false)
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(false)
                .build()
                .show(new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        // Get the RGB values of the selected color
                        redValue = Color.red(color);
                        greenValue = Color.green(color);
                        blueValue = Color.blue(color);

                        if(Viewcode==1)
                        {
                            bgColorValues = redValue+","+greenValue+","+blueValue;
                            // Set the background color of the color preview view to the selected color
                            bgColorPreviewView.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                        }
                        else if(Viewcode==0)
                        {
                            tableColorValues = redValue+","+greenValue+","+blueValue;
                            // Set the background color of the color preview view to the selected color
                            tableColorPreviewView.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                        }
                        else
                        {
                            coTableColorValues = redValue+","+greenValue+","+blueValue;
                            // Set the background color of the color preview view to the selected color
                            coTableColorPreviewView.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                        }


                    }

                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SelectedBorderType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setToDefaults()
    {
        bgColorValues ="255,255,255";
        tableColorValues ="36,113,163";
        coTableColorValues ="34,129,8";
        bgColorPreviewView.setBackgroundColor(Color.rgb(255,255,255));
        tableColorPreviewView.setBackgroundColor(Color.rgb(36, 113, 163));
        coTableColorPreviewView.setBackgroundColor(Color.rgb(34, 129, 8));
        int position = adapter.getPosition("Groove Border");
        spinner.setSelection(position);
        getLogoSize.setText("60");
        getPdfBorderWidth.setText("1");
    }

    private String saveToInternalStorage(Bitmap bitmapImage){


        int originalWidth = bitmapImage.getWidth();
        int originalHeight = bitmapImage.getHeight();

        int newWidth = originalWidth / 2;
        int newHeight = originalHeight / 2;

        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmapImage, newWidth, newHeight, true);

        String imagenamam = uid+"_sign";

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,imagenamam+".png");

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