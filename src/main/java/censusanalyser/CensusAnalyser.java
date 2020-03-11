package censusanalyser;

import com.google.gson.Gson;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CensusAnalyser {

    public enum FieldName{
        STATE,POPULATION,DENSITY,AREA
    }

    public enum Country{
        INDIA,US
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

    public int loadCensusData(Country country, String... csvFilePath) throws CensusAnalyserException {
        censusStateMap = CensusAdapterFactory.getCensusData(country,csvFilePath);
        return censusStateMap.size();
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

        Comparator<CensusDAO> censusComparator = Comparator.comparing(census -> census.state);
        censusList = censusStateMap.values().stream().collect(Collectors.toList());
        this.sort(this.sortMap.get(field),censusList);
        String sortedStateCensusJson = new Gson().toJson(censusList);
        return sortedStateCensusJson;
    }

    private void sort(Comparator<CensusDAO> censusComparator, List<CensusDAO> censusList) {
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
