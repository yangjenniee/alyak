package com.learntodroid.simplealarmclock.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learntodroid.simplealarmclock.R;

public class PresentActivity extends AppCompatActivity {
    TextView textView_pill_states;
    Button btn_reset;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mShouldRef = mRootRef.child("Give_pill");
    private DatabaseReference mPillRef = mRootRef.child("약통 현황");
    private DatabaseReference mPillIndex = mRootRef.child("차례").child("번호");
    private String D_should = "";
    int D_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present);

        textView_pill_states = findViewById(R.id.textView_pill_states);
        textView_pill_states.setMovementMethod(new ScrollingMovementMethod());
        btn_reset = findViewById(R.id.btn_reset);

        mShouldRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                D_should = snapshot.getValue().toString();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mRootRef.addValueEventListener(new ValueEventListener() { //파이어베이스에 값이 바뀌면
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot should_snapshot = snapshot.child("Give_pill").child("should");
                D_should = should_snapshot.getValue().toString();

                DataSnapshot num_snapshot = snapshot.child("차례").child("번호");
                //tv.append(snapshot.toString());
                D_index = Integer.parseInt(num_snapshot.getValue().toString());

                textView_pill_states.setText("현재 약통 현황\n");
                DataSnapshot pill_state_arr = snapshot.child("약통 현황");
                // tv.append(pill_state_arr.toString());

                for(int i = 1; i<9; i++) //약통현황을 세팅함
                {
                    DataSnapshot child = pill_state_arr.child("칸" + Integer.toString(i));
                    textView_pill_states.append("칸" + Integer.toString(i) + child.getValue() + "   ");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_reset.setOnClickListener(new View.OnClickListener() { //재투입 버튼을 누르면
            @Override
            public void onClick(View v) {
                mShouldRef.child("should").setValue("N"); //파이어베이스값 변경
                mPillIndex.setValue(1); //차례를 1로 바꿈

                for(int i = 1; i<9; i++)
                {
                    mPillRef.child("칸" + Integer.toString(i)).setValue("o"); //파이어베이스 칸 값을 다시 다 o로
                }
            }
        });
    }
}