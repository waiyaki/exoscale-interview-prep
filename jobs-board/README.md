# jobs

A simple mock HTTP API for a minimalist job board application.

##

## Installation

Download from http://example.com/FIXME.

## Usage

Start the API server using

```bash
$ lein run
```

This will run the project http://localhost:8080. A different port can be specified using the `-p PORT` optional argument to `lein run`:

```bash
$ lein run -p 8081
```

## API Endpoints

| Endpoint         | Functionality                |
| ---------------- | ---------------------------- |
| GET /jobs        | List all the available jobs. |
| POST /jobs       | Create a new job.            |
| DELETE /jobs/:id | Delete a job.                |

## API Request and Response Examples

The examples below are using [httpie](https://httpie.org/) in a single session. By default, the API accepts a `JSON` payload and returns a `JSON` response when available. Other response formats (`EDN`, `transit`) can be returned, when the appropriate `Accept` header is provided (`application/edn`, `application/transit+json`).

### Jobs API

_POST /jobs_

```bash
$ echo '{ "title": "Backend engineer", "description": "Clojure backend engineer at Exoscale", "company": { "name": "Exoscale" } }' | http POST :8080/jobs
```

**Response (application/json)**

```json
{
  "company": {
    "id": "432dbe93-926f-4303-84ec-1099187c9ece",
    "name": "Exoscale"
  },
  "description": "Clojure backend engineer at Exoscale",
  "id": "05ab0ea9-4712-4cc2-8dee-a7fa99d177d0",
  "title": "Backend engineer"
}
```

Alternatively, a job for a particular existing company can be created by providing a company `id` instead of a company `name`. When company `name` is provided, the company is created as part of the request.

```bash
$ echo '{ "title": "Back-office engineer", "description": "Python backoffice engineer at Exoscale", "company": { "id": "432dbe93-926f-4303-84ec-1099187c9ece" } }' | http POST :8080/jobs Accept:application/edn
```

**Response (application/edn)**

```clojure
{:id #uuid "43fb46aa-1ae4-421f-a5c6-269c72cac9b2", :title "Back-office engineer", :description "Python backoffice engineer at Exoscale", :company {:name "Exoscale", :id #uuid "432dbe93-926f-4303-84ec-1099187c9ece"}}
```

_GET /jobs_

```bash
$ http :8080/jobs
```

**Response (application/json)**

```json
[
  {
    "company": {
      "id": "a659c753-26b7-48f2-a9a5-db8fa80072c6",
      "name": "Exoscale"
    },
    "description": "Clojure backend engineer at Exoscale",
    "id": "56cba355-5081-4abe-bf7a-a2a3e741e423",
    "title": "Backend engineer"
  },
  {
    "company": {
      "id": "a659c753-26b7-48f2-a9a5-db8fa80072c6",
      "name": "Exoscale"
    },
    "description": "Python backoffice engineer at Exoscale",
    "id": "5a52f361-d799-494c-9de4-83e2b149294b",
    "title": "Back-office engineer"
  }
]
```

_DELETE /jobs/:id_

```bash
$ http DELETE :8080/jobs/56cba355-5081-4abe-bf7a-a2a3e741e423
```

```txt
HTTP/1.1 204 No Content

```

## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
