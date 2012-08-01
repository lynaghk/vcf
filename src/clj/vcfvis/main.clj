(ns vcfvis.main
  (:gen-class)
  (:use [vcfvis.server :only [start!]]
        [bcbio.variation.api.shared :only [set-config-from-file!]]
        [clojure.tools.cli :only [cli]]))

(defn -main [& args]
  (let [[opts args banner] (cli args
                                ["-h" "--help" "Show help" :default false :flag true]
                                ["--port" "Webserver port" :default 8080 :parse-fn #(Integer. %)]
                                ["-c" "--config" "Default genome config YAML" :default "config/web-processing.yaml"])]

    (when (:help opts)
      (println banner)
      (System/exit 0))
    
    (set-config-from-file! (:config opts))
    
    (start! (:port opts))))

