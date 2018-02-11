package com.slb.manara;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FileProcessingInputs {
    private Map<String, List<File>> inputFilesList;
    private List<String> diagnosticFilterTags;
    private String[] WELL_HEADER_MAPPING;
    private String[] STATIONIDENTIFIER_HEADER_MAPPING;
    private String[] headerList;
    private String currWellName;
    private String currStationName;
    private String currLateralName;
    private String fileOut;
    private List<String[]> allRecords;
    private String wellMappingFileName;
    private String tagMappingSheetName;
    private String tagsMappingFileName;
    private String tagsNameSheetName;
    private String stationIdentifierMappingFromFile;
    private String preProcessedOpDir;
    private String currProcessingFileName;

    public String getCurrProcessingFileName() {
        return currProcessingFileName;
    }

    public void setCurrProcessingFileName(String currProcessingFileName) {
        this.currProcessingFileName = currProcessingFileName;
    }

    public String getPreProcessedOpDir() {
        return preProcessedOpDir;
    }

    public void setPreProcessedOpDir(String preProcessedOpDir) {
        this.preProcessedOpDir = preProcessedOpDir;
    }

    public String getCurrLateralName() {
        return currLateralName;
    }

    public void setCurrLateralName(String currLateralName) {
        this.currLateralName = currLateralName;
    }

    private String timestampUnitConvVal;
    boolean timestampConversionFlag;

    public boolean isTimestampConversionFlag() {
        return timestampConversionFlag;
    }

    public void setTimestampConversionFlag(boolean timestampConversionFlag) {
        this.timestampConversionFlag = timestampConversionFlag;
    }

    public String getTimestampUnitConvVal() {
        return timestampUnitConvVal;
    }

    public void setTimestampUnitConvVal(String timestampUnitConvVal) {
        this.timestampUnitConvVal = timestampUnitConvVal;
    }

    public Map<String, List<File>> getInputFilesList() {
        return inputFilesList;
    }

    public void setInputFilesList(Map<String, List<File>> inputFilesList) {
        this.inputFilesList = inputFilesList;
    }

    public List<String> getDiagnosticFilterTags() {
        return diagnosticFilterTags;
    }

    public void setDiagnosticFilterTags(List<String> diagnosticFilterTags) {
        this.diagnosticFilterTags = diagnosticFilterTags;
    }

    public String[] getWELL_HEADER_MAPPING() {
        return WELL_HEADER_MAPPING;
    }

    public void setWELL_HEADER_MAPPING(String[] WELL_HEADER_MAPPING) {
        this.WELL_HEADER_MAPPING = WELL_HEADER_MAPPING;
    }

    public String[] getSTATIONIDENTIFIER_HEADER_MAPPING() {
        return STATIONIDENTIFIER_HEADER_MAPPING;
    }

    public void setSTATIONIDENTIFIER_HEADER_MAPPING(String[] STATIONIDENTIFIER_HEADER_MAPPING) {
        this.STATIONIDENTIFIER_HEADER_MAPPING = STATIONIDENTIFIER_HEADER_MAPPING;
    }

    public String getWellMappingFileName() {
        return wellMappingFileName;
    }

    public void setWellMappingFileName(String wellMappingFileName) {
        this.wellMappingFileName = wellMappingFileName;
    }

    public String getStationIdentifierMappingFromFile() {
        return stationIdentifierMappingFromFile;
    }

    public void setStationIdentifierMappingFromFile(String stationIdentifierMappingFromFile) {
        this.stationIdentifierMappingFromFile = stationIdentifierMappingFromFile;
    }

    public String[] getHeaderList() {
        return headerList;
    }

    public void setHeaderList(String[] headerList) {
        this.headerList = headerList;
    }

    public String getCurrWellName() {
        return currWellName;
    }

    public void setCurrWellName(String currWellName) {
        this.currWellName = currWellName;
    }

    public String getFileOut() {
        return fileOut;
    }

    public void setFileOut(String fileOut) {
        this.fileOut = fileOut;
    }

    public List<String[]> getAllRecords() {
        return allRecords;
    }

    public void setAllRecords(List<String[]> allRecords) {
        this.allRecords = allRecords;
    }

    public String getTagMappingSheetName() {
        return tagMappingSheetName;
    }

    public void setTagMappingSheetName(String tagMappingSheetName) {
        this.tagMappingSheetName = tagMappingSheetName;
    }

    public String getTagsMappingFileName() {
        return tagsMappingFileName;
    }

    public void setTagsMappingFileName(String tagsMappingFileName) {
        this.tagsMappingFileName = tagsMappingFileName;
    }

    public String getTagsNameSheetName() {
        return tagsNameSheetName;
    }

    public void setTagsNameSheetName(String tagsNameSheetName) {
        this.tagsNameSheetName = tagsNameSheetName;
    }

    public String getCurrStationName() {
        return currStationName;
    }

    public void setCurrStationName(String currStationName) {
        this.currStationName = currStationName;
    }
}
