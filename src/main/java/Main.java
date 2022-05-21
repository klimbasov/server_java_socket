import com.server.www.Impl.ServerImpl;
import com.server.www.Server;

public class Main {
    public static void main(String[] argvs)
    {
        // creating an object of the class ServerSide
        try(Server server = new ServerImpl(3399)){
            server.run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
