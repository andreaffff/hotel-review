package hotelReviewBackend;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
//TODO commentare il codice
// TODO controllo su risultati nulli (quando si cerca un id non inserito nel DB) se possibile farlo con un middleware che vale per qualsiasi risultato con campi null
@ApplicationPath("/api")
public class MyApplication extends Application {
}