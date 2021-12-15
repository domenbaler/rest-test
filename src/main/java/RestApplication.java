import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
@OpenAPIDefinition(info = @Info(title = "Rest-test api", description = "Simple Rest api which concurrently extracts titles from websites", version = "v1.0.0", contact = @Contact(name = "API Support", url = "https://www.example.com/support", email = "support@example.com"), license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")), servers = @Server(url = "http://localhost:8080"))
public class RestApplication extends Application {

}