package com.example.qurrah.UI;





//  double click on register button
// validate all fields at the same time





import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qurrah.Model.UserProfile;
import com.example.qurrah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputLayout userName , phone, userPassword, userEmail;
//    private EditText phone, userPassword, userEmail;
    private Button regButton;
    private TextView userLogin,term;
    private Spinner spinner;
    String email, name, password, phoneNumber , phoneN;
    Boolean result;
    boolean valid ;
    private CheckBox policyCheck ;
    private ProgressBar progressBar;
    private ArrayList<String> phoneNumbers;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Setup views
        userName = findViewById(R.id.etUserName);
        userPassword = findViewById(R.id.etUserPassword);
        userEmail = findViewById(R.id.etUserEmail);
        regButton = findViewById(R.id.btnRegister);
        userLogin = findViewById(R.id.tvUserLogin);
        //term=findViewById(R.id.terms);
        phone = findViewById(R.id.phone);
        policyCheck = findViewById(R.id.policy);
        progressBar = findViewById(R.id.progressBar);

        //Get text data
        name = userName.getEditText().getText().toString().trim();
        password = userPassword.getEditText().getText().toString().trim();
        email = userEmail.getEditText().getText().toString().trim();
        phoneNumber = phone.getEditText().getText().toString().trim();
        phoneN = phone.getEditText().getText().toString().trim();
        phoneNumbers = new ArrayList<>();


        //FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //regButton.setEnabled(false);
        regButton.setAlpha(0.6f);


        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        //Get Users phones
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                phoneNumbers.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    String No = userProfile.getPhone();
                    phoneNumbers.add(No);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








        userName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = userName.getEditText().getText().toString().trim();
                if(name.length()>=3)
                validateName();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                name = userName.getEditText().getText().toString().trim();
                if (validateN()){

                    regButton.setEnabled(true);
                    regButton.setAlpha(1f);
                }else {
                    regButton.setEnabled(false);
                    regButton.setAlpha(0.6f);
                }

                if(name.length()>=3)
                    validateName();
            }
        });



        userEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email = userEmail.getEditText().getText().toString().trim();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    validateEmail();}
            }

            @Override
            public void afterTextChanged(Editable editable) {
                email = userEmail.getEditText().getText().toString().trim();
                if (validateN()){
                    regButton.setEnabled(true);
                    regButton.setAlpha(1f);
                }else {
                    regButton.setEnabled(false);
                    regButton.setAlpha(0.6f);
                }
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    validateEmail();}
            }
        });



        userPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = userPassword.getEditText().getText().toString().trim();
                if(password.length()>=7)
                    validatePass();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                password = userPassword.getEditText().getText().toString().trim();
                if (validateN()){
                    regButton.setEnabled(true);
                    regButton.setAlpha(1f);
                }else {
                    regButton.setEnabled(false);
                    regButton.setAlpha(0.6f);
                }

                if(password.length()>=7)
                    validatePass();
            }
        });


        phone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneN = phone.getEditText().getText().toString().trim();
                if (phoneN.length() == 9 && phoneN.indexOf(0)== 5)
                    validatePhone();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                phoneN = phone.getEditText().getText().toString().trim();
                if (validateN()){
                    regButton.setEnabled(true);
                    regButton.setAlpha(1f);
                }else {
                    regButton.setEnabled(false);
                    regButton.setAlpha(0.6f);
                }

                if (phoneN.length() == 9 && phoneN.charAt(0)=='5')
                    validatePhone();

            }
        });



        policyCheck.setOnClickListener(new View.OnClickListener() {
           @Override
              public void onClick(View view) {
               AlertDialog.Builder builder1 = new AlertDialog.Builder(RegistrationActivity.this);
               builder1.setMessage("١. يجب ألا يقل عمرك عن 13 عاما" +"\n\n"+
                       "٢. يجب ان تقدم معلومات صحيحة ودقيقة لـ قرة كما يجب عليك الالتزام لأي اشعارات تقوم بها فيما يخص الخدمات التي يقدمها التطبيق لضمان عدم تعرقل أي عمليات تشغيليه."
                       +"\n\n"+"٣. باستخدام تطبيق قرة فأنت توافق على الالتزام بهذه الشروط والأحكام، وفي حال عدم موافقتك على الشروط والأحكام يتعين عليك عدم استخدام التطبيق." +
                       "\n\n"+"٤. نحتفظ بحقنا بتغيير الشروط والأحكام من وقت لآخر وعليه يتعين عليك الاطلاع على هذه الشروط والأحكام بشكلٍ دوري، ولا يتوجب علينا الاتصال بك أو إعلامك بأي تغييرات تم إجراؤها على الشروط والأحكام، ويعتبر استمرار استخدامك للتطبيق بمثابة موافقة على الشروط والأحكام المطبقة في وقت استخدامك التطبيق كجزء من التزامنا بجعل تطبيق  مكانا ترغب في زيارته باستمرار فإننا نرحب بكافة تعليقاتك على أي من السياسات أو القواعد.");
               builder1.setCancelable(true);

               builder1.setPositiveButton(
                       "أوافق",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {}


                       });

               builder1.setNegativeButton(
                       "لا أوافق",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               dialog.cancel();
                               policyCheck.setChecked(false);
                           }
                       });

               AlertDialog alert11 = builder1.create();

               alert11.show();
               alert11.setCanceledOnTouchOutside(false);


               validatePolicy();
                }
            });





        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                validateName();
//                validateEmail();
//                validatePass();
//                validatePhone();
//                validatePolicy();

                // will remove recalling methods
                if(validateName()&& validateEmail()&& validatePass()&&validatePhone()&&validatePolicy()) {
                    //if(duplicateEmail()&&duplicatePhone())  // find something to make the method wait , it returns true before it search the whole database
//                        if(duplicatePhone())
                            step1();
                }
//                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });

//        term.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(RegistrationActivity.this, terms.class));
//            }
//        });

    }

    private void setupUIViews(){

    }


    private Boolean validateName() {

        if (name.isEmpty()) {
            userName.setError("الرجاء ادخال اسم المستخدم");
            return false;
        }
        if (name.length() >= 3 ) {
            userName.setError(null);
            return true;

        }else {
            userName.setError("أدخل اسم مستخدم مكون من 3 خانات أو اكثر");
            return false;
        }

    }


    private Boolean validateEmail() {

        if (email.isEmpty()) {
            userEmail.setError("الرجاء ادخال البريد الالكتروني");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("صيغة البريد الالكتروني غير صحيحة");
            return false;
        }
        else if(duplicateEmail())
            userEmail.setError(null);
        return true;
    }



    private Boolean validatePass() {

        if (password.isEmpty()) {
            userPassword.setError("الرجاء ادخال كلمة المرور");
            return false;
        }

        else if (password.length() < 7) {
            userPassword.setError("أدخل كلمة مرور من 7 خانات أو اكثر");
            return false;
        }

        else
            userPassword.setError(null);
        return true;
    }



    private Boolean validatePhone() {

        if (phoneN.isEmpty()) {
            phone.setError("الرجاء ادخال رقم الجوال");
            return false;
        }
        else if (phoneN.length() != 9 || phoneN.charAt(0)!= '5') {
            phone.setError("صيغة الرقم غير صحيحة");
            return false;
        }
        else if(duplicatePhone())
            phone.setError(null);
        return true;
    }


    private Boolean validatePolicy() {

        if (!(policyCheck.isChecked())) {
            policyCheck.setError("الرجاء الموافقة على الشروط والأحكام");
            return false;
        }
        else
            policyCheck.setError(null);
        return true;
    }




    private boolean validateN(){
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneN.isEmpty()) {
            return false;
        }
        return true;
    }



    private Boolean duplicateEmail() {


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {

                    valid = task.getResult().getSignInMethods().isEmpty();

                    if (valid) {

                    } else {
                        userEmail.setError("البريد الالكتروني مستخدم مسبقا");
                    }
                }
            }
        });

        return valid;
    }



    private Boolean duplicatePhone() {

        valid=true;

        phoneNumber = "+966"+phoneN;

        for(String s : phoneNumbers) {

            if(s.equals(phoneNumber)){
                valid = false;
            phone.setError("رقم الهاتف مستخدم مسبقاً");
            return valid;
            }

        }

        if(valid)
        phone.setError(null);






//        Query phoneQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phone").equalTo(phoneNumber);
//        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getChildrenCount() > 0) {
//                    valid = false;
//                    phone.setError("رقم الهاتف مستخدم مسبقاً");
//                    phone.requestFocus();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        });

        return valid;
    }




    protected void step1() {
        try{

            Intent intent = new Intent(RegistrationActivity.this, VerifyPhoneActivity.class);
            Bundle extras = new Bundle();
            extras.putString("phonenumber",phoneN);
            extras.putString("email",email);
            extras.putString("name",name);
            extras.putString("password",password);

            intent.putExtras(extras);
            startActivity(intent);}

        catch (Exception e){ e.getStackTrace();}

    }


}