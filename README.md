# Neo4j Seed Providers Extra

Extra seed providers for Neo4j Enterprise

- S3 SSO : Support for S3 Seed Provider with AWS SSO/STS


## Build

```bash
mvn clean package
```

## Install

Copy the jar in the plugins directory of your Neo4j Enterprise installation

Add the following line to your neo4j configuration

```
dbms.databases.seed_from_uri_providers=S3ExtraSeedProvider
```

## S3 Extra Seed Provider

```
CREATE DATABASE foo OPTIONS {existingData: 'use', seedURI: 'aws-s3://mybucket/folder/mydb.backup'} WAIT
```

# License

Apache 2.0