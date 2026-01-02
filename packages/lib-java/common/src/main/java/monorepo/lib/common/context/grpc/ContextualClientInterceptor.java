package monorepo.lib.common.context.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.micrometer.observation.Observation;
import monorepo.lib.common.context.Context;
import monorepo.lib.common.context.ContextHolder;
import monorepo.lib.common.util.JsonUtil;
import org.jspecify.annotations.Nullable;

/**
 * Contextual client interceptor for gRPC.
 *
 * @author Freeman
 * @since 2025/11/19
 */
public final class ContextualClientInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        var context = ContextHolder.getOrNull();
        if (context == null) {
            return next.newCall(method, callOptions);
        }

        var call = next.newCall(method, callOptions);
        return new ContextualClientCall<>(call, context, method);
    }

    private static final class ContextualClientCall<Req, Resp>
            extends ForwardingClientCall.SimpleForwardingClientCall<Req, Resp> {

        private final Context context;

        @Nullable private final Observation observation;

        private final MethodDescriptor<Req, Resp> method;

        ContextualClientCall(ClientCall<Req, Resp> delegate, Context context, MethodDescriptor<Req, Resp> method) {
            super(delegate);
            this.context = context;
            this.observation = context.observationRegistry().getCurrentObservation();
            this.method = method;
        }

        @Override
        public void start(Listener<Resp> responseListener, Metadata headers) {
            for (var entry : context.getPropagatedHeaders().entrySet()) {
                var key = Metadata.Key.of(entry.getKey(), Metadata.ASCII_STRING_MARSHALLER);
                if (!headers.containsKey(key)) {
                    for (var value : entry.getValue()) {
                        headers.put(key, value);
                    }
                }
            }
            super.start(responseListener, headers);
        }

        @Override
        public void sendMessage(Req message) {
            if (observation != null && !observation.isNoop() && method.getType() == MethodDescriptor.MethodType.UNARY) {
                observation.highCardinalityKeyValue("grpc.request.message", JsonUtil.stringify(message));
            }
            super.sendMessage(message);
        }
    }
}
