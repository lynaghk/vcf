(ns vcfvis.api
  (:use compojure.core
        [cemerick.friend :only [current-authentication]]
        [bcbio.variation.api.run :only [do-analysis]]
        [cheshire.core :only [generate-string]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [bcbio.variation.api.file :as bc-file]
            [bcbio.variation.api.metrics :as bc-metrics]))

(defn clj-response [x]
  {:status 202
   :headers {"Content-Type" "application/clojure; charset=utf-8"}
   :body (pr-str x)})

(defroutes api-routes
  (GET "/context" req
       (when-let [rclient (:client (current-authentication))]
         (clj-response {:files (bc-file/list-files-w-cache rclient :vcf)
                        :metrics (bc-metrics/available-metrics nil)
                        :username (:identity (current-authentication))})))

  (GET "/metrics" req
       (when-let [rclient (:client (current-authentication))]
         (let [{{file-urls :file-urls} :params} req]
           (clj-response
            (for [file-url file-urls]
              (bc-metrics/plot-ready-metrics file-url
                                             :rclient rclient))))))

  (GET "/vcf" req
       (when-let [rclient (:client (current-authentication))]
         (let [{{file-url :file-url} :params} req]
           (let [raw (bc-metrics/get-raw-metrics file-url
                                                 :rclient rclient :use-subsample? true)]
             (generate-string
              {:clj (pr-str {:file-url file-url
                             :available-metrics (-> raw first (dissoc :id) keys set)})
               :raw raw})))))

  (POST "/filter" req
        (when-let [rclient (:client (current-authentication))]
          (let [{{:keys [file-url metrics]} :params} req]
            (clj-response
             ;;do-analysis returns a seq, but the result is always just one file
             (first (do-analysis :filter
                                 {:filename file-url
                                  :metrics metrics}
                                 rclient))))))

  (route/not-found "API ERROR =("))
