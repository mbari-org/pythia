# Amphiaraus

yolov5 model server using [djl](https://djl.ai/)

## Model preparation.

This server requires a torchscript model and names file. You can convert a `.pt` file to torchscript b by cloning the yolov5 repo and running:

```bash
python export.py --weights /path/to/model/my-best-model.pt --include torchscript
```

[![vertx](https://img.shields.io/badge/vert.x-4.4.0-purple.svg)](https://vertx.io)

- <https://github.com/deepjavalibrary/djl/blob/master/examples/src/main/java/ai/djl/examples/inference/ObjectDetection.java>
- <https://github.com/deepjavalibrary/djl/issues/1563>
- <http://djl.ai/engines/pytorch/pytorch-engine/>
- <https://docs.djl.ai/jupyter/object_detection_with_model_zoo.html>
- <https://github.com/deepjavalibrary/djl-demo>
- To convert pt files to torchscript, see <https://github.com/ultralytics/yolov5/issues/251>


This application was generated using <http://start.vertx.io>

## Building

To launch your tests:
```
./mvnw clean test
```

To package your application:
```
./mvnw clean package
```

To run your application:
```
./mvnw clean compile exec:java
```

## Help

- [Vert.x Documentation](https://vertx.io/docs/)
- [Vert.x Stack Overflow](https://stackoverflow.com/questions/tagged/vert.x?sort=newest&pageSize=15)
- [Vert.x User Group](https://groups.google.com/forum/?fromgroups#!forum/vertx)
- [Vert.x Discord](https://discord.gg/6ry7aqPWXy)
- [Vert.x Gitter](https://gitter.im/eclipse-vertx/vertx-users)


