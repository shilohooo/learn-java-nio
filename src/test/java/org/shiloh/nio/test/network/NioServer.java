package org.shiloh.nio.test.network;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;
import static org.shiloh.util.ByteUtils.trimByteData;

/**
 * 服务端
 *
 * @author shiloh
 * @date 2023/2/25 17:47
 */
public class NioServer {
    public static void main(String[] args) throws Exception {
        // 打开一个 ServerSocketChannel
        final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定一个地址
        final InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8092);
        serverSocketChannel.bind(inetSocketAddress);
        // 设定为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 打开一个选择器
        final Selector selector = Selector.open();
        // 注册选择器，并指定事件：OP_ACCEPT，表示可以接收连接
        serverSocketChannel.register(selector, OP_ACCEPT);
        // 循环等待客户端连接
        while (true) {
            // 等待 3秒，如果没有事件则跳过（返回 0 相当于没有事件
            if (selector.select(3000L) == 0) {
                System.out.println("服务器等待3秒，没有连接。");
                continue;
            }
            // 获取事件
            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 遍历事件数据
            final Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
            while (selectionKeyIterator.hasNext()) {
                // 获取当前事件
                final SelectionKey selectionKey = selectionKeyIterator.next();
                // 判断事件类型
                if (selectionKey.isAcceptable()) {
                    // 如果是可接收连接事件，服务端可以与客户端建立连接，获取 socketChannel
                    final SocketChannel socketChannel = serverSocketChannel.accept();
                    // 设置成非阻塞
                    socketChannel.configureBlocking(false);
                    // 把 socketChannel 注册到选择器中，监听读事件，并绑定一个缓冲区
                    socketChannel.register(selector, OP_READ, ByteBuffer.allocate(1024));
                }
                if (selectionKey.isReadable()) {
                    // 如果是读事件，获取一个 socketChannel
                    final SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    // 获取该通道绑定的缓冲区
                    final ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                    // 打印从客户端接收到的数据
                    socketChannel.read(byteBuffer);
                    final byte[] dataArr = byteBuffer.array();
                    // 去掉字节数组中多余的0，不然转字符串时，字符串中会包含乱码

                    System.out.println("data from client: " + new String(trimByteData(dataArr)));
                }
                // 从事件集合中移除已经处理的事件，防止重复处理
                selectionKeyIterator.remove();
            }
        }
    }
}
