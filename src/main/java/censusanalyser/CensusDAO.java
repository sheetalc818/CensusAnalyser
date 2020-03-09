package censusanalyser;

import java.util.Comparator;

public class IndiaCensusDAO {
    public double population;
    public double densityPerSqKm;
    public double area;
    public String state;
    public String stateCode;

    public IndiaCensusDAO(IndiaCensusCSV indiaCensusCSV) {
        state = indiaCensusCSV.state;
        area = indiaCensusCSV.areaInSqKm;
        densityPerSqKm = indiaCensusCSV.densityPerSqKm;
        population = indiaCensusCSV.population;
    }

    public IndiaCensusDAO(IndiaStateCodeCSV indiaStateCodeCSV){
        stateCode = indiaStateCodeCSV.stateCode;
    }
}
