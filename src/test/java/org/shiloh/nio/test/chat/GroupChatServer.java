package org.shiloh.nio.test.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static org.shiloh.util.ByteUtils.trimByteData;

/**
 * Nio 多人聊天室 - 服务端
 *
 * @author shiloh
 * @date 2023/2/25 18:40
 */
public class GroupChatServer {
    /**
     * 选择器
     */
    private Selector selector;

    /**
     * 服务器 socket channel
     */
    private ServerSocketChannel serverSocketChannel;

    /**
     * 服务端绑定地址
     */
    public static final String HOST = "127.0.0.1";

    /**
     * 服务端监听端口
     */
    public static final int PORT = 8099;

    public GroupChatServer() {
        this.init();
    }

    /**
     * 启动服务端
     *
     * @author shiloh
     * @date 2023/2/25 18:45
     */
    public void start() {
        this.listen();
    }

    /**
     * 服务端初始化
     *
     * @author shiloh
     * @date 2023/2/25 18:42
     */
    private void init() {
        try {
            // 打开一个选择器
            this.selector = Selector.open();
            // 打开 server socket channel
            this.serverSocketChannel = ServerSocketChannel.open();
            // 绑定地址、端口号
            this.serverSocketChannel.bind(new InetSocketAddress(HOST, PORT));
            // 设置为非阻塞模式
            this.serverSocketChannel.configureBlocking(false);
            // 把通道注册到选择器中，并指定可接收连接事件
            this.serverSocketChannel.register(this.selector, OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听客户端连接，接收客户端消息，转发到其他客户端
     *
     * @author shiloh
     * @date 2023/2/25 18:45
     */
    private void listen() {
        try {
            while (true) {
                // 获取监听的事件总数
                final int totalEvents = this.selector.select(3000L);
                if (totalEvents == 0) {
                    System.out.println("等待连接中...");
                    continue;
                }
                // 获取监听到的事件集合
                final Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    final SelectionKey eventKey = iterator.next();
                    // 判断事件类型
                    if (eventKey.isAcceptable()) {
                        // 可接收连接事件，获取客户端连接
                        final SocketChannel clientChannel = this.serverSocketChannel.accept();
                        // 设置为非阻塞模式
                        clientChannel.configureBlocking(false);
                        // 将客户端连接对应的通道注册到选择器中，并指定可读取事件
                        clientChannel.register(this.selector, SelectionKey.OP_READ);
                        System.out.println(clientChannel.getRemoteAddress() + "上线了~~");
                    } else if (eventKey.isReadable()) {
                        // 可读取事件，读取客户端发送过来的消息，转发到其他客户端
                        this.readData(eventKey);
                    }
                    // 移除已处理过的事件，避免重复处理
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 读取客户端发送过来的消息，转发到其他客户端
     *
     * @param eventKey key 对象
     * @author shiloh
     * @date 2023/2/25 18:51
     */
    private void readData(SelectionKey eventKey) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = (SocketChannel) eventKey.channel();
            // 创建一个缓冲区
            final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            // 把通道的数据写入缓冲区
            final int count = socketChannel.read(byteBuffer);
            // 判断是否读取到了数据
            if (count > 0) {
                // 将消息转换为字符串，并转发给其他客户端
                final String msg = new String(trimByteData(byteBuffer.array()));
                System.out.println("msg from client: " + msg);
                this.notifyAllClient(socketChannel, msg);
            }
        } catch (Exception e) {
            try {
                // 打印离线通知
                if (socketChannel != null) {
                    System.out.println(socketChannel.getRemoteAddress() + "离线了...");
                    // 关闭流
                    socketChannel.close();
                }
                // 取消注册
                eventKey.cancel();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 转发消息到其他客户端
     *
     * @param ignoreChannel 不需要通知的 Channel
     * @param msg           消息内容
     * @author shiloh
     * @date 2023/2/25 18:57
     */
    private void notifyAllClient(SocketChannel ignoreChannel, String msg) throws IOException {
        System.out.println("服务器转发消息~");
        for (final SelectionKey key : this.selector.keys()) {
            final Channel channel = key.channel();
            // channel 的实际类型为 SocketChannel，这里需要排除掉不需要通知的 channel
            // JDK instanceof 新语法，可直接在判断中声明变量去使用，无需手动强转
            if (channel instanceof final SocketChannel socketChannel && channel != ignoreChannel) {
                // 将消息包装成一个 byte buffer
                final ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                socketChannel.write(byteBuffer);
            }
        }
    }

    public static void main(String[] args) {
        // 启动服务器
        new GroupChatServer().start();
    }
}
