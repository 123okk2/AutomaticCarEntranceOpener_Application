package com.example.carinout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import org.json.JSONObject;

public class CreateCarPermission extends AppCompatActivity {
    private static AmazonDynamoDBClient ddb = null;
    static DynamoDBMapper dynamoDBMapper;

    int type=0;
    String ownCarName="", ownMemo="";

    EditText carName;
    EditText memo;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_car_permission);

        carName = (EditText) findViewById(R.id.carNumber) ;
        memo = (EditText) findViewById(R.id.memo);
        btn = (Button) findViewById(R.id.regBtn);

        type = getIntent().getIntExtra("type",0);
        System.out.println("adasdasds" + type);
        if(type == 1) {
            Toast.makeText(getApplicationContext(), "수정은 메모만 가능합니다.\n차번호 수정시 삭제 후 재등록 부탁드립니다.", Toast.LENGTH_SHORT).show();
            ownCarName = getIntent().getStringExtra("ownCarName");
            ownMemo = getIntent().getStringExtra("ownMemo");
            carName.setText(ownCarName);
            carName.setEnabled(false);
            memo.setText(ownMemo);
            btn.setText("수정");
        }
        AWSMobileClient.getInstance().initialize(this).execute();

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        CognitoCachingCredentialsProvider credentials = new CognitoCachingCredentialsProvider(
                this,
                "secretKey", // 자격 증명 풀 ID
                Regions.US_EAST_1 // 리전
        );

        //System.out.println(ddb.getRegions());
        if(ddb == null) {
            ddb = new AmazonDynamoDBClient(credentials);
            ddb.setRegion(Region.getRegion(Regions.US_EAST_1));
        }

        if(dynamoDBMapper == null) {
            dynamoDBMapper = new DynamoDBMapper(ddb);
        }

    }

    public void onClickBtn(View v) {
        new JSONParse().execute();
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(CreateCarPermission.this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("잠시만 기다려주세요.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String carNumber;
            String Memo;
            try {
                switch (type) {
                    case 0:
                        final CarPermissionInfo carPermissionInfo = new CarPermissionInfo();

                        carNumber= carName.getText().toString();
                        Memo = memo.getText().toString();

                        carPermissionInfo.setCarNumber(carNumber);
                        carPermissionInfo.setMemo(Memo);

                        dynamoDBMapper.save(carPermissionInfo);
                        break;
                    case 1:
                        final CarPermissionInfo newPermission = new CarPermissionInfo();

                        carNumber = carName.getText().toString();
                        Memo = memo.getText().toString();

                        newPermission.setCarNumber(carNumber);
                        newPermission.setMemo(Memo);
                        dynamoDBMapper.save(newPermission);
                }
            }
            catch (Exception e) {
                type = 5;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject J) {
            pDialog.dismiss();
            switch (type) {
                case 0:
                    Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(getApplicationContext(), "실패했습니다.", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.onPostExecute(J);
            finish();
        }

    };
}
