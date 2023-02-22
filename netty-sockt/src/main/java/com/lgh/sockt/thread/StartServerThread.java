package com.lgh.sockt.thread;
import com.lgh.sockt.server.StartServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author liuguanghu
 * @title: StartServerThread
 * @projectName eparking-microservice-nacos
 * @description: scokt服务端启动
 * @date 2022/9/21 17:39
 */
@Component
public class StartServerThread implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        new StartServer(8768).start();
    }
}
