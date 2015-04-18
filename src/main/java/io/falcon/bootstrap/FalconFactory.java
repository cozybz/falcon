package io.falcon.bootstrap;

import io.falcon.service.FalconService;

/**
 * Created by cozybz@gmail.com on 2015/4/15.
 */
public class FalconFactory {
    public static <T extends FalconService> T remoteService(Class<T> implClass, String host, int ip) {
        //TODO
        return null;
    }
}
