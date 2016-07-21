package com.redhat.xpaas.qe;

import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * @author Radek Koubsky (rkoubsky@redhat.com)
 */
public class Client {
	static final String CONTAINER_ID = "RollingUpdates";
	static final String CONTAINER_V1 = "RollingUpdates=com.redhat.xpaas.bpms:migration-process:1.0.0";
	static final String CONTAINER_V2 = "RollingUpdates=com.redhat.xpaas.bpms:migration-process:1.0.1";
	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

	public static void main(final String[] args) throws Exception {
		final String hostName = args[0];

		final ActiveMQSslConnectionFactory openwireFactory = new ActiveMQSslConnectionFactory(
				"failover:ssl://" + hostName + ":443?soTimeout=30000");
		openwireFactory.setUserName("mqUser");
		openwireFactory.setPassword("mqPassword");

		openwireFactory.setKeyStore(Paths.get(".", "broker.ks").toAbsolutePath().normalize().toString());
		openwireFactory.setKeyStorePassword("password");
		openwireFactory.setTrustStore(Paths.get(".", "broker.ts").toAbsolutePath().normalize().toString());
		openwireFactory.setTrustStorePassword("password");

		final javax.jms.Queue sendQueue = new ActiveMQQueue("queue/KIE.SERVER.REQUEST");
		final javax.jms.Queue receiveQueue = new ActiveMQQueue("queue/KIE.SERVER.RESPONSE");

		final KieServicesConfiguration config = KieServicesFactory.newJMSConfiguration(openwireFactory,
				sendQueue,
				receiveQueue);
		config.setTimeout(60_000L);
		final KieServicesClient kieServiceClientV1 = KieServicesFactory.newKieServicesClient(config);
		final ProcessServicesClient processServicesClientV1 = kieServiceClientV1.getServicesClient(ProcessServicesClient.class);
		final QueryServicesClient queryServicesClientV1 = kieServiceClientV1.getServicesClient(QueryServicesClient.class);

		final ProcessDefinition procDefV1 = queryServicesClientV1.findProcesses(0, 50)
				.stream()
				.filter(def -> "1.0".equals(def.getVersion())).iterator().next();

		LOGGER.info("Process definition V1: {}", procDefV1);
		final ProcessInstance processInstV1 = processServicesClientV1.getProcessInstance("RollingUpdates=com.redhat.xpaas.bpms:migration-process:1.0.0",
				processServicesClientV1.startProcess("RollingUpdates=com.redhat.xpaas.bpms:migration-process:1.0.0",
						procDefV1.getId()));
		LOGGER.info("Process instance V1: {}", processInstV1);

		final KieServicesClient kieServicesClientV2 = KieServicesFactory.newKieServicesClient(config);
		final ProcessServicesClient processServicesClientV2 = kieServicesClientV2.getServicesClient(ProcessServicesClient.class);
		final QueryServicesClient queryServicesClientV2 = kieServicesClientV2.getServicesClient(QueryServicesClient.class);
		final ProcessDefinition procDefV2 = queryServicesClientV2.findProcesses(0, 50)
				.stream()
				.filter(def -> "2.0".equals(def.getVersion())).iterator().next();
		LOGGER.info("Process definition V2: {}", procDefV2);
		final ProcessInstance processInstV2 = processServicesClientV2.getProcessInstance("RollingUpdates=com.redhat.xpaas.bpms:migration-process:1.0.1",
				processServicesClientV2.startProcess("RollingUpdates=com.redhat.xpaas.bpms:migration-process:1.0.1",
						procDefV2.getId()));
		LOGGER.info("Process instance V2: {}", processInstV2);

		LOGGER.info("In conversation id: {}", kieServiceClientV1.getConversationId());
		LOGGER.info("Client V1 findProcessInstancesByContainerId: {}",
				queryServicesClientV1.findProcessInstancesByContainerId(CONTAINER_ID,
						null,
						0,
						50));
		LOGGER.info("Out conversation id: {}", kieServiceClientV1.getConversationId());
	}
}
