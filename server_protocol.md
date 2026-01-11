# JDconnect Server Framework Specification

This document is intentionally strict and machine-readable.
It may be used as input to code generation tools or LLMs to generate compliant sender scripts.

This document defines the **authoritative framework** for all server-side scripts and services that send data payloads to the JDconnect Android app via Firebase Cloud Messaging (FCM).

This is the single source of truth. Any script, service, or future extension **must comply** with this document.

---

## 1. Scope and Philosophy

- This framework defines how a server:
  - Registers itself with the app
  - Emits immutable event facts
  - Communicates via FCM data messages only

- The server is a **dumb event producer**.
- The app is responsible for:
  - Validation
  - Persistence
  - Categorization
  - Notification behavior

- The server **never** creates notifications directly.

---

## 2. Global Invariants

### 2.1 Project Identity
- **Firebase Project ID**: `your project id here`
- Hardcoded in all sender logic
- Never changes

### 2.2 Server Identity
- **server_id**: `your server id`
- Unique per server
- Immutable unless manually changed by the operator
- Embedded directly in all scripts

### 2.3 Host Identity
- **hostname**: `your hostname`
- Hardcoded
- Hostname changes do not imply server identity changes

---

## 3. Filesystem Layout (Authoritative)

All paths are absolute. No relative paths are allowed.(unless you build a different system)

```
/etc/jdconnect/
├── secrets/
│   ├── service-account.json
│   └── fcm_tokens.json
├── script-output/
│   └── logs/
└── scripts/
```

### 3.1 Permissions
- All files are root-owned
- Scripts are executed via `sudo` or systemd
- No multi-user guarantees are provided or required

---

## 4. Credentials and Tokens

### 4.1 Service Account
- File: `/etc/jdconnect/secrets/service-account.json`
- Shared by all sender scripts
- If missing or unreadable:
  - Log error
  - Exit immediately

### 4.2 FCM Tokens
- File: `/etc/jdconnect/secrets/fcm_tokens.json`
- Structure:

```json
{
  "tokens": ["token1", "token2", "token3"]
}
```

- All tokens are treated equally
- Token-specific failures:
  - Logged
  - Ignored
  - Do not affect other tokens

---

## 5. Logging Rules

- Logging is mandatory for all scripts
- Format: plaintext or JSON (implementation choice)
- One log file per script

Log location:
```
/etc/jdconnect/script-output/logs/<script_name>.log
```

---

## 6. Sender Core Interface

A single core sender **must exist** and be reused everywhere.

### 6.1 Requirements
- Implemented in Python
- Exposed as:
  - Importable module (for scripts)
  - CLI interface (for manual use)

### 6.2 Behavior
- Sends a single payload to **all configured FCM tokens**
- No retries
- No batching
- No dry-run mode
- Stateless by design

---

## 7. Time and Ordering

- Server time is authoritative
- NTP must be enabled
- Timestamp format:
  - Epoch milliseconds
  - Stringified number

- No assumption of:
  - Ordered delivery
  - Guaranteed delivery

The app must tolerate any arrival order.

---

## 8. Common Message Envelope (Mandatory)

All messages **must** contain the following top-level fields:

| Field | Description |
|------|------------|
| v | Must be `1` (stringified integer) |
| type | `server` | `event` | `heartbeat` |
| node_id | Non-empty string identifying the producer |
| ts | Epoch milliseconds (stringified) |

If any required field is missing or invalid, the message becomes **INVALID**.

---

## 9. Server Registration Message

### 9.1 Type
```
type = "server"
```

### 9.2 Payload

Top-level fields plus:

```
server = <stringified JSON object>
```

### 9.3 Embedded Server Object (Source of Truth)

```json
{
  "id": "server_id",
  "name": "friendly_name",
  "hostname": "your_hostname",
  "vpn_ip": null
}
```

- `id` and `name` are mandatory
- `hostname` is mandatory
- This object is the **authoritative source** for server identity

### 9.4 Semantics
- Registration is idempotent
- Same `server_id` may be resent any time
- Metadata changes occur only by operator intent

---

## 10. Event Messages

### 10.1 Type
```
type = "event"
```

### 10.2 Payload

Top-level fields plus:

```
event = <stringified JSON object>
```

### 10.3 Embedded Event Object

#### Required fields

| Field | Description |
|------|------------|
| id | Unique event ID |
| server_id | Must match server.id |
| category | Strict enum |
| level | INFO | WARNING | CRITICAL | UNKNOWN |
| title | Short human-readable summary |

#### Optional fields

| Field | Description |
|------|------------|
| message | Long-form description |

### 10.4 Event ID Format

```
<node_id>:<epoch_ms>
```

Generated server-side.

---

## 11. Event Categories (Strict)

Allowed values:

```
SYSTEM
SERVICE
RESOURCE
CUSTOM
CONNECTIVITY
INVALID
HEARTBEAT
```

- Categories are case-sensitive
- No custom category names allowed
- Unknown values become INVALID

---

## 12. Heartbeat Messages

- Flat structure
- No embedded object
- Used for internal connectivity tracking

---

## 13. Execution Model

### 13.1 Automated
- systemd services
- systemd timers
- Hybrid setups allowed

### 13.2 Manual
- Triggered via bash aliases
- Example:
```
alias register_server='python register_server.py'
```

Manual triggers must reuse the same sender core.

---

## 14. Payload Size Constraint

- **FCM data payload limit: 4 KB**
- All payloads **must** stay below this limit
- Oversized payloads are invalid by definition

---

## 15. Explicit Non-Goals (v1)

This framework intentionally does **not** support:

- Local queuing
- Retries
- Guaranteed delivery
- Message ordering
- Compression
- Encryption beyond HTTPS
- Bidirectional communication

These may be revisited in future versions.

---

## 16. Final Rule

If a script or service contradicts this document, **the script is wrong**.

This document is law.