# car-booking

A car booking service that allows customer to register, rent a car and find nearby cars without a framework
inspired by https://www.stubbornjava.com/posts/lightweight-embedded-java-rest-server-without-a-framework,
and written in kotlin.

tech stack:
- undertow
- kotlin

## Concept
- Customer can start to rent a car after registration. 
- Customer can find nearby cars that are not rented yet.
- Customer can stop renting and pay the rent 

## How-to
1. `./gradlew shadowJar`
2. `java -jar build/libs/car-booking-1.0-SNAPSHOT.jar`

The application starts at `localhost:8080` 

##  Example requests
Please find example requests here:
https://documenter.getpostman.com/view/6268661/SzS1Toi4?version=latest

The spec can be found in `swagger.yaml`
