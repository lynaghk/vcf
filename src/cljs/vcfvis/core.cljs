(ns vcfvis.core
  (:use-macros [c2.util :only [pp p interval]]
               [reflex.macros :only [computed-observable constrain!]])
  (:require [clojure.set :as set]

            [reflex.core :as reflex]
            [dubstep.pubsub :as dubstep.pubsub]))

;;;;;;;;;;;;;;;;;;;;
;;From server

(def !context
  "Background info from server; includes descriptions and defaults for different metrics, user-specific info like available files, &c."
  (atom {}))

(def !vcfs
  "VCFs currently under analysis."
  (atom []))

;;VCFs look like
{:file-url "gs://myfile.vcf"
 :available-metrics #{"QUAL" "PR" "QD" "MD"}
 :raw "<js array of js objects for use with CrossFilter>"}

;;;;;;;;;;;;;;;;;;;;
;;Derived

(def !available-files
  "User's files available on the server."
  (computed-observable
   (get @!context :files [])))

;;Y U No haz identity, clojure.set/intersection!?!?!
(defn intersection
  ([] #{})
  ([& args] (apply set/intersection args)))

(def !shared-metrics
  "Metrics"
  (computed-observable
   ;;TODO, order metrics by relevance/usefulness to biologists.
   (reduce intersection (map :available-metrics @!vcfs))))

;;;;;;;;;;;;;;;;;;;;
;;From UI

(def !metric
  "The metric to be displayed on the main histogram."
  (atom {}))

(defn select-metric! [metric]
  (reset! !metric metric))


(constrain! ;;Make sure that the selected metric is always one of the shared metrics
 (let [shared @!shared-metrics]
   (when (seq shared)
     (when-not (some #{@!metric} shared)
       (select-metric! (first shared))))))
