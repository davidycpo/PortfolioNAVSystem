Generate Java Classes from Protobuf

protoc --java_out=src/main/java src/main/proto/PriceChange.proto

protoc --java_out=src/main/java src/main/proto/PortfolioNAV.proto

3 Main, Run in this order

1. PortfolioNAVSystem
2. PortfolioResultListener
3. MarketDataProvider