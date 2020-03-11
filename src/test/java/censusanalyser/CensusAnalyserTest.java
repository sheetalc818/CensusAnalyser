package censusanalyser;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CensusAnalyserTest {

    private static final String INDIA_CENSUS_CSV_FILE_PATH = "./src/test/resources/IndiaStateCensusData.csv";
    private static final String WRONG_CSV_FILE_PATH = "./src/main/resources/IndiaStateCensusData.csv";
    private static final String INDIA_STATE_CSV_FILE_PATH = "./src/test/resources/IndiaStateCode.csv";
    private static final String US_CENSUS_CSV_FILE_PATH = "./src/test/resources/USCensusData.csv";

    @Test
    public void givenIndianCensusCSVFileReturnsCorrectRecords() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            int numOfRecords = censusAnalyser.loadIndiaCensusData(INDIA_CENSUS_CSV_FILE_PATH);
            Assert.assertEquals(29,numOfRecords);
        } catch (CensusAnalyserException e) { }
    }

    @Test
    public void givenIndiaCensusData_WithWrongFile_ShouldThrowException() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            ExpectedException exceptionRule = ExpectedException.none();
            exceptionRule.expect(CensusAnalyserException.class);
            censusAnalyser.loadIndiaCensusData(WRONG_CSV_FILE_PATH);
        } catch (CensusAnalyserException e) {
            Assert.assertEquals(CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM,e.type);
        }
    }

    @Test
    public void givenIndianStateCSV_ShouldReturnExactCount()  {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            int numOfStateCode = censusAnalyser.loadIndianStateCode(INDIA_STATE_CSV_FILE_PATH);
            Assert.assertEquals(37,numOfStateCode);
        } catch (CensusAnalyserException e) { }
    }

    @Test
    public void givenIndiaCensusData_WhenSortedOnState_ShouldReturnSortedResult() {
        try {
        CensusAnalyser censusAnalyser = new CensusAnalyser();
        censusAnalyser.loadIndiaCensusData(INDIA_CENSUS_CSV_FILE_PATH);
        String sortedStateCensusData = censusAnalyser.getSortedCensusCSV(CensusAnalyser.FieldName.STATE);
        IndiaCensusCSV[] indiaCensusCSVS = new Gson().fromJson(sortedStateCensusData, IndiaCensusCSV[].class);
        Assert.assertEquals("Andhra Pradesh",indiaCensusCSVS[0].state);
        } catch (CensusAnalyserException e) { }
    }

    @Test
    public void givenIndiaCensusData_WhenSortedOnPopulation_ShouldReturnSortedResult() {
        CensusAnalyser censusAnalyser = new CensusAnalyser();
        try {
            censusAnalyser.loadIndiaCensusData(INDIA_CENSUS_CSV_FILE_PATH);
            String result = censusAnalyser.getSortedCensusCSV(CensusAnalyser.FieldName.POPULATION);
            IndiaCensusCSV[] indiaCensusCSVS = new Gson().fromJson(result, IndiaCensusCSV[].class);
            Assert.assertEquals("Uttar Pradesh", indiaCensusCSVS[indiaCensusCSVS.length - 1].state.trim());
        } catch (CensusAnalyserException e) { }
    }

    @Test
    public void givenIndiaCensusData_WhenSortedOnDensity_ShouldReturnSortedResult() {
        CensusAnalyser censusAnalyser = new CensusAnalyser();
        try {
            censusAnalyser.loadIndiaCensusData(INDIA_CENSUS_CSV_FILE_PATH);
            String result = censusAnalyser.getSortedCensusCSV(CensusAnalyser.FieldName.DENSITY);
            IndiaCensusCSV[] indiaCensusCSVS = new Gson().fromJson(result, IndiaCensusCSV[].class);
            Assert.assertEquals("Arunachal Pradesh", indiaCensusCSVS[0].state.trim());
            Assert.assertEquals("Bihar", indiaCensusCSVS[indiaCensusCSVS.length-1].state.trim());
        } catch (CensusAnalyserException e) { }
    }

    @Test
    public void givenIndiaCensusData_WhenSortOnArea_ShouldReturnSortedResult() {
        CensusAnalyser censusAnalyser = new CensusAnalyser();
        try {
            censusAnalyser.loadIndiaCensusData(INDIA_CENSUS_CSV_FILE_PATH);
            System.out.println("Testing1");
            String result = censusAnalyser.getSortedCensusCSV(CensusAnalyser.FieldName.AREA);
            System.out.println("Testing2");
            IndiaCensusCSV[] indiaCensusCSVS = new Gson().fromJson(result, IndiaCensusCSV[].class);
            System.out.println("Testing3");
            Assert.assertEquals("Rajasthan", indiaCensusCSVS[indiaCensusCSVS.length-1].state.trim());
            System.out.println("Test4");
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenUSCensusData_ShouldReturnCorrectRecordCount() {
        try {
            CensusAnalyser censusAnalyser = new CensusAnalyser();
            int count = censusAnalyser.loadUSCensusData(US_CENSUS_CSV_FILE_PATH);
            Assert.assertEquals(51,count);
        } catch (CensusAnalyserException e) {
            e.printStackTrace();
        }
    }
}






