```mermaid
%%{init: {'theme': 'dark'}}%%
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

  classDef volumes fill:#0f544e,stroke:#23968b
  class Vdatabases volumes
  classDef ports fill:#5a5757,stroke:#b6c2ff
  class P0,P1,P2,P3 ports
```