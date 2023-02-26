package org.shiloh.nio.test.network;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 客户端
 *
 * @author shiloh
 * @date 2023/2/25 18:01
 */
public class NioClient {
    public static void main(String[] args) throws Exception {
        // 打开一个 socketChannel
        final SocketChannel socketChannel = SocketChannel.open();
        // 设置为非阻塞模式
        socketChannel.configureBlocking(false);
        // 连接到一个指定的服务端地址
        final InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8092);
        final boolean isConnected = socketChannel.connect(inetSocketAddress);
        // 判断连接是否成功
        if (!isConnected) {
            // 等待连接的过程中，可以干点别的事情
            if (!socketChannel.finishConnect()) {
                System.out.println("连接服务器需要一点时间，在这期间可以干点别的事情...");
            }
        }
        // 连接成功后向服务器发送一点数据
        final String msg = "Hello Java Nio~~~";
        final ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        // 把 byte buffer 数据写入到通道中
        socketChannel.write(byteBuffer);
        // 让程序卡在这个位置，不关闭连接，观察服务端是否成功接收到了数据
        System.in.read();
    }
}
