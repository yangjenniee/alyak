package com.learntodroid.simplealarmclock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learntodroid.simplealarmclock.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OcrActivity extends AppCompatActivity {

    private static void writeMultiPart(OutputStream out, String jsonMessage, File file, String boundary) throws
            IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition:form-data; name=\"message\"\r\n\r\n");
        sb.append(jsonMessage);
        sb.append("\r\n");

        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        out.flush();

        if (file != null && file.isFile()) {
            out.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            StringBuilder fileString = new StringBuilder();
            fileString
                    .append("Content-Disposition:form-data; name=\"file\"; filename=");
            fileString.append("\"" + file.getName() + "\"\r\n");
            fileString.append("Content-Type: application/octet-stream\r\n\r\n");
            out.write(fileString.toString().getBytes(StandardCharsets.UTF_8));
            out.flush();

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                out.write("\r\n".getBytes());
            }

            out.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        }
        out.flush();
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
                                buffer.append("약품명 : ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                            }
                            else if (tag.equals(("")))// TODO 태그 붙이기
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

    TextView text;

    /* ocr template 실행 결과 로그 파일 */
    final static String foldername = "/sdcard/Download/TestLog";
    final static String filename = "logfile.txt";

    /* 복용 중인 약물 리스트 배열*/
    List<String> pillArray = new ArrayList<>();


    private String key = "zA1cJpZpqx5HQ%2BosZHEl%2BN8xWAioTcx7%2BiJ%2B%2FKwcujAGwdeEpqUoa9diI6FXtH1TDxb5R9qGKIBpo3b1k8E6cg%3D%3D";
    private String data;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("복용 중인 약 리스트");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);


        text = (TextView) findViewById(R.id.result);
        text.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();

        // storage permission
        Thread ocr = new Thread() {
            public void run() {
                String apiURL = "https://cd1dc5982510479798b47bfa5ba2c33f.apigw.ntruss.com/custom/v1/12335/cde98aee7daf46fed346e2029be27a64fd49ca4b718d29ffdf24180b685e6cd6/general";
                String secretKey = "QmdDRGV5V2ZyeG9jYXFhV1Z5ZUFRZ3NyZ3VtQUFwa0Y=";
                String photopath = intent.getStringExtra("key");

                String imageFile = photopath;
                try {
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setUseCaches(false);
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setReadTimeout(30000);
                    con.setRequestMethod("POST");
                    String boundary = "----" + UUID.randomUUID().toString().replaceAll("-", "");
                    con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    con.setRequestProperty("X-OCR-SECRET", secretKey);
                    JSONObject json = new JSONObject();
                    json.put("version", "V2");
                    json.put("requestId", UUID.randomUUID().toString());
                    json.put("timestamp", System.currentTimeMillis());
                    JSONObject image = new JSONObject();
                    image.put("format", "jpg");
                    image.put("name", "demo");
                    JSONArray images = new JSONArray();
                    images.put(image);
                    json.put("images", images);
                    String postParams = json.toString();
                    con.connect();
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    long start = System.currentTimeMillis();
                    File file = new File(imageFile);
                    writeMultiPart(wr, postParams, file, boundary);
                    wr.close();
                    int responseCode = con.getResponseCode();
                    BufferedReader br;
                    if (responseCode == 200) {
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    /* 응답한 데이터를 받은 response를 json 파일로 바꾸는 코드 시작 */
                    JSONObject reJson = new JSONObject(response.toString());
                    /* url */
                    JSONArray reJsonChildArray = reJson.getJSONArray("images").getJSONObject(0).getJSONArray("fields");
                    int listIdx = 0;
                    // debug text.append("e");

                    if (reJsonChildArray != null) {
                        mConditionRef.removeValue(); // database 내용 초기화
                        for (int i = 0; i < reJsonChildArray.length(); i++) {
                            String checkString = (String) reJsonChildArray.getJSONObject(i).get("inferText");
                            char ch = checkString.charAt(checkString.length() - 1);
                            if (ch == '정') {
                                String pillName = (String) reJsonChildArray.getJSONObject(i).get("inferText");
                                // text.append(pillName + "\n");
                                pillArray.add((pillName));
                                mConditionRef.child("약품" + Integer.toString(++listIdx)).setValue(pillName);
                            }
                        }
                    }

                    /* 응답한 데이터를 받은 response를 json 파일로 바꾸는 코드 끝 */

                    /* 응답을 txt 파일로 저장하는 코드 시작 */
                    File dir = new File(foldername);
                    //디렉토리 폴더가 없으면 생성함
                    if (!dir.exists()) {
                        dir.mkdir();
                    }


                    //파일 output stream 생성
                    FileOutputStream fos = new FileOutputStream(foldername + "/" + filename, false);
                    //파일쓰기
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                    String content = response.toString();
                    writer.write(content);
                    writer.flush();
                    writer.close();
                    fos.close();
                    /* 응답을 txt 파일로 저장하는 코드 끝 */
                    System.out.println(response);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        };
        Thread showInfo = new Thread() {
            @Override
            public void run() {
                try {
                    // debug text.append("s");
                    data = getXmlData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(data);
                        }
                    });
                } catch (Exception e) {
                    text.append(e.toString());
                }
            }
        };

        ocr.start();
        try {
            ocr.join();
        } catch (Exception e) {
            text.append(e.toString());
        }
        showInfo.start();
    }


}