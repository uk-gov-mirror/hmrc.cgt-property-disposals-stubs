
# cgt-property-disposals-stubs

# Test Data

## Business Partner Records and Subscription Create

| NINO        | SA UTR    |  TRN      |    BPR                                                                 | Subscription Status Response                        | Subscription Create Response                  |
| ----------- | --------- | --------- |----------------------------------------------------------------------- | --------------------------------------------------- | --------------------------------------------- |
| `CG123456D` |   -       |    -      | hard coded BPR                                                         |  not subscribed                                     | CGT reference number `XACGTP123456789`        |
| `AB123456C` |   -       |    -      | hard coded BPR                                                         |  not subscribed                                     | CGT reference number `XYCGTP001000170`        |
| `EM000...`  |   -       |    -      | no email address                                                       |  not subscribed                                     | -                                             |
| `EM001...`  |   -       |    -      | uk contact address with no postcode                                    |  not subscribed                                     | -                                             |
| `EM002...`  |   -       |    -      | no email address and uk contact address with no postcode               |  not subscribed                                     | -                                             |
|    -        | `...89`   |  `...89`  | organisation name "Plip Plop Trusts" and no email address              |  not subscribed                                     | -                                             |
|    -        | `...99`   |    -      | individual with no email address                                       |  not subscribed                                     | -                                             |
| `αβ111111.` |   -       |    -      | non UK address, country code αβ, e.g. `HK111111C` maps to Hong Kong    |  not subscribed                                     | -                                             |   
| `ER400...`  | `...5400` | `...5400` | 400 (Bad Request) response                                             |  not subscribed                                     | -                                             |
| `ER404...`  | `...5404` | `...5404` | 404 (Not Found) response                                               |  not subscribed                                     | -                                             |
| `ER409...`  | `...5409` | `...5409` | 409 (Conflict) response                                                |  not subscribed                                     | -                                             |
| `ER500...`  | `...5500` | `...5500` | 500 (Internal Server Error) response                                   |  not subscribed                                     | -                                             |
| `ER503...`  | `...5503` | `...5503` | 503 (Service Unavailable) response                                     |  not subscribed                                     | -                                             |
| `ES400...`  | `...4400` |    -      | Individual                                                             |  not subscribed                                     | 400 (Bad Request) response                    |
| `ES403...`  | `...4403` |    -      | Individual                                                             |  not subscribed                                     | 403 (Forbidden) response (already subscribed) |
| `ES500...`  | `...4500` |    -      | Individual                                                             |  not subscribed                                     | 500 (Internal Server Error) response          |
| `ES503...`  | `...4503` |    -      | Individual                                                             |  not subscribed                                     | 503 (Service Unavailable) response            |
|     -       | `...3400` | `...3400` | Trust                                                                  |  not subscribed                                     | 400 (Bad Request) response                    |
|     -       | `...3403` | `...3403` | Trust                                                                  |  not subscribed                                     | 403 (Forbidden) response (already subscribed) |
|     -       | `...3500` | `...3500` | Trust                                                                  |  not subscribed                                     | 500 (Internal Server Error) response          |
|     -       | `...3503` | `...3503` | Trust                                                                  |  not subscribed                                     | 503 (Service Unavailable) response            |
| `SB01...`   | `...5901` |     -     | hard coded individual BPR                                              |  subscribed                                         | -                                             |            
|     -       | `...5801` | `5801...` | hard coded trust BPR                                                   |  subscribed                                         | -                                             |            
| `SB02...`   | `...5902` |     -     | hard coded individual BPR                                              |   registration form received                         | -                                             |            
|     -       | `...5802` | `5802...` | hard coded trust BPR                                                   |   registration form received                         | -                                             |            
| `SB03...`   | `...5903` |     -     | hard coded individual BPR                                              |   sent to DS                                         | -                                             |            
|     -       | `...5803` | `5803...` | hard coded trust BPR                                                   |   sent to DS                                         | -                                             |            
| `SB04...`   | `...5904` |     -     | hard coded individual BPR                                              |   DS outcome in progress                             | -                                             |            
|     -       | `...5804` | `5804...` | hard coded trust BPR                                                   |   DS outcome in progress                             | -                                             |            
| `SB05...`   | `...5905` |     -     | hard coded individual BPR                                              |   rejected                                           | -                                             |            
|     -       | `...5805` | `5805...` | hard coded trust BPR                                                   |   rejected                                           | -                                             |            
| `SB06...`   | `...5906` |     -     | hard coded individual BPR                                              |   in processing                                      | -                                             |            
|     -       | `...5806` | `5806...` | hard coded trust BPR                                                   |   in processing                                      | -                                             |            
| `SB07...`   | `...5907` |     -     | hard coded individual BPR                                              |   create failed                                      | -                                             |            
|     -       | `...5807` | `5807...` | hard coded trust BPR                                                   |   create failed                                      | -                                             |            
| `SB08...`   | `...5908` |     -     | hard coded individual BPR                                              |   withdrawal                                         | -                                             |            
|     -       | `...5808` | `5808...` | hard coded trust BPR                                                   |   withdrawal                                         | -                                             |            
| `SB09...`   | `...5909` |     -     | hard coded individual BPR                                              |   sent to Rcm                                        | -                                             |            
|     -       | `...5809` | `5809...` | hard coded trust BPR                                                   |   sent to Rcm                                        | -                                             |            
| `SB10...`   | `...5910` |     -     | hard coded individual BPR                                              |   approved with conditions                           | -                                             |            
|     -       | `...5810` | `5810...` | hard coded trust BPR                                                   |   approved with conditions                           | -                                             |            
| `SB11...`   | `...5911` |     -     | hard coded individual BPR                                              |   revoked                                            | -                                             |            
|     -       | `...5811` | `5811...` | hard coded trust BPR                                                   |   revoked                                            | -                                             |            
| `SB12...`   | `...5912` |     -     | hard coded individual BPR                                              |   deregistered                                       | -                                             |            
|     -       | `...5812` | `5812...` | hard coded trust BPR                                                   |   deregistered                                       | -                                             |            
| `SB13...`   | `...5913` |     -     | hard coded individual BPR                                              |   contract object inactive                           | -                                             |            
|     -       | `...5813` | `5813...` | hard coded trust BPR                                                   |   contract object inactive                           | -                                             |            
| `SB14...`   | `...5914` |     -     | hard coded individual BPR                                              |   400 (Bad Request) response (INVALID_REGIME)        | -                                             |            
|     -       | `...5814` | `5814...` | hard coded trust BPR                                                   |   400 (Bad Request) response (INVALID_REGIME)        | -                                             |            
| `SB15...`   | `...5915` |     -     | hard coded individual BPR                                              |  400 (Bad Request) response (INVALID_BPNUMBER)      | -                                             |            
|     -       | `...5815` | `5815...` | hard coded trust BPR                                                   |  400 (Bad Request) response (INVALID_BPNUMBER)      | -                                             |            
| `SB16...`   | `...5916` |     -     | hard coded individual BPR                                              |  400 (Bad Request) response (INVALID_CORRELATIONID) | -                                             |            
|     -       | `...5816` | `5816...` | hard coded trust BPR                                                   |  400 (Bad Request) response (INVALID_CORRELATIONID) | -                                             |            
| `SB17...`   | `...5917` |     -     | hard coded individual BPR                                              |  404 (Not Found) response                           | -                                             |            
|     -       | `...5817` | `5817...` | hard coded trust BPR                                                   |  404 (Not Found) response                           | -                                             |            
| `SB18...`   | `...5918` |     -     | hard coded individual BPR                                              |  500 (Internal Server Error) response               | -                                             |            
|     -       | `...5818` | `5818...` | hard coded trust BPR                                                   |  500 (Internal Server Error) response               | -                                             |            
| `SB19...`   | `...5919` |     -     | hard coded individual BPR                                              |  503 (Service Unavailable) response                 | -                                             |            
|     -       | `...5819` | `5819...` | hard coded trust BPR                                                   |  503 (Service Unavailable) response                 | -                                             |            

                                                                                                           
## Register without ID and Subscription Create
| Address line 1          | Register without ID Response                                                              | Subscription Create Response                                                           |
| ----------------------- | ----------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| `Fail Registration αβγ` | http status `αβγ`, e.g. `Fail Registration 500` results in 500 (Internal Server Error)    | -                                                                                      |
| `Fail Subscription αβγ` | -                                                                                         | http status `αβγ`, e.g. `Fail Subscription 500` results in 500 (Internal Server Error) |                            |

N.B. ` αβγ` must be one of `400`, `403`, `500` or `503`.  


## Subscription Display

| CGT Reference     | Subscription Display Response                |
| ----------------- | -------------------------------------------- |
| `XK.............` | individual details - registered without id   |
| `XL.............` | individual details - registered with id      |
| `XM.............` | trust details - registered without id        |
| `XN.............` | trust details - registered with id           |
| `XACGTP123456703` | 400 (Bad Request) `INVALID_REGIME`           |
| `XACGTP123456704` | 400 (Bad Request) `INVALID_IDTYPE`           |
| `XACGTP123456705` | 400 (Bad Request) `INVALID_REQUEST`          |
| `XACGTP123456706` | 400 (Bad Request) `INVALID_CORRELATIONID`    |
| `XACGTP123456707` | 404 (Bad Request) `NOT_FOUND`                |
| `XACGTP123456708` | 500 (Bad Request) `SERVER_ERROR`             |
| `XACGTP123456709` | 503 (Bad Request) `SERVICE_UNAVAILABLE`      |
| anything else     | individual details - registered with id      |


## Listing and viewing returns with charges

| CGT Reference |  List Return          | View Return                           | Charge info  |
| ------------- | --------------------- | ------------------------------------- |------------- |
|  `XD...`      |        -              |               -                       | delta charge | 
|  `....1`      |  hard coded returns   |               -                       |       -      |        
|  `....0`      |  nil return           |               -                       |       -      |       
|  `....x`      |  no sent returns      |               -                       |       -      |       
|  `...2x`      |        -              | multiple disposal return              |       -      |       
|  `...3x`      |        -              | single indirect disposal return       |       -      |       
|  `...4x`      |        -              | multiple indirect disposal return     |       -      |       
|  `...5x`      |        -              | single mixed use disposal return      |       -      |       
|  `...6x`      |        -              | multiple disposal residential return  |       -      |       
|  `...yx`      |        -              | single disposal return                |       -      |       



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
