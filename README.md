# Portfolio NAV System

## Description

This project consists of 3 separate services to show the real time value of a given portfolio provided in the
position.csv file.

### The PortfolioNAVSystem

1. Read and Parse the position CSV file
2. Persist the parsed Portfolio into DB (SQLite)
3. Consume PriceChange from the MarketDataProvider
4. Calculate PortfolioNAV based on the new price
5. Publish PortfolioNAV to PortfolioResultListener

### MarketDataProvider

1. Read the available stocks from DB (SQLite)
2. Simulate stock movement by generating PriceChange according to a discrete geometric Brownian motion at a random
   interval (0.5-2seconds)
3. Publish the PriceChange to the PortfolioNAVSystem

### PortfolioResultListener

1. Consume the PortfolioNAV from PortfolioNAVSystem
2. Pretty print the PortfolioNAV

## How to build

1. Generate Java Classes from Protobuf  
   `protoc --java_out=src/main/java src/main/proto/PortfolioNAV.proto`
2. Gradle build  
   `./gradlew build`

## How to run

Start the services in this order

1. PortfolioNAVSystem
2. PortfolioResultListener
3. MarketDataProvider

## Assumption:

1. Symbol length is variable between 4-6 bytes
2. A random stock is picked at a random interval between 0.5-2 seconds
3. The maturity date of any option is also after the current date

PriceChange ByteBuffer Allocation

* Symbol Length: 4 bytes (int)
* Symbol: 6 bytes (String)
* Price: 8 bytes (double)
* Total: 18 bytes

## Diagram

![System Diagram](diagram/systemDiagram.png)  
Diagram Source Code: diagram/systemDiagram.puml
