package system.exceptions;

public class UrlNotSupportedException extends Exception {

    public UrlNotSupportedException(String url) {
        super("Url '" + url + "' is not supported.");
    }

}
