package com.example.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    private static final Logger log = LoggerFactory.getLogger(StateMachineConfig.class);

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal().source(PaymentState.NEW).target(PaymentState.AUTHORIZING).event(PaymentEvent.AUTHORIZE)
                .and()
                .withExternal().source(PaymentState.AUTHORIZING).target(PaymentState.AUTHORIZED).event(PaymentEvent.AUTHORIZE_SUCCESS)
                .and()
                .withExternal().source(PaymentState.AUTHORIZING).target(PaymentState.AUTHORIZE_FAILED).event(PaymentEvent.AUTHORIZE_FAIL)
                .and()
                .withExternal().source(PaymentState.AUTHORIZED).target(PaymentState.CAPTURING).event(PaymentEvent.CAPTURE)
                .and()
                .withExternal().source(PaymentState.CAPTURING).target(PaymentState.CAPTURED).event(PaymentEvent.CAPTURE_SUCCESS)
                .and()
                .withExternal().source(PaymentState.CAPTURING).target(PaymentState.CAPTURE_FAILED).event(PaymentEvent.CAPTURE_FAIL)
                .and()
                .withExternal().source(PaymentState.CAPTURED).target(PaymentState.REFUNDED).event(PaymentEvent.REFUND);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> listener = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info("State changed from {} to {}", from != null ? from.getId() : "none", to.getId());
            }
        };
        config.withConfiguration().listener(listener);
    }
}
