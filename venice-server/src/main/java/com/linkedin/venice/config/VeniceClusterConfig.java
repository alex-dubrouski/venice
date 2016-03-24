package com.linkedin.venice.config;

import static com.linkedin.venice.ConfigKeys.*;

import com.google.common.collect.ImmutableMap;
import com.linkedin.venice.exceptions.ConfigurationException;
import com.linkedin.venice.exceptions.UndefinedPropertyException;
import com.linkedin.venice.exceptions.VeniceException;
import com.linkedin.venice.meta.PersistenceType;
import com.linkedin.venice.offsets.BdbOffsetManager;
import com.linkedin.venice.store.bdb.BdbStorageEngineFactory;
import com.linkedin.venice.store.memory.InMemoryStorageEngineFactory;
import com.linkedin.venice.utils.Props;

import java.io.File;
import java.util.Map;


/**
 * class that maintains config very specific to a Venice cluster
 */
public class VeniceClusterConfig {

  public static final Map<PersistenceType, String> storageEngineFactoryClassNameMap =
      ImmutableMap.of(PersistenceType.IN_MEMORY, InMemoryStorageEngineFactory.class.getName(),
          PersistenceType.BDB, BdbStorageEngineFactory.class.getName());

  private String clusterName;
  protected String dataBasePath;
  private String offsetManagerType = null;
  private String offsetDatabasePath = null;
  private long offsetManagerFlushIntervalMs;

  private boolean enableConsumptionAcksForAzkabanJobs;

  private boolean helixEnabled;
  private String zookeeperAddress;

  private String kafkaConsumptionAcksBrokerUrl;

  private PersistenceType persistenceType;

  // SimpleConsumer fetch buffer size.
  private int fetchBufferSize;
  // SimpleConsumer socket timeout.
  private int socketTimeoutMs;
  // Number of times the SimpleConsumer will retry fetching topic-partition leadership metadata.
  private int numMetadataRefreshRetries;
  // Back off duration between metadata fetch retries.
  private int metadataRefreshBackoffMs;


  private String kafkaBootstrapServers;

  private int kafkaAutoCommitIntervalMs;

  private boolean kafkaEnableAutoOffsetCommit;


  public VeniceClusterConfig(Props clusterProperties)
      throws ConfigurationException {
    checkProperties(clusterProperties);
  }

  protected void checkProperties(Props clusterProps) throws ConfigurationException {
    clusterName = clusterProps.getString(CLUSTER_NAME);

    helixEnabled = clusterProps.getBoolean(HELIX_ENABLED);
    zookeeperAddress = clusterProps.getString(ZOOKEEPER_ADDRESS);
    offsetManagerType = clusterProps.getString(OFFSET_MANAGER_TYPE, "bdb"); // Default "bdb"
    offsetDatabasePath = clusterProps.getString(OFFSET_DATA_BASE_PATH,
        System.getProperty("java.io.tmpdir") + File.separator + BdbOffsetManager.OFFSETS_STORE_NAME);
    offsetManagerFlushIntervalMs = clusterProps.getLong(OFFSET_MANAGER_FLUSH_INTERVAL_MS, 10000); // 10 sec default
    enableConsumptionAcksForAzkabanJobs = clusterProps.getBoolean(ENABLE_CONSUMPTION_ACKS_FOR_AZKABAN_JOBS, false);
    if (enableConsumptionAcksForAzkabanJobs) {
      kafkaConsumptionAcksBrokerUrl = clusterProps.getString(KAFKA_CONSUMPTION_ACKS_BROKER_URL);
      if (kafkaConsumptionAcksBrokerUrl.isEmpty()) {
        throw new ConfigurationException("The kafka broker url cannot be empty when consumption acknowledgement is enabled!");
      }
    }

    try {
      persistenceType = PersistenceType.valueOf(clusterProps.getString(PERSISTENCE_TYPE,
          PersistenceType.IN_MEMORY.toString()));
    } catch (UndefinedPropertyException ex) {
      throw new ConfigurationException("persistence type undefined", ex);
    }
    if (!storageEngineFactoryClassNameMap.containsKey(persistenceType)) {
      throw new ConfigurationException("unknown persistence type: " + persistenceType);
    }

    kafkaBootstrapServers = clusterProps.getString(KAFKA_BOOTSTRAP_SERVERS);
    if (kafkaBootstrapServers == null || kafkaBootstrapServers.isEmpty()) {
      throw new ConfigurationException("kafkaBootstrapServers can't be empty");
    }
    kafkaAutoCommitIntervalMs = clusterProps.getInt(KAFKA_AUTO_COMMIT_INTERVAL_MS);
    fetchBufferSize = clusterProps.getInt(KAFKA_CONSUMER_FETCH_BUFFER_SIZE, 64 * 1024);
    socketTimeoutMs = clusterProps.getInt(KAFKA_CONSUMER_SOCKET_TIMEOUT_MS, 1000);
    numMetadataRefreshRetries = clusterProps.getInt(KAFKA_CONSUMER_NUM_METADATA_REFRESH_RETRIES, 3);
    metadataRefreshBackoffMs = clusterProps.getInt(KAFKA_CONSUMER_METADATA_REFRESH_BACKOFF_MS, 1000);
    kafkaEnableAutoOffsetCommit = clusterProps.getBoolean(KAFKA_CONSUMER_ENABLE_AUTO_OFFSET_COMMIT, false);
  }

  public String getClusterName() {
    return clusterName;
  }

  public String getOffsetManagerType() {
    return offsetManagerType;
  }

  public String getOffsetDatabasePath() {
    return offsetDatabasePath;
  }

  public long getOffsetManagerFlushIntervalMs() {
    return offsetManagerFlushIntervalMs;
  }

  public boolean isEnableConsumptionAcksForAzkabanJobs() {
    return enableConsumptionAcksForAzkabanJobs;
  }

  public void setEnableConsumptionAcksForAzkabanJobs(boolean enableConsumptionAcksForAzkabanJobs) {
    this.enableConsumptionAcksForAzkabanJobs = enableConsumptionAcksForAzkabanJobs;
  }

  public boolean isHelixEnabled() {
    return helixEnabled;
  }

  public String getZookeeperAddress() {
    return zookeeperAddress;
  }

  public String getKafkaConsumptionAcksBrokerUrl() {
    return kafkaConsumptionAcksBrokerUrl;
  }

  public void setKafkaConsumptionAcksBrokerUrl(String kafkaConsumptionAcksBrokerUrl) {
    this.kafkaConsumptionAcksBrokerUrl = kafkaConsumptionAcksBrokerUrl;
  }

  public PersistenceType getPersistenceType() {
    return persistenceType;
  }

  public String getKafkaBootstrapServers() {
    return kafkaBootstrapServers;
  }

  public boolean kafkaEnableAutoOffsetCommit() {
    return kafkaEnableAutoOffsetCommit;
  }

  public int getFetchBufferSize() {
    return fetchBufferSize;
  }

  public int getSocketTimeoutMs() {
    return socketTimeoutMs;
  }

  public int getNumMetadataRefreshRetries() {
    return numMetadataRefreshRetries;
  }

  public int getMetadataRefreshBackoffMs() {
    return metadataRefreshBackoffMs;
  }

  public int getKafkaAutoCommitIntervalMs() {
    return kafkaAutoCommitIntervalMs;
  }
}
