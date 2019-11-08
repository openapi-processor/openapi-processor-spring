---
layout: default
title: Home
nav_order: 1
description: "Home Description"
permalink: /
---

# openapi-generatr-spring

a simple [OpenAPI][openapi] interface only code generator for [Spring Boot][springboot].


## Features

- generates only Java interfaces and Java model classes (get/set pojos) for all defined endpoints and schemas
  to allow (nearly) full control of the endpoint implementation. It does not generate any other file. 

  implemented
  {: .label .label-green }

- interfaces and models are implemented & compiled by your project. 

- gradle support by using the [openapi-generatr-gradle][generatr-gradle] plugin.

  implemented
  {: .label .label-green }

- simple & flexible type mappings with generic support (one level) to map schemas defined in the openapi.yaml to
  existing java classes. For example to map the openapi `array` type to different Java collections or to map paging
  parameters and results to Spring types like `Page<>` & `Pageable`.
   
  it is possible to define the mapping globally or for a specific response or parameter or even only for a specific
  endpoint. 

  partially implemented
  {: .label .label-green }
  
- add additional parameters to an endpoint which are not defined in the openapi description. For example to pass
  a `HttpServletRequest` to the endpoint implementation.

  planned
  {: .label .label-yellow }
 
- handle multiple responses by generating one endpoint method for each response content type.
 
  planned
  {: .label .label-yellow }

- WebFlux support, may need its own generatr. 

  planned
  {: .label .label-yellow }

- the generated code is nicely formatted.

  planned
  {: .label .label-yellow }

- the generated code does not use swagger annotations. There is no need to generate the documentation from the code
  when the code is generated from the documentation (i.e. an openapi.yaml). 


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

[generatr-gradle]: https://github.com/hauner/openapi-generatr-gradle

[openapi]: https://www.openapis.org/
[springboot]: https://spring.io/projects/spring-boot
[license]: http://www.apache.org/licenses/LICENSE-2.0.txt
