# tourguide project
This library contains tourguide methods for the tripmaster workspace.

## Endpoints
Get /getLastLocation
Get /getNearbyAttraction
Get /getRewards
Get /getAllLastLocations
Get /getTripDeals

## Build
Generate an executable jar file with the gradle build/bootJar task.

## Installation
Generate a docker image with the gradle tripmaster/saveDockerImage task.

## Run
Ensure that gps, reward and trip images have been installed (launch the gradle tripmaster/saveDockerImage task for each of them).
Start the application with the gradle tripmaster/runDockerCompose task.