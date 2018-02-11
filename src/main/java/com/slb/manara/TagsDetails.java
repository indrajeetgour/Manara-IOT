package com.slb.manara;

public class TagsDetails {

    private String tagID;
    private String tagName;
    private String tagUnitConversionA;
    private String tagUnitConversionB;

    public TagsDetails(String tagID, String tagName, String tagUnitConversionA, String tagUnitConversionB) {
        this.tagID = tagID;
        this.tagName = tagName;
        this.tagUnitConversionA = tagUnitConversionA;
        this.tagUnitConversionB = tagUnitConversionB;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagUnitConversionA() {
        return tagUnitConversionA;
    }

    public void setTagUnitConversionA(String tagUnitConversionA) {
        this.tagUnitConversionA = tagUnitConversionA;
    }

    public String getTagUnitConversionB() {
        return tagUnitConversionB;
    }

    public void setTagUnitConversionB(String tagUnitConversionB) {
        this.tagUnitConversionB = tagUnitConversionB;
    }

    @Override
    public String toString() {
        return "TagsDetails{" +
                "tagID='" + tagID + '\'' +
                ", tagName='" + tagName + '\'' +
                ", tagUnitConversionA='" + tagUnitConversionA + '\'' +
                ", tagUnitConversionB='" + tagUnitConversionB + '\'' +
                '}';
    }
}
