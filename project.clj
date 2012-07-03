(defproject com.keminglabs/vcf "0.0.1-SNAPSHOT"
  :description "Genetic variant metric visualization tool"
  :dependencies [[org.clojure/clojure "1.4.0"]

                 [com.keminglabs/c2 "0.2.1-SNAPSHOT"]
                 [com.keminglabs/chosen "0.1.7-SNAPSHOT"]]
  
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"
                 ;;Needed when compiling scratch data, but don't leave in because it'll pollute cljs builds with old libs.
                 ;;"vendor/bcbio.variation/src" "vendor/bcbio.variation/lib/*"
                 ]
  
  :plugins [[lein-cljsbuild "0.2.2"]]

  :cljsbuild {:builds
              [{:source-path "src/cljs"
                :compiler {:output-to "public/vcf.js"
                           
                           ;; :optimizations :advanced
                           ;; :pretty-print false
                           
                           :optimizations :whitespace
                           :pretty-print true

                           :externs ["externs/jquery.js"
                                     "resources/closure-js/externs"]}}]})
