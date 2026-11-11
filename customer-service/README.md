# Customer Service

A Java and Spring Boot portfolio project that will provide an authoritative source of customer profile information for a larger microservices platform.

## Overview

When services independently maintain customer details, their copies can become inconsistent. Customer Service will own the current customer profile, including names, contact details, addresses, and preferences.

Other services will retrieve profiles through REST APIs or consume customer-change events through Kafka. Downstream services may keep local copies where appropriate. Historical records, such as the email recorded when an order was placed, remain the responsibility of the service that owns those records.

## Current Progress

- Spring Boot starter application runs locally.
- A practice `GET /welcome` endpoint is implemented to return `Customer Service is running`.
- Plain Java `Customer` and `Address` models are defined.
- `CustomerStatus` defines `ACTIVE`, `INACTIVE`, and `DELETED`.
- The customer model has an initialized address collection and an `addAddress` helper that sets both sides of the relationship.
- The generated application-context test completes successfully.

The models are not persisted yet. Business rules below are agreed design decisions, not implemented guarantees. The welcome endpoint's browser response still needs to be confirmed separately from the application-context test.

## Planned Features

- Customer CRUD, partial updates, validation, and soft deletion.
- Search with multiple filters, pagination, and sorting.
- PostgreSQL persistence with Flyway migrations and database constraints.
- Optimistic locking for concurrent updates.
- Kafka customer events and reliable publishing through a transactional outbox.
- Redis caching and rate limiting.
- JWT authentication with administrator and read-only service roles.
- OpenAPI documentation, automated behavior tests, metrics, and structured logging.
- Docker Compose, Prometheus, Grafana, and GitHub Actions.

Performance targets will be evaluated through load testing; no throughput or latency guarantees have been established yet.

## Technology

Currently used:

- Java 17 (developed using Amazon Corretto 17).
- Spring Boot 4.1.2.
- Spring Web MVC and Validation.
- Maven with the Maven Wrapper.
- Lombok.
- JUnit and Spring Boot testing support.

IntelliJ IDEA is the development IDE, but is not required to run the project.

## Prerequisites

- JDK 17, with `JAVA_HOME` configured for terminal use.
- Internet access for downloading Maven and project dependencies on the first run.
- A copy of this repository.

A separate Maven installation is not required because the Maven Wrapper is included. No database, Kafka, or Redis instance is required at this checkpoint.

## Running Locally

Open a terminal in the project root: the directory containing `pom.xml` and the Maven Wrapper files.

### Windows PowerShell

```powershell
.\mvnw.cmd spring-boot:run
```

### macOS or Linux

```bash
./mvnw spring-boot:run
```

Alternatively, run `CustomerServiceApplication` from IntelliJ.

Once startup completes, visit [http://localhost:8080/welcome](http://localhost:8080/welcome). The expected response is:

```text
Customer Service is running
```

The default port is `8080`. The root path `/` does not currently have an application endpoint. Stop the terminal-run application with `Ctrl+C`.

## Running Tests

From the same project root:

### Windows PowerShell

```powershell
.\mvnw.cmd test
```

### macOS or Linux

```bash
./mvnw test
```

The generated `contextLoads` test checks that Spring can load the application context. It does not yet verify the welcome response, customer business rules, or the address helper. Those need dedicated tests as implementation progresses.

The latest shared IntelliJ test run loaded the context and finished with exit code `0`.

## Initial Domain Model

`Customer` contains a UUID identifier, version, name, email, phone number, date of birth, creation and update timestamps, status, preferred time zone, language, marketing preference, tags, and associated addresses.

`Address` contains an identifier, address lines, city, state or region, postal code, country code, primary indicator, and a reference to its customer.

The intended relationship is one customer to multiple addresses, with each address belonging to one customer. Database mapping and the ER diagram will be added in the next design steps.

## Design Decisions

### Customer identity and ownership

Customer UUIDs identify customer records across services. Email is a changeable contact detail and must not be used as the permanent identity linking historical records.

### Addresses

Every customer must have a primary address. Changing the primary address retains the previous address as an additional address unless removal is explicitly requested. Removing an address must not leave the customer without a primary address; a replacement must be designated in the same operation when necessary.

Only one address per customer may be primary. The model currently represents this with an address-level primary indicator; enforcement will be implemented later.

### Soft deletion and reads

Deleting a customer changes their status to `DELETED` rather than physically deleting the customer record. Ordinary `GET /customers/{id}` requests will return `404 Not Found` for deleted customers. Deleted records will remain available through explicitly requested, authorized internal search.

### Email reuse

A new customer may reuse the email of a deleted customer, but receives a new UUID. Reusing an email must never grant access to the old customer's records. Duplicate email addresses remain prohibited among non-deleted customers.

### Update behavior

`PUT` replaces the editable profile and requires all mandatory fields. `PATCH` changes only supplied fields. Both must validate the resulting profile. Server-managed fields, such as the customer ID and creation timestamp, are not freely replaceable by clients.

Omitted PATCH fields remain unchanged. Explicit nulls must be distinguished from omitted fields and validated according to whether the field is optional.

### Data types

Phone numbers, postal codes, and country codes are strings because they represent identifiers rather than quantities. Birth dates use `LocalDate`; creation and update timestamps use `Instant`. Tags use a set to represent multiple labels without duplicates.

## Open Questions

- May a new customer reuse a phone number belonging to a deleted customer?
- Which roles may search deleted records, and how will that access be requested?
- Will restoring a deleted customer be supported? If so, how will email conflicts be handled?
- Which profile fields, beyond the primary address, are required at creation?
- Which fields may be explicitly cleared through PATCH?
- What operations are permitted for `INACTIVE` customers?
- Are changes to saved addresses retained as history? Address history is not part of the current implementation.

## Project Status

This is an early-stage portfolio and learning project. It is not production-ready. This README will evolve as features are implemented and verified.
