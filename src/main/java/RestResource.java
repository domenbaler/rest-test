import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Produces(MediaType.APPLICATION_JSON)
@Path("/rest")
@Tag(name = "Rest test")
public class RestResource {
    private static final ExecutorService executor1 = Executors.newFixedThreadPool(1);
    private static final ExecutorService executor2 = Executors.newFixedThreadPool(2);
    private static final ExecutorService executor3 = Executors.newFixedThreadPool(3);
    private static final ExecutorService executor4 = Executors.newFixedThreadPool(4);

    private static final String url1 = "https://www.result.si/projekti";
    private static final String url2 = "https://www.result.si/o-nas";
    private static final String url3 = "https://www.result.si/kariera";
    private static final String url4 = "https://www.result.si/blog";

    private static final Set<String> titles = new HashSet<>();

    private static int numOfSuccessfulCalls = 0;
    private static int numOfUnsuccessfulCalls = 0;

    private synchronized static void incrementSuccessfulCalls(){
        numOfSuccessfulCalls++;
    }

    private synchronized static void incrementUnsuccessfulCalls(){
        numOfUnsuccessfulCalls++;
    }

    private synchronized static void addTitle(String title){
        titles.add(title);
    }

    private static void getHelper(String url){
        try{
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if(con.response().statusCode() == 200){
                incrementSuccessfulCalls();
                addTitle(doc.title());
            }else{
                incrementUnsuccessfulCalls();
            }
        }catch(IOException e){
            incrementUnsuccessfulCalls();
        }
    }

    @GET
    @Operation(summary = "Get titles and number of calls", description = "Returns titles extracted from websites and number of successful and unsuccessful calls.")
    @APIResponses({
            @APIResponse(description = "A JSON object containing website titles and number of successful/unsuccessful calls", responseCode ="200", content = @Content(mediaType = "application/json"))
    })
    public synchronized Response getStatistics() {

        Map<String, String> json = new HashMap<>();
        json.put("NumberOfSuccessfulCalls", "" + numOfSuccessfulCalls);
        json.put("NumberOfUnsuccessfulCalls", "" + numOfUnsuccessfulCalls);

        int counter = 1;
        for(String title : titles){
            json.put("Title"+counter, title);
            counter++;
        }

        return Response.ok(json).build();
    }

    @POST
    @Operation(summary = "Trigger website title extraction", description = "Sends GET requests on 1-4 threads, extracts their titles and saves them to a global variable.")
    @Parameter( description = "integer between 1 and 4 which determines how many concurrent threads are used", required = true, example = "2", schema = @Schema(type = SchemaType.INTEGER, minimum = "1", maximum = "4"))
    @APIResponses({
            @APIResponse(description = "Title extraction triggered successfully", responseCode ="204")
    })
    @Path("/{numOfSimCalls}")
    public Response getResources(@PathParam("numOfSimCalls") int numOfSimCalls) {

        if(numOfSimCalls < 1 || numOfSimCalls > 4){
             return Response.status(400).build();
        }

        switch(numOfSimCalls) {
            case 1:
                executor1.execute(new Thread(() -> getHelper(url1)));
                executor1.execute(new Thread(() -> getHelper(url2)));
                executor1.execute(new Thread(() -> getHelper(url3)));
                executor1.execute(new Thread(() -> getHelper(url4)));
                break;
            case 2:
                executor2.execute(new Thread(() -> getHelper(url1)));
                executor2.execute(new Thread(() -> getHelper(url2)));
                executor2.execute(new Thread(() -> getHelper(url3)));
                executor2.execute(new Thread(() -> getHelper(url4)));
                break;
            case 3:
                executor3.execute(new Thread(() -> getHelper(url1)));
                executor3.execute(new Thread(() -> getHelper(url2)));
                executor3.execute(new Thread(() -> getHelper(url3)));
                executor3.execute(new Thread(() -> getHelper(url4)));
                break;
            case 4:
                executor4.execute(new Thread(() -> getHelper(url1)));
                executor4.execute(new Thread(() -> getHelper(url2)));
                executor4.execute(new Thread(() -> getHelper(url3)));
                executor4.execute(new Thread(() -> getHelper(url4)));
                break;
            default:
                System.out.println("Something went wrong with path parameter");
        }

        return Response.status(204).build();
    }

}