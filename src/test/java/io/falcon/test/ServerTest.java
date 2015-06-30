package io.falcon.test;

import io.falcon.bootstrap.FalconBootStrap;
import io.falcon.bootstrap.FalconFactory;
import io.falcon.service.BaseFalconService;
import org.junit.Test;

/**
 * Created by cozybz@gmail.com on 2015/4/18.
 */
public class ServerTest {
    @Test
    public void runServer() {
        BaseFalconService service = new BaseFalconService();

        FalconBootStrap.startService(service, 8080);

        //BaseFalconService testService = FalconFactory.remoteService(BaseFalconService.class, "127.0.0.1", 8080);
    }
}
