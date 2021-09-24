```mermaid
%%{init: {'theme': 'default'}}%%
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

  classDef volumes fill:#fdfae4,stroke:#867a22
  class Vsettings,Vapiuser,V0,Vsettings,Vapicontactbook,Vscriptssql volumes
```