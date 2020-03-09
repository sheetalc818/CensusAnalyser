package censusanalyser;

import com.opencsv.bean.CsvBindByName;

import java.util.Comparator;


public class IndiaStateCodeCSV {
    @CsvBindByName(column = "State Name", required = true)
    public String state;

    @CsvBindByName(column = "StateCode", required = true)
    public String stateCode;

    @Override
    public String toString() {
        return "IndiaStateCodeCSV{" +
                "state='" + state + '\'' +
                ", stateCode='" + stateCode + '\'' +
                '}';
    }
}

