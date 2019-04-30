# Interview Assignment for OpenLaw

## Assignment Description
Build a REST web service that exposes an endpoint to upload an arbitrary text file of size <= 10MB. Parse the file (assume any uploaded file is well formatted and contains only ascii characters) for the following information:

The total word count

The counts of each occurrence of a word

Return the parsed information in the HTTP response body in JSON format.


## How to Run

Prerequisites: scala and sbt

Use basic sbt commands to work with the app.

`sbt run` to run

This will start app server listening on **port 8080**. If you want to change the port, you can find it in configuration (`src/main/resources/application.conf`).

There is only one API route: **POST /parse**. It accepts *Content-Type: multipart/form-data* and expects exactly one text file under form key *words*. See [How to Test](#how-to-test) section below for example API call.


## How to Test

Test manually with curl:

`curl -F words=@test-file.txt http://localhost:8080/parse`

*File `test-file.txt` is located in the project root directory.*

Or run unit tests with `sbt test`


## Example response (formatted)
```
{
    "total": 6,
    "wordCount": {
        "one": 1,
        "three": 3,
        "two": 2
    }
}
```
