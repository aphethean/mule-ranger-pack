package org.jforce.mule.redis;

import org.jforce.reschedule.Job;
import org.mule.api.transport.Connector;
import org.mule.transport.AbstractConnectorTestCase;

public class RedisSchedulerConnectorTest extends AbstractConnectorTestCase {
    @Override
    public Connector createConnector() throws Exception {
        return new RedisSchedulerConnector(muleContext);
    }

    @Override
    public Object getValidMessage() throws Exception {
        return new Job("TEST");
    }

    @Override
    public String getTestEndpointURI() {
        return "reschedule://schedulerId?redisHost=localhost&jalla=balla";
    }
}
