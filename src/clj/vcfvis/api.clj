(ns vcfvis.api
  (:use compojure.core
        [cemerick.friend :only [current-authentication]]
        [bcbio.variation.api.run :only [do-analysis]])
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
   :body (pr-str (cond
                  ;;TODO: figure out why cljs reader is blowing up on instant literals.
                  (map? x) (dissoc x :created-on)
                  (seq? x) (map #(dissoc % :created-on) x)
                  :else x))})

(defroutes api-routes
  (GET "/files" req
       (when-let [creds (gs-creds req)]
         (clj-response (bc-file/get-files :vcf creds))))

  (GET "/metrics" req
        (when-let [creds (gs-creds req)]
          (let [{{file-urls :file-urls} :params} req]
            (clj-response
             (for [file-url file-urls]
               (bc-metrics/plot-ready-metrics file-url
                                              :creds creds))))))
  
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
