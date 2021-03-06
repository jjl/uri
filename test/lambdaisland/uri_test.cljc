(ns lambdaisland.uri-test
  #?@(:clj
      [(:require
        [clojure.test :as t :refer [are deftest is testing]]
        [lambdaisland.uri :as uri])
       (:import lambdaisland.uri.URI)]
      :cljs
      [(:require [lambdaisland.uri :as uri]
                 [cljs.test :refer-macros [deftest is testing are]])]))

(deftest parsing
  (testing "happy path"
    (are [x y] (= y (uri/parse x))
      "http://user:password@example.com:8080/path?query=value#fragment"
      (uri/URI. "http" "user" "password" "example.com" "8080" "/path" "query=value" "fragment")

      "/happy/path"
      (uri/URI. nil nil nil nil nil "/happy/path" nil nil)

      "relative/path"
      (uri/URI. nil nil nil nil nil "relative/path" nil nil)

      "http://example.com"
      (uri/URI. "http" nil nil "example.com" nil nil nil nil)
      )))

(deftest joining
  (are [x y] (= (uri/parse y) (apply uri/join (map uri/parse x)))
    ["http://foo.bar"              "https://baz.com"]   "https://baz.com"
    ["http://example.com"          "/a/path"]           "http://example.com/a/path"
    ["http://example.com/foo/bar"  "/a/path"]           "http://example.com/a/path"
    ["http://example.com/foo/bar"  "a/relative/path"]   "http://example.com/foo/a/relative/path"
    ["http://example.com/foo/bar/" "a/relative/path"]   "http://example.com/foo/bar/a/relative/path"
    ["/foo/bar/"                   "a/relative/path"]   "/foo/bar/a/relative/path"
    ["http://example.com"          "a/relative/path"]   "http://example.com/a/relative/path"
    ["http://example.com/a/b/c/d/" "../../x/y"]         "http://example.com/a/b/x/y")

  (testing "https://www.w3.org/2004/04/uri-rel-test.html"
    (are [x y] (= y (str (uri/join (uri/parse "http://a/b/c/d;p?q") (uri/parse x))))
      "g" "http://a/b/c/g"
      "./g" "http://a/b/c/g"
      "g/" "http://a/b/c/g/"
      "/g" "http://a/g"
      "//g" "http://g"
      "?y" "http://a/b/c/d;p?y"
      "g?y" "http://a/b/c/g?y"
      "#s" "http://a/b/c/d;p?q#s"
      "g#s" "http://a/b/c/g#s"
      "g?y#s" "http://a/b/c/g?y#s"
      ";x" "http://a/b/c/;x"
      "g;x" "http://a/b/c/g;x"
      "g;x?y#s" "http://a/b/c/g;x?y#s"
      "" "http://a/b/c/d;p?q"
      "." "http://a/b/c/"
      "./" "http://a/b/c/"
      ".." "http://a/b/"
      "../" "http://a/b/"
      "../g" "http://a/b/g"
      "../.." "http://a/"
      "../../" "http://a/"
      "../../g" "http://a/g"
      "../../../g" "http://a/g"
      "../../../../g" "http://a/g"
      "/./g" "http://a/g"
      "/g" "http://a/g"
      "g." "http://a/b/c/g."
      ".g" "http://a/b/c/.g"
      "g.." "http://a/b/c/g.."
      "..g" "http://a/b/c/..g"
      "./../g" "http://a/b/g"
      "./g/" "http://a/b/c/g/"
      "g/h" "http://a/b/c/g/h"
      "h" "http://a/b/c/h"
      "g;x=1/./y" "http://a/b/c/g;x=1/y"
      "g;x=1/../y" "http://a/b/c/y"
      "g?y/./x" "http://a/b/c/g?y/./x"
      "g?y/../x" "http://a/b/c/g?y/../x"
      "g#s/./x" "http://a/b/c/g#s/./x"
      "g#s/../x" "http://a/b/c/g#s/../x"
      "http:g" "http:g"))

  (testing "coerces its arguments"
    (is (= (uri/join "http://x/y/z" "/a/b/c") (uri/parse "http://x/a/b/c")))
    #?(:clj
       (is (= (uri/join (java.net.URI. "http://x/y/z") "/a/b/c") (uri/parse "http://x/a/b/c"))))))

(deftest lambdaisland-uri-URI
  (let [example "http://usr:pwd@example.com:8080/path?query=value#fragment"
        parsed (uri/uri example)]
    (testing "it allows keyword based access"
      (is (= (:scheme parsed) "http"))
      (is (= (:user parsed)) "usr")
      (is (= (:password parsed)) "pwd")
      (is (= (:host parsed)) "example.com")
      (is (= (:port parsed)) "8080")
      (is (= (:path parsed)) "/path")
      (is (= (:query parsed)) "query=value")
      (is (= (:fragment parsed)) "fragment"))
    (testing "it allows map-style access"
      (is (= (parsed :scheme) "http"))
      (is (= (parsed :user)) "usr")
      (is (= (parsed :password)) "pwd")
      (is (= (parsed :host)) "example.com")
      (is (= (parsed :port)) "8080")
      (is (= (parsed :path)) "/path")
      (is (= (parsed :query)) "query=value")
      (is (= (parsed :fragment)) "fragment"))
    (testing "it converts correctly to string"
      (is (= (str parsed) example)))))
