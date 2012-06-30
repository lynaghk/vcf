(ns vcfvis.core
  (:use-macros [c2.util :only [pp p interval]]
               [reflex.macros :only [computed-observable]])
  (:use [clojure.set :only [intersection]])
  (:require [reflex.core :as reflex]))


(def !available-filenames
  "User's files available on the server.
   May want to refactor this design if users have more than a few dozen files."
  (atom []))

(def !vcfs
  "VCFs currently under analysis."
  (atom []))

(def !shared-metrics
  "Metrics"
  (computed-observable
   ;;TODO, order metrics by relevance/usefulness to biologists.
   (apply intersection
          (map (fn [vcf]
                 (set (map (fn [metric]
                             (select-keys metric [:id :desc]))
                           (vcf :metrics))))
               @!vcfs))))


(def !metric
  "The metric to be displayed on the histogram."
  (atom {:id "QUAL"}))




(defn vcf-metric [vcf metric-id]
  (first (filter #(= metric-id (% :id))
                 (vcf :metrics))))


