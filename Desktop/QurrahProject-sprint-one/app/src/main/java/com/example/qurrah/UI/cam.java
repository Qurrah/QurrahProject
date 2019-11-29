//<?xml version="1.0" encoding="utf-8"?>
//<!--<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"-->
//<!--    xmlns:app="http://schemas.android.com/apk/res-auto"-->
//<!--    xmlns:tools="http://schemas.android.com/tools"-->
//<!--    android:layout_width="match_parent"-->
//<!--    android:layout_height="match_parent"-->
//<!--    android:background="#F5F4F5"-->
//<!--    tools:context="com.example.qurrah.UI.UpdatePassword">-->
//
//<!--    <RelativeLayout-->
//<!--        android:id="@+id/relativeLayout"-->
//<!--        android:layout_width="match_parent"-->
//<!--        android:layout_height="200dp"-->
//<!--        android:background="#776A77"-->
//<!--        app:layout_constraintBottom_toTopOf="@+id/NewPassword"-->
//<!--        app:layout_constraintTop_toTopOf="parent"-->
//<!--        app:layout_constraintVertical_bias="0.0"-->
//<!--        tools:layout_editor_absoluteX="0dp">-->
//<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
//        xmlns:app="http://schemas.android.com/apk/res-auto"
//        xmlns:tools="http://schemas.android.com/tools"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        android:id="@+id/parent"
//        android:background="@mipmap/qurrah_background"
//        tools:context="com.example.qurrah.UI.helpActivity">
//
//<LinearLayout
//        android:id="@+id/contentLayout"
//                android:layout_width="match_parent"
//                android:layout_height="match_parent"
//                tools:ignore="MissingConstraints"
//                android:orientation="vertical"
//                android:layout_marginLeft="30dp"
//                android:layout_marginTop="100dp"
//                android:layout_marginRight="30dp"
//                android:layout_marginBottom="60dp"
//                android:paddingTop="120dp"
//                android:paddingStart="50dp"
//                android:background="@drawable/default_frame"
//                >
//<!--        <ImageView-->
//<!--            android:layout_width="120dp"-->
//<!--            android:layout_height="120dp"-->
//<!--            android:layout_centerHorizontal="true"-->
//<!--            android:layout_centerVertical="true"-->
//<!--            android:background="@drawable/ic_logo" />-->
//
//<TextView
//            android:id="@+id/aboutUs"
//                    android:layout_width="wrap_content"
//                    android:layout_height="wrap_content"
//                    android:layout_marginStart="8dp"
//                    android:layout_marginTop="8dp"
//                    android:layout_marginEnd="8dp"
//                    android:layout_marginBottom="8dp"
//                    android:ems="10"
//                    android:hint="من نحن ؟"
//                    android:inputType="textPassword"
//                    android:textAlignment="center"
//                    android:textSize="20dp"
//                    android:textColor="@color/colorPrimaryDark"
//                    app:layout_constraintBottom_toBottomOf="parent"
//                    app:layout_constraintEnd_toEndOf="parent"
//                    app:layout_constraintHorizontal_bias="0.489"
//                    app:layout_constraintStart_toStartOf="parent"
//                    app:layout_constraintTop_toTopOf="parent"
//                    app:layout_constraintVertical_bias="0.377" />
//
//
//<TextView
//            android:layout_width="248dp"
//                    android:layout_height="150dp"
//                    android:layout_marginStart="8dp"
//                    android:layout_marginTop="8dp"
//                    android:layout_marginEnd="8dp"
//                    android:layout_marginBottom="8dp"
//                    android:ems="10"
//                    android:text="نحن طالبات من جامعة الملك سعود قسم هندسة البرمجيات.
//                    واجهنا ولمسنا مشكلة حولنا نعاني منها بفقدان اشياء من حولنا
//                    وطلب المساعدة من غيرنا
//                    ورأينا تأثيرها فصنعنا 'قرة' لحلها ."
//
//                    android:textColor="@color/darkPurple"
//                    android:textSize="18dp"
//                    app:layout_constraintBottom_toBottomOf="parent"
//                    app:layout_constraintEnd_toEndOf="parent"
//                    app:layout_constraintHorizontal_bias="0.489"
//                    app:layout_constraintStart_toStartOf="parent"
//                    app:layout_constraintTop_toTopOf="parent"
//                    app:layout_constraintVertical_bias="0.377" />
//
//<TextView
//            android:id="@+id/communicate"
//                    android:layout_width="wrap_content"
//                    android:layout_height="wrap_content"
//                    android:layout_marginStart="8dp"
//                    android:layout_marginTop="10dp"
//                    android:layout_marginEnd="8dp"
//                    android:layout_marginBottom="8dp"
//                    android:ems="10"
//                    android:hint="للتواصل والدعم"
//                    android:textAlignment="center"
//                    android:textColor="@color/colorPrimaryDark"
//                    android:textSize="20dp"
//                    app:layout_constraintBottom_toBottomOf="parent"
//                    app:layout_constraintEnd_toEndOf="parent"
//                    app:layout_constraintHorizontal_bias="0.489"
//                    app:layout_constraintStart_toStartOf="parent"
//                    app:layout_constraintTop_toTopOf="parent"
//                    app:layout_constraintVertical_bias="0.377" />
//
//<TextView
//            android:id="@+id/email"
//                    android:layout_width="198dp"
//                    android:layout_height="40dp"
//                    android:textAlignment="center"
//                    android:textSize="20dp"
//                    android:layout_marginTop="20dp"
//                    android:layout_marginRight="30dp"
//                    android:background="@drawable/purple_buttons"
//                    android:textAllCaps="false"
//                    android:textColor="#ffffff"
//                    app:layout_constraintBottom_toBottomOf="parent"
//                    app:layout_constraintEnd_toEndOf="parent"
//                    app:layout_constraintHorizontal_bias="0.503"
//                    app:layout_constraintStart_toStartOf="parent"
//                    app:layout_constraintTop_toTopOf="parent"
//                    app:layout_constraintVertical_bias="0.815" />
//
//<TextView
//            android:id="@+id/twitter"
//                    android:layout_width="198dp"
//                    android:layout_height="36dp"
//                    android:layout_marginTop="10dp"
//                    android:layout_marginRight="30dp"
//                    android:textAlignment="center"
//                    android:textSize="20dp"
//                    android:background="@drawable/purple_buttons"
//                    android:textAllCaps="false"
//                    android:textColor="#ffffff"
//                    app:layout_constraintBottom_toBottomOf="parent"
//                    app:layout_constraintEnd_toEndOf="parent"
//                    app:layout_constraintHorizontal_bias="0.503"
//                    app:layout_constraintStart_toStartOf="parent"
//                    app:layout_constraintTop_toTopOf="parent"
//                    app:layout_constraintVertical_bias="0.815" />
//
//
//</LinearLayout>
//
//
//
//
//<LinearLayout
//        android:id="@+id/imgLayout"
//                android:layout_width="wrap_content"
//                android:layout_height="wrap_content"
//                android:background="@drawable/circle"
//                android:orientation="vertical"
//                android:elevation="5dp"
//                app:layout_constraintEnd_toEndOf="parent"
//                app:layout_constraintStart_toStartOf="parent"
//                app:layout_constraintTop_toTopOf="parent"
//                tools:ignore="MissingConstraints"
//                android:layout_marginTop="30dp"
//                >
//
//<ImageView
//            android:id="@+id/ivProfile"
//                    android:layout_width="130dp"
//                    android:layout_height="78dp"
//                    android:layout_marginStart="8dp"
//                    android:layout_marginLeft="20dp"
//                    android:layout_marginTop="35dp"
//                    android:layout_marginEnd="8dp"
//                    android:layout_marginBottom="8dp"
//                    app:layout_constraintBottom_toTopOf="@+id/contentLayout"
//                    app:layout_constraintEnd_toEndOf="parent"
//                    app:layout_constraintHorizontal_bias="0.497"
//                    app:layout_constraintStart_toStartOf="parent"
//                    app:layout_constraintTop_toTopOf="parent"
//                    app:layout_constraintVertical_bias="0.126"
//                    app:srcCompat="@drawable/ic_help_purple_24dp" />
//
//
//
//</LinearLayout>
//</androidx.constraintlayout.widget.ConstraintLayout>
