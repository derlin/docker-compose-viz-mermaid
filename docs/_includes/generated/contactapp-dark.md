```mermaid
%%{init: {'theme': 'dark'}}%%
flowchart TB
  Vsettings([settings]) -. /root/.m2 .-x apiuser[api-user]
  Vapiuser{{./api-user}} x-. /app .-x apiuser
  V0{ } x-. /cache .-x apiuser
  Vsettings -. /root/.m2 .-x apicontactbook[api-contact-book]
  Vapicontactbook{{./api-contact-book}} x-. /app .-x apicontactbook
  Vscriptssql{{./scripts/sql}} x-. /docker-entrypoint-initdb.d .-x mysql[(mysql)]
  apicontactbook --> mysql
  apicontactbook --> zookeeper
  apicontactbook --> kafka
  apiuser --> mysql
  apiuser --> zookeeper
  apiuser --> kafka
  kafka --> zookeeper
  P0((8081)) -.-> apiuser
  P1((8082)) -.-> apicontactbook
  P2((9092)) -.-> kafka

  classDef volumes fill:#0f544e,stroke:#23968b
  class Vsettings,Vapiuser,V0,Vsettings,Vapicontactbook,Vscriptssql volumes
  classDef ports fill:#5a5757,stroke:#b6c2ff
  class P0,P1,P2 ports
```