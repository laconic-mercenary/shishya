# Shishya

Shishya is a distributed file transfer and communication system that allows for efficient data transmission across multiple nodes.

## Features

- Distributed file transfer
- Client-server architecture
- Configurable network settings
- Support for both UDP and TCP protocols
- Asynchronous event-driven communication
- Customizable data packet handling

## Components

### Shishya Server

The server component is responsible for receiving and assembling data packets.

Key features:
- Configurable through properties files
- Customizable data packet completion and rejection listeners
- Logging support

Usage:
```java
java -jar shishya-server.jar path/to/config.properties
```

### Shishya Distributed
The distributed component allows for multi-threaded data throughput.

Key features:

- Support for multiple bind ports and addresses
- Client and server modes

Usage:

#### Maven

THe more likely use case is to use the distributed component as a library.


```xml
<dependency>
    <groupId>com.frontier</groupId>
	<artifactId>shishya-distributed</artifactId>
    <version>1.0-RC1</version>
</dependency>
```

#### Test Executable

For testing purposes, it is also executable. 

```bash
java -jar shishya-distributed.jar -SERVER -bindPorts "port1,port2" -bindAddress "address" -bufferSize size
java -jar shishya-distributed.jar -CLIENT -bindPorts "port1,port2" -bindAddress "address" -bufferSize size -targetPort "tport1,tport2" -targetAddress "taddress" [-file "path/to/file"]
```

### Shishya Client
The client component is used for sending data to Shishya servers.

Key features:

Configurable target address and port
Support for file serialization and transfer

#### Usage:

For testing, you can run it as an executable jar command.

```bash
java -jar shishya-client.jar bindAddress bindPort filePath targetAddress targetPort
```

#### Configuration
Shishya components can be configured using command-line arguments or properties files, depending on the component. Refer to the usage instructions for each component for specific configuration options.

## Building
This project uses Java and can be built using your preferred build tool (e.g., Maven, Gradle).

## Contributing
Contributions to Shishya are welcome. Please ensure your code adheres to the existing style and include appropriate tests for new features.

