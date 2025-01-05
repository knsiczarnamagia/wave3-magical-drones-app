# Spring Boot backend app

A backend service for the Magical Drones project.

# How to run locally?

## Profiles

The project contains two run profiles: production (`prod`) and development (`dev`).
The current profile is specified in the `src/main/resources/application.yaml` config file.
You may temporarily change the setting there, although I recommend to set it in the app
execution command as an option: `mvn spring-boot:run -"Dspring-boot.run.profiles"=dev`.

### `prod` production profile

The `prod` profile connects to AWS S3 to store images. To use it locally, you need to:
- Set up an account on AWS Console
- Create a new S3 bucket named "magical-drones"
- Create a new User group on AWS IAM with the `AmazonS3FullAccess` permission
- Create a new User attached to that group
- Set the received credentials inside the `~/.aws/credentials` file:
```
[default]
aws_access_key_id=<value from AWS access portal>
aws_secret_access_key=<value from AWS access portal>
```
- Alternatively, you may also set the values as environment variables named: `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`.

Useful resources:
- [Setting AWS credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-temporary.html)
- [Controlling access to a bucket with user policies](https://docs.aws.amazon.com/AmazonS3/latest/userguide/walkthrough1.html)

### `dev` development profile
The `dev` profile does not connect to any external services, instead it saves files in a local bucket located at `./backend/magical-drones` temporary directory.

Therefore, I recommend using the `dev` profile for quick local testing.

## Prerequisites

1. To run the app, you first need to install:
   - Java 21
   - PostgreSQL 17


2. Set up the database
- Create a new `magicaldrones_dev` on port 5432:
```sql
CREATE DATABASE magicaldrones_dev;
```

3. Environment variables

The project depends on the following environment variables, which need to be specified on a local machine before running:
- `MD_DB_USERNAME` - the username of the database user
- `MD_DB_PWD` - the password to the database user

4. JWT signature keys

The public-private RSA key pair for signing JWT tokens are stored in .pem files inside the `/src/main/resources/certs` directory. The folder is defined in `.gitignore` so that it's not commited to the public repo.

To create the public and private RSA keys, you may use openssl:

```shell
# create a private key
openssl genrsa -out keypair.pem 2048
# generate the public key
openssl rsa -in keypair.pem -pubout -out public.pem
# convert the private key from PEM to PKCS8 format
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
```
Now, move the `public.pem` and `private.pem` files to `/src/main/resources/certs` and remove the `keypair.pem` file.

**Please make sure you place the .pem files inside `/src/main/resources/certs`! If you want to save them in another directory, make sure you add it to `.gitignore`!**

*Note: the production AWS solution will handle the RSA key storage differently. I'll describe it here, once it's implemented.*

## Build and run
While this project uses Maven as a build tool, it's not required to have it installed on your local machine as the Maven Wrapper is included in the project's source code. It means that, whenever you run the Maven wrapper (`./mvnw'), Maven will automatically pull Maven from source and use it to run the command. So, to build and run the application in `dev` profile from the terminal, use:

```shell
./mvnw spring-boot:run -"Dspring-boot.run.profiles"=dev
```

# Potential issues

## `pom.xml`: `Resource registered by this uri is not recognized`

Thanks, @remigiuszsek, for pointing out this problem which may occur to some folks during initial Maven setup. Things that we tried out which should fix the issue:
- Add the "https://maven.apache.org/xsd/maven-4.0.0.xsd" uri in the File > Settings > Languages & Frameworks > Schemas and DTDs in IntelliJ settings
- Invalidate IntelliJ caches: File -> Invalidate caches
- Right-click inside `pom.xml` and Maven -> Reload project/Sync project and restart the IntelliJ