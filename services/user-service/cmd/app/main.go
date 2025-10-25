package main

import (
	"fmt"
	"log"
	"net"

	"google.golang.org/grpc"
)

func main() {
	// TODO: Initialize logger, tracer, metrics
	// TODO: Load configuration
	// TODO: Initialize database connection
	// TODO: Register gRPC server

	listener, err := net.Listen("tcp", ":50051")
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}

	grpcServer := grpc.NewServer()

	// TODO: Register services
	// userv1.RegisterUserServiceServer(grpcServer, &server{})

	fmt.Println("User service listening on :50051")
	if err := grpcServer.Serve(listener); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
