package miaoshademo.RabbitMq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String QUEUE="queue";
    public static final String TOPIC_Q1="topic.queue1";
    public static final String TOPIC_Q2="topic.queue2";
    public static final String TOPIC_EXCHANGE="topicExchange";
    public static final String MIAOSHA_QUEUE="miaosha.queue";

    //需要创建的消息队列在这里添加@Bean注解创建
    @Bean
    public Queue miaoshaQueue(){
        return new Queue(MIAOSHA_QUEUE,true);
    }
}
