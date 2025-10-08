package org.hyeonqz.kafkalab.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaAdminClientService {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final AdminClient adminClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ListTopicsResult callAdminClientTopicList() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        CompletableFuture<Void> future = new CompletableFuture<>();

        AtomicInteger attempt = new AtomicInteger(1);

        // 토픽 리스트 조회
        return adminClient.listTopics();
    }

    public void callConsumerGroupsList() throws Exception{
        adminClient.listConsumerGroups().valid().get().forEach(System.out::println);
    }

    // adminClient 를 사용하여 커밋 정보 얻어오는 방법
    public void getCommitInfo() {
    }

    public void getMetadataByBroker() throws ExecutionException, InterruptedException {
        DescribeClusterResult cluster = adminClient.describeCluster();

        cluster.clusterId().get(); // 클러스터 ID 확인
        cluster.controller().get(); // 컨트롤러 브로커 확인
        cluster.nodes().get();  // 노드 확인
    }

}
