module github.com/yourorg/monorepo/services/payment-service

go 1.25.5

require github.com/yourorg/monorepo/packages/proto-gen-go/monorepo v0.0.0

require (
	golang.org/x/net v0.48.0 // indirect
	golang.org/x/sys v0.39.0 // indirect
	golang.org/x/text v0.32.0 // indirect
	google.golang.org/genproto/googleapis/rpc v0.0.0-20251202230838-ff82c1b0f217 // indirect
	google.golang.org/grpc v1.77.0 // indirect
	google.golang.org/protobuf v1.36.10 // indirect
)

replace github.com/yourorg/monorepo/packages/proto-gen-go/monorepo => ../../packages/proto-gen-go/monorepo
