package com.example.parkingbooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Registration extends AppCompatActivity {

    public static EditText first_name,sir_name,email,mobile,address;
    public static ImageView image;
    public static TextView dob;
    public static RadioGroup gender;
    public static Button signup;
    public static Uri imageuri=null;
    public static Calendar calendar;
    public static String image_random;
    public static int day,month,year;
    public static ValueEventListener listener1,listener2;
    public static DatePickerDialog picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().hide();
        image_random= UUID.randomUUID().toString();
        calendar=Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH)+1;
        year = calendar.get(Calendar.YEAR);
        image=findViewById(R.id.registration_profile);
        image.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent,8);
                    }
                }
        );
        first_name=findViewById(R.id.register_first_name);
        sir_name=findViewById(R.id.register_sir_name);
        email=findViewById(R.id.register_email);
        mobile=findViewById(R.id.register_mobile);
        address=findViewById(R.id.register_address);
        gender=findViewById(R.id.register_gender);
        gender.check(R.id.register_radio_male);
        gender.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton check=findViewById(checkedId);
                        Toast.makeText(getApplicationContext(),check.getText().toString(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        signup=findViewById(R.id.register_signup);
        signup.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String s_fullname=first_name.getText().toString().trim()+" "+sir_name.getText().toString().trim();
                        String s_email=email.getText().toString().trim();
                        String s_mobile=mobile.getText().toString().trim();
                        String s_dob=dob.getText().toString().trim();
                        String s_gender=((RadioButton)findViewById(gender.getCheckedRadioButtonId())).getText().toString();
                        String s_address=address.getText().toString().trim();
                        if(!first_name.getText().toString().trim().equals("")){
                            if(!sir_name.getText().toString().trim().equals("")){
                                if(checkEmail(s_email)){
                                    if(checkMobile(s_mobile)){
                                        if(checkDate(s_dob)){
                                            if(!s_address.equals("")){
                                                FirebaseDatabase database=FirebaseDatabase.getInstance();
                                                DatabaseReference user=database.getReference("Users");
                                                DatabaseReference link=database.getReference("Mobile-Email");
                                                    Map<String,String> map=new HashMap<String,String>();
                                                    map.put("Full Name ",s_fullname);
                                                    map.put("Email ",s_email);
                                                    map.put("Mobile ",s_mobile);
                                                    map.put("Date Of Birth ",s_dob);
                                                    map.put("Gender ",s_gender);
                                                    map.put("Address",s_address);
                                                    if(imageuri!=null)
                                                        map.put("Profile image ",image_random);
                                                    else
                                                        map.put("Profile image ","not a image");
                                                    Registration.this.userExists(user,link,s_email,s_mobile,map);
                                            }
                                            else
                                                address.setError("Please Enter your address.");
                                        }
                                        else
                                            dob.setError("Please Select your correct Date of birth.");
                                    }
                                    else
                                        mobile.setError("Please Enter correct mobile number,");
                                }
                                else
                                    email.setError("Please Enter correct email with domain @gmail.com");
                            }
                            else
                                sir_name.setError("Please Enter Last name");
                        }
                        else
                            first_name.setError("Please Enter First name.");
                    }
                }
        );
        dob=findViewById(R.id.register_dob);
        dob.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        picker = new DatePickerDialog(Registration.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        dob.setText((dayOfMonth + "-" + (monthOfYear + 1) + "-" + year).toString());
                                    }
                                }, year, month, day);
                        picker.show();
                    }
                }
        );
        sir_name.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            if (start == 0 && s.charAt(start) == ' ')
                                sir_name.setText("");
                            else if (start != 0 && s.charAt(start) == ' ') {
                                sir_name.setText(s.subSequence(0,start));
                                sir_name.setSelection(start);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        first_name.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            if (start == 0 && s.charAt(start) == ' ')
                                first_name.setText("");
                            else if (start != 0 && s.charAt(start) == ' ') {
                                first_name.setText(s.subSequence(0,start));
                                first_name.clearFocus();
                                sir_name.requestFocus();
                                sir_name.setCursorVisible(true);
                            }
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==8&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null) {
            imageuri = data.getData();
            image.setImageURI(imageuri);
        }
    }
    private static boolean checkMobile(String mobile_no){
        int[]arr=new int[10];
        if(mobile_no.length()==10) {
            for (int i = 0;i<mobile_no.length();i++){
                char ch=mobile_no.charAt(i);
                if(ch<'0'&&ch>'9')
                    return false;
                arr[Integer.parseInt(ch+"")]++;
            }
            for(int i=0;i<arr.length;i++){
                if(arr[i]>4)
                    return false;
            }
            return true;
        }
        return false;
    }
    private static boolean checkEmail(String em){
        if(em.length()>=15&&em.substring(em.length()-10).equals("@gmail.com"))
            return true;
        return  false;
    }
    private static boolean checkDate(String dateofbirth){
        String[]arr=dateofbirth.split("-");
        if((!dateofbirth.equals(""))&&!dateofbirth.contains("mm")) {
            int dd = Integer.parseInt(arr[0]);
            int mm = Integer.parseInt(arr[1]);
            int yyyy = Integer.parseInt(arr[2]);

            Log.i("ram",dd+mm+yyyy+"");
                return true;
        }
        return false;
    }
    private void userExists(final DatabaseReference user, final DatabaseReference link, final String s_email,final String s_mobile,final Map<String,String> map){

        listener1=user.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(s_email.substring(0,s_email.length()-10))) {

                            listener2=link.addValueEventListener(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.hasChild(s_mobile)) {
                                                boolean check=Registration.this.fillData(user,link,s_email,s_mobile,map);
                                                if(check)
                                                    Toast.makeText(Registration.this,"successfully added",Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(Registration.this,"Not added to data base",Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                Toast.makeText(Registration.this,"user already exists",Toast.LENGTH_SHORT).show();
                                            link.removeEventListener(listener2);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    }
                            );
//9579248832   devendra patel
                        }
                        else
                            Toast.makeText(Registration.this,"user already exists",Toast.LENGTH_SHORT).show();
                        user.removeEventListener(listener1);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                }
        );
    }
    public boolean fillData(final DatabaseReference user, final DatabaseReference link, final String s_email,final String s_mobile,final Map<String,String> map){

        DatabaseReference new_user=user.child(s_email.substring(0,s_email.length()-10));
        new_user.setValue(map);

        DatabaseReference new_user_link=link.child(s_mobile);
        new_user_link.setValue(s_email);

        if(imageuri!=null){
            FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
            StorageReference root=firebaseStorage.getReference("Profile images/"+image_random+".png");
            final ProgressDialog pd=new ProgressDialog(this);
            pd.setTitle("Imgae Uploading........");
            pd.show();

            root.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //pd.dismiss();
                            Snackbar.make(findViewById(android.R.id.content),"image uploaded",Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //pd.dismiss();
                            Toast.makeText(getApplicationContext(),"not uploaded",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progresspercetage=(100.00*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                    pd.setMessage("Percentage"+((int)progresspercetage)+"%");
                                    if(progresspercetage==100)
                                        pd.dismiss();
                                }
                            }
                    );

        }
        return true;
    }
}