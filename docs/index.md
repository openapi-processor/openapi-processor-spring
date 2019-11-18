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

a simple [OpenAPI][openapi] interface only code generator for [Spring Boot][springboot].
{: .mb-6 }

Note that the generatr is still in an early state of development and not all features are completely implemented.
(November 2019)
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

- <span class="label label-green">partially implemented</span> generates only java interfaces and java model classes
  (get/set pojos) for all defined endpoints and schemas to allow (nearly) full control of the endpoint
   implementation. It does not generate any other file. 
  
  - <span class="label label-red">caveat</span> property names in the openapi description must be java
    compatible. The generatr does not yet generate a `@JsonProperty` name to map the api name to a 
    java property name.

  - <span class="label label-red">caveat</span> no `requestBody:` support at the moment.

  - <span class="label label-red">caveat</span> limited parameter support:
     - <span class="label label-green">done</span> query parameters, i.e. `in: query`
     - <span class="label label-red">todo</span> path parameters, i.e. `in: path`
     - <span class="label label-red">todo</span> header parameters, i.e. `in: header`
     - <span class="label label-red">todo</span> cookie parameters, i.e. `in: cookie`
{: .mb-5 }

- <span class="label label-green">partially implemented</span> simple & flexible type mappings with generic support
  (one level) to map schemas defined in the openapi.yaml to existing java types. For example to map the openapi
  `array` type to different java collections or to map paging parameters and results to Spring types like `Page<>`
   & `Pageable`.
   
  it is possible to define the mapping globally or for a specific response or parameter or even only for a specific
  endpoint. 

  - <span class="label label-red">caveat</span> the mapping can be defined at all levels but is not yet honored
    at all places.
{: .mb-5 }
    
- <span class="label label-green">implemented</span> gradle support via [openapi-generatr-gradle][generatr-gradle] plugin.

  - <span class="label label-red">caveat</span> the gradle plugin is currently the only option to run the
  generatr.
{: .mb-5 }


- <span class="label label-yellow">planned</span> add additional parameters to an endpoint which are not defined in
  the openapi description. For example to pass a `HttpServletRequest` to the endpoint implementation.
 
- <span class="label label-yellow">planned</span> handle multiple responses by generating one endpoint method for
  each response content type.
 
- <span class="label label-yellow">planned</span> WebFlux support, may need its own generatr. 

- <span class="label label-yellow">planned</span> nicely formatted source code by running a code formatter.

- the generated code does not use swagger annotations. There is no need to generate the documentation from the code
  when the code is generated from the documentation (i.e. an openapi.yaml). 


The generated source code has to be included in a project to compile it. This is easily done
with the [openapi-generatr-gradle][generatr-gradle] plugin. See [Using Gradle][docs-gradle].
{: .note .info .mb-6}

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

[bintray]: https://bintray.com/hauner/openapi-generatr
[generatr-gradle]: https://github.com/hauner/openapi-generatr-gradle
[generatr-releases]: https://github.com/hauner/openapi-generatr-spring/releases
[generatr-license]: https://github.com/hauner/openapi-generatr-spring/blob/master/LICENSE

[openapi]: https://www.openapis.org/
[springboot]: https://spring.io/projects/spring-boot
[license]: http://www.apache.org/licenses/LICENSE-2.0.txt
