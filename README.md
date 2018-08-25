# EventStore

[![Build Status](https://travis-ci.org/biharck/eventstore.svg?branch=master)](https://travis-ci.org/biharck/eventstore)

## Usage

Create the EventStore:

```java
EventStore eventStore = new EventStoreBuilder()
                 .setProvider(new InMemoryProvider()) // Could use different providers, like MongoDBProvider, MySQLProvider etc
                 .setPublisher(new InMemoryPublisher()) // Opcional. Support different publishers, like RabbitmqPublisher etc
                 .createEventStore();
```

Accessing an event stream:

```java
EventStream ordersStream = eventStore.getEventStream("orders", "1234567");
```
