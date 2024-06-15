package baseCodes;

public class IncorrectPasswordException extends Exception {

    private String fileName;

    public IncorrectPasswordException(String fileName, Throwable cause) {
        super("Incorrect password for file: " + fileName, cause);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
