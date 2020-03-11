package censusanalyser;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.StreamSupport;

public class CensusAnalyser {

    public enum FieldName{
        STATE,POPULATION,DENSITY,AREA
    }

    List<CensusDAO> censusList = null ;
    Map<FieldName,Comparator<CensusDAO>> sortMap = null;
    Map<String,CensusDAO> censusStateMap = null;

    public CensusAnalyser() {
        this.censusList = new ArrayList<CensusDAO>();
        this.sortMap = new HashMap<>();
        this.censusStateMap = new HashMap<>();
        this.sortMap.put(FieldName.POPULATION,Comparator.comparing(census->census.population));
        this.sortMap.put(FieldName.STATE,Comparator.comparing(census->census.state));
        this.sortMap.put(FieldName.DENSITY,Comparator.comparing(census->census.populationDensity));
        this.sortMap.put(FieldName.AREA,Comparator.comparing(census->census.totalArea));
    }

    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        return this.loadCensusData(csvFilePath,IndiaCensusCSV.class);
    }

    public int loadUSCensusData(String usCensusCsvFilePath) throws CensusAnalyserException {
        return this.loadCensusData(usCensusCsvFilePath,USCensusCSV.class);
    }

    private<E> int loadCensusData(String csvFilePath, Class<E> censusCSVClass) throws CensusAnalyserException {
        try(Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));){
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<E> csvFileIterator = csvBuilder.getCSVFileIterator(reader, censusCSVClass);
            Iterable<E> csvFileIterable=()-> csvFileIterator;
            if (censusCSVClass.getName().equals("censusanalyser.IndiaCensusCSV")){
                StreamSupport.stream(csvFileIterable.spliterator(),false)
                        .map(IndiaCensusCSV.class::cast)
                        .forEach(censusCSV -> censusStateMap.put(censusCSV.state,new CensusDAO(censusCSV)));
            }else if (censusCSVClass.getName().equals("censusanalyser.USCensusCSV")){
                StreamSupport.stream(csvFileIterable.spliterator(),false)
                        .map(USCensusCSV.class::cast)
                        .forEach(censusCSV -> censusStateMap.put(censusCSV.state,new CensusDAO(censusCSV)));
            }
            return censusStateMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(), CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }
    }

    public int loadIndianStateCode(String csvFilePath) throws CensusAnalyserException {
        try(Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));){
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();

            Iterator<IndiaStateCodeCSV> stateCSVIterator = csvBuilder.getCSVFileIterator(reader,IndiaStateCodeCSV.class) ;
            return this.getCount(stateCSVIterator);
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }
    }

    private <E> int getCount(Iterator<E> iterator){
        Iterable<E> csvIterable = () -> iterator;
        int numOfEntries = (int) StreamSupport.stream(csvIterable.spliterator(),false).count();
        return numOfEntries;
    }

    public String getSortedCensusCSV(FieldName field) throws CensusAnalyserException {
        if (censusList == null || censusList.size() == 0 ){
            throw new CensusAnalyserException("No_census_data",CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        this.sort(this.sortMap.get(field));
        String sortedStateCensusJson = new Gson().toJson(censusList);
        return sortedStateCensusJson;
    }

    private void sort(Comparator<CensusDAO> censusComparator) {
        for (int i=0;i<censusList.size()-1;i++){
            for (int j=0;j<censusList.size()-i-1;j++){
                CensusDAO census1 = censusList.get(j);
                CensusDAO census2 = censusList.get(j+1);
                //Ascending order
                if (censusComparator.compare(census1,census2) > 0){
                    censusList.set(j,census2);
                    censusList.set(j+1,census1);
                }
            }
        }
    }
}
