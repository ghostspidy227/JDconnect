# JDconnect

> ⚠️ **Vibe-coded, not enterprise-certified**
>
> This project was built pragmatically and iteratively.
> It behaves as documented and has been tested to that extent, but it is **not exhaustively tested** and makes **no production guarantees**.


JDconnect is an **Android monitoring receiver app** designed to ingest server-side events and heartbeats via **Firebase Cloud Messaging (FCM) data messages**, persist them locally, derive connectivity state, and notify the user when meaningful state changes occur.

This repository contains the **client-side receiver framework**. It is intentionally opinionated, strict, and minimal.

JDconnect was originally built for personal use and is being prepared for reuse. All personal identifiers, secrets, and instance-specific configuration are expected to be supplied by **you**, not the codebase.

---

<p align="center">
  <img src="app/src/main/ic_launcher-playstore.png" alt="JDconnect Logo" width="180"/>
</p>


---

## Core Philosophy

- Servers are **dumb event producers**
- The Android app is the **source of truth** for:
  - Validation
  - Persistence
  - Connectivity state derivation
  - Notification behavior
- Communication uses **FCM data messages only**
- No assumptions are made about:
  - Message ordering
  - Guaranteed delivery
  - Retries

If something goes wrong, the app records it instead of pretending everything is fine.

---

## What JDconnect Does

- Receives **server registration**, **events**, and **heartbeats** via FCM
- Stores all events locally using Room
- Tracks server connectivity using heartbeat timestamps
- Emits **CONNECTIVITY events** when servers go ONLINE or OFFLINE
- Sends notifications based on event category and severity
- Provides a minimal UI to:
  - View servers
  - Browse event history
  - Inspect event details
- Optionally launches SSH sessions via **Termux**

---

## What JDconnect Does *Not* Do (By Design)

- No retries
- No local queuing
- No guaranteed delivery
- No message ordering
- No compression
- No encryption beyond HTTPS/FCM
- No bidirectional communication


---

## Architecture Overview

```
FCM Data Message
      ↓
MessageParser (strict protocol validation)
      ↓
MessageIngestor
      ↓
Repositories (Server / Event)
      ↓
Room Database
      ↓
ConnectionStateEvaluator
      ↓
Notifications + UI
```

---

## Message Protocol

All incoming messages **must** follow the JDconnect Server Event Framework specification.

### Mandatory Envelope Fields

| Field | Description |
|------|------------|
| `v` | Protocol version (must be `1`) |
| `type` | `server` | `event` | `heartbeat` |
| `node_id` | Non-empty producer identifier |
| `ts` | Epoch milliseconds (server time) |

Messages missing required fields or using unknown values are treated as **INVALID** and stored as such.

---

## Heartbeats and Connectivity State

JDconnect derives server connectivity entirely from **heartbeat timing**.

### How It Works

- Each heartbeat updates `lastHeartbeatAt` for a server
- The app periodically evaluates all servers
- If a server has not sent a heartbeat within a computed threshold, it is marked **OFFLINE**
- When a heartbeat arrives for an OFFLINE server, it is marked **ONLINE**

Both transitions generate **CONNECTIVITY events**.

### Heartbeat Threshold Formula

```
offline_threshold = heartbeat_interval × grace_multiplier
```

### Examples

- Heartbeat every **1 minute**, grace **1.5** → OFFLINE after **1 minute 30 seconds**
- Heartbeat every **30 minutes**, grace **1.5** → OFFLINE after **45 minutes**

### Configuration (Settings Screen)

The Settings tab allows configuring:

- **Heartbeat interval (minutes)**
- **Grace multiplier**
- **Heartbeat monitoring enabled/disabled(not in ui yet)**

These values are used exclusively by the app to derive connectivity state. Servers remain unaware of them.

---

## Notifications

Notification behavior is entirely client-side.

- HEARTBEAT events never notify
- CONNECTIVITY state changes notify
- INVALID messages notify
- RESOURCE / SERVICE events notify based on severity

The server never controls notifications.

---

## Firebase Setup (Required)

This project **does not include Firebase credentials**.

To build your own instance:

1. Create a Firebase project
2. Add an Android app
3. Enable Firebase Cloud Messaging
4. Download `google-services.json`
5. Place it in:

```
app/google-services.json
```

You **must** use your own Firebase project and FCM tokens.

---

## Package Name

The default package name in this repository is a placeholder.

You are expected to rename it to match your own application ID:

```
com.yourname.jdconnect
```

This is required for Firebase to function correctly.

---

## Server-Side Integration

JDconnect expects servers to send messages via FCM **data payloads only**.

A reference server-side specification defines:

- Filesystem layout
- Credential handling
- Logging rules
- Message formats
- Explicit non-goals

Servers are expected to:

- Use their own FCM service account
- Broadcast messages to all registered device tokens
- Never retry or reorder messages

---

## Termux SSH Integration (Optional)

JDconnect can launch SSH sessions via Termux using its `RUN_COMMAND` interface.

Requirements:

- Termux installed
- `allow-external-apps` enabled in Termux
- JDconnect granted `RUN_COMMAND` permission
- By default the app uses `root` as the SSH username (can be changed in code)
- This feature is optional and can be removed without affecting core functionality

---

## Development Notes

- Kotlin + Jetpack Compose
- Room for persistence
- No dependency injection framework
- Explicit, readable data flow
- Strict parsing and validation by design

---

## License

Choose a license that matches your intent (MIT / Apache 2.0 recommended).

---

## Server-Side Protocol & Contract

This specification defines the exact message formats and operational rules for any server sending data to JDconnect. It may also be used as input for code generation or script templating tools.

**[JDconnect Server Protocol Specification](server_protocol.md)**

---

## Security Responsibility & Data Sensitivity Warning

JDconnect does **not** provide end-to-end security guarantees beyond those offered by Firebase Cloud Messaging itself.

All security decisions are the responsibility of the user.

- Messages are accepted based on protocol validity, not cryptographic trust
- Server identity is assumed to be trusted by the operator
- Payloads are not encrypted beyond HTTPS/FCM transport
- No authentication, signing, or replay protection is enforced by the app

**It is strongly recommended to send only operational signals**, such as:
- errors
- warnings
- state changes
- health or availability indicators

Do **not** transmit:
- credentials
- secrets
- personal data
- sensitive business information

JDconnect is designed for monitoring and observability, not secure data transport.  
If you need stronger guarantees, you must implement them yourself or use a different system.


---

## Final Note

JDconnect is intentionally strict.

If a server script contradicts the protocol, **the script is wrong**.

If something fails, it is recorded, not hidden.

That philosophy is the core of this project.

