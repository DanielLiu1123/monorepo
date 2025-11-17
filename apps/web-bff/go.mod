module github.com/yourorg/monorepo/apps/web-bff

go 1.25.4

replace (
	github.com/yourorg/monorepo/packages/grpc-clients => ../../packages/grpc-clients
	github.com/yourorg/monorepo/packages/proto-gen-go => ../../packages/proto-gen-go
)
