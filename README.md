[![Build Status](https://travis-ci.org/lambdaisland/uri.svg?branch=master)](https://travis-ci.org/lambdaisland/uri)

# lambdaisland/uri

A pure Clojure/ClojureScript URI library.

Key features

- 100% cross-platform `.cljc`
- RFC compliant joining of URIs
- relative URIs as first class citizens

## Rationale

There are a number of Clojure libraries for working with URI/URLs (see
[Similar projects](#similar_projects) below). They all rely on `java.net.URI`,
which has two major shortcomings.

The first is that relative URIs are not first class. A `java.net.URI` instance
is always fully qualified. This is needlessly restrictive.

The second is that doing an equality check on a `java.net.URI` instance
[causes a DNS lookup](http://brian.pontarelli.com/2006/12/05/mr-gosling-why-did-you-make-url-equals-suck/).
This means URIs are not value objects.

## Installation

To install, add the following dependency to your project or build file:

``` clojure
[lambdaisland/uri "1.0.0"]
```

## API docs

[lambdaisland/uri API docs](https://lambdaisland.github.io/uri/)

## Usage

``` clojure
(require '[lambdaisland.uri :refer [uri join]])


;; uri :: String -> lambdaisland.uri.URI
(uri "//example.com/foo/bar")
;;=> #lambdaisland/uri "//example.com/foo/bar"


;; A URI is a record, use assoc to update specific parts
;; Use `str` if you want the URI back as a string
(str
 (assoc (uri "//example.com/foo/bar")
        :scheme "https"
        :user "arne"
        :password "supersecret"
        :host "lambdaisland.com"
        :port "3333"
        :path "/hello/world"
        :query "q=5"
        :fragment "section1"))
;;=> "https://arne:supersecret@lambdaisland.com:3333/hello/world?q=5#section1"


;; RFC compliant joining of relative URIs
(join "//example.com/foo/bar" "./~arne/site/" "../foo.png")
;;=> #lambdaisland/uri "//example.com/foo/~arne/foo.png"


;; Arguments to `join` are coerced, you can pass strings, java.net.URI, or any x
;; for which `(str x)` returns a URI string.
(join (java.net.URI. "http://example.com/foo/bar")  (uri "./~arne/site/") "../foo.png")
;;=> #lambdaisland/uri "http://example.com/foo/~arne/foo.png"


;; URI implements IFn for keyword based lookup, so it's fully
;; interface-compatible with Clojure maps.
(:path (uri "http://example.com/foo/bar"))


;; Instances of URI are printed with a #lambdaisland/uri reader tag. To read
;; them back from EDN, use the provided readers.
(require '[clojure.edn :as edn])

(edn/read-string
 {:readers lambdaisland.uri/edn-readers}
 "#lambdaisland/uri \"http://example.com/foo/~arne/foo.png\"")
```

## Similar projects

* [cemerick/url](https://github.com/cemerick/url)
  Cross platform (cljx), Clojure version uses `java.net.URI`.
* [michaelklishin/urly](https://github.com/michaelklishin/urly)
  Based on `java.net.URI`.
* [codonnell/uri](https://github.com/codonnell/uri)
  Based on `java.net.URI`.

## Further reading

This library implements the algorithm specified in Section 5.2 of [RFC3986](https://www.ietf.org/rfc/rfc3986.txt), "Uniform Resource Identifier (URI): Generic Syntax".

It has been tested against [this list of test cases compiled by the W3C](https://www.w3.org/2004/04/uri-rel-test.html).

## License

Copyright © 2017 Arne Brasseur

Distributed under the [Mozilla Public License 2.0](https://www.mozilla.org/media/MPL/2.0/index.txt).
