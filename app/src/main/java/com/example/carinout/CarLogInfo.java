package com.example.carinout;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "CarLogInfo")
public class CarLogInfo{
    private String CarNumber;
    private String InOutDate;

    @DynamoDBHashKey(attributeName = "CarNumber")
    @DynamoDBAttribute(attributeName = "CarNumber")
    public String getCarNumber() {
        return CarNumber;
    }

    public void setCarNumber(final String carNumber) {
        this.CarNumber = carNumber;
    }

    @DynamoDBHashKey(attributeName = "Date")
    @DynamoDBAttribute(attributeName = "Date")
    public String getInOutDate() {
        return InOutDate;
    }

    public void setInOutDate(final String inOutDate) {
        this.InOutDate = inOutDate;
    }

}
