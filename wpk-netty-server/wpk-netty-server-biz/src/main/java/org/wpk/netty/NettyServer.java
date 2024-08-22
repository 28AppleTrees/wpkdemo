package org.wpk.netty;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.wpk.netty.channelhandler.WebSocketChannelHandler;
import org.wpk.netty.config.NettyServerProperties;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * netty-server服务器类
 */
@Slf4j
@Component
public class NettyServer implements ApplicationRunner {
    @Autowired
    private WebSocketChannelHandler webSocketChannelHandler;
    @Autowired
    private NettyServerProperties serverProperties;
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Autowired
    private NamingService namingService;

    @Override
    public void run(ApplicationArguments args) {
        new Thread(this::start).start();
    }


    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // bossGroup 负责处理和管理所有的连接请求, 并将这些请求分配给 workerGroup, 少量线程
        bossGroup = new NioEventLoopGroup(serverProperties.getBossThreads());
        // workerGroup 负责处理与每个连接相关的读写操作, 更多线程
        workerGroup = new NioEventLoopGroup(serverProperties.getWorkerThreads());
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 指定操作系统内核在处理连接请求队列中的最大连接数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // ChannelInitializer 定义channel初始化pipeline, 主要设置channel的处理器链
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // HttpServerCodec, 处理 HTTP 请求和响应
                            pipeline.addLast(new HttpServerCodec());
                            // 将 HTTP 请求或响应的多个部分合并成一个完整的对象, FullHttpRequest
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            // WebSocketServerProtocolHandler, 处理ws协议
                            pipeline.addLast(new WebSocketServerProtocolHandler(serverProperties.getWebSocketPath(), null, true, 65536, false, true));
                            // IdleStateHandler, 读写超时设置, 处理空闲状态, 分别触发 IdleStateEvent.READER_IDLE/WRITER_IDLE/ALL_IDLE
                            pipeline.addLast(new IdleStateHandler(serverProperties.getReaderIdleTime(), serverProperties.getWriterIdleTime(),
                                    serverProperties.getAllIdleTime(),
                                    TimeUnit.SECONDS));

                            // 自定义 ChannelInboundHandlerAdapter 或 ChannelOutboundHandlerAdapter
                            pipeline.addLast(webSocketChannelHandler);
                        }
                    });
            // 输出配置项
            log.info("netty-server properties:{}", serverProperties);

            // 绑定端口（阻塞）, 直到绑定操作完成
            ChannelFuture channelFuture = serverBootstrap.bind(serverProperties.getPort()).sync();
            log.info("netty-server start port:{}", serverProperties.getPort());
            log.info("netty-server start webSocketPath:{}", serverProperties.getWebSocketPath());

            // 注册nacos
            registerNacos();
            log.info("netty-server register with nacos");

            // 监听通道（阻塞）
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            stop();
        }
    }

    @PreDestroy
    private void stop() {
        // 注销nacos
        deregisterNacos();
        if (Objects.nonNull(bossGroup)) {
            bossGroup.shutdownGracefully();
        }
        if (Objects.nonNull(workerGroup)) {
            workerGroup.shutdownGracefully();
        }
    }

    private void registerNacos() {
        try {
            Instance instance = new Instance();
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            instance.setIp(ip);
            instance.setPort(serverProperties.getPort());
            // 注册registerInstance(服务名, 分组, 实例)
            namingService.registerInstance(serverProperties.getName(), nacosDiscoveryProperties.getGroup(), instance);
            log.info("nacos注册[服务名:{},分组:{}]", serverProperties.getName(), nacosDiscoveryProperties.getGroup());
        } catch (NacosException e) {
            log.error("NamingFactory.createNamingService 异常");
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private void deregisterNacos() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        String ip = address.getHostAddress();
        try {
            // 注销deregisterInstance(服务名, ip, port, 分组)
            String serverName = serverProperties.getName();
            Integer serverPort = serverProperties.getPort();
            namingService.deregisterInstance(serverName, nacosDiscoveryProperties.getGroup(), ip, serverPort);
            log.info("nacos注销[服务名:{},分组:{},地址:{}]", serverName, nacosDiscoveryProperties.getGroup(),
                    ip + ":" + serverPort);
        } catch (NacosException e) {
            log.error("NamingFactory.createNamingService 异常");
            throw new RuntimeException(e);
        }
    }
}
