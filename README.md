# Webhook notification plugin for GoCD

This is a plugin based on the [notifications-skeleton](https://github.com/gocd-contrib/notification-skeleton-plugin) to notify remote webhooks of changes to [build stages](https://plugin-api.gocd.org/19.8.0/notifications/#stage-status-changed) and [build agents](https://plugin-api.gocd.org/19.8.0/notifications/#agent-status-changed). Each webhook will receive the change notification as a HTTP/JSON POST message.

## Configuration

The configuration for this plugin is expected to be in [Java Properties](https://docs.oracle.com/javase/7/docs/api/java/util/Properties.html) format.

This plugin searches for the configuration file in the following order:

1. File defined by the environment variable `WEBHOOK_NOTIFY_CONFIG`.
2. `webhook_notify.properties` at the user's home directory. Typically it's the `go` user's home directory (`/var/go`).
3. `webhook_notify.properties` present at the `CRUISE_SERVER_DIR` environment variable location.

You can find the details on where and how to setup environment variables for GoCD [here](https://docs.gocd.org/current/installation/install/server/linux.html#location-of-gocd-server-files).

Minimalistic configuration would be something like:

```properties
stage.status.endpoint.1=http://example.com/stage/alpha
stage.status.endpoint.2=http://example.com/stage/beta

agent.status.endpoint.1=http://example.com/agent/alpha
agent.status.endpoint.2=http://example.com/agent/beta

# This is useful in environments where the endpoints can be completely trusted.
# It is a terrible idea to use this to connect to public HTTPS endpoints.
# Set this to true at your own risk.
trust.all.https=false
```

You don't need to configure any agent or stage webhooks/endpoints, but a configuration file must be present.

## Building the code base

To build the jar, run `./gradlew clean test assemble`
