package org.jforce.mule.redis;

import org.jforce.reschedule.Job;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.transport.AbstractMessageDispatcher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.DefaultRedisZSet;
import org.springframework.data.redis.support.collections.RedisMap;
import org.springframework.data.redis.support.collections.RedisZSet;

import java.nio.charset.Charset;

public class RedisSchedulerMessageDispatcher extends AbstractMessageDispatcher {

    private RedisSchedulerConnector connector;
    private RedisMap<String, Job> jobMap;
    private RedisZSet<String> triggerSet;

    public RedisSchedulerMessageDispatcher(OutboundEndpoint endpoint) {
        super(endpoint);
        this.connector = (RedisSchedulerConnector) endpoint.getConnector();

        RedisTemplate<String, Job> redisTemplate = createRedisTemplate();
        this.jobMap = new DefaultRedisMap<String, Job>("jobs", redisTemplate);

        String scheduleId = endpoint.getEndpointURI().getAddress();
        this.triggerSet = new DefaultRedisZSet<String>(scheduleId, new StringRedisTemplate(connector.getConnectionFactory()), 0);
    }

    @Override
    protected void doDispatch(MuleEvent event) throws Exception {

        if (connector.getConnection() == null) {
            throw new IllegalStateException("No Redis Connection.");
        }

        MuleMessage message = event.getMessage();
        Job job = message.getPayload(Job.class);
        jobMap.put(job.getKey(), job);

        triggerSet.add(job.getKey(), job.getTriggerTime().getTime());
    }

    @Override
    protected MuleMessage doSend(MuleEvent event) throws Exception {
        doDispatch(event);
        return null;
    }

    private RedisTemplate<String, Job> createRedisTemplate() {

        StringRedisSerializer keySerializer = new StringRedisSerializer(Charset.forName("UTF-8"));
        GenericToStringSerializer<Job> valueSerializer = new GenericToStringSerializer<Job>(Job.class, Charset.forName("UTF-8"));

        RedisTemplate<String, Job> template = new RedisTemplate<String, Job>();
        template.setConnectionFactory(connector.getConnectionFactory());
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        return template;
    }

}
