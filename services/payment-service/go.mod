module github.com/yourorg/monorepo/services/payment-service

go 1.25.5

require github.com/yourorg/monorepo/packages/proto-gen-go v0.0.0

require (
	golang.org/x/net v0.47.0 // indirect
	golang.org/x/sys v0.38.0 // indirect
	golang.org/x/text v0.31.0 // indirect
	google.golang.org/genproto/googleapis/rpc v0.0.0-20251111163417-95abcf5c77ba // indirect
	google.golang.org/grpc v1.77.0 // indirect
	google.golang.org/protobuf v1.36.10 // indirect
)

replace github.com/yourorg/monorepo/packages/proto-gen-go => ../../packages/proto-gen-go
