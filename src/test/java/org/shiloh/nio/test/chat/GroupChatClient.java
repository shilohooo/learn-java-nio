package org.shiloh.nio.test.chat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.nio.channels.SelectionKey.OP_READ;
import static org.shiloh.nio.test.chat.GroupChatServer.HOST;
import static org.shiloh.nio.test.chat.GroupChatServer.PORT;
import static org.shiloh.util.ByteUtils.trimByteData;

/**
 * Nio 多人聊天室 - 客户端
 *
 * @author shiloh
 * @date 2023/2/25 19:04
 */
public class GroupChatClient {
    /**
     * 选择器
     */
    private Selector selector;

    /**
     * 客户端连接通道
     */
    private SocketChannel socketChannel;

    /**
     * 用户名
     */
    private String username;

    public GroupChatClient() {
        this.init();
    }

    /**
     * 初始化
     *
     * @author shiloh
     * @date 2023/2/25 19:05
     */
    private void init() {
        try {
            // 打开一个选择器
            this.selector = Selector.open();
            // 连接到指定的服务器
            this.socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            // 设置为非阻塞模式
            this.socketChannel.configureBlocking(false);
            // 注册到选择器中，并指定可读事件
            this.socketChannel.register(this.selector, OP_READ);
            // 获取用户名
            this.username = this.socketChannel.getLocalAddress()
                    .toString()
                    .substring(1);
            System.out.println(this.username + " is ok~~");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务端发送消息
     *
     * @author shiloh
     * @date 2023/2/25 19:07
     */
    private void sendMsg(String msg) {
        msg = this.username + "说: " + msg;
        try {
            this.socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取服务端发送过来的消息
     *
     * @author shiloh
     * @date 2023/2/25 19:09
     */
    private void readMsg() {
        try {
            final int count = this.selector.select();
            if (count > 0) {
                final Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    final SelectionKey selectionKey = iterator.next();
                    // 判断事件类型是否为可读事件
                    if (selectionKey.isReadable()) {
                        // 获取消息
                        final SocketChannel channel = (SocketChannel) selectionKey.channel();
                        // 创建一个缓冲区
                        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        // 从服务器的通道中读取数据到缓冲区
                        channel.read(byteBuffer);
                        // 转为为字符串并打印
                        System.out.println(new String(trimByteData(byteBuffer.array())));
                    }
                    // 移除已处理的事件，避免重复处理
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final GroupChatClient chatClient = new GroupChatClient();
        // 启动线程，读取服务器转发过来的消息
        new Thread(() -> {
            while (true) {
                chatClient.readMsg();
                try {
                    TimeUnit.SECONDS.sleep(3L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        // 主线程发送消息到服务器
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            final String msg = scanner.nextLine();
            // 发送消息到服务器
            chatClient.sendMsg(msg);
        }
    }
}
