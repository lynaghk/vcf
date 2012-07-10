(ns vcfvis.core
  (:use-macros [c2.util :only [pp p interval]]
               [reflex.macros :only [computed-observable constrain!]])
  (:use [clojure.set :only [intersection]])
  (:require [reflex.core :as reflex]))

;;;;;;;;;;;;;;;;;;;;
;;From server

(def !available-files
  "User's files available on the server.
   May want to refactor this design if users have more than a few dozen files."
  (atom []))

(def !vcfs
  "VCFs currently under analysis."
  (atom []))


;;;;;;;;;;;;;;;;;;;;
;;Derived

(def !shared-metrics
  "Metrics"
  (computed-observable
   ;;TODO, order metrics by relevance/usefulness to biologists.
   (let [sets (map (fn [vcf]
                     (set (map (fn [metric]
                                 (select-keys metric [:id :desc]))
                               (vcf :metrics))))
                   @!vcfs)]
     
     (when (seq sets)
       (apply intersection sets)))))







;;;;;;;;;;;;;;;;;;;;
;;From UI

(def !metric
  "The metric to be displayed on the histogram."
  (atom {}))

(defn select-metric! [metric]
  (reset! !metric metric))


(constrain! ;;Make sure that the selected metric is always one of the shared metrics
 (let [shared @!shared-metrics]
   (when (seq shared)
     (when-not (some #{@!metric} shared)
       (select-metric! (first shared))))))

;;;;;;;;;;;;;;;;;;;;
;;Querying

(defn vcf-metric [vcf metric-id]
  (first (filter #(= metric-id (% :id))
                 (vcf :metrics))))
