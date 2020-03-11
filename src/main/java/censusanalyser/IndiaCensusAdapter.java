package censusanalyser;

import com.test.CSVBuilderException;
import com.test.CSVBuilderFactory;
import com.test.ICSVBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

public class IndiaCensusAdapter extends CensusAdapter {
    Map<String,CensusDAO> censusStateMap = null;
    @Override
    public Map<String, CensusDAO> loadCensusData(String... csvFilePath) throws CensusAnalyserException {
        Map<String,CensusDAO> censusStateMap = super.loadCensusData(IndiaCensusCSV.class,csvFilePath[0]);
        //this.loadIndianStateCode(censusStateMap,csvFilePath[1]);
        return censusStateMap;
    }

//    public Map<String, CensusDAO> loadCensusData(CensusAnalyser.Country country, String[] csvFilePath) throws CensusAnalyserException {
//        if (country.equals(CensusAnalyser.Country.INDIA))
//            return this.loadCensusData(IndiaCensusCSV.class,csvFilePath);
//        else if (country.equals(CensusAnalyser.Country.US))
//            return this.loadCensusData(USCensusCSV.class,csvFilePath);
//        else throw new CensusAnalyserException("Invalid country", CensusAnalyserException.ExceptionType.INVALID_COUNTRY);
//    }

    private int loadIndianStateCode(Map<String, CensusDAO> censusStateMap, String csvFilePath) throws CensusAnalyserException {
        try(Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));){
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();

            Iterator<IndiaStateCodeCSV> stateCSVIterator = csvBuilder.getCSVFileIterator(reader,IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> stateCSVIterable = ()->stateCSVIterator;
            StreamSupport.stream(stateCSVIterable.spliterator(),false)
                    .filter(csvState->censusStateMap.get(csvState.state) != null)
                    .forEach(csvState -> censusStateMap.get(csvState.state).stateCode = csvState.stateCode);
            return censusStateMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }
    }
}
