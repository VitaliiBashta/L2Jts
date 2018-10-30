package org.mmocore.commons.net.nio.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unchecked")
public class SelectorThread<T extends MMOClient> extends Thread {
    private static final Logger _log = LoggerFactory.getLogger(SelectorThread.class);
    private static final List<SelectorThread> ALL_SELECTORS = new ArrayList<>();
    private static final SelectorStats stats = new SelectorStats();
    private final Selector _selector = Selector.open();
    // Implementations
    private final IPacketHandler<T> _packetHandler;
    private final IMMOExecutor<T> _executor;
    private final IClientFactory<T> _clientFactory;
    // Configs
    private final SelectorConfig _sc;
    private final int HELPER_BUFFER_SIZE;

    // MAIN BUFFERS
    private final ByteBuffer DIRECT_WRITE_BUFFER;
    private final ByteBuffer WRITE_BUFFER, READ_BUFFER;
    // ByteBuffers General Purpose Pool
    private final Queue<ByteBuffer> _bufferPool;
    private final List<MMOConnection<T>> _connections;
    private IAcceptFilter _acceptFilter;
    private boolean _shutdown;
    private T WRITE_CLIENT;

    public SelectorThread(final SelectorConfig sc, final IPacketHandler<T> packetHandler, final IMMOExecutor<T> executor, final IClientFactory<T> clientFactory,
                          final IAcceptFilter acceptFilter) throws IOException {
        synchronized (ALL_SELECTORS) {
            ALL_SELECTORS.add(this);
        }

        _sc = sc;
        _acceptFilter = acceptFilter;
        _packetHandler = packetHandler;
        _clientFactory = clientFactory;
        _executor = executor;

        _bufferPool = new ArrayDeque<>(_sc.HELPER_BUFFER_COUNT);
        _connections = new CopyOnWriteArrayList<>();

        DIRECT_WRITE_BUFFER = ByteBuffer.wrap(new byte[_sc.WRITE_BUFFER_SIZE]).order(_sc.BYTE_ORDER);
        WRITE_BUFFER = ByteBuffer.wrap(new byte[_sc.WRITE_BUFFER_SIZE]).order(_sc.BYTE_ORDER);
        READ_BUFFER = ByteBuffer.wrap(new byte[_sc.READ_BUFFER_SIZE]).order(_sc.BYTE_ORDER);
        HELPER_BUFFER_SIZE = Math.max(_sc.READ_BUFFER_SIZE, _sc.WRITE_BUFFER_SIZE);

        for (int i = 0; i < _sc.HELPER_BUFFER_COUNT; i++) {
            _bufferPool.add(ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(_sc.BYTE_ORDER));
        }
    }

    public static CharSequence getStats() {
        final StringBuilder list = new StringBuilder();

        list.append("selectorThreadCount: .... ").append(ALL_SELECTORS.size()).append('\n');
        list.append("=================================================\n");
        list.append("getTotalConnections: .... ").append(stats.getTotalConnections()).append('\n');
        list.append("getCurrentConnections: .. ").append(stats.getCurrentConnections()).append('\n');
        list.append("getMaximumConnections: .. ").append(stats.getMaximumConnections()).append('\n');
        list.append("getIncomingBytesTotal: .. ").append(stats.getIncomingBytesTotal()).append('\n');
        list.append("getOutgoingBytesTotal: .. ").append(stats.getOutgoingBytesTotal()).append('\n');
        list.append("getIncomingPacketsTotal:  ").append(stats.getIncomingPacketsTotal()).append('\n');
        list.append("getOutgoingPacketsTotal:  ").append(stats.getOutgoingPacketsTotal()).append('\n');
        list.append("getMaxBytesPerRead: ..... ").append(stats.getMaxBytesPerRead()).append('\n');
        list.append("getMaxBytesPerWrite: .... ").append(stats.getMaxBytesPerWrite()).append('\n');
        list.append("=================================================\n");

        return list;
    }

    public void openServerSocket(final InetAddress address, final int tcpPort) throws IOException {
        final ServerSocketChannel selectable = ServerSocketChannel.open();
        selectable.configureBlocking(false);

        selectable.socket().bind(address == null ? new InetSocketAddress(tcpPort) : new InetSocketAddress(address, tcpPort));
        selectable.register(getSelector(), selectable.validOps());
        setName("SelectorThread:" + selectable.socket().getLocalPort());
    }

    protected ByteBuffer getPooledBuffer() {
        if (_bufferPool.isEmpty()) {
            return ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(_sc.BYTE_ORDER);
        }
        return _bufferPool.poll();
    }

    protected void recycleBuffer(final ByteBuffer buf) {
        if (_bufferPool.size() < _sc.HELPER_BUFFER_COUNT) {
            buf.clear();
            _bufferPool.add(buf);
        }
    }

    protected void freeBuffer(final ByteBuffer buf, final MMOConnection<T> con) {
        if (buf == READ_BUFFER) {
            READ_BUFFER.clear();
        } else {
            con.setReadBuffer(null);
            recycleBuffer(buf);
        }
    }

    @Override
    public void run() {
        int totalKeys;
        Set<SelectionKey> keys;
        Iterator<SelectionKey> itr;
        Iterator<MMOConnection<T>> conItr;
        SelectionKey key;
        MMOConnection<T> con;
        long currentMillis;

        // main loop
        for (; ; ) {
            try {
                if (isShuttingDown()) {
                    closeSelectorThread();
                    break;
                }

                currentMillis = System.currentTimeMillis();

                conItr = _connections.iterator();
                while (conItr.hasNext()) {
                    con = conItr.next();
                    if (con.isPengingClose()) {
                        if (!con.isPendingWrite() || currentMillis - con.getPendingCloseTime() >= 10000L) {
                            closeConnectionImpl(con);
                            continue;
                        }
                    }
                    if (con.isPendingWrite()) {
                        if (currentMillis - con.getPendingWriteTime() >= _sc.INTEREST_DELAY) {
                            con.enableWriteInterest();
                        }
                    }
                }

                totalKeys = getSelector().selectNow();

                if (totalKeys > 0) {
                    keys = getSelector().selectedKeys();
                    itr = keys.iterator();

                    while (itr.hasNext()) {
                        key = itr.next();
                        itr.remove();

                        if (key.isValid()) {
                            try {
                                if (key.isAcceptable()) {
                                    acceptConnection(key);
                                    continue;
                                } else if (key.isConnectable()) {
                                    finishConnection(key);
                                    continue;
                                }

                                if (key.isReadable()) {
                                    readPacket(key);
                                }
                                if (key.isValid()) {
                                    if (key.isWritable()) {
                                        writePacket(key);
                                    }
                                }
                            } catch (CancelledKeyException cke) {

                            }
                        }
                    }
                }

                try {
                    Thread.sleep(_sc.SLEEP_TIME);
                } catch (InterruptedException ie) {
                }
            } catch (IOException e) {
                _log.error("Error in " + getName(), e);

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ie) {

                }
            }
        }
    }

    protected void finishConnection(final SelectionKey key) {
        try {
            ((SocketChannel) key.channel()).finishConnect();
        } catch (IOException e) {
            final MMOConnection<T> con = (MMOConnection<T>) key.attachment();
            final T client = con.getClient();
            client.getConnection().onForcedDisconnection();
            closeConnectionImpl(client.getConnection());
        }
    }

    protected void acceptConnection(final SelectionKey key) {
        final ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc;
        SelectionKey clientKey;
        try {
            while ((sc = ssc.accept()) != null) {
                if (getAcceptFilter() == null || getAcceptFilter().accept(sc)) {
                    sc.configureBlocking(false);
                    clientKey = sc.register(getSelector(), SelectionKey.OP_READ);

                    final MMOConnection<T> con = new MMOConnection<>(this, sc.socket(), clientKey);
                    final T client = getClientFactory().create(con);
                    client.setConnection(con);
                    con.setClient(client);
                    clientKey.attach(con);

                    _connections.add(con);
                    stats.increaseOpenedConnections();
                } else {
                    sc.close();
                }
            }
        } catch (IOException e) {
            _log.error("Error in " + getName(), e);
        }
    }

    protected void readPacket(final SelectionKey key) {
        final MMOConnection<T> con = (MMOConnection<T>) key.attachment();

        if (con.isClosed()) {
            return;
        }

        ByteBuffer buf;
        int result = -2;

        if ((buf = con.getReadBuffer()) == null) {
            buf = READ_BUFFER;
        }

        // if we try to to do a read with no space in the buffer it will read 0 bytes
        // going into infinite loop
        if (buf.position() == buf.limit()) {
            _log.error("Read buffer exhausted for client : " + con.getClient() + ", try to adjust buffer size, current : " + buf.capacity() + ", primary : " + (buf == READ_BUFFER) + ". Closing connection.");
            closeConnectionImpl(con);
        } else {

            try {
                result = con.getReadableByteChannel().read(buf);
            } catch (IOException e) {
                //error handling goes bellow
            }

            if (result > 0) {
                buf.flip();

                stats.increaseIncomingBytes(result);

                int i;
                // читаем из буфера максимум пакетов
                for (i = 0; this.tryReadPacket2(key, con, buf); i++) {
                }
            } else if (result == 0) {
                closeConnectionImpl(con);
            } else if (result == -1) {
                closeConnectionImpl(con);
            } else {
                con.onForcedDisconnection();
                closeConnectionImpl(con);
            }
        }

        if (buf == READ_BUFFER) {
            buf.clear();
        }
    }

    protected boolean tryReadPacket2(final SelectionKey key, final MMOConnection<T> con, final ByteBuffer buf) {
        if (con.isClosed()) {
            return false;
        }

        final int pos = buf.position();
        // проверяем, хватает ли нам байт для чтения заголовка и не пустого тела пакета
        if (buf.remaining() > _sc.HEADER_SIZE) {
            // получаем ожидаемый размер пакета
            int size = buf.getShort() & 0xffff;

            // проверяем корректность размера
            if (size <= _sc.HEADER_SIZE || size > _sc.PACKET_SIZE) {
                _log.error("Incorrect packet size : " + size + "! Client : " + con.getClient() + ". Closing connection.");
                closeConnectionImpl(con);
                return false;
            }

            //ожидаемый размер тела пакета
            size -= _sc.HEADER_SIZE;

            // проверяем, хватает ли байт на чтение тела
            if (size <= buf.remaining()) {
                stats.increaseIncomingPacketsCount();
                parseClientPacket(getPacketHandler(), buf, size, con);
                buf.position(pos + size + _sc.HEADER_SIZE);

                // закончили чтение из буфера, почистим
                if (!buf.hasRemaining()) {
                    freeBuffer(buf, con);
                    return false;
                }

                return true;
            }

            // не хватает данных на чтение тела пакета, сбрасываем позицию
            buf.position(pos);
        }

        if (pos == buf.capacity()) {
            _log.warn("Read buffer exhausted for client : " + con.getClient() + ", try to adjust buffer size, current : " + buf.capacity() + ", primary : " + (buf == READ_BUFFER) + '.');
        }

        // не хватает данных, переносим содержимое первичного буфера во вторичный
        if (buf == READ_BUFFER) {
            allocateReadBuffer(con);
        } else {
            buf.compact();
        }

        return false;
    }

    protected void allocateReadBuffer(final MMOConnection<T> con) {
        con.setReadBuffer(getPooledBuffer().put(READ_BUFFER));
        READ_BUFFER.clear();
    }

    protected boolean parseClientPacket(final IPacketHandler<T> handler, final ByteBuffer buf, final int dataSize, final MMOConnection<T> con) {
        final T client = con.getClient();

        final int pos = buf.position();

        client.decrypt(buf, dataSize);
        buf.position(pos);

        if (buf.hasRemaining()) {
            //  apply limit
            final int limit = buf.limit();
            buf.limit(pos + dataSize);
            final ReceivablePacket<T> rp = handler.handlePacket(buf, client);

            if (rp != null) {
                rp.setByteBuffer(buf);
                rp.setClient(client);

                if (rp.read()) {
                    con.recvPacket(rp);
                }

                rp.setByteBuffer(null);
            }
            buf.limit(limit);
        }
        return true;
    }

    protected void writePacket(final SelectionKey key) {
        final MMOConnection<T> con = (MMOConnection<T>) key.attachment();

        prepareWriteBuffer(con);

        DIRECT_WRITE_BUFFER.flip();
        final int size = DIRECT_WRITE_BUFFER.remaining();

        int result = -1;

        try {
            result = con.getWritableChannel().write(DIRECT_WRITE_BUFFER);
        } catch (IOException e) {
            // error handling goes on the if bellow
        }

        // check if no error happened
        if (result >= 0) {
            stats.increaseOutgoingBytes(result);

            // check if we written everything
            if (result != size) {
                con.createWriteBuffer(DIRECT_WRITE_BUFFER);
            }

            if (!con.getSendQueue().isEmpty() || con.hasPendingWriteBuffer())
            // запись не завершена
            {
                con.scheduleWriteInterest();
            }
        } else {
            con.onForcedDisconnection();
            closeConnectionImpl(con);
        }
    }

    protected T getWriteClient() {
        return WRITE_CLIENT;
    }

    protected ByteBuffer getWriteBuffer() {
        return WRITE_BUFFER;
    }

    protected void prepareWriteBuffer(final MMOConnection<T> con) {
        WRITE_CLIENT = con.getClient();
        DIRECT_WRITE_BUFFER.clear();

        if (con.hasPendingWriteBuffer()) // если осталось что-то с прошлого раза
        {
            con.movePendingWriteBufferTo(DIRECT_WRITE_BUFFER);
        }

        if (DIRECT_WRITE_BUFFER.hasRemaining() && !con.hasPendingWriteBuffer()) {
            int i;
            final Queue<SendablePacket<T>> sendQueue = con.getSendQueue();
            SendablePacket<T> sp;

            for (i = 0; i < _sc.MAX_SEND_PER_PASS; i++) {
                synchronized (con) {
                    if ((sp = sendQueue.poll()) == null) {
                        break;
                    }
                }

                try {
                    stats.increaseOutgoingPacketsCount();
                    putPacketIntoWriteBuffer(sp, true); // записываем пакет в WRITE_BUFFER
                    WRITE_BUFFER.flip();
                    if (DIRECT_WRITE_BUFFER.remaining() >= WRITE_BUFFER.limit()) {
                        DIRECT_WRITE_BUFFER.put(WRITE_BUFFER);
                    } else
                    // если не осталось места в DIRECT_WRITE_BUFFER для WRITE_BUFFER то мы его запишев в следующий раз
                    {
                        con.createWriteBuffer(WRITE_BUFFER);
                        break;
                    }
                } catch (Exception e) {
                    _log.error("Error in " + getName(), e);
                    break;
                }
            }
        }

        WRITE_BUFFER.clear();
        WRITE_CLIENT = null;
    }

    protected final void putPacketIntoWriteBuffer(final SendablePacket<T> sp, final boolean encrypt) {
        WRITE_BUFFER.clear();

        // reserve space for the size
        final int headerPos = WRITE_BUFFER.position();
        WRITE_BUFFER.position(headerPos + _sc.HEADER_SIZE);

        // write content to buffer
        sp.write();

        // size (incl header)
        int dataSize = WRITE_BUFFER.position() - headerPos - _sc.HEADER_SIZE;
        if (dataSize == 0) {
            WRITE_BUFFER.position(headerPos);
            return;
        }
        WRITE_BUFFER.position(headerPos + _sc.HEADER_SIZE);
        if (encrypt) {
            WRITE_CLIENT.encrypt(WRITE_BUFFER, dataSize);
            // recalculate size after encryption
            dataSize = WRITE_BUFFER.position() - headerPos - _sc.HEADER_SIZE;
        }

        // prepend header
        WRITE_BUFFER.position(headerPos);
        WRITE_BUFFER.putShort((short) (_sc.HEADER_SIZE + dataSize));
        WRITE_BUFFER.position(headerPos + _sc.HEADER_SIZE + dataSize);
    }

    protected SelectorConfig getConfig() {
        return _sc;
    }

    protected Selector getSelector() {
        return _selector;
    }

    protected IMMOExecutor<T> getExecutor() {
        return _executor;
    }

    protected IPacketHandler<T> getPacketHandler() {
        return _packetHandler;
    }

    protected IClientFactory<T> getClientFactory() {
        return _clientFactory;
    }

    protected IAcceptFilter getAcceptFilter() {
        return _acceptFilter;
    }

    public void setAcceptFilter(final IAcceptFilter acceptFilter) {
        _acceptFilter = acceptFilter;
    }

    protected void closeConnectionImpl(final MMOConnection<T> con) {
        try {
            // notify connection
            con.onDisconnection();
        } finally {
            try {
                // close socket and the SocketChannel
                con.close();
            } catch (IOException e) {
                // ignore, we are closing anyway
            } finally {
                // очистить буферы
                con.releaseBuffers();
                // очистим очереди
                con.clearQueues();
                // обнуляем соединение у клиента
                con.getClient().setConnection(null);
                // обнуляем соединение у ключа
                con.getSelectionKey().attach(null);
                // отменяем ключ
                con.getSelectionKey().cancel();

                _connections.remove(con);

                stats.decreseOpenedConnections();
            }
        }
    }

    public void shutdown() {
        _shutdown = true;
    }

    public boolean isShuttingDown() {
        return _shutdown;
    }

    protected void closeAllChannels() {
        final Set<SelectionKey> keys = getSelector().keys();
        for (final SelectionKey key : keys) {
            try {
                key.channel().close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    protected void closeSelectorThread() {
        closeAllChannels();

        try {
            getSelector().close();
        } catch (IOException e) {
            // Ignore
        }
    }
}