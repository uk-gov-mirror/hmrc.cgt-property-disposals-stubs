
# cgt-property-disposals-stubs

# Test Data

## Business Partner Records and Subscription

| NINO      | SA UTR  | BPR                                                               | Subscription Response                 |
| --------- | ------  | ----------------------------------------------------------------- | ------------------------------------- |
| CG123456D |   -     | hard coded BPR                                                    | CGT reference number XACGTP123456789  |
| AB123456C |   -     | hard coded BPR                                                    | CGT reference number XYCGTP001000170  |
| EM000...  |   -     | no email address                                                  | -                                     |
|    -      | ...89   | organisation name "Plip Plop Trusts" and no email address         | -                                     |
|    -      | ...99   | individual with no email address                                  | -                                     |
| αβ111111. |   -     | non UK address, country code αβ, e.g. HK111111C maps to Hong Kong | -                                     |   
| ER400...  | ...5400 | 400 (Bad Request) response                                        | -                                     |
| ER404...  | ...5404 | 404 (Not Found) response                                          | -                                     |
| ER409...  | ...5409 | 409 (Conflict) response                                           | -                                     |
| ER500...  | ...5500 | 500 (Internal Server Error) response                              | -                                     |
| ER503...  | ...5503 | 503 (Service Unavailable) response                                | -                                     |
| ES400...  |   -     | -                                                                 | 400 (Bad Request) response            |
| ES404...  |   -     | -                                                                 | 404 (Not Found) response              |
| ES409...  |   -     | -                                                                 | 409 (Conflict) response               |
| ES500...  |   -     | -                                                                 | 500 (Internal Server Error) response  |
| ES503...  |   -     | -                                                                 | 503 (Service Unavailable) response    |


### License                                                                                                             
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
