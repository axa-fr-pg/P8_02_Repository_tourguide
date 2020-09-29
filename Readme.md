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
Ensure that all project images have been generated (tourguide as well as gps, reward and trip).
Start the application with the gradle tripmaster/runDockerCompose task.