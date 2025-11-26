package monorepo.services.todo.server;

import grpcstarter.server.GrpcService;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.protobuf.SimpleRequest;
import io.grpc.testing.protobuf.SimpleResponse;
import io.grpc.testing.protobuf.SimpleServiceGrpc;
import java.util.List;
import monorepo.lib.common.util.ThreadUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/27
 */
@GrpcService
public class SimpleServer extends SimpleServiceGrpc.SimpleServiceImplBase {

    private final RestClient restClient;

    public SimpleServer(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public void unaryRpc(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {

        var future = ThreadUtil.supplyAsync(() -> restClient
                .get()
                .uri("https://my-json-server.typicode.com/typicode/demo/posts")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Post>>() {}));

        var posts = future.join();

        var response = SimpleResponse.newBuilder()
                .setResponseMessage("Fetched " + posts.size() + " posts")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    record Post(Integer id, String title) {}
}
