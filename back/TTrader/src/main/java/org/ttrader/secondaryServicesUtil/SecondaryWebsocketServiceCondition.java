package org.ttrader.secondaryServicesUtil;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.List;

public class SecondaryWebsocketServiceCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        List<String> profiles = Arrays.asList(context.getEnvironment().getActiveProfiles());
        return profiles.contains("finnhub-service") || profiles.contains("tinkoff-service");
    }
}
