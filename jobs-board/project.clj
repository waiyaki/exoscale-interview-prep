(defproject jobs-board "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.reader "1.3.2"]
                 [metosin/reitit "0.3.10"]
                 [metosin/ring-http-response "0.9.1"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [com.taoensso/timbre "4.10.0"]]
  :main ^:skip-aot jobs.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev     {:source-paths ["env/dev/src"]
                       :dependencies [[ring/ring-devel "1.7.1"]]
                       :main         jobs.dev}})
