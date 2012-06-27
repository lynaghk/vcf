(defproject com.keminglabs/vcf "0.0.1-SNAPSHOT"
  :description "Genetic variant metric visualization tool"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.keminglabs/c2 "0.2.0-SNAPSHOT"]]
  
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"
                 "vendor/bcbio.variation/src" "vendor/bcbio.variation/lib/*"]
  
  :plugins [[lein-cljsbuild "0.2.1"]]

  :cljsbuild {:builds
              [{:source-path "src/cljs"
                :compiler {:output-to "public/vcf.js"
                           :pretty-print true
                           :optimizations :whitespace}}]})
