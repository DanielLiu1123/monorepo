package monorepo.lib.common.context.grpc;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.ArrayList;
import monorepo.lib.common.context.Context;
import monorepo.lib.common.context.ContextHolder;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/19
 */
public final class ContextualServerInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        var ctx = buildContext(headers);

        ContextHolder.set(ctx);
        try {
            return new ContextualListener<>(next.startCall(call, headers), ctx);
        } finally {
            ContextHolder.remove();
        }
    }

    private static Context buildContext(Metadata headers) {
        var context = new Context();
        for (var key : headers.keys()) {
            var values = headers.getAll(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
            if (values != null) {
                var v = new ArrayList<String>();
                for (var value : values) {
                    v.add(value);
                }
                context.addHeader(key, v);
            }
        }
        return context;
    }

    private static final class ContextualListener<Req>
            extends ForwardingServerCallListener.SimpleForwardingServerCallListener<Req> {

        private final Context context;

        private ContextualListener(ServerCall.Listener<Req> delegate, Context context) {
            super(delegate);
            this.context = context;
        }

        @Override
        public void onMessage(Req message) {
            doInContext(() -> super.onMessage(message));
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
            ContextHolder.set(context);
            try {
                runnable.run();
            } finally {
                ContextHolder.remove();
            }
        }
    }
}
