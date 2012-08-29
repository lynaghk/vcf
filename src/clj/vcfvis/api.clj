(ns vcfvis.api
  (:use compojure.core
        [cemerick.friend :only [current-authentication]]
        [bcbio.variation.api.run :only [do-analysis]]
        [bcbio.variation.index.metrics :only [expose-metrics]]
        [cheshire.core :only [generate-string]])
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

(defroutes api-routes
  (GET "/context" req
       (when-let [creds (gs-creds req)]
         (clj-response {:files (bc-file/get-files :vcf creds)
                        :metrics expose-metrics
                        :username (:identity (current-authentication))})))

  (GET "/metrics" req
       (when-let [creds (gs-creds req)]
         (let [{{file-urls :file-urls} :params} req]
           (clj-response
            (for [file-url file-urls]
              (bc-metrics/plot-ready-metrics file-url
                                             :creds creds))))))

  (GET "/vcf" req
       (when-let [creds (gs-creds req)]
         (let [{{file-url :file-url} :params} req]
           (let [raw (bc-metrics/get-raw-metrics file-url
                                                 :creds creds)]
             (generate-string
              {:clj (pr-str {:file-url file-url
                             :available-metrics (-> raw first (dissoc :id) keys set)})
               :raw raw})))))

  (POST "/filter" req
        (when-let [creds (gs-creds req)]
          (let [{{:keys [file-url metrics]} :params} req]
            (clj-response
             ;;do-analysis returns a seq, but the result is always just one file
             (first (do-analysis :filter
                                 {:filename file-url
                                  :metrics metrics}
                                 creds))))))

  (route/not-found "API ERROR =("))
