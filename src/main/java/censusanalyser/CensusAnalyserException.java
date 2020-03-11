package censusanalyser;

public class CensusAnalyserException extends Exception {


    enum ExceptionType {
        CENSUS_FILE_PROBLEM,NO_CENSUS_DATA,INVALID_COUNTRY;
    }

    public ExceptionType type;

    public CensusAnalyserException(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }

    public CensusAnalyserException(String message, ExceptionType type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public CensusAnalyserException(String message, String name) {
        super(message);
        this.type = ExceptionType.valueOf(name);
    }
}
