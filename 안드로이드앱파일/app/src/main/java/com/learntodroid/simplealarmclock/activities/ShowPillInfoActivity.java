package com.learntodroid.simplealarmclock.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learntodroid.simplealarmclock.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ShowPillInfoActivity extends AppCompatActivity {
    List<String> pillArray = new ArrayList<>();

    String data;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("복용 중인 약 리스트");

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pill_info);
        Intent intent = getIntent();

        textView = (TextView) findViewById(R.id.info);
        textView.setMovementMethod(new ScrollingMovementMethod());
        mConditionRef.addValueEventListener(new ValueEventListener() { //파이어베이스 값이 변경되면
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int idx =0;
                textView.append("복용 중인 약품 리스트 \n");
                for(DataSnapshot postSnapshot: snapshot.getChildren())
                {
                    String result = postSnapshot.getValue().toString();
                    pillArray.add(result); //약 목록 배열에 약들을 추가함
                }

                new Thread() {
                    @Override
                    public void run() { //쓰레드로 제어
                        try {
                            data = getXmlData();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText("복용 중인 약품 리스트\n\n");
                                    textView.append(data); //getxmldata의 결과를 textview에 붙여넣기
                                }
                            });
                        } catch (Exception e) {
                            textView.append(e.toString());
                        }
                    }
                }.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public String getXmlData() {
        StringBuffer buffer = new StringBuffer();
        for (int idx = 0; idx < pillArray.size(); idx++) {
            String queryUrl = "http://apis.data.go.kr/1471057/MdcinPrductPrmisnInfoService1/getMdcinPrductItem?ServiceKey=zA1cJpZpqx5HQ%2BosZHEl%2BN8xWAioTcx7%2BiJ%2B%2FKwcujAGwdeEpqUoa9diI6FXtH1TDxb5R9qGKIBpo3b1k8E6cg%3D%3D&item_name=" + pillArray.get(idx) + "&numOfRows=1";
            try {
                URL url = new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
                InputStream is = url.openStream(); //url위치로 입력스트림 연결
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is, StandardCharsets.UTF_8)); //inputstream 으로부터 xml 입력받기
                String tag;
                int i = 0;
                xpp.next();
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            tag = xpp.getName();//태그 이름 얻어오기
                            if (tag.equals("item")) ;
                            else if (tag.equals("ENTP_NAME")) {
                                buffer.append("제조사명 : ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            } else if (tag.equals("ITEM_NAME")) {
                                buffer.append("약품명 " + Integer.toString(idx+1)+ " : ");
                                xpp.next();

                                buffer.append(xpp.getText());
                                    buffer.append("\n");
                            }

                            else if (tag.equals("PARAGRAPH"))  {
                                xpp.next();
                                if (xpp.getText().contains("성인")){
                                    buffer.append("복용법 : \n");
                                    buffer.append(xpp.getText());
                                    buffer.append("\n");
                                }
                            }
                            break;
                        case XmlPullParser.TEXT:
                            break;

                        case XmlPullParser.END_TAG:
                            tag = xpp.getName();
                            if (tag.equals("item")) buffer.append("\n");
                            break;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch blocke.printStackTrace();
            }
        }
        return buffer.toString();//StringBuffer 문자열 객체 반환
    }
}