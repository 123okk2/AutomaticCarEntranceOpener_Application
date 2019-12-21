package com.example.carinout;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "CarPermission")
public class CarPermissionInfo {
    private String CarNumber;
    private String Memo;

    @DynamoDBHashKey(attributeName = "CarNumber")
    @DynamoDBAttribute(attributeName = "CarNumber")
    public String getCarNumber() {
        return CarNumber;
    }

    public void setCarNumber(final String carNumber) {
        this.CarNumber = carNumber;
    }

    @DynamoDBAttribute(attributeName = "Memo")
    public String getMemo() {
        return Memo;
    }

    public void setMemo(final String memo) {
        this.Memo = memo;
    }
}
