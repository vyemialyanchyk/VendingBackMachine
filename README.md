# VendingBackMachine platform backend #

## Summary ##

* Java backend for VendingBackMachine management platform
* Target platform: Java 8 x64, Tomcat 8, MySql

## How do I get set up? ##
Use Gradle 3.x build tool

* Running Spring boot application with Gradle

```
#!shell
gradle build
cd vending-back-machine-ws
gradle bootRun
```

* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### DB ###

#### Prerequisites ####
Project maintains several DB users and roles with correct access level.

To create/update schema
```
#!shell
gradle update -P db_username=your_user -P db_password=your_pwd
```

#### Lombok ####
Make sure that Lombok plugin is installed and 'Enable annotation processing' in checked in settings.
