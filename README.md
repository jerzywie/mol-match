# mol-match

A simple program to evaluate how molecules might bond together

## Building

This is a Clojure CLI tools project

To build an uberjar:

- Make sure there is a `pom.xml` file (generate with `clj -Spom`)

- Issue the following command
```
clojure -A:build-depstar -m hf.depstar.uberjar mol-match.jar -C -m mol-match.core
```

## Usage

```java -jar mol-match.jar <sequence-length> <output-file>```
