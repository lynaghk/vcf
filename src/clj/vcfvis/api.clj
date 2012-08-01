(ns vcfvis.api
  (:use compojure.core
        [cemerick.friend :only [current-authentication]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [bcbio.variation.api.file :as bc-file]
            [bcbio.variation.api.metrics :as bc-metrics]))

(defn gs-creds
  "Helper fn to extract GenomeSpace client from session and return creds map expected by bcbio api fns."
  [req]
  (when-let [gs-client (:client (current-authentication))]
    {:client gs-client}))

(defn clj-response [x]
  {:status 202
   :headers {"Content-Type" "application/clojure; charset=utf-8"}
   :body (pr-str x)})

(def reference "vendor/bcbio.variation/test/data/GRCh37.fa")


(defroutes api-routes
  (GET "/files" req
       (when-let [creds (gs-creds req)]
         (clj-response (bc-file/get-files :vcf creds))))

  (GET "/metrics" req
        (when-let [creds (gs-creds req)]
          (let [{{file-urls :file-urls} :params} req]
            (clj-response
             (for [file-url file-urls]
               (dissoc (bc-metrics/plot-ready-metrics file-url
                                                      reference
                                                      :creds creds
                                                      :cache-dir "/tmp/")
                       ;;TODO: figure out why cljs reader is blowing up on instant literals.
                       :created-on))))))

  (route/not-found "API ERROR =("))
