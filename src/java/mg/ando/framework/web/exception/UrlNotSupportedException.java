package mg.ando.framework.web.exception;

public class UrlNotSupportedException extends Exception {

    public UrlNotSupportedException(String url) {
        super("Url '" + url + "' is not supported.");
    }

}
