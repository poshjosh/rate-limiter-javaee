package com.looseboxes.ratelimiter.web.javaee.wip;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.annotation.NodeData;
import com.looseboxes.ratelimiter.node.Node;
import com.looseboxes.ratelimiter.util.RateConfigList;
import com.looseboxes.ratelimiter.web.core.RateLimiterNodeContext;
import com.looseboxes.ratelimiter.web.javaee.RequestRateLimitingFilter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.lang.reflect.Method;
import java.util.List;

public class RateLimiterDynamicFeature2 implements DynamicFeature {

    private final List<Node<NodeData<RateLimiter<ContainerRequestContext>>>> propertiesLeafNodes;
    private final List<Node<NodeData<RateLimiter<ContainerRequestContext>>>> annotationsLeafNodes;

    @javax.inject.Inject
    public RateLimiterDynamicFeature2(RateLimiterNodeContext<ContainerRequestContext, ContainerRequestContext> nodeContext) {
        this.propertiesLeafNodes = nodeContext.getPropertyNodes(Node::isLeaf);
        this.annotationsLeafNodes = nodeContext.getAnnotationNodes(Node::isLeaf);
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {

        String [] pathPatterns = getPathPatterns(resourceInfo);
        for(Node<NodeData<RateLimiter<ContainerRequestContext>>> node : propertiesLeafNodes) {
            if(isSamePattern(node, pathPatterns)) {
                register(featureContext, node, 0);
            }
        }

        Method resourceMethod = resourceInfo.getResourceMethod();
        for(Node<NodeData<RateLimiter<ContainerRequestContext>>> node : annotationsLeafNodes) {
            if(isSameSource(node, resourceMethod)) {
                register(featureContext, node, 0);
            }
        }
    }

    private String [] getPathPatterns(ResourceInfo resourceInfo) {
        // TODO
        throw new UnsupportedOperationException();
    }

    private boolean isSamePattern(Node<NodeData<RateLimiter<ContainerRequestContext>>> node, String [] patterns) {
        NodeData<RateLimiter<ContainerRequestContext>> nodeData = getNodeValue(node);
        RateConfigList rateConfigList = (RateConfigList)nodeData.getSource();
        // TODO
        throw new UnsupportedOperationException();
    }

    private boolean isSameSource(Node<NodeData<RateLimiter<ContainerRequestContext>>> node, Object source) {
        NodeData<RateLimiter<ContainerRequestContext>> nodeData = getNodeValue(node);
        return source.equals(nodeData.getSource());
    }

    private void register(FeatureContext featureContext, Node<NodeData<RateLimiter<ContainerRequestContext>>> node, int priority) {
        NodeData<RateLimiter<ContainerRequestContext>> nodeData = getNodeValue(node);
        RateLimiter<ContainerRequestContext> rateLimiter = nodeData.getValue();
        if(RateLimiter.noop().equals(rateLimiter)) {
            return;
        }
        featureContext.register(new RequestRateLimitingFilter(rateLimiter), priority);
        node.getParentOptional().ifPresent(parent -> register(featureContext, parent, priority + 1));
    }

    private <T> NodeData<RateLimiter<T>> getNodeValue(Node<NodeData<RateLimiter<T>>> node) {
        return node.getValueOptional().orElseThrow(NullPointerException::new);
    }
}
