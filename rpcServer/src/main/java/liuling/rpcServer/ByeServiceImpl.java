package liuling.rpcServer;


import liuling.rpcCore.annotation.Service;
import liuling.serverInterface.ByeService;

/**
 * @author ziyang
 */
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
