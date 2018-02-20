package com.slb.manara;

import com.google.common.collect.Multimap;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FileProcessingInputs {
    private static String mFileType;
    private static String iFileType;
    private static String manaraTagSheetName;
    private static String manaraMeasurementFile;
    boolean timestampConversionFlag;
    private Map<String, List<File>> inputFilesList;
    private List<String> diagnosticFilterTags;
    private List<String> diagnosticIWICFilterTags;
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
    private String tagMappingIWICSheetName;
    private String tagsMappingFileName;
    private String tagsNameSheetName;
    private String stationIdentifierMappingFromFile;
    private String preProcessedOpDir;
    private String currProcessingFileName;
    private String timestampUnitConvVal;
    private String tagsNameIWICSheetName;
    private String fileTypeFlag;
    private Multimap<String, String> superSetOfAllTagsMultiMapManara;
    private Multimap<String, String> superSetOfAllTagsMultiMapIWIC;
    private String[] IWICIDENTIFIER_HEADER_MAPPING;
    private String iWICIdentifierMappingFromFile;

    public String getiWICIdentifierMappingFromFile() {
        return iWICIdentifierMappingFromFile;
    }

    public void setiWICIdentifierMappingFromFile(String iWICIdentifierMappingFromFile) {
        this.iWICIdentifierMappingFromFile = iWICIdentifierMappingFromFile;
    }

    public String[] getIWICIDENTIFIER_HEADER_MAPPING() {
        return IWICIDENTIFIER_HEADER_MAPPING;
    }

    public void setIWICIDENTIFIER_HEADER_MAPPING(String[] IWICIDENTIFIER_HEADER_MAPPING) {
        this.IWICIDENTIFIER_HEADER_MAPPING = IWICIDENTIFIER_HEADER_MAPPING;
    }

    public static String getManaraMeasurementFile() {
        return manaraMeasurementFile;
    }

    public static void setManaraMeasurementFile(String manaraMeasurementFile) {
        FileProcessingInputs.manaraMeasurementFile = manaraMeasurementFile;
    }

    public static String getManaraTagSheetName() {
        return manaraTagSheetName;
    }

    public static void setManaraTagSheetName(String manaraTagSheetName) {
        FileProcessingInputs.manaraTagSheetName = manaraTagSheetName;
    }

    public static String getmFileType() {
        return mFileType;
    }

    public static void setmFileType(String mFileType) {
        FileProcessingInputs.mFileType = mFileType;
    }

    public static String getiFileType() {
        return iFileType;
    }

    public static void setiFileType(String iFileType) {
        FileProcessingInputs.iFileType = iFileType;
    }

    public Multimap<String, String> getSuperSetOfAllTagsMultiMapManara() {
        return superSetOfAllTagsMultiMapManara;
    }

    public void setSuperSetOfAllTagsMultiMapManara(Multimap<String, String> superSetOfAllTagsMultiMapManara) {
        this.superSetOfAllTagsMultiMapManara = superSetOfAllTagsMultiMapManara;
    }

    public Multimap<String, String> getSuperSetOfAllTagsMultiMapIWIC() {
        return superSetOfAllTagsMultiMapIWIC;
    }

    public void setSuperSetOfAllTagsMultiMapIWIC(Multimap<String, String> superSetOfAllTagsMultiMapIWIC) {
        this.superSetOfAllTagsMultiMapIWIC = superSetOfAllTagsMultiMapIWIC;
    }


    public List<String> getDiagnosticIWICFilterTags() {
        return diagnosticIWICFilterTags;
    }

    public void setDiagnosticIWICFilterTags(List<String> diagnosticIWICFilterTags) {
        this.diagnosticIWICFilterTags = diagnosticIWICFilterTags;
    }

    public String getTagMappingIWICSheetName() {
        return tagMappingIWICSheetName;
    }

    public void setTagMappingIWICSheetName(String tagMappingIWICSheetName) {
        this.tagMappingIWICSheetName = tagMappingIWICSheetName;
    }

    public String getFileTypeFlag() {
        return fileTypeFlag;
    }

    public void setFileTypeFlag(String fileTypeFlag) {
        this.fileTypeFlag = fileTypeFlag;
    }

    public String getCurrProcessingFileName() {
        return currProcessingFileName;
    }

    public void setCurrProcessingFileName(String currProcessingFileName) {
        this.currProcessingFileName = currProcessingFileName;
    }

    public String getTagsNameIWICSheetName() {
        return tagsNameIWICSheetName;
    }

    public void setTagsNameIWICSheetName(String tagsNameIWICSheetName) {
        this.tagsNameIWICSheetName = tagsNameIWICSheetName;
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
