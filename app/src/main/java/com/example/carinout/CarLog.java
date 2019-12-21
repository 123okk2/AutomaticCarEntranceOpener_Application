package com.example.carinout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class CarLog extends Fragment {
    private static AmazonDynamoDBClient ddb = null;
    static DynamoDBMapper dynamoDBMapper;

    ListView logListView;
    CarLogAdapter cla;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.carlog, container, false);

        logListView = (ListView) view.findViewById(R.id.logListView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        AWSMobileClient.getInstance().initialize(getContext()).execute();

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        CognitoCachingCredentialsProvider credentials = new CognitoCachingCredentialsProvider(
                getContext(),
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

        new JSONParse().execute();

    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("잠시만 기다려주세요.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            ArrayList<CarLogInfo> cal = new ArrayList<>();
            cla = new CarLogAdapter();

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<CarLogInfo> carLogList = dynamoDBMapper.scan(CarLogInfo.class, scanExpression);

            for (int i = 0; i < carLogList.size() ; i++) {
                cal.add(carLogList.get(i));
            }

            //정렬
            Collections.sort(cal, new Comparator<CarLogInfo>() {
                @Override
                public int compare(CarLogInfo c1, CarLogInfo c2) {
                    return c2.getInOutDate().compareTo(c1.getInOutDate());
                }
            });

            for (int i = 0; i < cal.size() ; i++) {
                cla.addItem(cal.get(i));
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject J) {
            pDialog.dismiss();
            logListView.setAdapter(cla);
        }
    };
}
