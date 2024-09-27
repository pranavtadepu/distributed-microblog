

# Distributed Microblog

A microblogging platform with a CLI client and a Spring Boot server, allowing users to post messages, attach files, and list messages with proper message signing using RSA.

## Client

The CLI client offers the following subcommands:

### 1. `post message [file-to-attach]`

- Posts a new message to the server.
- If `file-to-attach` is specified, it is base64 encoded before sending.

### 2. `list [--starting id] [--count number] [--save-attachment]`

- Lists the latest messages.
- Optional parameters:
  - `--starting`: Specifies the starting message ID. If not provided, starts from the most recent message.
  - `--count`: Number of messages to retrieve. Defaults to 10. Maximum allowed is 20.
  - `--save-attachment`: If provided, saves any attachments by creating a `.out` file with the decoded content.

- Message Format:
  ```
  100: 2024-03-13T19:38-07:00 author_name says "message content" 📎
  ```
  The paperclip emoji 📎 indicates an attached file.

### 3. `create userid`

- Generates a public/private key pair and stores the user ID and private key in a `mb.ini` file.
- The user ID must consist of only lowercase letters and numbers.

## Message Signing

Messages are signed using RSA. The SHA-256 digest is calculated over the following fields in the message JSON:
- `date`, `author`, `message`, `attachment`

All whitespace is removed before computing the digest.

## Server

The Spring Boot server handles the following REST requests:

### 1. **POST /messages/create** (Post a Message)

#### Request:
```json
{
  "date": "2024-03-13T19:38-07:00",
  "author": "ben",
  "message": "hello world!",
  "attachment": "aSdlfkJ888oidfjwe+",
  "signature": "as/f32230FS+"
}
```

#### Response:
```json
{
  "message-id": 2323
}
```

### 2. **POST /messages/list** (List Messages)
limit default value is 10, maximum is 20. \
next with -1 fetches the last message, messages are returned in reverse order.
#### Request:
```json
{
  "limit": 10,  
  "next": -1   
}
```

#### Response:
```json
[
  {
    "message-id": 27,
    "date": "2024-03-13T19:38-07:00",
    "author": "ben",
    "message": "hello world!",
    "attachment": "aSdlfkJ888oidfjwe+",
    "signature": "as/f32230FS+"
  }
]
```

### 3. **POST /user/create** (Create a User)

#### Request:
```json
{
  "user": "lowercase_a-z_numbers",
  "public-key": "-----BEGIN PUBLIC KEY-----\n...\n-----END PUBLIC KEY-----"
}
```

#### Response:
```json
{ "message": "welcome" }
```

### 4. **GET /user/{username}/public-key** (Fetch User’s Public Key)

Returns the PEM encoded public key of the specified user.

## General Error Response
```json
{
  "error": "signature didn't match"
}
```
