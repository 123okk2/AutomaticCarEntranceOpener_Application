package com.example.carinout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CarLogAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<CarLogInfo> listViewItemList = new ArrayList<>() ;

    // ListViewAdapter의 생성자
    public CarLogAdapter() {}

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.logitem, parent, false);
        }

        TextView carNum = (TextView) convertView.findViewById(R.id.carnumber);
        TextView inoutdate = (TextView) convertView.findViewById(R.id.inoutdate);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        CarLogInfo listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        carNum.setText(listViewItem.getCarNumber());
        inoutdate.setText(listViewItem.getInOutDate());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    public CarLogInfo getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(CarLogInfo c) {
        listViewItemList.add(c);
    }
}