(ns vcfvis.controls
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [constrain!]]
               [dubstep.macros :only [publish! subscribe!]])
  (:use [chosen.core :only [ichooseu! options]]
        [c2.core :only [unify]]
        [c2.util :only [clj->js]])
  (:require [vcfvis.core :as core]
            [vcfvis.histogram :as histogram]
            [singult.core :as singult]
            [c2.dom :as dom]
            [c2.event :as event]))


;;;;;;;;;;;;;;;;;;
;;File multiselect
(def file-selector
  (let [$selector (dom/append! "#file-selector"
                               [:select {:multiple "multiple"
                                         :data-placeholder "Select VCF files"}])
        !c (ichooseu! $selector)]

    ;;The selector should always reflect the user's avaliable flies
    (constrain!
     (options !c (map (fn [{:keys [filename id]}]
                        {:text filename :value id})
                      @core/!available-files)))
    !c))




;;;;;;;;;;;;;;;;;;;;;;;
;;Metrics mini-hists
(bind! "#metrics"
       (let [shared (set (map :id @core/!shared-metrics))
             selected-metric @core/!metric
             metrics (for [m (vals (@core/!context :metrics))]
                       (assoc m
                         :selected? (= m selected-metric)
                         :shared? (contains? shared (m :id))))]
         [:div#metrics
          (unify metrics
                 (fn [{:keys [id desc selected? shared?]}]
                   [:div.metric {:id (str "metric-" id)
                                 :class (str (when selected? "selected")
                                             " " (when-not shared?  "disabled"))}
                    [:h2 id]
                    [:span desc]
                    [:div.mini-hist]]))]))

(event/on "#metrics" :click
          (fn [d] (core/select-metric! (dissoc d :selected? :shared?))))



(subscribe! {:filter-updated metric}
            (let [vcf (first @core/!vcfs)] ;;TODO, faceting
              ;;Redraw all metrics besides the one whose filter was updated.
              (doseq [m (remove #{metric} @core/!shared-metrics)]
                (singult/merge! (dom/select (str "#metric-" (:id m) " .mini-hist"))
                                [:div.mini-hist (histogram/hist-svg* vcf m
                                                                     :margin 0
                                                                     :height 100
                                                                     :width 250
                                                                     :bars? false)]))))


;;;;;;;;;;;;;;;;;;;;;;;
;;TODO: download button

;; (case (get @data/!analysis-status (vcf :filename))
;;                  :completed  [:button.btn {:properties {:disabled true}} "Completed"]
;;                  :running    [:button.btn {:properties {:disabled true}} "Running..."]
;;                  nil         [:button.btn {:properties {:disabled false}} "Export subset"])
