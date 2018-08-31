# EventStore

[![CircleCI](https://circleci.com/gh/biharck/eventstore.svg?style=svg)](https://circleci.com/gh/biharck/eventstore)[![Maven Central](https://maven-badges.herokuapp.com/maven-central/br.net.eventstore/event.store/badge.svg)](https://maven-badges.herokuapp.com/maven-central/br.net.eventstore/event.store)


## Usage

### Create the EventStore:

```java
EventStore eventStore = new EventStoreBuilder()
                 .setProvider(new InMemoryProvider()) provider
                 .setPublisher(new InMemoryPublisher()) // Opcional. Support different publishers, like RabbitmqPublisher, RedisPublisher etc
                 .createEventStore();
```

### Reading and writing events:

Accessing an event stream:

```java
EventStream ordersStream = eventStore.getEventStream("orders", "1234567");
```

Adding events to the stream:

```java
EventStream ordersStream = eventStore.getEventStream("orders", "1234567");
ordersStream.addEvent(new Event("My Event Payload")); // Could pass a JSON string here
```

Loading events from the stream:

```java
EventStream ordersStream = eventStore.getEventStream("orders", "1234567");
List<Event> events = ordersStream.getEvents();
Order order = ordersAggregation.loadFromHistory(events)
```

### Reacting to events:

Listening for new events in event streams:

```java
eventStore.subscribe("orders", message -> {
    System.out.println(message.getAggregation());
    System.out.println(message.getStreamId());
    System.out.println(getEvent().getPayload());
});
```

Removing the subscription to eventStore channels:

```java
Subscription subscription = eventStore.subscribe("orders", message -> {
    System.out.println(message.getAggregation());
    System.out.println(message.getStreamId());
    System.out.println(getEvent().getPayload());
});

// ...
subscription.remove();
 
```
