---
layout: default
title: Home
nav_order: 1
description: "Home Description"
permalink: /
---

[![][badge-license]][generatr-license]
[![][badge-ci]][workflow-ci]

# openapi-generatr-spring
{: .no_toc }

a simple [OpenAPI][openapi] interface only (& model) code generator for [Spring Boot][springboot].

It is useful in an API first approach where you API is explicitly defined by an OpenAPI yaml file
before it gets implemented. 

The generatr generates java interfaces based on the endpoint description of the API and simple POJO
classes for parameter or response objects defined in th API. It is **your** task to create the controller
classes that implement the interfaces. 
 
The interfaces will help to keep the implementation in sync with the API. If anything relevant changes
in the API the interface changes and the compiler will warn that the interface is not implemented
correctly.

See the [generatr intro][docs-generatr]{:target="_blank"} for a short example.
{: .mb-6 }

February 2020: The generatr is ready to try but note that the generatr is still in an early state of
development and may not generate the correct code yet in all cases. See [feedback](#feedback).
{: .note .info .mb-6}


## table of contents
{: .no_toc .text-delta }

1. replaced by toc
{:toc}

## Latest Version

See the [maven repository][bintray]{:target="_blank"}.

## Releases

See the [release notes][generatr-releases]{:target="_blank"}.

## Features

- generates only java interfaces and java model classes (get/set POJOs) for all defined endpoints and schemas to
 allow (nearly) full control of the endpoint implementation. It does not generate any other file. See
 [generatr][docs-generatr].

- powerful type mappings with generic support (one level) to map schemas defined in the openapi.yaml to
  existing java types. For example to map the openapi `array` type to different java collections or to
  map paging parameters and results to Spring types like `Page<>` & `Pageable`. See [type mapping][docs-mapping].
   
  it is possible to define the mapping globally or for a specific response or parameter or even only for a specific
  endpoint. 

- generates human readable code.
    
- gradle support via [openapi-generatr-gradle][generatr-gradle] plugin (the plugin is currently the only option
 to run the generatr).

- add additional parameters to an endpoint which are not defined in the OpenAPI description. For example to pass
 a `HttpServletRequest` to the endpoint implementation. <span class="label label-green">since 1.0.0.M6</span>

- supports bean validations. The constraints of the openapi description are mapped to java bean validation
 annotations. <span class="label label-green">since 1.0.0.M6</span>
 
- allows to exclude endpoints from generation. This is useful if the generatr does not create the correct code for
 an endpoint. That way the generatr can still be used for all the other endpoints.
   <span class="label label-green">since 1.0.0.M6</span>

- handle multiple responses by generating one endpoint method for each response content type.
   <span class="label label-green">since 1.0.0.M8</span>

- <span class="label label-yellow">planned</span> WebFlux support, may need its own generatr. 

- the generated code does not use swagger annotations. There is no need to generate the documentation from the code
  when the code is generated from the documentation (i.e. an openapi.yaml). 


The generated source code has to be included in a project to compile it. This is easily done
with the [openapi-generatr-gradle][generatr-gradle] plugin. See [Using Gradle][docs-gradle].
{: .note .info .mb-6}

## Feedback

In case some feature is missing or the generated code is not 100% what you would expect create an [issue][generatr-issues]
preferably with a test case. Providing a test case will help significantly :-) 

A test case is a single folder with an openapi.yaml file and the expected Java files the generatr should create.
The structure looks like this:

    my-new-test-case/
        openapi.yaml
        mapping.yaml
        generated/
           api/
               AnEndpointInterface.java
               .. more api interfaces ..
           model/
               AModelClass.java
               AnotherModelClass.java
               .. more model files ..

The `mapping.yaml` contains the type mapping information and is an optional file.

See the [existing integration tests][generatr-int-resources] for a couple of examples. 

## License

openapi-generatr-spring  is distributed by [Apache License 2.0][license].

## Contributors

<ul class="list-style-none">
{% for contributor in site.github.contributors %}
  <li class="d-inline-block mr-1">
     <a href="{{ contributor.html_url }}"><img src="{{ contributor.avatar_url }}" width="32" height="32" alt="{{ contributor.login }}"/></a>
  </li>
{% endfor %}
</ul>

[badge-license]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?labelColor=313A42
[badge-ci]: https://github.com/hauner/openapi-generatr-spring/workflows/ci/badge.svg

[workflow-ci]: https://github.com/hauner/openapi-generatr-spring/actions?query=workflow%3Aci

[docs-gradle]: /openapi-generatr-spring/gradle.html
[docs-generatr]: /openapi-generatr-spring/generatr/
[docs-mapping]: /openapi-generatr-spring/mapping/

[bintray]: https://bintray.com/hauner/openapi-generatr
[generatr-gradle]: https://github.com/hauner/openapi-generatr-gradle
[generatr-releases]: https://github.com/hauner/openapi-generatr-spring/releases
[generatr-license]: https://github.com/hauner/openapi-generatr-spring/blob/master/LICENSE
[generatr-int-resources]: https://github.com/hauner/openapi-generatr-spring/tree/master/src/testInt/resources
[generatr-issues]: https://github.com/hauner/openapi-generatr-spring/issues

[openapi]: https://www.openapis.org/
[springboot]: https://spring.io/projects/spring-boot
[license]: http://www.apache.org/licenses/LICENSE-2.0.txt
