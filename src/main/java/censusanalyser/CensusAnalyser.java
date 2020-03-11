package censusanalyser;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
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
        censusStateMap = new CensusLoader().loadCensusData(csvFilePath,IndiaCensusCSV.class);
        return censusStateMap.size();
    }

    public int loadUSCensusData(String usCensusCsvFilePath) throws CensusAnalyserException {
        censusStateMap = new CensusLoader().loadCensusData(usCensusCsvFilePath,USCensusCSV.class);
        return censusStateMap.size();
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
        if (censusStateMap == null || censusStateMap.size() == 0 ){
            throw new CensusAnalyserException("No_census_data",CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }

        Comparator<CensusDAO> comparing = Comparator.comparing(census -> census.state);
        censusList = censusStateMap.values().stream().collect(Collectors.toList());
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
