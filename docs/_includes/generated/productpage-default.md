```mermaid
%%{init: {'theme': 'default'}}%%
flowchart TB
  Vdatabases{{./databases}} x-. /docker-entrypoint-initdb.d .-x mongodb[(mongodb)]
  productpage --> reviews
  productpage --> ratings
  productpage --> details
  ratings --> mongodb
  reviews --> ratings
  P0((8080)) -.-> ratings
  P1((8081)) -.-> details
  P2((8082)) -.-> reviews
  P3((8083)) -.-> productpage

  classDef volumes fill:#fdfae4,stroke:#867a22
  class Vdatabases volumes
  classDef ports fill:#f8f8f8,stroke:#ccc
  class P0,P1,P2,P3 ports
```