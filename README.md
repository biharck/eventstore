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

Adding events to the stream:

```java
EventStream ordersStream = eventStore.getEventStream("orders", "1234567");
ordersStream.addEvent(new Event("My Event Payload")); // Could pass a JSON string here
```

Loadinf events from the stream:

```java
EventStream ordersStream = eventStore.getEventStream("orders", "1234567");
List<Event> events = ordersStream.getEvents();
Order order = ordersAggregation.loadFromHistory(events)
```