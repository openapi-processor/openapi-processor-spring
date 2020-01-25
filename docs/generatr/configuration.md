---
layout: default
title: Configuration
parent: The generatr
nav_order: 4
---

# Configuration

The configuration of the generatr is given in the `mapping.yaml`. It does contain some general options
and the type mapping information.


A mapping yaml looks like this  

    options:
      package-name: com.github.hauner.openapi
      bean-validation: true 
    
    map:
     # see link below


## options:

- `package-name`: (**required**) the root package name of the generated interfaces & models. The package
 folder tree will be created inside the `targetDir` (see [using gradle][docs-gradle]). 
 
  Interfaces and models will be generated into the `api` and `model` subpackages of `package-name`.

  - so the final package name of the generated interfaces will be `"${package-name}.api"`  
  - and the final package name of the generated models will be `"${package-name}.model"`  
  {: .mb-5 }
  
- `bean-validation` (**optional**, `true` or `false`) enables generation of bean validation annotations.
 Defaults to `false`. See [Bean Validation][bean-validation]{:target="_blank"}.

## map:

Using type mapping we can tell the generatr to map types (schemas) from an openapi.yaml description to
a specific existing java type instead of generating a model class from the source OpenAPI type. 

The type mapping is described in [java type mapping][docs-mapping].


[docs-mapping]: /openapi-generatr-spring/mapping/
[docs-gradle]:  /openapi-generatr-spring/gradle.html
[bean-validation]: https://beanvalidation.org/
