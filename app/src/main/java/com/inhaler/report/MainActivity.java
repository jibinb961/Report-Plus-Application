package com.inhaler.report;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telecom.StatusHints;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.text.style.ParagraphStyle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.IOException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.crypto.OutputStreamAesEncryption;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.ElementPropertyContainer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.GrooveBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.BorderRadius;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.renderer.IRenderer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private Button createAgain,PdfPath,PdfSend;
    private DatabaseReference ClassesRef;
    private DatabaseReference MarksRef,StudentsRef,DetailsRef,ExamsRef;
    private String classid,studentid,Remarks;
    private Integer aplus,a,bplus,b,fail,c;
    private Boolean enabledChart = false;
    private Integer examcount = 0,exactexamcount=0,activityexamcount=0,activityexactcount=0;
    private HashMap<String,String> studentmarks = new HashMap<>(); //using it to store each subject marks
    private HashMap<String,String> studentactivitymarks = new HashMap<>(); //using it to store each activity marks
    private HashMap<String, String> studentinfo = new HashMap<>();
    private HashMap<String,String> allactivemarks = new HashMap<>();
    private HashMap<String,Integer> gradeMap = new HashMap<>();
    private Bitmap chartBitmap;
    private ArrayList<String> allexams = new ArrayList<>(); //using
    private ArrayList<String> allactivityexams = new ArrayList<>();
    private String tablecolorarray[],bgcolorarray[],cotablecolorarray[];
    private ArrayList<String> subjectmarksarray = new ArrayList<>(); //using to store each subject marks.
    private ArrayList<String> activitymarksarray = new ArrayList<>(); //using to store each activitya,rks
    private HashMap<String,String> allexammarks = new HashMap<>(); //using to retrive marks from prev intent
    private HashMap<String,String> schoolinfo = new HashMap<>();
    private HashMap<String,String> pdfsettings = new HashMap<>();
    private Float OverallTotal=0f,OverallMax=0f;
    private FirebaseUser currentUser;
    private String uid,parentphonenumber,ClassName,ExamName;
    private AdView bannerAd;
//    private Document pdfdocument;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();




        classid = getIntent().getStringExtra("classid");
        studentid = getIntent().getStringExtra("studentid");
        Remarks = getIntent().getStringExtra("remarks");
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes").child(classid);
        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students").child(studentid);
        DetailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");


        StudentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("parentphone").exists())
                {
                    parentphonenumber = snapshot.child("parentphone").getValue(String.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        studentinfo = (HashMap<String, String>) getIntent().getSerializableExtra("studentinfo");
        schoolinfo = (HashMap<String, String>) getIntent().getSerializableExtra("schoolinfo");
        allexammarks = (HashMap<String, String>) getIntent().getSerializableExtra("allmarks");
        allactivemarks = (HashMap<String, String>) getIntent().getSerializableExtra("allactivemarks");
        pdfsettings = (HashMap<String, String>) getIntent().getSerializableExtra("pdfsettings");

        tablecolorarray = pdfsettings.get("tablecolor").split(",");
        cotablecolorarray = pdfsettings.get("cotablecolor").split(",");
        bgcolorarray = pdfsettings.get("fontcolor").split(",");



        //conveting hashmap into tree map to order exam names
        Map<String,String> exammap = new TreeMap<>(allexammarks);
        Map<String,String> activitymap = new TreeMap<>(allactivemarks);


        //for loop to count the numebr of exams, anfd store number of exams
        for (Map.Entry<String, String> entry : exammap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            allexams.add(key);
            subjectmarksarray.add(value);
            value = value.substring(1,value.length()-1);
            String[] subjectsandmarks = value.split(", ");
            for(int i=0;i<subjectsandmarks.length;i++)
            {
                String[] subjectandmarks= subjectsandmarks[i].split("=");
                subjectandmarks[0] = subjectandmarks[0].trim();
                if(studentmarks.get(subjectandmarks[0])==null)
                {
                    studentmarks.put(subjectandmarks[0].trim(),subjectandmarks[1]);
                }
                else
                {
                    String alreadymarks = studentmarks.get(subjectandmarks[0]);
                    studentmarks.put(subjectandmarks[0].trim(),alreadymarks+","+subjectandmarks[1]);

                }

            }
          
            examcount++;
            exactexamcount++;
        }


        //for loop to count the numebr of exams, and store number of exams
        try {
            for (Map.Entry<String, String> entry : activitymap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                allactivityexams.add(key);
                activitymarksarray.add(value);
                value = value.substring(1,value.length()-1);
                String[] subjectsandmarks = value.split(", ");
                for(int i=0;i<subjectsandmarks.length;i++)
                {

                    String[] subjectandmarks= subjectsandmarks[i].split("=");
                    subjectandmarks[0] = subjectandmarks[0].trim();
                    if(studentactivitymarks.get(subjectandmarks[0])==null)
                    {
                        studentactivitymarks.put(subjectandmarks[0].trim(),subjectandmarks[1]);
                    }
                    else
                    {
                        String alreadymarks = studentactivitymarks.get(subjectandmarks[0]);
                        studentactivitymarks.put(subjectandmarks[0].trim(),alreadymarks+","+subjectandmarks[1]);

                    }

                }

                activityexamcount++;
                activityexactcount++;
            }
        }
        catch (Exception e)
        {

        }



        bannerAd = (AdView) findViewById(R.id.pdf_ad_view);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        bannerAd.loadAd(adRequest);


        MarksRef = FirebaseDatabase.getInstance().getReference().child("Marks");
        createAgain = (Button) findViewById(R.id.create_again);
        PdfPath = (Button) findViewById(R.id.pdf_path);
        PdfSend = (Button) findViewById(R.id.pdf_send);

        PdfSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("activationkey").exists())
                        {
                            if (!(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)) {
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ReportPlus/"+studentinfo.get("studentname").
                                        replace(" ","")+studentinfo.get("rollnumber").trim()+".pdf");
                                Uri photoURI = FileProvider.getUriForFile(MainActivity.this  , BuildConfig.APPLICATION_ID + ".provider",file);

                                if(!(parentphonenumber==null))
                                {

                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.setType("text/plain");
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "This is a text message");

                                    sendIntent.setType("application/pdf");
                                    sendIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
                                    sendIntent.setPackage("com.whatsapp");
                                    sendIntent.putExtra("jid", parentphonenumber+"@s.whatsapp.net");
                                    startActivity(sendIntent);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "No parent phone number updated!", Toast.LENGTH_SHORT).show();
                                }



                            }
                            else
                            {
                                Uri contentUri = MediaStore.Files.getContentUri("external");

                                String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";

                                String[] selectionArgs = new String[]{Environment.DIRECTORY_DOCUMENTS + "/Report Plus/"};

                                Cursor cursor = getContentResolver().query(contentUri, null, selection, selectionArgs, null);

                                Uri uri = null;

                                if (cursor.getCount() == 0) {
                                    Toast.makeText(MainActivity.this, "No file found in \"" + Environment.DIRECTORY_DOCUMENTS + "/Report Plus/\"", Toast.LENGTH_LONG).show();
                                } else {
                                    while (cursor.moveToNext()) {
                                        String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));

                                        if (fileName.equals(studentinfo.get("studentname").
                                                replace(" ","")+studentinfo.get("rollnumber").trim()+".pdf")) {
                                            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                                            uri = ContentUris.withAppendedId(contentUri, id);

                                            if(!(parentphonenumber==null))
                                            {
                                                Intent sendIntent = new Intent();
                                                sendIntent.setAction(Intent.ACTION_SEND);
                                                sendIntent.setType("text/plain");
                                                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is a text message");

                                                sendIntent.setType("application/pdf");
                                                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                                sendIntent.setPackage("com.whatsapp");
                                                sendIntent.putExtra("jid", parentphonenumber+"@s.whatsapp.net");
                                                startActivity(sendIntent);
                                            }
                                            else

                                            {
                                                Toast.makeText(MainActivity.this, "No parent phone number specified!", Toast.LENGTH_SHORT).show();
                                            }


                                            break;
                                        }
                                    }

                                    if (uri == null) {
                                        Toast.makeText(MainActivity.this, "Check in Documents/Report Plus/ folder", Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            InputStream inputStream = getContentResolver().openInputStream(uri);

                                            int size = inputStream.available();

                                            byte[] bytes = new byte[size];


                                        } catch (IOException | FileNotFoundException e) {
                                            Toast.makeText(MainActivity.this, "Fail to read file", Toast.LENGTH_SHORT).show();
                                        } catch (java.io.IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }


                            }
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                            // set title
                            builder.setTitle("Premium Feature!");
                            builder.setMessage("This feature is only available for premium users!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            builder.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });



        createAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this,DashboardActivity.class);
                startActivity(intent1);
                finish();
            }
        });


        PdfPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ReportPlus/"+studentinfo.get("studentname").
                            replace(" ","")+studentinfo.get("rollnumber").trim()+".pdf");
                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this  , BuildConfig.APPLICATION_ID + ".provider",file);


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(photoURI, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
                else
                {
                    Uri contentUri = MediaStore.Files.getContentUri("external");

                    String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";

                    String[] selectionArgs = new String[]{Environment.DIRECTORY_DOCUMENTS + "/Report Plus/"};

                    Cursor cursor = getContentResolver().query(contentUri, null, selection, selectionArgs, null);

                    Uri uri = null;

                    if (cursor.getCount() == 0) {
                        Toast.makeText(MainActivity.this, "No file found in \"" + Environment.DIRECTORY_DOCUMENTS + "/Report Plus/\"", Toast.LENGTH_LONG).show();
                    } else {
                        while (cursor.moveToNext()) {
                            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));

                            if (fileName.equals(studentinfo.get("studentname").
                                    replace(" ","")+studentinfo.get("rollnumber").trim()+".pdf")) {
                                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                                uri = ContentUris.withAppendedId(contentUri, id);

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "application/pdf");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent);

                                break;
                            }
                        }

                        if (uri == null) {
                            Toast.makeText(MainActivity.this, "Check in Documents/Report Plus/ folder", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(uri);

                                int size = inputStream.available();

                                byte[] bytes = new byte[size];


                            } catch (IOException | FileNotFoundException e) {
                                Toast.makeText(MainActivity.this, "Fail to read file", Toast.LENGTH_SHORT).show();
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }

            }
        });

        ClassesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aplus = Integer.valueOf(snapshot.child("aplus").getValue(String.class));
                a = Integer.valueOf(snapshot.child("a").getValue(String.class));
                bplus = Integer.valueOf(snapshot.child("bplus").getValue(String.class));
                b = Integer.valueOf(snapshot.child("b").getValue(String.class));
                c = Integer.valueOf(snapshot.child("c").getValue(String.class));
                fail = Integer.valueOf(snapshot.child("fail").getValue(String.class));
                ClassName = snapshot.child("classname").getValue(String.class);

                gradeMap.put("a",a);
                gradeMap.put("aplus",aplus);
                gradeMap.put("b",b);
                gradeMap.put("bplus",bplus);
                gradeMap.put("fail",fail);
                gradeMap.put("c",c);

                try {
                    createPdfdebug();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


    private void createPdfdebug() throws FileNotFoundException {

        examcount = (examcount*2)+4;
        activityexamcount = (activityexamcount*2)+2;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//
            CreateSAamplePdf();
        }
        else
        {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,101);
        }

        String folder_main = "ReportPlus";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/" + folder_main,studentinfo.get("studentname").
                replace(" ","")+studentinfo.get("rollnumber").trim()+".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);

        Document document = new Document(pdfDocument);
        document.setRenderer(new CustomDocumentRenderer(document,pdfsettings));



        //Code to get the key school logo from internal storage

        ContextWrapper cw = new ContextWrapper(this);
        Bitmap logo=null;
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f1=new File(directory, currentUser.getUid()+".png");
            logo = BitmapFactory.decodeStream(new FileInputStream(f1));

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        if(logo!=null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            assert logo != null;
            logo.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] bitmapdata = stream.toByteArray();
            ImageData imageData = ImageDataFactory.create(bitmapdata);
            Image image = new Image(imageData);
            image.setHeight(60);
            image.setWidth(60);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image).setTextAlignment(TextAlignment.CENTER);
        }


        Border border1 = new GrooveBorder(2);
        border1.setColor(ColorConstants.BLACK);



        //student info
        document.add(new Paragraph(schoolinfo.get("schoolname") + " \n " + schoolinfo.get("schooladdress") + "\n Contact : " + schoolinfo.get("schoolcontact")).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Report Card " + studentinfo.get("academicyear")).setTextAlignment(TextAlignment.CENTER).setFontSize(14).setBold().setFontColor(new DeviceRgb(36, 113, 163)).setBold().setUnderline());
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Name : " + " " + studentinfo.get("studentname") + "     Class : " + studentinfo.get("classname")).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Roll No : " + studentinfo.get("rollnumber") + "     Parent's Name : " + studentinfo.get("fathername") + "     Class Teacher : " + studentinfo.get("classteacher")).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));

        float columnWidth =0f;
        columnWidth = (600/ Float.parseFloat(String.valueOf(examcount)));
        float eachColumnWidth[] = new float[examcount+1];
        ///this is working for android 10 -  pxil xl api 30
        //ref 1
        for(int i=0;i<examcount+1;i++)
        {
            eachColumnWidth[i] = columnWidth;
        }


        Table table = new Table(eachColumnWidth);


            table.addCell(new Cell(2, 2).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Subjects")
                            .setFontColor(ColorConstants.WHITE)));

        //adding exams - adding exam headings to the table
        for(int k=0;k<exactexamcount;k++)
        {
            table.addCell(new Cell(1, 2).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()),
                            Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph(allexams.get(k))
                            .setFontColor(ColorConstants.WHITE)));
        }
        //adding grand total column to the table

        table.addCell(new Cell(1, 2).
                setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                        Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Grand total")
                        .setFontColor(ColorConstants.WHITE)));
        table.addCell(new Cell(2, 2).
                setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                        Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Grade")
                        .setFontColor(ColorConstants.WHITE)));
        //adding obtained and max columns according to the exam count
        for(int j=0;j<exactexamcount;j++)
        {
            table.addCell(new Cell(1, 1).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Obt")
                            .setFontColor(ColorConstants.WHITE)));
            table.addCell(new Cell(1, 1).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Max")
                            .setFontColor(ColorConstants.WHITE)));
        }
        //adding total and max columns under grand total
        table.addCell(new Cell(1, 1).
                setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                        Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Total")
                        .setFontColor(ColorConstants.WHITE)));
        table.addCell(new Cell(1, 1).
                setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                        Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Max")
                        .setFontColor(ColorConstants.WHITE)));

        //adding subject marks, row by row
        Float OverallTotal=0f,OverallMax=0f;
        for (Map.Entry<String, String> entry1 : studentmarks.entrySet()) {
            Float totalmarks = 0f,maxmarks=0f;
            String key = entry1.getKey();
            String value = entry1.getValue();

            String marks[] = value.split(",");
            String summarks[] = new String[marks.length];

            boolean validsub = true;
            for(int j=0;j<marks.length;j++) // implement this feature in all recurring parts!
            {
                if(marks[j].equals("-"))
                {
                    validsub = false;
                }
                else
                {
                    validsub = true;
                    break;
                }
            }
            if(validsub) {
                table.addCell(new Cell(1, 2).add(new Paragraph(key)
                        .setFontColor(ColorConstants.BLACK)));

                for (int i = 0; i < marks.length; i++) {
                    if (marks[i].equals("?")) {
                        table.addCell(new Cell(1, 1).add(new Paragraph("Abs").setFontColor(ColorConstants.RED)));
                    } else {
                        table.addCell(new Cell(1, 1).add(new Paragraph(marks[i]).setFontColor(ColorConstants.BLACK)));

                    }
                    if (i % 2 == 0) {
                        if (marks[i].equals("-") || marks[i].equals("?")) {
                            marks[i] = "0";
                        }
                        totalmarks = totalmarks + Float.parseFloat(marks[i]);

                    } else {
                        if (marks[i].equals("-") || marks[i].equals("?")) {
                            marks[i] = "0";
                        }
                        maxmarks = maxmarks + Float.parseFloat(marks[i]);
                    }

                }
                OverallTotal = OverallTotal + totalmarks;
                OverallMax = OverallMax + maxmarks;
                Integer grade =  (int) ((totalmarks/maxmarks)*100);
                table.addCell(new Cell(1, 1).add(new Paragraph(totalmarks.toString()).setFontColor(ColorConstants.BLACK)));
                table.addCell(new Cell(1, 1).add(new Paragraph(maxmarks.toString()).setFontColor(ColorConstants.BLACK)));
                if(grade > gradeMap.get("aplus"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("A+").setFontColor(ColorConstants.BLACK)));
                }
                else if(grade> gradeMap.get("a"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("A").setFontColor(ColorConstants.BLACK)));

                }
                else if(grade > gradeMap.get("bplus"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("B+").setFontColor(ColorConstants.BLACK)));

                }
                else if(grade > gradeMap.get("b"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("B").setFontColor(ColorConstants.BLACK)));

                }
                else if(grade > gradeMap.get("c"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("C").setFontColor(ColorConstants.BLACK)));

                }
                else if(grade > gradeMap.get("fail"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("D").setFontColor(ColorConstants.BLACK)));

                }
                else
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("F").setFontColor(ColorConstants.RED)));

                }

            }
        }
        Integer totaloutofmarks = Math.round(OverallMax);
        Integer columncountforgrandtotal = examcount -2;
        Integer grade1 =  (int) ((OverallTotal/OverallMax)*100);
        table.addCell(new Cell(1, columncountforgrandtotal).add(new Paragraph("Grand Total\t ").setFontColor(ColorConstants.BLACK).setBold().setTextAlignment(TextAlignment.RIGHT)));
        table.addCell(new Cell(1, 1).add(new Paragraph(OverallTotal.toString()).setFontColor(ColorConstants.BLACK).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setBold()));
        table.addCell(new Cell(1, 1).add(new Paragraph(totaloutofmarks.toString()).setFontColor(ColorConstants.BLACK).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setBold()));
        if(grade1 > gradeMap.get("aplus"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("A+").setFontColor(ColorConstants.BLACK)));
        }
        else if(grade1> gradeMap.get("a"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("A").setFontColor(ColorConstants.BLACK)));

        }
        else if(grade1 > gradeMap.get("bplus"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("B+").setFontColor(ColorConstants.BLACK)));

        }
        else if(grade1 > gradeMap.get("b"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("B").setFontColor(ColorConstants.BLACK)));

        }
        else if(grade1 > gradeMap.get("c"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("C").setFontColor(ColorConstants.BLACK)));

        }
        else if(grade1 > gradeMap.get("fail"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("D").setFontColor(ColorConstants.BLACK)));

        }
        else
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("F").setFontColor(ColorConstants.RED)));

        }


        table.setBorder(border1);
        document.add(table);

        document.add(new Paragraph("\n"));
        //working for api 29

        float activecolumnWidth =0f;
        activecolumnWidth = (600/ Float.parseFloat(String.valueOf(activityexamcount)));
        float eachactiveColumnWidth[] = new float[activityexamcount];
        ///this is working for android 10 -  pxil xl api 30
        for(int i=0;i<activityexamcount;i++)
        {
            eachactiveColumnWidth[i] = activecolumnWidth;
        }


        if(!allactivityexams.isEmpty())
        {
            Table table2 = new Table(eachactiveColumnWidth);
            table2.addCell(new Cell(1, 2).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(cotablecolorarray[0].trim()),
                            Integer.parseInt(cotablecolorarray[1].trim()), Integer.parseInt(cotablecolorarray[2].trim()))).add(new Paragraph("Co Scholastic Areas")
                            .setFontColor(ColorConstants.WHITE)));

            //adding exams - adding exam(acitvity) headings to the table
            for(int k=0;k<activityexactcount;k++)
            {

                table2.addCell(new Cell(1, 2).
                        setBackgroundColor(new DeviceRgb(Integer.parseInt(cotablecolorarray[0].trim()),
                                Integer.parseInt(cotablecolorarray[1].trim()), Integer.parseInt(cotablecolorarray[2].trim()))).add(new Paragraph(allactivityexams.get(k))
                                .setFontColor(ColorConstants.WHITE)));
            }
            for (Map.Entry<String, String> entry1 : studentactivitymarks.entrySet()) {
                String key = entry1.getKey();
                String value = entry1.getValue();
                table2.addCell(new Cell(1, 2).add(new Paragraph(key)
                        .setFontColor(ColorConstants.BLACK)));
                String activemarks[] = value.split(",");
                for(int j=0;j<activemarks.length;j++)
                {
                    table2.addCell(new Cell(1, 2).add(new Paragraph(activemarks[j])
                            .setFontColor(ColorConstants.BLACK)));
                }







            }
            if(!(studentinfo.get("studentheight").equals("")))
            {
                table2.addCell(new Cell(1, 3).add(new Paragraph("Height")
                        .setFontColor(ColorConstants.BLACK)));
                table2.addCell(new Cell(1, 3).add(new Paragraph(studentinfo.get("studentheight"))
                        .setFontColor(ColorConstants.BLACK)));
            }

            if(!(studentinfo.get("studentweight").equals("")))
            {
                table2.addCell(new Cell(1, 3).add(new Paragraph("Weight")
                        .setFontColor(ColorConstants.BLACK)));
                table2.addCell(new Cell(1, 3).add(new Paragraph(studentinfo.get("studentweight"))
                        .setFontColor(ColorConstants.BLACK)));
            }





            table2.setBorder(border1);

            document.add(table2);
        }
        if(!Remarks.isEmpty())
        {
            document.add(new Paragraph("Remarks : "+Remarks).setTextAlignment(TextAlignment.LEFT));
        }

        if(pdfsettings.get("signature").equals("true"))
        {
            ContextWrapper contextWrapper = new ContextWrapper(this);
            Bitmap sign = null;
            File dir = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
            try {
                File f1 = new File(dir, currentUser.getUid() + "_sign.png");
                sign = BitmapFactory.decodeStream(new FileInputStream(f1));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(sign!=null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                assert sign != null;
                sign.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                ImageData imageData = ImageDataFactory.create(bitmapdata);
                Image image = new Image(imageData);
                document.add(new Paragraph("\n"));
                image.setHeight(60);
                image.setWidth(90);
                image.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                document.add(image).setTextAlignment(TextAlignment.CENTER);
                document.add(new Paragraph(pdfsettings.get("signeename")).setTextAlignment(TextAlignment.RIGHT)
                        .setMarginRight(20).setFontColor(ColorConstants.BLACK).setBold().setMultipliedLeading(0.5f));
                document.add(new Paragraph(pdfsettings.get("signeerole")).setTextAlignment(TextAlignment.RIGHT)
                        .setMarginRight(20).setFontColor(ColorConstants.DARK_GRAY).setMultipliedLeading(0.5f));
            }
            else
            {
                Toast.makeText(MainActivity.this, "no signature", Toast.LENGTH_SHORT).show();
            }




        }




        BorderRadius borderRadius = new BorderRadius(7);

        Toast.makeText(this, "PDF Generated Succesfully", Toast.LENGTH_LONG).show();
        document.close();
        DetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count =0;
                if(snapshot.child("pdfcount").exists())
                {
                    String pdfcount = snapshot.child("pdfcount").getValue(String.class);
                    count = Integer.parseInt(pdfcount);
                    count = count + 1;
                    DetailsRef.child("pdfcount").setValue(String.valueOf(count));

                }
                else
                {
                    DetailsRef.child("pdfcount").setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    public void checkPermission(String permission, int requestCode)
    {

        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {



        }
    }

//    @RequiresApi(api >= Build.VERSION_CODES.Q)
    boolean checkPermissionR() {


return true;
    }



    void requestPermisson()
    {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s",new Object[]{getApplicationContext().getPackageName()})));
            startActivity(intent);

        }catch (Exception e)
        {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void CreatePdfforQ()
    {
        ClassesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aplus = Integer.valueOf(snapshot.child("aplus").getValue(String.class));
                a = Integer.valueOf(snapshot.child("a").getValue(String.class));
                bplus = Integer.valueOf(snapshot.child("bplus").getValue(String.class));
                b = Integer.valueOf(snapshot.child("b").getValue(String.class));
                fail = Integer.valueOf(snapshot.child("fail").getValue(String.class));


                gradeMap.put("a",a);
                gradeMap.put("aplus",aplus);
                gradeMap.put("b",b);
                gradeMap.put("bplus",bplus);
                gradeMap.put("fail",fail);


                    CreateSAamplePdf();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void CreateSAamplePdf() {

        Uri contentUri = MediaStore.Files.getContentUri("external");

        String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";

        String[] selectionArgs = new String[]{Environment.DIRECTORY_DOCUMENTS + "/Report Plus/"};

        Cursor cursor = getContentResolver().query(contentUri, null, selection, selectionArgs, null);

        Uri uri1 = null;

        if ((cursor.getCount() == 0)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                createPDF();
            }
        } else {

            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));

                if (fileName.equals(studentinfo.get("studentname").
                        replace(" ","")+studentinfo.get("rollnumber").trim()+".pdf")) {
                    //must include extension
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                    try {
                        uri1 = ContentUris.withAppendedId(contentUri, id);
                        OutputStream outputStream = getContentResolver().openOutputStream(uri1, "rwt");      //overwrite mode, see below
                        PdfWriter pdfWrite1r = new PdfWriter(outputStream);
                        PdfDocument pdfDocument = new PdfDocument(pdfWrite1r);
                        Document document = new Document(pdfDocument);
                        document.add(new Paragraph("thi sis owerriten"));
                        document.close();
                        outputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }


                    break;
                }

            }

            if (uri1 == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    createPDF();
                }

            } else {
                try {

                    OutputStream outputStream = getContentResolver().openOutputStream(uri1, "rwt");      //overwrite mode, see below
                    PdfWriter pdfWrite1r = new PdfWriter(outputStream);
                    PdfDocument pdfDocument = new PdfDocument(pdfWrite1r);
                    Document document = new Document(pdfDocument);
                    document.setRenderer(new CustomDocumentRenderer(document,pdfsettings));

                    document = Rewritepdf(document);



                    document.close();

                    outputStream.close();

                } catch (IOException | FileNotFoundException e) {
                    Toast.makeText(MainActivity.this, "Fail to write file", Toast.LENGTH_SHORT).show();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void createPDF()
    {
        try {
            ContentValues values = new ContentValues();

            values.put(MediaStore.MediaColumns.DISPLAY_NAME, studentinfo.get("studentname").
                    replace(" ", "") + studentinfo.get("rollnumber").trim() + ".pdf");       //file name
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");        //file extension, will automatically add to file
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Report Plus/");     //end "/" is not mandatory


            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);      //important!

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            PdfWriter pdfWrite1r = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWrite1r);
            Document document = new Document(pdfDocument);

            document.setRenderer(new CustomDocumentRenderer(document,pdfsettings));

            //code to fetch the school logo
            ContextWrapper cw = new ContextWrapper(this);
            Bitmap logo = null;
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            try {
                File f1 = new File(directory, currentUser.getUid() + ".png");
                logo = BitmapFactory.decodeStream(new FileInputStream(f1));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(logo!=null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                assert logo != null;
                logo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                ImageData imageData = ImageDataFactory.create(bitmapdata);
                Image image = new Image(imageData);
                image.setHeight(60);
                image.setWidth(60);
                image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(image).setTextAlignment(TextAlignment.CENTER);
            }

            Border border1 = new GrooveBorder(2);
            border1.setColor(ColorConstants.BLACK);



            //student info
            document.add(new Paragraph(schoolinfo.get("schoolname") + " \n " + schoolinfo.get("schooladdress") + "\n Contact : " + schoolinfo.get("schoolcontact")).setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Report Card " + studentinfo.get("academicyear")).setTextAlignment(TextAlignment.CENTER).setFontSize(14).setBold().setFontColor(new DeviceRgb(36, 113, 163)).setBold().setUnderline());
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Name : " + " " + studentinfo.get("studentname") + "     Class : " + studentinfo.get("classname")).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Roll No : " + studentinfo.get("rollnumber") + "     Parent's Name : " + studentinfo.get("fathername") + "     Class Teacher : " + studentinfo.get("classteacher")).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));
             //ref 2
            float columnWidth =0f;
            columnWidth = (600/ Float.parseFloat(String.valueOf(examcount)));
            float eachColumnWidth[] = new float[examcount+1];
            ///this is working for android 10 -  pxil xl api 30
            for(int i=0;i<examcount+1;i++)
            {
                eachColumnWidth[i] = columnWidth;
            }


            Table table = new Table(eachColumnWidth);

            table.addCell(new Cell(2, 2).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Subjects")
                            .setFontColor(ColorConstants.WHITE)));

            //adding exams - adding exam headings to the table
            for(int k=0;k<exactexamcount;k++)
            {
                table.addCell(new Cell(1, 2).
                        setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                                Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph(allexams.get(k))
                                .setFontColor(ColorConstants.WHITE)));
            }
            //adding grand total column to the table

            table.addCell(new Cell(1, 2).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Grand total")
                            .setFontColor(ColorConstants.WHITE)));
            table.addCell(new Cell(2, 2).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Grade")
                            .setFontColor(ColorConstants.WHITE)));
            //adding obtained and max columns according to the exam count
            for(int j=0;j<exactexamcount;j++)
            {
                table.addCell(new Cell(1, 1).
                        setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                                Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Obt")
                                .setFontColor(ColorConstants.WHITE)));
                table.addCell(new Cell(1, 1).
                        setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                                Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Max")
                                .setFontColor(ColorConstants.WHITE)));
            }
            //adding total and max columns under grand total
            table.addCell(new Cell(1, 1).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Total")
                            .setFontColor(ColorConstants.WHITE)));
            table.addCell(new Cell(1, 1).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Max")
                            .setFontColor(ColorConstants.WHITE)));

            //adding subject marks, row by row
            Float OverallTotal=0f,OverallMax=0f;
            for (Map.Entry<String, String> entry1 : studentmarks.entrySet()) {
                Float totalmarks = 0f,maxmarks=0f;
                String key = entry1.getKey();
                String value = entry1.getValue();

                String marks[] = value.split(",");
                String summarks[] = new String[marks.length];
                boolean validsub = true;
                for(int j=0;j<marks.length;j++) // implement this feature in all recurring parts!
                {
                    if(marks[j].equals("-"))
                    {
                        validsub = false;
                    }
                    else
                    {
                        validsub = true;
                        break;
                    }
                }
                if(validsub) {
                    table.addCell(new Cell(1, 2).add(new Paragraph(key)
                            .setFontColor(ColorConstants.BLACK)));

                    for (int i = 0; i < marks.length; i++) {
                        if (marks[i].equals("?")) {
                            table.addCell(new Cell(1, 1).add(new Paragraph("Abs").setFontColor(ColorConstants.RED)));
                        } else {
                            table.addCell(new Cell(1, 1).add(new Paragraph(marks[i]).setFontColor(ColorConstants.BLACK)));

                        }
                        if (i % 2 == 0) {
                            if (marks[i].equals("-") || marks[i].equals("?")) {
                                marks[i] = "0";
                            }
                            totalmarks = totalmarks + Float.parseFloat(marks[i]);

                        } else {
                            if (marks[i].equals("-") || marks[i].equals("?")) {
                                marks[i] = "0";
                            }
                            maxmarks = maxmarks + Float.parseFloat(marks[i]);
                        }


                    }
                    OverallTotal = OverallTotal + totalmarks;
                    OverallMax = OverallMax + maxmarks;
                    Integer grade =  (int) ((totalmarks/maxmarks)*100);
                    table.addCell(new Cell(1, 1).add(new Paragraph(totalmarks.toString()).setFontColor(ColorConstants.BLACK)));
                    table.addCell(new Cell(1, 1).add(new Paragraph(maxmarks.toString()).setFontColor(ColorConstants.BLACK)));
                    if(grade > gradeMap.get("aplus"))
                    {
                        table.addCell(new Cell(1, 2).add(new Paragraph("A+").setFontColor(ColorConstants.BLACK)));
                    }
                    else if(grade> gradeMap.get("a"))
                    {
                        table.addCell(new Cell(1, 2).add(new Paragraph("A").setFontColor(ColorConstants.BLACK)));

                    }
                    else if(grade > gradeMap.get("bplus"))
                    {
                        table.addCell(new Cell(1, 2).add(new Paragraph("B+").setFontColor(ColorConstants.BLACK)));

                    }
                    else if(grade > gradeMap.get("b"))
                    {
                        table.addCell(new Cell(1, 2).add(new Paragraph("B").setFontColor(ColorConstants.BLACK)));

                    }
                    else if(grade > gradeMap.get("c"))
                    {
                        table.addCell(new Cell(1, 2).add(new Paragraph("C").setFontColor(ColorConstants.BLACK)));

                    }
                    else if(grade > gradeMap.get("fail"))
                    {
                        table.addCell(new Cell(1, 2).add(new Paragraph("D").setFontColor(ColorConstants.BLACK)));

                    }
                    else
                    {
                        table.addCell(new Cell(1, 2).add(new Paragraph("F").setFontColor(ColorConstants.RED)));

                    }

                }
            }
            Integer totaloutofmarks = Math.round(OverallMax);
            Integer columncountforgrandtotal = examcount -2;
            Integer grade1 =  (int) ((OverallTotal/OverallMax)*100);
            table.addCell(new Cell(1, columncountforgrandtotal).add(new Paragraph("Grand Total\t ").setFontColor(ColorConstants.BLACK).setBold().setTextAlignment(TextAlignment.RIGHT)));
            table.addCell(new Cell(1, 1).add(new Paragraph(OverallTotal.toString()).setFontColor(ColorConstants.BLACK).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setBold()));
            table.addCell(new Cell(1, 1).add(new Paragraph(totaloutofmarks.toString()).setFontColor(ColorConstants.BLACK).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setBold()));
            if(grade1 > gradeMap.get("aplus"))
            {
                table.addCell(new Cell(1, 2).add(new Paragraph("A+").setFontColor(ColorConstants.BLACK)));
            }
            else if(grade1> gradeMap.get("a"))
            {
                table.addCell(new Cell(1, 2).add(new Paragraph("A").setFontColor(ColorConstants.BLACK)));

            }
            else if(grade1 > gradeMap.get("bplus"))
            {
                table.addCell(new Cell(1, 2).add(new Paragraph("B+").setFontColor(ColorConstants.BLACK)));

            }
            else if(grade1 > gradeMap.get("b"))
            {
                table.addCell(new Cell(1, 2).add(new Paragraph("B").setFontColor(ColorConstants.BLACK)));

            }
            else if(grade1 > gradeMap.get("c"))
            {
                table.addCell(new Cell(1, 2).add(new Paragraph("C").setFontColor(ColorConstants.BLACK)));

            }
            else if(grade1 > gradeMap.get("fail"))
            {
                table.addCell(new Cell(1, 2).add(new Paragraph("D").setFontColor(ColorConstants.BLACK)));

            }
            else
            {
                table.addCell(new Cell(1, 2).add(new Paragraph("F").setFontColor(ColorConstants.RED)));

            }


            table.setBorder(border1);
            document.add(table);

            document.add(new Paragraph("\n"));
            float activecolumnWidth =0f;
            activecolumnWidth = (600/ Float.parseFloat(String.valueOf(activityexamcount)));
            float eachactiveColumnWidth[] = new float[activityexamcount];
            ///this is working for android 10 -  pxil xl api 30
            for(int i=0;i<activityexamcount;i++)
            {
                eachactiveColumnWidth[i] = activecolumnWidth;
            }


            if(!allactivityexams.isEmpty())
            {
                Table table2 = new Table(eachactiveColumnWidth);
                table2.addCell(new Cell(1, 2).
                        setBackgroundColor(new DeviceRgb(Integer.parseInt(cotablecolorarray[0].trim()),
                                Integer.parseInt(cotablecolorarray[1].trim()), Integer.parseInt(cotablecolorarray[2].trim()))).add(new Paragraph("Co Scholastic Areas")
                                .setFontColor(ColorConstants.WHITE)));

                //adding exams - adding exam(acitvity) headings to the table
                for(int k=0;k<activityexactcount;k++)
                {

                    table2.addCell(new Cell(1, 2).
                            setBackgroundColor(new DeviceRgb(Integer.parseInt(cotablecolorarray[0].trim()),
                                    Integer.parseInt(cotablecolorarray[1].trim()), Integer.parseInt(cotablecolorarray[2].trim()))).add(new Paragraph(allactivityexams.get(k))
                                    .setFontColor(ColorConstants.WHITE)));
                }
                for (Map.Entry<String, String> entry1 : studentactivitymarks.entrySet()) {
                    String key = entry1.getKey();
                    String value = entry1.getValue();
                    table2.addCell(new Cell(1, 2).add(new Paragraph(key)
                            .setFontColor(ColorConstants.BLACK)));
                    String activemarks[] = value.split(",");
                    for(int j=0;j<activityexactcount;j++)
                    {
                        table2.addCell(new Cell(1, 2).add(new Paragraph(activemarks[j])
                                .setFontColor(ColorConstants.BLACK)));
                    }







                }
                if(!(studentinfo.get("studentheight").equals("")))
                {
                    table2.addCell(new Cell(1, (int) activecolumnWidth).add(new Paragraph("Height : "+studentinfo.get("studentheight"))
                            .setFontColor(ColorConstants.BLACK)));

                }

                if(!(studentinfo.get("studentweight").equals("")))
                {
                    table2.addCell(new Cell(1, (int)activecolumnWidth).add(new Paragraph("Weight : "+studentinfo.get("studentweight"))
                            .setFontColor(ColorConstants.BLACK)));

                }





                table2.setBorder(border1);

                document.add(table2);
            }
            if(!Remarks.isEmpty())
            {
                document.add(new Paragraph("Remarks : "+Remarks).setTextAlignment(TextAlignment.LEFT));

            }
            if(pdfsettings.get("signature").equals("true"))
            {
                ContextWrapper contextWrapper = new ContextWrapper(this);
                Bitmap sign = null;
                File dir = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
                try {
                    File f1 = new File(dir, currentUser.getUid() + "_sign.png");
                    sign = BitmapFactory.decodeStream(new FileInputStream(f1));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if(sign!=null)
                {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    assert sign != null;
                    sign.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();
                    ImageData imageData = ImageDataFactory.create(bitmapdata);
                    Image image = new Image(imageData);
                    document.add(new Paragraph("\n"));
                    image.setHeight(60);
                    image.setWidth(90);
                    image.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                    document.add(image).setTextAlignment(TextAlignment.CENTER);
                    document.add(new Paragraph(pdfsettings.get("signeename")).setTextAlignment(TextAlignment.RIGHT)
                            .setMarginRight(20).setFontColor(ColorConstants.BLACK).setBold().setMultipliedLeading(0.5f));
                    document.add(new Paragraph(pdfsettings.get("signeerole")).setTextAlignment(TextAlignment.RIGHT)
                            .setMarginRight(20).setFontColor(ColorConstants.DARK_GRAY).setMultipliedLeading(0.5f));
                }
                else
                {
                    Toast.makeText(MainActivity.this, "no signature", Toast.LENGTH_SHORT).show();
                }
            }


            BorderRadius borderRadius = new BorderRadius(7);

            Toast.makeText(this, "PDF Generated Succesfully", Toast.LENGTH_LONG).show();
            document.close();

            outputStream.close();

            DetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count =0;
                    if(snapshot.child("pdfcount").exists())
                    {
                        String pdfcount = snapshot.child("pdfcount").getValue(String.class);
                        count = Integer.parseInt(pdfcount);
                        count = count + 1;
                        DetailsRef.child("pdfcount").setValue(String.valueOf(count));

                    }
                    else
                    {
                        DetailsRef.child("pdfcount").setValue("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public Document Rewritepdf(Document document)
    {
        ContextWrapper cw = new ContextWrapper(this);
        Bitmap logo = null;
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f1 = new File(directory, currentUser.getUid() + ".png");
            logo = BitmapFactory.decodeStream(new FileInputStream(f1));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(logo!=null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            assert logo != null;
            logo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            ImageData imageData = ImageDataFactory.create(bitmapdata);
            Image image = new Image(imageData);
            image.setHeight(60);
            image.setWidth(60);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image).setTextAlignment(TextAlignment.CENTER);
        }


        Border border1 = new GrooveBorder(2);
        border1.setColor(ColorConstants.BLACK);





        //student info
        document.add(new Paragraph(schoolinfo.get("schoolname") + " \n " + schoolinfo.get("schooladdress") + "\n Contact : " + schoolinfo.get("schoolcontact")).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Report Card " + studentinfo.get("academicyear")).setTextAlignment(TextAlignment.CENTER).setFontSize(14).setBold().setFontColor(new DeviceRgb(36, 113, 163)).setBold().setUnderline());
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Name : " + " " + studentinfo.get("studentname") + "     Class : " + studentinfo.get("classname")).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Roll No : " + studentinfo.get("rollnumber") + "     Parent's Name : " + studentinfo.get("fathername") + "     Class Teacher : " + studentinfo.get("classteacher")).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));
        //ref 3
        float columnWidth =0f;
        columnWidth = (600/ Float.parseFloat(String.valueOf(examcount)));
        float eachColumnWidth[] = new float[examcount+1];
        ///this is working for android 10 -  pxil xl api 30
        for(int i=0;i<examcount+1;i++)
        {
           eachColumnWidth[i] = columnWidth;
        }


        Table table = new Table(eachColumnWidth);

        table.addCell(new Cell(2, 2).
                setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                        Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Subjects")
                .setFontColor(ColorConstants.WHITE)));

        //adding exams - adding exam headings to the table
        for(int k=0;k<exactexamcount;k++)
        {
            table.addCell(new Cell(1, 2).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph(allexams.get(k))
                            .setFontColor(ColorConstants.WHITE)));
        }
        //adding grand total column to the table

        table.addCell(new Cell(1, 2).
                setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                        Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Grand total")
                .setFontColor(ColorConstants.WHITE)));
        table.addCell(new Cell(2, 2).
                setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                        Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Grade")
                        .setFontColor(ColorConstants.WHITE)));

        //adding obtained and max columns according to the exam count
        for(int j=0;j<exactexamcount;j++)
        {
            table.addCell(new Cell(1, 1).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Obt")
                            .setFontColor(ColorConstants.WHITE)));
            table.addCell(new Cell(1, 1).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                            Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Max")
                            .setFontColor(ColorConstants.WHITE)));
        }
        //adding total and max columns under grand total
        table.addCell(new Cell(1, 1).
                setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                        Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Total")
                .setFontColor(ColorConstants.WHITE)));
        table.addCell(new Cell(1, 1).
                setBackgroundColor(new DeviceRgb(Integer.parseInt(tablecolorarray[0].trim()),
                        Integer.parseInt(tablecolorarray[1].trim()), Integer.parseInt(tablecolorarray[2].trim()))).add(new Paragraph("Max")
                .setFontColor(ColorConstants.WHITE)));


        //adding subject marks, row by row
        Float OverallTotal=0f,OverallMax=0f;
        for (Map.Entry<String, String> entry1 : studentmarks.entrySet()) {
            Float totalmarks = 0f,maxmarks=0f;
            String key = entry1.getKey();
            String value = entry1.getValue();

            String marks[] = value.split(",");
            String summarks[] = new String[marks.length];
            boolean validsub = true;
            for(int j=0;j<marks.length;j++) // implement this feature in all recurring parts!
            {
                if(marks[j].equals("-"))
                {
                    validsub = false;
                }
                else
                {
                    validsub = true;
                    break;
                }
            }
            if(validsub)
            {
                table.addCell(new Cell(1, 2).add(new Paragraph(key)
                        .setFontColor(ColorConstants.BLACK)));
                for(int i=0;i<marks.length;i++)
                {
                    if(marks[i].equals("?"))
                    {
                        table.addCell(new Cell(1, 1).add(new Paragraph("Abs").setFontColor(ColorConstants.RED)));
                    }
                    else
                    {
                        table.addCell(new Cell(1, 1).add(new Paragraph(marks[i]).setFontColor(ColorConstants.BLACK)));

                    }
                    if(i%2==0)
                    {
                        if(marks[i].equals("-") || marks[i].equals("?"))
                        {
                            marks[i]="0";
                        }
                        totalmarks = totalmarks + Float.parseFloat(marks[i]);

                    }
                    else
                    {
                        if(marks[i].equals("-") || marks[i].equals("?"))
                        {
                            marks[i]="0";
                        }
                        maxmarks = maxmarks + Float.parseFloat(marks[i]);
                    }



                }
                OverallTotal = OverallTotal + totalmarks;
                OverallMax = OverallMax + maxmarks;
                Integer grade =  (int) ((totalmarks/maxmarks)*100);
                table.addCell(new Cell(1, 1).add(new Paragraph(totalmarks.toString()).setFontColor(ColorConstants.BLACK)));
                table.addCell(new Cell(1, 1).add(new Paragraph(maxmarks.toString()).setFontColor(ColorConstants.BLACK)));
                if(grade > gradeMap.get("aplus"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("A+").setFontColor(ColorConstants.BLACK)));
                }
                else if(grade> gradeMap.get("a"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("A").setFontColor(ColorConstants.BLACK)));

                }
                else if(grade > gradeMap.get("bplus"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("B+").setFontColor(ColorConstants.BLACK)));

                }
                else if(grade > gradeMap.get("b"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("B").setFontColor(ColorConstants.BLACK)));

                }
                else if(grade > gradeMap.get("c"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("C").setFontColor(ColorConstants.BLACK)));

                }
                else if(grade > gradeMap.get("fail"))
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("D").setFontColor(ColorConstants.BLACK)));

                }
                else
                {
                    table.addCell(new Cell(1, 2).add(new Paragraph("F").setFontColor(ColorConstants.RED)));

                }
            }


        }
        Integer totaloutofmarks = Math.round(OverallMax);
        Integer columncountforgrandtotal = examcount -2;
        Integer grade1 =  (int) ((OverallTotal/OverallMax)*100);

        table.addCell(new Cell(1, columncountforgrandtotal).add(new Paragraph("Grand Total\t ").setFontColor(ColorConstants.BLACK).setBold().setTextAlignment(TextAlignment.RIGHT)));
        table.addCell(new Cell(1, 1).add(new Paragraph(OverallTotal.toString()).setFontColor(ColorConstants.BLACK).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setBold()));
        table.addCell(new Cell(1, 1).add(new Paragraph(totaloutofmarks.toString()).setFontColor(ColorConstants.BLACK).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setBold()));

        if(grade1 > gradeMap.get("aplus"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("A+").setFontColor(ColorConstants.BLACK)));
        }
        else if(grade1> gradeMap.get("a"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("A").setFontColor(ColorConstants.BLACK)));

        }
        else if(grade1 > gradeMap.get("bplus"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("B+").setFontColor(ColorConstants.BLACK)));

        }
        else if(grade1 > gradeMap.get("b"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("B").setFontColor(ColorConstants.BLACK)));

        }
        else if(grade1 > gradeMap.get("c"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("C").setFontColor(ColorConstants.BLACK)));

        }
        else if(grade1 > gradeMap.get("fail"))
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("D").setFontColor(ColorConstants.BLACK)));

        }
        else
        {
            table.addCell(new Cell(1, 2).add(new Paragraph("F").setFontColor(ColorConstants.RED)));

        }


        //working set
        table.setBorder(border1);
        document.add(table);


        document.add(new Paragraph("\n"));

        float activecolumnWidth =0f;
        activecolumnWidth = (600/ Float.parseFloat(String.valueOf(activityexamcount)));
        float eachactiveColumnWidth[] = new float[activityexamcount];
        ///this is working for android 10 -  pxil xl api 30
        for(int i=0;i<activityexamcount;i++)
        {
            eachactiveColumnWidth[i] = activecolumnWidth;
        }


        if(!allactivityexams.isEmpty())
        {
            Table table2 = new Table(eachactiveColumnWidth);
            table2.addCell(new Cell(1, 2).
                    setBackgroundColor(new DeviceRgb(Integer.parseInt(cotablecolorarray[0].trim()),
                            Integer.parseInt(cotablecolorarray[1].trim()), Integer.parseInt(cotablecolorarray[2].trim()))).add(new Paragraph("Co Scholastic Areas")
                            .setFontColor(ColorConstants.WHITE)));

            //adding exams - adding exam(acitvity) headings to the table
            for(int k=0;k<activityexactcount;k++)
            {

                table2.addCell(new Cell(1, 2).
                        setBackgroundColor(new DeviceRgb(Integer.parseInt(cotablecolorarray[0].trim()),
                                Integer.parseInt(cotablecolorarray[1].trim()), Integer.parseInt(cotablecolorarray[2].trim()))).add(new Paragraph(allactivityexams.get(k))
                                .setFontColor(ColorConstants.WHITE)));
            }
            for (Map.Entry<String, String> entry1 : studentactivitymarks.entrySet()) {
                String key = entry1.getKey();
                String value = entry1.getValue();
                table2.addCell(new Cell(1, 2).add(new Paragraph(key)
                        .setFontColor(ColorConstants.BLACK)));
                String activemarks[] = value.split(",");
                for(int j=0;j<activityexactcount;j++)
                {
                    table2.addCell(new Cell(1, 2).add(new Paragraph(activemarks[j])
                            .setFontColor(ColorConstants.BLACK)));
                }







            }
            if(!(studentinfo.get("studentheight").equals("")))
            {
                table2.addCell(new Cell(1, (int) activecolumnWidth).add(new Paragraph("Height : "+studentinfo.get("studentheight"))
                        .setFontColor(ColorConstants.BLACK)));

            }

            if(!(studentinfo.get("studentweight").equals("")))
            {
                table2.addCell(new Cell(1, (int)activecolumnWidth).add(new Paragraph("Weight : "+studentinfo.get("studentweight"))
                        .setFontColor(ColorConstants.BLACK)));

            }





            table2.setBorder(border1);

            document.add(table2);

        }
        if(!Remarks.isEmpty())
        {
            document.add(new Paragraph("Remarks : "+Remarks).setTextAlignment(TextAlignment.LEFT));

        }
        if(pdfsettings.get("signature").equals("true"))
        {
            ContextWrapper contextWrapper = new ContextWrapper(this);
            Bitmap sign = null;
            File dir = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
            try {
                File f1 = new File(dir, currentUser.getUid() + "_sign.png");
                sign = BitmapFactory.decodeStream(new FileInputStream(f1));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(sign!=null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                assert sign != null;
                sign.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                ImageData imageData = ImageDataFactory.create(bitmapdata);
                Image image = new Image(imageData);
                document.add(new Paragraph("\n"));
                image.setHeight(60);
                image.setWidth(90);
                image.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                document.add(image).setTextAlignment(TextAlignment.CENTER);

                document.add(new Paragraph(pdfsettings.get("signeename")).setTextAlignment(TextAlignment.RIGHT)
                        .setMarginRight(20).setFontColor(ColorConstants.BLACK).setBold().setMultipliedLeading(0.5f));
                document.add(new Paragraph(pdfsettings.get("signeerole")).setTextAlignment(TextAlignment.RIGHT)
                        .setMarginRight(20).setFontColor(ColorConstants.DARK_GRAY).setMultipliedLeading(0.5f));
            }
            else
            {
                Toast.makeText(MainActivity.this, "no signature", Toast.LENGTH_SHORT).show();
            }
        }


        BorderRadius borderRadius = new BorderRadius(7);


        Toast.makeText(this, "PDF Generated Succesfully", Toast.LENGTH_LONG).show();

        DetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count =0;
                if(snapshot.child("pdfcount").exists())
                {
                    String pdfcount = snapshot.child("pdfcount").getValue(String.class);
                    count = Integer.parseInt(pdfcount);
                    count = count + 1;
                    DetailsRef.child("pdfcount").setValue(String.valueOf(count));

                }
                else
                {
                    DetailsRef.child("pdfcount").setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return document;

    }

    @Override
    protected void onStart() {
        super.onStart();
        DetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("activationkey").exists())
                {
                    bannerAd.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}