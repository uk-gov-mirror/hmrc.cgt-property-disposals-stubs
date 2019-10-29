
# cgt-property-disposals-stubs

# Test Data

## Business Partner Records and Subscription Create

| NINO        | SA UTR    | BPR                                                                 | Subscription Create Response            |
| ----------- | --------- | ------------------------------------------------------------------- | --------------------------------------- |
| `CG123456D` |   -       | hard coded BPR                                                      | CGT reference number `XACGTP123456789`  |
| `AB123456C` |   -       | hard coded BPR                                                      | CGT reference number `XYCGTP001000170`  |
| `EM000...`  |   -       | no email address                                                    | -                                       |
|    -        | `...89`   | organisation name "Plip Plop Trusts" and no email address           | -                                       |
|    -        | `...99`   | individual with no email address                                    | -                                       |
| `αβ111111.` |   -       | non UK address, country code αβ, e.g. `HK111111C` maps to Hong Kong | -                                       |   
| `ER400...`  | `...5400` | 400 (Bad Request) response                                          | -                                       |
| `ER404...`  | `...5404` | 404 (Not Found) response                                            | -                                       |
| `ER409...`  | `...5409` | 409 (Conflict) response                                             | -                                       |
| `ER500...`  | `...5500` | 500 (Internal Server Error) response                                | -                                       |
| `ER503...`  | `...5503` | 503 (Service Unavailable) response                                  | -                                       |
| `ES400...`  |   -       | -                                                                   | 400 (Bad Request) response              |
| `ES404...`  |   -       | -                                                                   | 404 (Not Found) response                |
| `ES409...`  |   -       | -                                                                   | 409 (Conflict) response                 |
| `ES500...`  |   -       | -                                                                   | 500 (Internal Server Error) response    |
| `ES503...`  |   -       | -                                                                   | 503 (Service Unavailable) response      |

## Register without ID and Subscription Create
| Address line 1          | Register without ID Response                                                              | Subscription Create Response                                                           |
| ----------------------- | ----------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| `Fail Registration αβγ` | http status `αβγ`, e.g. `Fail Registration 500` results in 500 (Internal Server Error)    | -                                                                                      |
| `Fail Subscription αβγ` | -                                                                                         | http status `αβγ`, e.g. `Fail Subscription 500` results in 500 (Internal Server Error) |                            |

N.B. ` αβγ` must be one of `400`, `404`, `409`, `500` or `503`.  


## Subscription Display

| CGT Reference     | Subscription Display Response                |
| ----------------- | -------------------------------------------- |
| `XLCGTP212487578` | individual details - registered without id   |
| `XLCGTP212487579` | individual details - registered with id      |
| `XACGTP123456701` | trust details - registered without id        |
| `XACGTP123456702` | trust details - registered with id           |
| `XACGTP123456703` | 400 (Bad Request) `INVALID_REGIME`           |
| `XACGTP123456704` | 400 (Bad Request) `INVALID_IDTYPE`           |
| `XACGTP123456705` | 400 (Bad Request) `INVALID_REQUEST`          |
| `XACGTP123456706` | 400 (Bad Request) `INVALID_CORRELATIONID`    |
| `XACGTP123456707` | 404 (Bad Request) `NOT_FOUND`                |
| `XACGTP123456708` | 500 (Bad Request) `SERVER_ERROR`             |
| `XACGTP123456709` | 503 (Bad Request) `SERVICE_UNAVAILABLE`      |
| anything else     | individual details - registered with id      |


## Subscription Update

| CGT Reference     | Subscription Update Response              |
| ----------------- | ----------------------------------------- |
| `XACGTP123456712` | 400 (Bad Request) `INVALID_REGIME`        |
| `XACGTP123456713` | 400 (Bad Request) `INVALID_IDTYPE`        |
| `XACGTP123456714` | 400 (Bad Request) `INVALID_IDVALUE`       |
| `XACGTP123456715` | 400 (Bad Request) `INVALID_REQUEST`       |
| `XACGTP123456716` | 400 (Bad Request) `INVALID_CORRELATIONID` |
| `XACGTP123456717` | 400 (Bad Request) `INVALID_PAYLOAD`       |
| `XACGTP123456718` | 404 (Bad Request) `NOT_FOUND`             |
| `XACGTP123456719` | 500 (Bad Request) `SERVER_ERROR`          |
| `XACGTP123456720` | 503 (Bad Request) `SERVICE_UNAVAILABLE`   |
| anything else     | 200 (OK)                                  |



### License                                                                                                             
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
