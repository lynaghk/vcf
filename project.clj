(defproject com.keminglabs/vcf "0.0.1-SNAPSHOT"
  :description "Genetic variant metric visualization tool"
  :license {:name "BSD" :url "http://www.opensource.org/licenses/BSD-3-Clause"}
  :dependencies [[org.clojure/clojure "1.4.0"]

                 [com.keminglabs/c2 "0.2.1-SNAPSHOT"]
                 [com.keminglabs/chosen "0.1.7-SNAPSHOT"]
                 
                 ;;Comment out until lein-cljsbulid deps conflict issue can be resolved.
                 ;;[bcbio.variation "0.0.1-SNAPSHOT"]
                 ]
  
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs"]
  
  :plugins [[lein-cljsbuild "0.2.4"]]

  :cljsbuild {:builds
              [{:source-path "src/cljs"
                :compiler {:output-to "public/vcf.js"
                           
                           ;; :optimizations :advanced
                           ;; :pretty-print false
                           
                           :optimizations :whitespace
                           :pretty-print true

                           :externs ["externs/jquery.js"
                                     "resources/closure-js/externs"]}}]})
