(ns vcfvis.main
  (:gen-class)
  (:use [vcfvis.server :only [start!]]
        [clojure.tools.cli :only [cli]]))

(defn -main [& args]
  (let [[opts args banner] (cli args
                                ["-h" "--help" "Show help" :default false :flag true]
                                ["--port" "Webserver port" :default 8080 :parse-fn #(Integer. %)])]

    (when (:help opts)
      (println banner)
      (System/exit 0))
    
    (start! (:port opts))))

