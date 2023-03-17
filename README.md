# pythia

Pythia is a web service for generating predictions of what is seen in an image using yolov5 and a custom model.

Web UI is provided by [CVisionAI](https://www.cvisionai.com/). Swagger docs will be at `http://hostname:8080/q/swagger-ui`. Health checks at `http://hostname:8080/q/health`

## Quick Start

### Create torchscript model from pt model

First, [convert your yolov5 model](https://docs.djl.ai/docs/pytorch/how_to_convert_your_model_to_torchscript.html) to torchscript. If you have a `.pt` file you can convert it to torchscript like so:

```bash
git clone https://github.com/ultralytics/yolov5  # clone
cd yolov5
pip install -r requirements.txt  # install

python export.py --agnostic-nms --weights my-yolo-model.pt --include torchscript
# This will create my-yolo-model.pt
```

### Start pythia

To launch pythia, you will need the torchscript model and the names file that was use the pt model. Note that image requires internet access in order to fetch the native libraries for the host platform when needed. Also the web UI will only work if the app is running on port 8080.

```bash
docker run -d \
  --name=pythia \
  --restart=always \
  -p 8080:8080 \
  -v /path/to/models/:/opt/models \
  mbari/pythia \
  run /opt/models/my-yolo-model.torchscript  /opt/models/my-yolo-model.names
```

## Quick Start for development

```bash
# start server
quarkus dev -Dquarkus.args="src/test/resources/models/mbari-mb-benthic-33k.torchscript src/test/resources/models/mbari-mb-benthic-33k.names"

# Send a request
curl -X POST 'http://localhost:8080/predict/' \
  -H "accept: application/json" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@src/test/resources/images/03_00_51_14.jpg;type=image/jpg"
```

The [model](https://doi.org/10.5281/zenodo.5539915) used in development is from the [FathomNet ModelZoo](https://github.com/fathomnet/models).

### Native libraries

Pythia depends on native libraries. These platform specific libraries can be downloaded at runtime or they can be included at build time via maven profiles.  

```bash
mvn help:active-profiles -P linux-arm -P -macos-arm

The following profiles are active:

 - linux-arm (source: org.mbari:pythia:1.0.0-SNAPSHOT)
```

vs.

```bash
mvn help:active-profiles -P linux-arm

The following profiles are active:

 - linux-arm (source: org.mbari:pythia:1.0.0-SNAPSHOT)
 - macos-arm (source: org.mbari:pythia:1.0.0-SNAPSHOT)```

## Quarkus Stuff

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev -Dquarkus.args="src/test/resources/models/mbari-mb-benthic-33k.torchscript src/test/resources/models/mbari-mb-benthic-33k.names"
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/pixel-prophet-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- RESTEasy Reactive ([guide](https://quarkus.io/guides/resteasy-reactive)): A JAX-RS implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
