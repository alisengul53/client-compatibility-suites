import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class StandardClusterTests {
    static CloudCluster sslDisabledCluster;
    static CloudCluster sslEnabledCluster;
    static CloudCluster tempCluster;
    static HazelcastCloudManager cloudManager;
    private static final Logger TestCasesLogger = LogManager.getLogger(StandardClusterTests.class);

    @BeforeAll
    public static void setUpClass()
    {
        cloudManager = new HazelcastCloudManager();
        //sslDisabledCluster = cloudManager.createHazelcastCloudStandardCluster(System.getenv("hzVersion"), false);
        //sslEnabledCluster = cloudManager.createHazelcastCloudStandardCluster(System.getenv("hzVersion"), true);
        sslEnabledCluster = cloudManager.getHazelcastCloudCluster("1651");
        //cloudManager.resumeHazelcastCloudCluster("1604");
        sslDisabledCluster = cloudManager.getHazelcastCloudCluster("1629");
    }

    @ParameterizedTest
    @CsvSource({
            //"true,true",
            "true,false",
            //"false,true",
            //"false,false"
    })
    public void StandardClusterTests(Boolean isSmartClient, Boolean isTlsEnabled) throws InterruptedException {
        ClientConfig config;
        if(isTlsEnabled)
        {
            config = Helper.getConfigForSslEnabledCluster(sslEnabledCluster.getNameForConnect(), sslEnabledCluster.getToken(), isSmartClient, sslEnabledCluster.getCertificatePath(), sslEnabledCluster.getTlsPassword());
            tempCluster = sslEnabledCluster;
            config.getConnectionStrategyConfig().getConnectionRetryConfig().setClusterConnectTimeoutMillis(10000);
        }
        else
        {
            config = Helper.getConfigForSslDisabledCluster(sslDisabledCluster.getNameForConnect(), sslDisabledCluster.getToken(), isSmartClient);
            tempCluster = sslDisabledCluster;
            config.getConnectionStrategyConfig().getConnectionRetryConfig().setClusterConnectTimeoutMillis(10000);
        }

        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);

        //TestCasesLogger.info("Create client");
//        clientProcess.start();
//        clientProcess.join();
          IMap<String, String> map = client.getMap("mapForTest");
          Helper.mapPutgetAndVerify(map);
//
//        TestCasesLogger.info("Scale up cluster from 2 node to 4");
//        cloudManager.scaleUpDownHazelcastCloudStandardCluster(tempCluster.getId(), 2);
//        Helper.mapPutgetAndVerify(map);
//
//        TestCasesLogger.info("Scale down cluster from 4 node to 2");
//        cloudManager.scaleUpDownHazelcastCloudStandardCluster(tempCluster.getId(), -2);
//        Helper.mapPutgetAndVerify(map);
        //TestCasesLogger.info("Stop cluster");
        //CloudCluster test = cloudManager.stopHazelcastCloudCluster(tempCluster.getId());

        //System.out.println("Test print");
        //TestCasesLogger.info("Resume cluster");
        //cloudManager.resumeHazelcastCloudCluster(tempCluster.getId());
        //Helper.mapPutgetAndVerify(map);
    }


    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void TryConnectSslClusterWithoutCertificates(Boolean isSmartClient)
    {
        ClientConfig config = Helper.getConfigForSslDisabledCluster(sslEnabledCluster.getNameForConnect(), sslEnabledCluster.getToken(), isSmartClient);
        config.getConnectionStrategyConfig().getConnectionRetryConfig().setClusterConnectTimeoutMillis(10000);
        boolean value = false;
        try
        {
            HazelcastClient.newHazelcastClient(config);
        }
        catch(Exception e)
        {
            value = true;
        }
        Assertions.assertTrue(value, "Client shouldn't be able to connect ssl cluster without certificates");
    }
}
