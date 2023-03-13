[![Java CI with Maven on Linux](https://github.com/MihailTeodor/attsw-final-project/actions/workflows/linux.yml/badge.svg)](https://github.com/MihailTeodor/attsw-final-project/actions/workflows/linux.yml)
[![Coverage Status](https://coveralls.io/repos/github/MihailTeodor/attsw-final-project/badge.svg?branch=master)](https://coveralls.io/github/MihailTeodor/attsw-final-project?branch=master)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MihailTeodor_attsw-final-project&metric=bugs)](https://sonarcloud.io/summary/new_code?id=MihailTeodor_attsw-final-project)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MihailTeodor_attsw-final-project&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=MihailTeodor_attsw-final-project)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=MihailTeodor_attsw-final-project&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=MihailTeodor_attsw-final-project)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MihailTeodor_attsw-final-project&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MihailTeodor_attsw-final-project)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=MihailTeodor_attsw-final-project&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=MihailTeodor_attsw-final-project)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MihailTeodor_attsw-final-project&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=MihailTeodor_attsw-final-project)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=MihailTeodor_attsw-final-project&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=MihailTeodor_attsw-final-project)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MihailTeodor_attsw-final-project&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MihailTeodor_attsw-final-project)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=MihailTeodor_attsw-final-project&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=MihailTeodor_attsw-final-project)


# Library project for Advanced Programming Techniques course.
## Test-Driven Development, Build Automation, Continuous Integration

The project consists in a very basic library managment of books and users, where users and books can be added and removed, and users can borrow and return borrowed books. The focus of the project is the correct usage of advanced techniques for build automation and continuous integration with a main focus on Test-Driven Development.

The application has been developed in Java 8 with a TDD approach and using advanced tools as Mutation Testing, Code Coverage, Docker and GitHub Actions as the CI server configured to build the application with Java 8 and 11. All the build process and dependency management is done with Maven. 

The GUI has been developed with swing and there are two versions of the application using either MySql or MongoDB as a database. The application has been developed and tested on Ubuntu 22.04.

## USAGE
From the `pom.xml`'s location directory, run the maven command `mvn clean verify` which will build and run all tests. There are two profiles that can be used for code coverage `jacoco` and mutiation testing `pit`.

Run `mvn clean verify -Pjacoco,pit` to build the application and run all tests, with the addition of test coverage and mutation testing alltogether.

In the `target` folder are generated two FatJARs one called `MONGO_app-jar-with-dependencies.jar` and `MYSQL_app-jar-with-dependencies.jar` for the application using **MongoDB** and **MySql** respectively. 

In order to run these jars:

* first run from the `pom.xml`'s directory the maven command `mvn docker:start@mongodb` or `mvn docker:start@mysql` which will start up the container for the **MongoDB** or **MySql** respectively.
*  Then, you can run the desired application, e.g. `java -jar MYSQL_app.jar-jar-with-dependencies.jar` for the MySql version from the `target` directory.
*  Finally, use `mvn docker:stop@mongodb` or `mvn docker:stop@mysql` to stop and remove the started container.


Each application can be used with an already existing database, by specifying the following arguments:

* MONGO database configuration:
  - `--mongo-host-1` -> MongoDB host-1 address (default = `localhost`)
  - `--mongo-host-2` -> MongoDB host-2 address (default = `localhost`)
  - `--mongo-host-3` -> MongoDB host-3 address (default = `localhost`)
  - `--mongo-port-1` -> MongoDB host-1 port (default = `27017`);
  - `--mongo-port-2` -> MongoDB host-2 port (default = `27018`);
  - `--mongo-port-3` -> MongoDB host-3 port (default = `27019`);
  - `--db-name` -> Database name (default = `library`);
  - `--db-user-collection` -> user collection name (default = `user`);
  - `--db-book-collection` -> book collection name (default = `book`);

* MYSQL database configuration:
  - `--mysql-host` -> MySql host address (default = `localhost`);
  - `--mysql-port` -> MySql host port (default = `3306`);
  - `--db-name` -> Database name (defaulkt = library);
  - `--db-user` -> username (default = `root`);
  - `--db-password` -> password (default = `password`);
