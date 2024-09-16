# distributed-microblog

# Client
CLI has 3 subcommands:
post message [file-to-attach]
if file-to-attach is given, it will be base64 encoded before sending to the server.
list [--starting id] [--count number] [--save-attachment]
if --save-attachment is given, a file will be created with the base64 decoded attachment named message-id.out
the number of messages to retrieve may be greater than 20, so you may need to make additional requests under the covers. number defaults to 10.
if --starting is not give, start at the end.
messages will be listed in the following way:
100: 2024-03-13T19:38-07:00 author_name says "this message may go multiple lines
that's okay. make sure to end with a quote. if there is an attachment
put a 📎 emoji after the quote and a space." 📎
create userid
will generate a public key pair and save the userid and private key to a mb.ini file that will be used later by post and list if the create is successful. the userid must consist of only lowercase letters and numbers.
# Message Signing
The digest was calculated over the JSON of the message with the fields:
"date", "author", "message", and  "attachment" in order. 
All whitespace for formatting are removed before calculating the SHA-256 digest and signed with RSA.
# Server
SpringBoot server services REST requests
API Design
POST MESSAGE:

POST request to /messages/create

request:
{
"date": "2024-03-13T19:38-07:00",
"author": "ben",
"message": "hello world!",
"attachment": "aSdlfkJ888oidfjwe+",
"signature": "as/f32230FS+"
}
response:
{
"message-id": 2323
}

LIST MESSAGES:

POST request to /messages/list

request:
{
"limit": 10, // default is 10 if limit is not present, error if greater than 20
"next": -1 // -1 is the last message. messages come in reverse order
}
response:
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

CREATE USER:

POST request to /user/create

request:
{
"user": "lowercase_a-z_numbers",
"public-key": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3Bsn4p1MKY02l8499qRz\nMg980BXswaduhnCBRTn6PBvEI2ywIzhOiQjq98HGiXWqbcNYibWmGuwUvhpDLzfJ\nOxtgRO9I612rd4xzs8tz+2pYDO1bOtwuGQTTTQ1jwlCyPyxYsNJgnr8wsiQE7siW\n9WbsuEYkzUOUIF7RGv8rW0dGNYdb8QLan8ghTu4es0uI2LfzBg3usFJahS+Pcih5\nDglphAcDJBbX+EbGytGUpkdYOoJNMi+AVPDjd8M9eujDjER1YxgvOMSK0GbzkDIW\nkzmU9SgDzUqjU5W5Q4P1eoz+QtL0m5E+uoXN8fuY5rw4cr4YE0srACsG30j41PfQ\nTQIDAQAB\n-----END PUBLIC KEY-----\n"
}

response:
{ message: "welcome" }

PUBLIC KEY REQUEST:

GET request to /user/username/public-key

returns the PEM encoded public key.


GENERAL ERROR RESPONSE:
{
"error": "signature didn't match"
}
