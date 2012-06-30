(defproject com.keminglabs/vcf "0.0.1-SNAPSHOT"
  :description "Genetic variant metric visualization tool"
  :dependencies [[org.clojure/clojure "1.4.0"]

                 [com.keminglabs/c2 "0.2.0"
                  :exclusions [org.clojure/core.match
                               com.keminglabs/singult]]
                 [com.keminglabs/singult "0.1.3-SNAPSHOT"]
                 [com.keminglabs/chosen "0.1.6"
                  :exclusions [org.clojure/core.match]]
                 [match "0.2.0-alpha10-SNAPSHOT"]
                 ]
  
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"
                 ;;Needed when compiling scratch data, but don't leave in because it'll pollute cljs builds with old libs.
                 ;;"vendor/bcbio.variation/src" "vendor/bcbio.variation/lib/*"
                 ]
  
  :plugins [[lein-cljsbuild "0.2.2"]]

  :cljsbuild {:builds
              [{:source-path "src/cljs"
                :compiler {:output-to "public/vcf.js"
                           :pretty-print true
                           ;;:optimizations :advanced
                           :optimizations :whitespace
                           :externs ["externs/jquery.js"]}}]})
