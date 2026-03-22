package org.hyeonqz.springlab.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

@Slf4j
public class SftpChannelFactory extends BasePooledObjectFactory<ChannelSftp> {
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public SftpChannelFactory(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    public ChannelSftp create() throws Exception {
        JSch jSch = new JSch();

        Session session = jSch.getSession(username, host, port);

        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        log.info("[SFTP] 새 세션 생성 완료");
        return channel;
    }

    @Override
    public PooledObject<ChannelSftp> wrap(ChannelSftp channelSftp) {
        return new DefaultPooledObject<>(channelSftp);
    }

    // 세션 유효성 검증
    @Override
    public boolean validateObject(PooledObject<ChannelSftp> pooledObject) {
        ChannelSftp channel = pooledObject.getObject();
        try {
            // Dead 세션 감지
            if (channel == null || channel.isClosed()
                    || !channel.isConnected()) {
                log.warn("[SFTP] Dead 세션 감지 → 폐기");
                return false;
            }
            // 실제 연결 확인 (pwd 호출로 검증)
            channel.pwd();
            return true;
        } catch (Exception e) {
            log.warn("[SFTP] 세션 유효성 검증 실패 → 폐기: {}",
                    e.getMessage());
            return false;
        }
    }

    // 세션 반납 시 정리
    @Override
    public void passivateObject(PooledObject<ChannelSftp> pooledObject) {
        // 필요 시 상태 초기화
    }

    // 세션 폐기
    @Override
    public void destroyObject(PooledObject<ChannelSftp> pooledObject) {
        ChannelSftp channel = pooledObject.getObject();
        try {
            if (channel != null && channel.isConnected()) {
                channel.getSession().disconnect();
                channel.disconnect();
                log.info("[SFTP] 세션 폐기 완료");
            }
        } catch (Exception e) {
            log.error("[SFTP] 세션 폐기 실패: {}", e.getMessage());
        }
    }
}
