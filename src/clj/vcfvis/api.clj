(ns vcfvis.api
  (:use compojure.core
        [cemerick.friend :only [current-authentication]]
        [cemerick.shoreleave.rpc :only [defremote]]
        [bcbio.variation.api.run :only [do-analysis]]
        [cheshire.core :only [generate-string]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [bcbio.variation.api.file :as bc-file]
            [bcbio.variation.api.metrics :as bc-metrics]))

(defremote ^{:remote-name :variant/context} variant-context
  "Retrieve user-level details about available files and global metrics."
  []
  (when-let [rclient (:client (current-authentication))]
    {:files (bc-file/list-files-w-cache rclient :vcf)
     :metrics (bc-metrics/available-metrics nil)
     :username (:identity (current-authentication))}))

(defn clj-response [x]
  {:status 202
   :headers {"Content-Type" "application/clojure; charset=utf-8"}
   :body (pr-str x)})

(defroutes api-routes
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
