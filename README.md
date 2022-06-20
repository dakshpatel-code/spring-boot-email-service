# spring-boot-email-service

````
send email
````
````
curl --location --request POST 'http://localhost:8080/api/1.0/send' \
--header 'Content-Type: application/json' \
--data-raw '{
    "from": "",
    "to": [
        ""
    ],
    "subject": "test mail from email service",
    "text": "this is test mail text message 3"
}'
````

````
receive email
````
````
curl --location --request GET 'http://localhost:8080/api/1.0/receive/{{limit}}'
````