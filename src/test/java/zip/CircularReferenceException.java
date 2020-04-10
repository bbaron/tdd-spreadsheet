package zip;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Aug 2, 2005
 * Time: 3:53:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class CircularReferenceException extends RuntimeException {
    public CircularReferenceException(String message) {
        super(message);
    }
}
