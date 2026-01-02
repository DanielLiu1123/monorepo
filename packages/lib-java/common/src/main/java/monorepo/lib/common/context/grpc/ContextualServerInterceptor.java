package monorepo.lib.common.context.grpc;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import monorepo.lib.common.context.Context;
import monorepo.lib.common.context.ContextHolder;
import monorepo.lib.common.util.JsonUtil;

/**
 * Contextual server interceptor for gRPC.
 *
 * @author Freeman
 * @since 2025/11/19
 */
public final class ContextualServerInterceptor implements ServerInterceptor {

    private final ObservationRegistry observationRegistry;

    public ContextualServerInterceptor(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        var ctx = buildContext(headers);
        return ContextHolder.getWithContext(
                ctx, () -> new ContextualListener<>(next.startCall(call, headers), ctx, call.getMethodDescriptor()));
    }

    private Context buildContext(Metadata metadata) {
        var headers = getHeaders(metadata);
        return new Context(headers, observationRegistry);
    }

    private static Map<String, List<String>> getHeaders(Metadata headers) {
        var result = new HashMap<String, List<String>>();
        for (var key : headers.keys()) {
            var values = headers.getAll(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
            if (values != null) {
                for (var value : values) {
                    result.computeIfAbsent(key, _ -> new ArrayList<>()).add(value);
                }
            }
        }
        return result;
    }

    private static final class ContextualListener<Req>
            extends ForwardingServerCallListener.SimpleForwardingServerCallListener<Req> {

        private final Context context;

        private final @Nullable Observation observation;

        private final MethodDescriptor<?, ?> descriptor;

        private ContextualListener(
                ServerCall.Listener<Req> delegate, Context context, MethodDescriptor<?, ?> descriptor) {
            super(delegate);
            this.context = context;
            this.observation = context.observationRegistry().getCurrentObservation();
            this.descriptor = descriptor;
        }

        @Override
        public void onMessage(Req message) {
            doInContext(() -> {
                if (observation != null
                        && !observation.isNoop()
                        && descriptor.getType() == MethodDescriptor.MethodType.UNARY) {
                    observation.highCardinalityKeyValue("grpc.request.message", JsonUtil.stringify(message));
                    observation.highCardinalityKeyValue("grpc.request.metadata", JsonUtil.stringify(context.headers()));
                }
                super.onMessage(message);
            });
        }

        @Override
        public void onHalfClose() {
            doInContext(super::onHalfClose);
        }

        @Override
        public void onCancel() {
            doInContext(super::onCancel);
        }

        @Override
        public void onComplete() {
            doInContext(super::onComplete);
        }

        @Override
        public void onReady() {
            doInContext(super::onReady);
        }

        private void doInContext(Runnable runnable) {
            ContextHolder.runWithContext(context, runnable);
        }
    }
}
