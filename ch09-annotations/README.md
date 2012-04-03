## _Clojure Programming_, Chapter 9

### Annotations

This project contains two examples related to using Java
annotations from within Clojure.

#### Defining JUnit 4.x tests in Clojure

The
[`com.clojurebook.annotations.junit`](src/main/clojure/com/clojurebook/annotations/junit.clj)
namespace defines a Java class using `gen-class` that defines three
methods, each of which has an `org.junit.JUnitTest` annotation applied.

Two of these tests are designed to fail (to demonstrate the expected
effects of the annotations).  All tests may be run via the standard
JUnit test runner with:

```
mvn test
```

#### Defining JAX-RS services in Clojure

The
[`com.clojurebook.annotations.jaxrs`](src/main/clojure/com/clojurebook/annotations/jaxrs.clj)
namespace defines a Java class using `deftype` that has a single `greet`
method.  That method, its argument, and the class itself each have
`javax.ws.rs` annotations applied, which defines how the class and
method should be exposed as an HTTP endpoint.

To test this, start a REPL with `mvn clojure:repl`, and start the web
service using the Grizzly embedded container:

```clojure
(com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory/create
  "http://localhost:8180/"
  {"com.sun.jersey.config.property.packages" "com.clojurebook.annotations.jaxrs"})
```

In another terminal, you can get the web service's WADL at
`http://localhost:8180/application.wadl`:

```
$ curl http://localhost:8180/application.wadl
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<application xmlns="http://research.sun.com/wadl/2006/10">
    <doc xmlns:jersey="http://jersey.java.net/" jersey:generatedBy="Jersey: 1.8 06/24/2011 12:17 PM"/>
    <resources base="http://localhost:8180/">
        <resource path="/greet/{visitorname}">
            <param xmlns:xs="http://www.w3.org/2001/XMLSchema" type="xs:string" style="template" name="visitorname"/>
            <method name="GET" id="greet">
                <response>
                    <representation mediaType="text/plain"/>
                </response>
            </method>
        </resource>
    </resources>
</application>
```

…and you can call the `greet` service implemented via the `deftype`
class:

```
$ curl http://localhost:8180/greet/Rose
Hello Rose!
```


