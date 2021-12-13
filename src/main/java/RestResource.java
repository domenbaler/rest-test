import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Path("/rest")
public class RestResource {
    private static int numOfSuccessfulCalls = 0;
    private static int numOfUnsuccessfulCalls = 0;

    private static final Set<String> titles = new HashSet<>();

    private static final String url1 = "https://www.result.si/projekti";
    private static final String url2 = "https://www.result.si/o-nas";
    private static final String url3 = "https://www.result.si/kariera";
    private static final String url4 = "https://www.result.si/blog";

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
            System.out.println(e);
        }
    }

    @GET
    public synchronized Response getStatistics() {

        Map<String, String> json = new HashMap<>();
        json.put("Number of Successful calls:", "" + numOfSuccessfulCalls);
        json.put("Number of Unsuccessful calls:", "" + numOfUnsuccessfulCalls);

        int counter = 1;
        for(String title : titles){
            json.put("Title "+counter, title);
            counter++;
        }

        return Response.ok(json).build();
    }

    @POST
    @Path("/{numOfSimCalls}")
    public Response getResources(@PathParam("numOfSimCalls") int numOfSimCalls) {

        if(numOfSimCalls < 1 || numOfSimCalls > 4){
             return Response.status(400).build();
        }

        switch(numOfSimCalls) {
            case 1:
                getHelper(url1);
                getHelper(url2);
                getHelper(url3);
                getHelper(url4);
                break;
            case 2:
                getHelper(url1);
                getHelper(url2);
                new Thread(() -> getHelper(url3)).start();
                new Thread(() -> getHelper(url4)).start();
                break;
            case 3:
                getHelper(url1);
                new Thread(() -> getHelper(url2)).start();
                new Thread(() -> getHelper(url3)).start();
                new Thread(() -> getHelper(url4)).start();
                break;
            case 4:
                new Thread(() -> getHelper(url1)).start();
                new Thread(() -> getHelper(url2)).start();
                new Thread(() -> getHelper(url3)).start();
                new Thread(() -> getHelper(url4)).start();
                break;
            default:
                System.out.println("Something went wrong with path parameter");
        }

        return Response.status(204).build();
    }

}