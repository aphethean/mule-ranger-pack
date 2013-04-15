package org.jforce.mule.redis;

import org.jforce.reschedule.Job;
import org.mule.api.transport.Connector;
import org.mule.transport.AbstractConnectorTestCase;

import java.util.Date;

public class RedisSchedulerConnectorTest extends AbstractConnectorTestCase {
    @Override
    public Connector createConnector() throws Exception {
        RedisSchedulerConnector connector = new RedisSchedulerConnector(muleContext);
        connector.setRedisHost("localhost");
        return connector;
    }

    @Override
    public Object getValidMessage() throws Exception {
        return new Job("TEST", new Date());
    }

    @Override
    public String getTestEndpointURI() {
        return "reschedule://schedulerId";
    }
}
