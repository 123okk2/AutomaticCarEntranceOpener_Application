package com.example.carinout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import org.json.JSONObject;

import java.util.ArrayList;

public class CarPermission extends Fragment {
    private static AmazonDynamoDBClient ddb = null;
    static DynamoDBMapper dynamoDBMapper;

    ListView permissionListView;
    CarPermissionAdapter cpa;
    FloatingActionButton flt;
    int functionType = 0;

    String ownCarName, ownMemo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.carpermission, container, false);

        permissionListView = (ListView) view.findViewById(R.id.permissionListView);
        flt = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        flt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRegister(v);
            }
        });

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

        functionType = 0;
        new JSONParse().execute();
    }

    public void onClickRegister(View v) {
        Intent i = new Intent(getActivity().getApplicationContext(), CreateCarPermission.class);
        i.putExtra("type", 0); //등록 시그널

        startActivity(i);
    }

    public void updatePermission(String ownCarName, String ownMemo) {

        Intent i = new Intent(getActivity().getApplicationContext(), CreateCarPermission.class);
        i.putExtra("type", 1);
        i.putExtra("ownCarName", ownCarName);
        i.putExtra("ownMemo", ownMemo);

        startActivity(i);
    }

    public void deletePermission(String ownCarName, String ownMemo) {
        this.ownCarName = ownCarName;
        this.ownMemo = ownMemo;

        functionType = 3;
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
            try {
                switch (functionType) {
                    case 0:
                        cpa = new CarPermissionAdapter();

                        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                        PaginatedScanList<CarPermissionInfo> carPermissionList = dynamoDBMapper.scan(CarPermissionInfo.class, scanExpression);

                        System.out.println(carPermissionList.get(0).getCarNumber());

                        for (int i = 0; i < carPermissionList.size(); i++) {
                            cpa.addItem(carPermissionList.get(i));
                        }
                        break;
                    case 3:
                        CarPermissionInfo carPermissionInfo2 = new CarPermissionInfo();
                        carPermissionInfo2.setCarNumber(ownCarName);
                        carPermissionInfo2.setMemo(ownMemo);
                        dynamoDBMapper.delete(carPermissionInfo2);
                        break;
                }
            }
            catch (IndexOutOfBoundsException e) {
                functionType = 6;
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
                functionType = 5;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject J) {
            pDialog.dismiss();
            switch (functionType) {
                case 0:
                    permissionListView.setAdapter(cpa);
                    permissionListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            final CarPermissionInfo cpi =((CarPermissionInfo) parent.getItemAtPosition(position));
                            System.out.println(" sadasdasdasd " + cpi.getCarNumber());

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                            // 제목셋팅
                            alertDialogBuilder.setTitle("작업을 선택하세요.");
                            // AlertDialog 셋팅
                            alertDialogBuilder
                                    .setMessage("")
                                    .setCancelable(true)
                                    .setPositiveButton("수정",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog, int id) {
                                                    // 댓글 수정
                                                    updatePermission(cpi.getCarNumber(), cpi.getMemo());
                                                }
                                            })
                                    .setNegativeButton("삭제",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog, int id) {
                                                    // 댓글 삭제
                                                    deletePermission(cpi.getCarNumber(), cpi.getMemo());
                                                }
                                            });
                            // 다이얼로그 생성
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // 다이얼로그 보여주기
                            alertDialog.show();
                            return false;
                        }
                    });
                    break;
                case 3:
                    Toast.makeText(getActivity().getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    functionType = 0;
                    new JSONParse().execute();
                    break;
                case 5:
                    Toast.makeText(getActivity().getApplicationContext(), "작업에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(getActivity().getApplicationContext(), "저장된 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    permissionListView.setAdapter(cpa);
                    break;
            }
            super.onPostExecute(J);
        }

    };
}
